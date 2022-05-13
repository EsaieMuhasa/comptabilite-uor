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

import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;

/**
 * @author Esaie MUHASA
 *
 */
class AllocationRecipeDaoSql extends UtilSql<AllocationRecipe> implements AllocationRecipeDao {

	public AllocationRecipeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}

	@Override
	public void create(AllocationRecipe a) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			create(connection, a);
			emitOnCreate(a);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	protected void create(Connection connection, AllocationRecipe t) throws DAOException, SQLException {
		long id = insertInTable(
				connection,
				new String[] {"percent", "recipe", "spend", "recordDate"},
				new Object[] {t.getPercent(), t.getRecipe().getId(), 
						t.getSpend().getId(), t.getRecordDate().getTime()});
		t.setId(id);
	}
	
	
	@Override
	protected void create(Connection connection, AllocationRecipe[] t) throws DAOException, SQLException {
		for (AllocationRecipe recipe : t) {
			create(connection, recipe);
		}
	}
	
	@Override
	public void create(AllocationRecipe[] t) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			connection.setAutoCommit(false);
			create(connection, t);
			connection.commit();
			emitOnCreate(t);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(AllocationRecipe t, long id) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			updateInTable(
					connection,
					new String[] {"percent", "recipe", "spend", "recordDate"},
					new Object[] {t.getPercent(), t.getRecipe().getId(), 
							t.getSpend().getId(), t.getRecordDate().getTime()}, id);
			t.setId(id);
			emitOnCreate(t);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public void update(AllocationRecipe[] all, long[] id) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			String fields [] = new String[] {"percent", "recipe", "spend", "recordDate"};
			connection.setAutoCommit(false);
			for (int i = 0; i < id.length; i++) {
				updateInTable(
						connection,
						fields,
						new Object[] {all[i].getPercent(), all[i].getRecipe().getId(), 
								all[i].getSpend().getId(), all[i].getRecordDate().getTime()}, id[i]);
				all[i].setId(id[i]);
			}
			connection.commit();
			emitOnCreate(all);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean check(long recipeId, long spendId) throws DAOException {
		return check(
				new String[] {"recipe", "spend"},
				new Object[] {recipeId, spendId});
	}

	@Override
	public AllocationRecipe find (AnnualRecipe recipe, AnnualSpend spend) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE recipe = %d AND spend =%d", getViewName(), recipe.getId(), spend.getId());
		System.out.println(SQL);
		
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if (result.next())
				return mapping(result, recipe, spend);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		throw new DAOException("Aucune configuration indexer par "+recipe.getId()+" "+spend.getId());
	}

	@Override
	public boolean checkByRecipe(long recipeId) throws DAOException {
		return check("recipe", recipeId);
	}

	@Override
	public int countByRecipe(long recipeId) throws DAOException {
		return count("recipe", recipeId);
	}

	@Override
	public List<AllocationRecipe> findByRecipe(AnnualRecipe recipe) throws DAOException {
		List<AllocationRecipe> list = new ArrayList<>();
		
		final String SQL = String.format("SELECT * FROM %s WHERE recipe = %d", getViewName(), recipe.getId());
		System.out.println(SQL);
		
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				list.add(mapping(result, recipe, null));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(list.isEmpty())
			throw new DAOException("Aucune configuration pour la recete indexer par "+recipe.getId());
		return list;
	}

	@Override
	public boolean checkBySpend(long spendId) throws DAOException {
		return check("spend", spendId);
	}

	@Override
	public int countBySpend(long spendId) throws DAOException {
		return count("spend", spendId);
	}

	@Override
	public List<AllocationRecipe> findBySpend(AnnualSpend spend) throws DAOException {
		List<AllocationRecipe> list = new ArrayList<>();
		
		final String SQL = String.format("SELECT * FROM %s WHERE spend = %d", getViewName(), spend.getId());
		System.out.println(SQL);
		
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				list.add(mapping(result, null, spend));
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(list.isEmpty())
			throw new DAOException("Aucune configuration pour depense indexer par "+spend.getId());
		return list;
	}

	@Override
	protected AllocationRecipe mapping(ResultSet result) throws SQLException, DAOException {
		AllocationRecipe allocation = new AllocationRecipe(result.getLong("id"));
		allocation.setRecordDate(new Date(result.getLong("recordDate")));
		allocation.setPercent(result.getFloat("percent"));
		allocation.setRecipe(new AnnualRecipe(result.getLong("recipe")));
		allocation.setSpend(new AnnualSpend(result.getLong("spend")));
		allocation.setCollected(result.getDouble("collected"));
		if(result.getLong("lastUpdate") != 0)
			allocation.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return allocation;
	}
	
	protected AllocationRecipe mapping(ResultSet result, AnnualRecipe recipe, AnnualSpend spend) throws SQLException, DAOException {
		AllocationRecipe allocation = new AllocationRecipe(result.getLong("id"));
		allocation.setRecordDate(new Date(result.getLong("recordDate")));
		allocation.setPercent(result.getFloat("percent"));
		allocation.setCollected(result.getDouble("collected"));
		if(recipe == null)
			allocation.setRecipe(factory.findDao(AnnualRecipeDao.class).findById(result.getLong("recipe")));
		else 
			allocation.setRecipe(recipe);
		if(spend == null)
			allocation.setSpend(factory.findDao(AnnualSpendDao.class).findById(result.getLong("spend")));
		else 
			allocation.setSpend(spend);
		if(result.getLong("lastUpdate") != 0)
			allocation.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return allocation;
	}

	@Override
	protected String getTableName() {
		return AllocationRecipe.class.getSimpleName();
	}

}
