/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.Faculty;

/**
 * @author Esaie MUHASA
 *
 */
class FacultyDaoSql extends OrientationDaoSql<Faculty> implements FacultyDao {

	public FacultyDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	protected synchronized Faculty maping(ResultSet result) throws SQLException, DAOException {
		Faculty f = new Faculty(result.getLong("id"));
		f.setAcronym(result.getString("acronym"));
		f.setName(result.getString("name"));
		f.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			f.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return f;
	}

	@Override
	protected String getTableName() {
		return Faculty.class.getSimpleName();
	}

}
