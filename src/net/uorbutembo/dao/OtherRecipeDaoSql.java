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
import net.uorbutembo.beans.OtherRecipe;

/**
 * @author Esaie MUHASA
 *
 */
class OtherRecipeDaoSql extends UtilSql<OtherRecipe> implements OtherRecipeDao {

	public OtherRecipeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public void create(OtherRecipe o) throws DAOException {
		try {
			long id = insertInTable(
					new String[] {"amount", "account", "collectionYear", "recordDate", "label"},
					new Object[] {
							o.getAmount(), o.getAccount().getId(),
							o.getCollectionYear().getId(),
							o.getRecordDate().getTime(),
							o.getLabel()
					});
			o.setId(id);
			emitOnCreate(o);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(OtherRecipe o, long id) throws DAOException {
		try {
			updateInTable(
					new String[] {"amount", "account", "collectionYear", "lastUpdate", "label"},
					new Object[] {
							o.getAmount(), o.getAccount().getId(),
							o.getCollectionYear().getId(),
							o.getLastUpdate().getTime(),
							o.getLabel()
					}, id);
			o.setId(id);
			emitOnUpdate(o);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkByAccount(long accountId) throws DAOException {
		return check("account", accountId);
	}

	@Override
	public int countByAccount(long accountId) throws DAOException {
		return count("account", accountId);
	}

	@Override
	public int countByAccount(long accountId, Date min, Date max) throws DAOException {
		int count = 0;
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account = %d AND (recordDate BETWEEN %d AND %d)",
				getTableName(), accountId, toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
		System.out.println(SQL);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			if(result.next())
				count  = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public List<OtherRecipe> findByAccount(AnnualRecipe account) throws DAOException {
		List<OtherRecipe> list = new ArrayList<>();
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account = %d ORDER BY recordDate DESC", getTableName(), account.getId());
		System.out.println(SQL);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			while (result.next())
				list.add(mapping(result, account));
			
			if(list.isEmpty())
				throw new DAOException("Aucune recete recu au compte indexer par "+account.getId());
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<OtherRecipe> findByAccount(AnnualRecipe account, Date min, Date max) throws DAOException {
		List<OtherRecipe> list = new ArrayList<>();
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account = %d AND (recordDate BETWEEN %d AND %d) ORDER BY recordDate DESC",
				getTableName(), account.getId(), toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
		System.out.println(SQL);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			while (result.next())
				list.add(mapping(result, account));
			
			if(list.isEmpty())
				throw new DAOException("Aucune recete recu au compte indexer par "+account.getId());
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return list;
	}

	@Override
	protected OtherRecipe mapping(ResultSet result) throws SQLException, DAOException {
		OtherRecipe recipe = new OtherRecipe(result.getLong("id"));
		recipe.setAmount(result.getDouble("amount"));
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setAccount(new AnnualRecipe(result.getLong("account")));
		recipe.setLabel(result.getString("label"));
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) 
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return recipe;
	}
	
	protected OtherRecipe mapping(ResultSet result, AnnualRecipe account) throws SQLException, DAOException {
		OtherRecipe recipe = new OtherRecipe(result.getLong("id"));
		recipe.setAmount(result.getDouble("amount"));
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setAccount(account);
		recipe.setLabel(result.getString("label"));
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) 
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return recipe;
	}

	@Override
	protected String getTableName() {
		return OtherRecipe.class.getSimpleName();
	}

}
