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
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 *
 */
class StudyClassDaoSql extends OrientationDaoSql<StudyClass> implements StudyClassDao{

	public StudyClassDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	protected synchronized StudyClass mapping(ResultSet result) throws SQLException, DAOException {
		StudyClass s = new StudyClass(result.getLong("id"));
		s.setAcronym(result.getString("acronym"));
		s.setName(result.getString("name"));
		s.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			s.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return s;
	}

	@Override
	public boolean checkByAcademicYear(AcademicYear year, Department department) throws DAOException {
		final String sql = getSQLRequestCheckByAcademicYear(year.getId())+String.format(" AND %s.department =  %d LIMIT 1 OFFSET 0", Promotion.class.getSimpleName(), department.getId());
		System.out.println(" -> "+sql);
		try(
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int countByAcademicYear(AcademicYear year, Department department) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<StudyClass> findByAcademicYear(AcademicYear year, Department department) throws DAOException {
		List<StudyClass> $return = new ArrayList<>();
		final String sql = getSQLRequestCheckByAcademicYear(year.getId())+String.format(" AND %s.department =  %d", Promotion.class.getSimpleName(), department.getId());
		System.out.println(" -> "+sql);
		try(
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			
			while (result.next()) 
				$return.add(this.mapping(result));
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if($return.isEmpty())
			throw new DAOException("No data in "+this.getTableName()+" table for "+year.getLabel()+" year");
		
		return $return;
	}

	@Override
	protected String getTableName() {
		return StudyClass.class.getSimpleName();
	}

	@Override
	protected String getSQLRequestFindByAcademicYear(long yearId) {
		String sql = String.format("SELECT DISTINCT %s.acronym AS acronym, %s.name AS name, %s.id AS id, %s.recordDate AS recordDate, %s.lastUpdate AS lastUpdate "
				+ "FROM %s INNER JOIN %s ON %s.id = %s.studyClass WHERE %s.academicYear = %d", 
				getTableName(), getTableName(), getTableName(), getTableName(), getTableName(),
				getTableName(), Promotion.class.getSimpleName(), getTableName(),
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		return sql;
	}

	@Override
	protected String getSQLRequestCheckByAcademicYear(long yearId) {
		return getSQLRequestFindByAcademicYear(yearId);
	}

	@Override
	protected String getSQLRequestCountByAcademicYear(long yearId) {
		String sql = String.format("SELECT COUNT(DISTINCT %s.id) AS nombre FROM %s INNER JOIN %s ON %s.id = %s.studyClass WHERE %s.academicYear = %d", 
				getTableName(), getTableName(), Promotion.class.getSimpleName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		return sql;
	}

}
