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
import net.uorbutembo.beans.PaymentLocation;

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
					new String[] {"amount", "account", "collectionYear", "collectionDate", "recordDate", "label", "location", "receivedNumber"},
					new Object[] {
							o.getAmount(), o.getAccount().getId(),
							o.getCollectionYear().getId(),
							o.getCollectionDate().getTime(),
							o.getRecordDate().getTime(),
							o.getLabel(),
							o.getLocation().getId(),
							o.getReceivedNumber() == 0? null : o.getReceivedNumber()
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
					new String[] {"amount", "account", "collectionYear", "collectionDate", "lastUpdate", "label", "location", "receivedNumber"},
					new Object[] {
							o.getAmount(), o.getAccount().getId(),
							o.getCollectionYear().getId(),
							o.getCollectionDate().getTime(),
							o.getLastUpdate().getTime(),
							o.getLabel(),
							o.getLocation().getId(),
							o.getReceivedNumber() == 0? null : o.getReceivedNumber()
					}, id);
			o.setId(id);
			emitOnUpdate(o);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public OtherRecipe findByReceivedNumber(int receivedNumber) throws DAOException {
		return find("receivedNumber", receivedNumber);
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
	public double getSoldByAccounts (long [] accounts, long location) throws DAOException {
		String [] ids = new String [accounts.length];
		for (int i = 0; i < accounts.length; i++)
			ids[i] = accounts[i]+"";
		
		final String SQL = String.format("SELECT SUM(amount) AS amount FROM %s WHERE account IN(%s) AND location = %d", getTableName(), String.join(", ", ids), location);
		double sum = 0;
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if(result.next())
				sum = result.getDouble("amount");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sum;
	}
	
	@Override
	public double getSoldByAcademicYear(AcademicYear year, Date date) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), year.getId());
		final String sql =String.format("SELECT SUM(amount) AS amount FROM %s WHERE account IN(%s) AND (collectionDate BETWEEN %d AND %d)", 
				getTableName(), sqlAccount, toMinTimestampOfDay(date).getTime(), toMaxTimestampOfDay(date).getTime());
		double sum = 0;
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if(result.next())
				sum = result.getDouble("amount");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sum;
	}
	
	@Override
	public double getSoldByAcademicYearBeforDate(AcademicYear year, Date date) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), year.getId());
		final String sql =String.format("SELECT SUM(amount) AS amount FROM %s WHERE account IN(%s) AND collectionDate <= %d", 
				getTableName(), sqlAccount, toMaxTimestampOfDay(date).getTime());
		double sum = 0;
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if(result.next())
				sum = result.getDouble("amount");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sum;
	}

	@Override
	public int countByAccount(long accountId, Date min, Date max) throws DAOException {
		int count = 0;
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account = %d AND (recordDate BETWEEN %d AND %d)",
				getTableName(), accountId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
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
				getTableName(), account.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
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
	public List<OtherRecipe> findByAcademicYear(long academicYearId, int limit, int offset) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), academicYearId);
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s) LIMIT %d OFFSET %d", getTableName(), sqlAccount, limit, offset);
		List<OtherRecipe> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if (data.isEmpty())
			throw new DAOException("Aucune operations pour l'annee indexer par "+academicYearId);
		
		return data;
	}

	@Override
	public List<OtherRecipe> findByAcademicYear(long academicYearId) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), academicYearId);
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s)", getTableName(), sqlAccount);
		List<OtherRecipe> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if (data.isEmpty())
			throw new DAOException("Aucune operations pour l'annee indexer par "+academicYearId);
		
		return data;
	}
	
	@Override
	public boolean checkByAcademicYear(AcademicYear year, int offset) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), year.getId());
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s) LIMIT 1 OFFSET %d", getTableName(), sqlAccount, offset);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<OtherRecipe> findByAcademicYear (long academicYearId, Date min, Date max) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), academicYearId);
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s) AND (collectionDate BETWEEN %d AND %d)",
				getTableName(), sqlAccount, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		List<OtherRecipe> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if (data.isEmpty())
			throw new DAOException("Aucune operations ["+min.toString()+" , "+max.toString()+"] pour l'annee indexer par "+academicYearId);
		
		return data;
	}

	@Override
	public boolean checkByAcademicYear(long academicYearId) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), academicYearId);
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s)", getTableName(), sqlAccount);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			return (result.next());
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int countByAcademicYear(long academicYearId) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), academicYearId);
		final String sql =String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account IN(%s)", getTableName(), sqlAccount);
		int count = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next())
				count = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public int countByAcademicYear(long academicYearId, Date min, Date max) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), academicYearId);
		final String sql =String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account IN(%s) AND (collectionDate BETWEEN %d AND %d)",
				getTableName(), sqlAccount, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		int count = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next())
				count = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public boolean checkByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), yearId);
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s) AND collectionDate < %d", 
				getTableName(), sqlAccount, toMinTimestampOfDay(date).getTime());
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<OtherRecipe> findByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		final String sqlAccount = String.format("SELECT id FROM %s WHERE academicYear = %d", AnnualRecipe.class.getSimpleName(), yearId);
		final String sql =String.format("SELECT * FROM %s WHERE account IN(%s) AND collectionDate < %d", 
				getTableName(), sqlAccount, toMinTimestampOfDay(date).getTime());
		List<OtherRecipe> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune operation en date du "+date.toString()+" pour l'annee indexer par "+yearId);
		
		return data;
	}
	
	@Override
	public List<OtherRecipe> findByAccount(AnnualRecipe account, PaymentLocation location) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkByAccount(long account, long location) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkByAccount(long account, long location, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int countByAccount(long account, long location, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<OtherRecipe> findByAccount(AnnualRecipe account, PaymentLocation location, Date min, Date max)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkByLocation(long location) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkByLocation(long location, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkByLocation(long location, long year) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	protected OtherRecipe baseMapping (ResultSet result) throws DAOException, SQLException {
		OtherRecipe recipe = new OtherRecipe(result.getLong("id"));
		recipe.setAmount(result.getDouble("amount"));
		recipe.setLabel(result.getString("label"));
		recipe.setCollectionDate(new Date(result.getLong("collectionDate")));
		recipe.setReceivedNumber(result.getInt("receivedNumber"));
		if(result.getLong("lastUpdate") != 0) 
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return recipe;
	}

	@Override
	protected OtherRecipe mapping(ResultSet result) throws SQLException, DAOException {
		OtherRecipe recipe = baseMapping(result);
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setAccount(new AnnualRecipe(result.getLong("account")));
		recipe.setLocation(factory.findDao(PaymentLocationDao.class).findById(result.getLong("location")));
		return recipe;
	}
	
	protected OtherRecipe mapping(ResultSet result, AnnualRecipe account) throws SQLException, DAOException {
		OtherRecipe recipe = baseMapping(result);
		recipe.setAccount(account);
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setCollectionDate(new Date(result.getLong("collectionDate")));
		recipe.setLocation(factory.findDao(PaymentLocationDao.class).findById(result.getLong("location")));
		return recipe;
	}
	
	protected OtherRecipe mapping(ResultSet result, AnnualRecipe account, PaymentLocation location) throws SQLException, DAOException {
		OtherRecipe recipe = baseMapping(result);
		recipe.setAccount(account);
		recipe.setLocation(location);
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setCollectionDate(new Date(result.getLong("collectionDate")));
		return recipe;
	}
	
	protected OtherRecipe mapping(ResultSet result, PaymentLocation location) throws SQLException, DAOException {
		OtherRecipe recipe = baseMapping(result);
		recipe.setAccount(new AnnualRecipe(result.getLong("account")));
		recipe.setLocation(location);
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setCollectionDate(new Date(result.getLong("collectionDate")));
		return recipe;
	}

	@Override
	protected String getTableName() {
		return OtherRecipe.class.getSimpleName();
	}

}
