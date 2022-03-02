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

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Promotion;

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
	public Department findById(long id) throws DAOException {
		Department d = super.findById(id);
		d.setFaculty(this.facultyDao.findById(d.getFaculty().getId()));
		return d;
	}

	@Override
	public void create(Department d) throws DAOException {
		try {
			long id = insertInTable(
					new String [] {"acronym", "name", "recordDate", "faculty"},
					new Object[] {d.getAcronym(), d.getName(), d.getRecordDate().getTime(), d.getFaculty().getId()});
			d.setId(id);
			this.emitOnCreate(d);
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
			this.emitOnUpdate(d);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement des modifications: "+e.getMessage(), e);
		}
	}

	@Override
	public int countByFaculty(long facultyId) throws DAOException {
		final String SQL_QUERY = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE faculty=%d", this.getViewName(),  facultyId);
		System.out.println(SQL_QUERY);
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
	public List<Department> findByFaculty(long facultyId) throws DAOException {
		List<Department> departments = new ArrayList<>();
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE faculty=%d", this.getViewName(),  facultyId);
		System.out.println(SQL_QUERY);
		Faculty fac = this.facultyDao.findById(facultyId);
		try (
				Connection connection =  this.getFactory().getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			while(result.next()) {
				Department dep = this.mapping(result, false);
				dep.setFaculty(fac);
				departments.add(dep);
			}
			
			if(departments.isEmpty()){
				throw new DAOException("Aucun departement pour la faculté: \n"+fac.getName());
			}
			
		} catch (SQLException e) {
			throw new DAOException(
					"Une erreur est survenue lors de la selection des departements de la \n"+fac.getName()+"\n"+e.getMessage(), e);
		}
		return departments;
	}
	
	@Override
	public List<Department> findByFaculty(Faculty faculty) throws DAOException {
		List<Department> departments = new ArrayList<>();
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE faculty=%d", this.getViewName(),  faculty.getId());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.getFactory().getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			while(result.next()) {
				Department dep = this.mapping(result, false);
				dep.setFaculty(faculty);
				departments.add(dep);
			} 
			
			if(departments.isEmpty()){
				throw new DAOException("Aucun departement pour la faculté: \n"+faculty.getName());
			}
			
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la selection des departements de la \n"+faculty.getName()+"\n"+e.getMessage(), e);
		}
		return departments;
	}
	
	@Override
	public List<Department> findByFaculty(Faculty faculty, AcademicYear year) throws DAOException {
		List<Department> departments = new ArrayList<>();
		final String SQL_QUERY = getSQLRequestFindByAcademicYear(year.getId())+String.format(" AND %s.faculty = %d", getTableName(), faculty.getId());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.getFactory().getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			while(result.next()) {
				Department dep = this.mapping(result, false);
				dep.setFaculty(faculty);
				departments.add(dep);
			} 
			
			if(departments.isEmpty()){
				throw new DAOException("Aucun departement pour la faculté: \n"+faculty.getName()+" a l'annee academique "+year.getLabel());
			}
			
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la selection des departements de la \n"+faculty.getName()+", pour l'annee academique "+year.getLabel()+"\n"+e.getMessage(), e);
		}
		return departments;
	}
	
	@Override
	public boolean checkByFaculty(Faculty faculty, AcademicYear year) throws DAOException {
		final String SQL_QUERY = getSQLRequestCheckByAcademicYear(year.getId())+String.format(" AND %s.faculty=%d", this.getTableName(),  faculty.getId());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.getFactory().getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			return result.next();
			
		} catch (SQLException e) {
			throw new DAOException(
					"Une erreur est survenue lors du comptage de départements de la faculté dont identifier par [ "+faculty.getAcronym()+" ]\n"+e.getMessage(), e);
		}
	}
	
	@Override
	public int countByFaculty(Faculty faculty, AcademicYear year) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected synchronized Department mapping(ResultSet result) throws SQLException, DAOException {
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
	
	protected synchronized Department mapping (ResultSet result, boolean fac) throws SQLException, DAOException {
		Department d = new Department(result.getLong("id"));
		d.setAcronym(result.getString("acronym"));
		d.setName(result.getString("name"));
		d.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			d.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		if(fac) {
			d.setFaculty(this.facultyDao.findById(result.getLong("faculty")));
		}
		return d;
	}

	@Override
	protected String getTableName() {
		return Department.class.getSimpleName();
	}
	
	@Override
	protected String getSQLRequestFindByAcademicYear(long yearId) {
		String sql = String.format("SELECT DISTINCT %s.acronym AS acronym, %s.name AS name, %s.faculty AS faculty, %s.id AS id, %s.recordDate AS recordDate, %s.lastUpdate AS lastUpdate "
				+ "FROM %s INNER JOIN %s ON %s.id = %s.department WHERE %s.academicYear = %d", 
				getTableName(), getTableName(), getTableName(), getTableName(), getTableName(), getTableName(),
				getTableName(), Promotion.class.getSimpleName(),
				getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		return sql;
	}

	@Override
	protected String getSQLRequestCheckByAcademicYear(long yearId) {
		return getSQLRequestFindByAcademicYear(yearId);
	}

	@Override
	protected String getSQLRequestCountByAcademicYear(long yearId) {
		String sql = String.format("SELECT COUNT(DISTINCT %s.id) AS nombre FROM %s INNER JOIN %s ON %s.id = %s.department WHERE %s.academicYear = %d", 
				getTableName(), getTableName(), Promotion.class.getSimpleName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		return sql;
	}

}
