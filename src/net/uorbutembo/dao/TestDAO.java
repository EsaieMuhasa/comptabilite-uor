/**
 * 
 */
package net.uorbutembo.dao;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

/**
 * @author Esaie MUHASA
 *
 */
public class TestDAO {

	/**
	 * @param args
	 */
	public static void test (PrintStream out) {
		try  {
			DefaultSqlDAOFactory factory = new DefaultSqlDAOFactory();
			
			Connection con = factory.getConnection();
			out.println("AutoCommit: "+con.getAutoCommit());
			DatabaseMetaData meta = con.getMetaData();
			
			ResultSet result = meta.getTables(null, null, null, new String [] {"TABLE", "VIEW"});
			
			out.println("----------TABLES & VIEWS-----------");
			while (result.next()) {
				out.printf("| -> %s\n", result.getString("TABLE_NAME"));
			}
			out.println("------------------------------\n\n");
			result.close();
			
			result = meta.getTables(null, null, null, new String [] {"TABLE", "VIEW"});
			while (result.next()) {
				
				out.printf("----| %s |----\n", result.getString("TABLE_NAME"));
				
				try (
						Statement statement = con.createStatement();
						ResultSet r = statement.executeQuery("SELECT * FROM "+result.getString("TABLE_NAME")+" LIMIT 1");
					){
					
					ResultSetMetaData m = r.getMetaData();
					
					for (int i=1; i <= m.getColumnCount(); i++) {
						out.printf("| %s\t: %s\n", m.getColumnTypeName(i), m.getColumnName(i));
					}
				}

				out.println("---------------------------");
			}
			
			result.close();
			con.close();
		} catch (Exception e) {
			out.println("-> Erreur: "+e.getMessage());
		}
	}

}
