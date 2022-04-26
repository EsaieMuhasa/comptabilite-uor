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
import net.uorbutembo.beans.AnnualRecipe;

/**
 * @author Esaie MUHASA
 *
 */
class AnnualRecipeDaoSql extends UtilSql<AnnualRecipe> implements AnnualRecipeDao {

	private UniversityRecipeDao universityRecipeDao;
	
	public AnnualRecipeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		universityRecipeDao = factory.findDao(UniversityRecipeDao.class);
	}

	@Override
	public void create(AnnualRecipe a) throws DAOException {
		try (Connection connection = factory.getConnection()){
			create(connection, a);
			emitOnCreate(a);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	protected void create(Connection connection, AnnualRecipe a) throws DAOException, SQLException{
		long id  = insertInTable(
				connection,
				new String [] {"universityRecipe", "academicYear", "recordDate"},
				new Object[] {
						a.getUniversityRecipe().getId(),
						a.getAcademicYear().getId(),
						a.getRecordDate().getTime()
				});
		a.setId(id);
	}
	
	@Override
	public void create(AnnualRecipe[] t) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			connection.setAutoCommit(false);
			for (AnnualRecipe recipe : t) {
				create(connection, recipe);
			}
			connection.commit();
			emitOnCreate(t);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(AnnualRecipe a, long id) throws DAOException {
		throw new DAOException("Operation non prise ne charge");
	}

	@Override
	public boolean check(long yearId, long recipeId) throws DAOException {
		return check(new String [] {"academicYear", "universityRecipe"}, new Object [] {yearId, recipeId});
	}

	@Override
	public boolean checkByAcademiYear(long yearId) throws DAOException {
		return check("academicYear", yearId);
	}

	@Override
	public int countByAcademiYear(long yearId) throws DAOException {
		return count("academicYear", yearId);
	}

	@Override
	public AnnualRecipe find(long yearId, long recipeId) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d AND universityRecipe = %d", getTableName(), yearId, recipeId);
		System.out.println(SQL);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			if(result.next())
				return mapping(result);
			throw new DAOException("Aucune configuration indexer par "+yearId+" "+recipeId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<AnnualRecipe> findByAcademicYear(AcademicYear year) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d", getTableName(), year.getId());
		System.out.println(SQL);
		List<AnnualRecipe> list = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			while(result.next())
				list.add(mapping(result, year));
			
			if(list.isEmpty())
				throw new DAOException("Aucune configuration pour l'annee cademique indexer par "+year.getId());
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<AnnualRecipe> findByAcademicYear(AcademicYear year, int limit, int offset) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d LIMIT d% OFFSET %d", getTableName(), year.getId(), limit, offset);
		System.out.println(SQL);
		List<AnnualRecipe> list = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			while(result.next())
				list.add(mapping(result, year));
			
			if(list.isEmpty())
				throw new DAOException("Aucune configuration pour l'annee cademique indexer par "+year.getId());
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return list;
	}

	@Override
	protected AnnualRecipe mapping (ResultSet result) throws SQLException, DAOException {
		AnnualRecipe recipe = new AnnualRecipe(result.getLong("id"));
		recipe.setAcademicYear(result.getLong("academicYear"));
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		recipe.setUniversityRecipe(result.getLong("universityRecipe"));
		if(result.getLong("lastUpdate") != 0) 
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return recipe;
	}
	
	protected AnnualRecipe mapping (ResultSet result, AcademicYear year) throws SQLException, DAOException {
		AnnualRecipe recipe = new AnnualRecipe(result.getLong("id"));
		recipe.setAcademicYear(year);
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		recipe.setUniversityRecipe(universityRecipeDao.findById(result.getLong("universityRecipe")));
		if(result.getLong("lastUpdate") != 0) 
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return recipe;
	}

	@Override
	protected String getTableName() {
		return AnnualRecipe.class.getSimpleName();
	}

}
