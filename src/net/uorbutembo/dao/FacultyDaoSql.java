/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Promotion;

/**
 * @author Esaie MUHASA
 *
 */
class FacultyDaoSql extends OrientationDaoSql<Faculty> implements FacultyDao {

	public FacultyDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	protected synchronized Faculty mapping(ResultSet result) throws SQLException, DAOException {
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
	
	@Override
	protected String getSQLRequestFindByAcademicYear(long yearId) {
		String sql = String.format("SELECT DISTINCT %s.acronym AS acronym, %s.name AS name, %s.id AS id, %s.recordDate AS recordDate, %s.lastUpdate AS lastUpdate "
				+ "FROM %s INNER JOIN %s ON %s.id = %s.faculty WHERE %s.id IN (SELECT %s.department FROM %s WHERE %s.academicYear = %d)", 
				getTableName(), getTableName(), getTableName(), getTableName(), getTableName(), 
				getTableName(), Department.class.getSimpleName(), getTableName(), Department.class.getSimpleName(), Department.class.getSimpleName(),
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		return sql;
	}

	@Override
	protected String getSQLRequestCheckByAcademicYear(long yearId) {
		return getSQLRequestFindByAcademicYear(yearId);
	}

	@Override
	protected String getSQLRequestCountByAcademicYear(long yearId) {
		String sql = String.format("SELECT COUNT(DISTINCT %s.id) AS nombre FROM %s INNER JOIN %s ON %s.id = %s.faculty WHERE %s.id IN  "
				+ "(SELECT %s.faculty FROM %s INNER JOIN %s ON %s.id = %s.department WHERE %s.academicYear = %d)", 
				getTableName(), getTableName(), Department.class.getSimpleName(), getTableName(), Department.class.getSimpleName(),  getTableName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Promotion.class.getSimpleName(), Department.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		return sql;
	}

}
