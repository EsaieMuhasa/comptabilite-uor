/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Orientation;

/**
 * @author Esaie MUHASA
 *
 */
abstract class OrientationDaoSql <T extends Orientation> extends UtilSql <T> implements OrientationDao<T> {

	public OrientationDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	

	@Override
	public synchronized void create(T f) throws DAOException {
		try {
			long id = insertInTable(
					new String [] {"acronym", "name", "recordDate"},
					new Object[] {f.getAcronym(), f.getName(), f.getRecordDate().getTime()});
			f.setId(id);
			this.emitOnCreate(f);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public synchronized void update(T f, long id) throws DAOException {
		try {
			updateInTable(
					new String [] {"acronym", "name", "lastUpdate"},
					new Object[] {f.getAcronym(), f.getName(), f.getLastUpdate().getTime()}, id);
			f.setId(id);
			this.emitOnUpdate(f);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement des modifications: "+e.getMessage(), e);
		}
	}


	@Override
	public boolean checkByAcademicYear(AcademicYear year) throws DAOException {
		final String sql = getSQLRequestCheckByAcademicYear(year.getId());
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
	public int countByAcademicYear(AcademicYear year) throws DAOException {
		int $return = 0;
		final String sql = getSQLRequestCountByAcademicYear(year.getId());
		try(
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				$return = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return $return;
	}


	@Override
	public List<T> findByAcademicYear(AcademicYear year) throws DAOException {
		List<T> $return = new ArrayList<>();
		final String sql = getSQLRequestFindByAcademicYear(year.getId());
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
	
	/**
	 * Doit renvoyer la requette SQL de selection des orientations en fonction de l'annee academique
	 * @param yearId
	 * @return
	 */
	protected abstract String getSQLRequestFindByAcademicYear (long yearId);
	
	/**
	 * Doit renvoyer la requette de verifications de l'existance des orienations pour i'annee en parametre
	 * @param yearId
	 * @return
	 */
	protected abstract String getSQLRequestCheckByAcademicYear (long yearId);
	
	/**
	 * Doit renvoyer la requette SQL de comptage des orientations qui ont fonctionner pour l'annee en parametre
	 * @param yearId
	 * @return
	 */
	protected abstract String getSQLRequestCountByAcademicYear (long yearId);


}
