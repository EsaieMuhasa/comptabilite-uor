/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;

/**
 * @author Esaie MUHASA
 *
 */
public class DepartmentDaoSql extends OrientationDaoSql<Department> implements DepartmentDao {

	private FacultyDao facultyDao;
	
	/**
	 * @param factory
	 */
	public DepartmentDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		this.facultyDao = factory.findDao(FacultyDao.class);
	}

	@Override
	public void create(Department d) throws DAOException {
		try {
			long id = insertInTable(
					new String [] {"acronym", "name", "recordDate", "faculty"},
					new Object[] {d.getAcronym(), d.getName(), d.getRecordDate().getTime(), d.getFaculty().getId()});
			d.setId(id);
			this.sendOnCreateEvent(d);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public void update(Department d, long id) throws DAOException {
		try {
			updateInTable(
					new String [] {"acronym", "name", "recordDate", "faculty"},
					new Object[] {d.getAcronym(), d.getName(), d.getRecordDate().getTime(), d.getFaculty().getId()}, id);
			d.setId(id);
			this.sendOnUpdateEvent(d);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement des modifications: "+e.getMessage(), e);
		}
	}

	@Override
	public int countByFaculty(int facultyId) throws DAOException {
		final String SQL_QUERY = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE faculty=%d", this.getViewName(),  facultyId);
		try (
				Connection connection =  this.getFactory().getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				return result.getInt("nombre");
			}
			
		} catch (SQLException e) {
			throw new DAOException(
					"Une erreur est survenue lors du comptage de départements de la faculté dont identifier par [ "+facultyId+" ]\n"+e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<Department> findByFaculty(int facultyId) throws DAOException {
		List<Department> departments = new ArrayList<>();
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE faculty=%d", this.getViewName(),  facultyId);
		Faculty fac = this.facultyDao.findById(facultyId);
		try (
				Connection connection =  this.getFactory().getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				Department dep = this.maping(result);
				dep.setFaculty(fac);
				departments.add(dep);
				
				while(result.next()) {
					dep = this.maping(result);
					dep.setFaculty(fac);
					departments.add(dep);
				}
			} else {
				throw new DAOException("Aucun departement pour la faculté: \n"+fac.getName());
			}
			
		} catch (SQLException e) {
			throw new DAOException(
					"Une erreur est survenue lors de la selection des departements de la \n"+fac.getName()+"\n"+e.getMessage(), e);
		}
		return departments;
	}

	@Override
	protected synchronized Department maping(ResultSet result) throws SQLException, DAOException {
		Department d = new Department(result.getLong("id"));
		d.setAcronym(result.getString("acronym"));
		d.setName(result.getString("name"));
		d.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			d.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		d.setFaculty(result.getLong("faculty"));
		return d;
	}

	@Override
	protected String getTableName() {
		return Department.class.getSimpleName();
	}

}
