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
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentLocation;

/**
 * @author Esaie MUHASA
 *
 */
class OutlayDaoSql extends UtilSql<Outlay> implements OutlayDao {

	public OutlayDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public void create(Outlay o) throws DAOException {
		try {
			long id = insertInTable(
					new String[] {"account", "academicYear", "amount", "wording", "deliveryDate", "deliveryYear", "recordDate", "location"},
					new Object[] {
							o.getAccount().getId(),
							o.getAcademicYear().getId(),
							o.getAmount(),
							o.getWording(),
							o.getDeliveryDate().getTime(),
							o.getDeliveryYear().getId(),
							o.getRecordDate().getTime(),
							o.getLocation().getId()
					});
			o.setId(id);
			emitOnCreate(o);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(Outlay o, long id) throws DAOException {
		try {
			updateInTable(
					new String[] {"account", "academicYear", "amount", "wording", "deliveryDate", "lastUpdate", "location"},
					new Object[] {
							o.getAccount().getId(),
							o.getAcademicYear().getId(),
							o.getAmount(),
							o.getWording(),
							o.getDeliveryDate().getTime(),
							o.getLastUpdate().getTime(),
							o.getLocation().getId()
					}, id);
			o.setId(id);
			emitOnUpdate(o);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkByAcademicYear(long yearId) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE academicYear = %d LIMIT 1",
				getTableName(), yearId);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkByAcademicYear(long yearId, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE academicYear = %d AND (deliveryDate BETWEEN %d AND %d) LIMIT 1",
				getTableName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int countByAcademicYear(long yearId) throws DAOException {
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE academicYear = %d",
				getTableName(), yearId);
		int count = 0;
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if(result.next())
				count = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public int countByAcademicYear(long yearId, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE academicYear = %d AND (deliveryDate BETWEEN %d AND %d)",
				getTableName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		int count = 0;
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if(result.next())
				count = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public List<Outlay> findByAcademicYear(long yearId) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d",
				getTableName(), yearId);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucune operation pour l'annee academique indexer par: "+yearId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	public List<Outlay> findByAcademicYear (long yearId, int limit, int offset) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d LIMIT %d OFFSET %d",
				getTableName(), yearId, limit, offset);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucune opération pour l'année academique indexer par: "+yearId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	public List<Outlay> findByAcademicYear(long yearId, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d AND (deliveryDate BETWEEN %d AND %d)",
				getTableName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucune opération pour l'année academique indexer par: "+yearId+" en date du "+DATE_TIME_FORMAT.format(min)+" au "+DATE_TIME_FORMAT.format(max));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	public List<Outlay> findByAcademicYear(long yearId, Date min, Date max, int limit, int offset) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE academicYear = %d AND (deliveryDate BETWEEN %d AND %d) LIMIT %d OFFSET %d",
				getTableName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime(), limit, offset);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucune opération pour l'année academique indexer par: "+yearId
						+" en date du "+DATE_TIME_FORMAT.format(min)+" au "+DATE_TIME_FORMAT.format(max)+", pour l'intervale de selection de "+offset+" à "+(offset+limit));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	public boolean checkByAccount(long accountId) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE account = %d LIMIT 1", getTableName(), accountId);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public double getSoldByAccount(long account) throws DAOException {
		final String SQL = String.format("SELECT SUM(amount) AS sold FROM %s WHERE account = %d", getTableName(), account);
		double sum = 0;
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if (result.next())
				sum = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sum;
	}
	
	@Override
	public double getSoldByAccount (long account, long location) throws DAOException {
		final String SQL = String.format("SELECT SUM(amount) AS sold FROM %s WHERE account = %d AND location = %d", getTableName(), account, location);
		double sum = 0;
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if (result.next())
				sum = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return sum;
	}
	
	@Override
	public boolean checkByAccount(long account, long location) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE account = %d AND location = %d LIMIT 1", getTableName(), account, location);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int countByAccount(long account, long location) throws DAOException {
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account = %d AND location = %d", getTableName(), account, location);
		int count = 0;
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if (result.next()) 
				count = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public boolean checkByAccount(long accountId, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE account = %d AND (deliveryDate BETWEEN %d AND %d) LIMIT 1",
				getTableName(), accountId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<Outlay> findByAccount(AnnualSpend account) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE account = %d", getTableName(), account.getId());
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId());
		return data;
	}
	
	@Override
	public List<Outlay> findByAccount(AnnualSpend account, PaymentLocation location) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE account = %d AND location = %d", getTableName(), account.getId(), location.getId());
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account, location));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId());
		return data;
	}

	@Override
	public List<Outlay> findByAccount(AnnualSpend account, int limit, int offset) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE account = %d LIMIT %d OFFSET %d", getTableName(), account.getId(), limit, offset);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId()+" pour l'intervale selection de "+offset+" a "+(offset+limit));
		return data;
	}

	@Override
	public List<Outlay> findByAccount(AnnualSpend account, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE account = %d AND (deliveryDate BETWEEN %d AND %d)", getTableName(), 
				account.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId()+" pour la l'intervale de date "+min+" a "+max);
		return data;
	}

	@Override
	public boolean checkByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE academicYear = %d AND deliveryDate <= %d LIMIT 1",
				getTableName(), yearId, toMaxTimestampOfDay(date).getTime());
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<Outlay> findByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE academicYear = %d AND deliveryDate <= %d",
				getTableName(), yearId, toMaxTimestampOfDay(date).getTime());
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune operation pour l'annee acadmeique index par "+yearId+" avant la date "+date.toString());
		return data;
	}

	protected Outlay baseMapping(ResultSet result) throws SQLException, DAOException{
		Outlay out = new Outlay(result.getLong("id"));
		out.setRecordDate(new Date(result.getLong("recordDate")));
		out.setDeliveryDate(new Date(result.getLong("deliveryDate")));
		out.setWording(result.getString("wording"));
		out.setReference(result.getString("reference"));
		out.setAmount(result.getDouble("amount"));
		
		if(result.getLong("lastUpdate") > 0) {
			out.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return out;
	}
	
	@Override
	protected Outlay mapping(ResultSet result) throws SQLException, DAOException {
		Outlay out = baseMapping(result);
		out.setAccount(result.getLong("account"));
		out.setAcademicYear(new AcademicYear(result.getLong("academicYear")));
		out.setLocation(factory.findDao(PaymentLocationDao.class).findById(result.getLong("location")));
		return out;
	}
	
	protected Outlay mapping(ResultSet result, AnnualSpend account) throws SQLException, DAOException {
		Outlay out = baseMapping(result);
		out.setAccount(account);
		out.setAcademicYear(new AcademicYear(result.getLong("academicYear")));
		out.setLocation(factory.findDao(PaymentLocationDao.class).findById(result.getLong("location")));
		return out;
	}
	
	protected Outlay mapping(ResultSet result, AnnualSpend account, PaymentLocation location) throws SQLException, DAOException {
		Outlay out = baseMapping(result);
		out.setAccount(account);
		out.setLocation(location);
		out.setAcademicYear(new AcademicYear(result.getLong("academicYear")));
		return out;
	}

	@Override
	protected String getTableName() {
		return Outlay.class.getSimpleName();
	}

}
