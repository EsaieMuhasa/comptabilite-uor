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

import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Outlay;

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
					new String[] {"account", "amount", "wording", "deliveryDate", "recordDate"},
					new Object[] {
							o.getAccount().getId(),
							o.getAmount(),
							o.getWording(),
							o.getDeliveryDate().getTime(),
							o.getRecordDate().getTime()
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
					new String[] {"account", "amount", "wording", "deliveryDate", "lastUpdate"},
					new Object[] {
							o.getAccount().getId(),
							o.getAmount(),
							o.getWording(),
							o.getDeliveryDate().getTime(),
							o.getLastUpdate().getTime()
					}, id);
			o.setId(id);
			emitOnUpdate(o);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkByAcademicYear(long yearId) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d) LIMIT 1",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId);
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT id FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d) AND (deliveryDate BETWEEN %d AND %d) LIMIT 1",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d)",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId);
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d) AND (deliveryDate BETWEEN %d AND %d)",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT * FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d)",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId);
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT * FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d) LIMIT %d OFFSET %d",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId, limit, offset);
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT * FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d) AND (deliveryDate BETWEEN %d AND %d)",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		System.out.println(SQL);
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
		final String SQL = String.format("SELECT * FROM %s WHERE account IN(SELECT id FROM %s WHERE academicYear = %d) AND (deliveryDate BETWEEN %d AND %d) LIMIT %d OFFSET %d",
				getTableName(), AnnualSpend.class.getSimpleName(), yearId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime(), limit, offset);
		System.out.println(SQL);
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
		System.out.println(SQL);
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
	public boolean checkByAccount(long accountId, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT id FROM %s WHERE account = %d AND (deliveryDate BETWEEN %d AND %d) LIMIT 1",
				getTableName(), accountId, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		System.out.println(SQL);
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
		System.out.println(SQL);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account));
			
			if(data.isEmpty())
				throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId());
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	public List<Outlay> findByAccount(AnnualSpend account, int limit, int offset) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE account = %d LIMIT %d OFFSET %d", getTableName(), account.getId(), limit, offset);
		System.out.println(SQL);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account));
			
			if(data.isEmpty())
				throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId()+" pour l'intervale selection de "+offset+" a "+(offset+limit));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	public List<Outlay> findByAccount(AnnualSpend account, Date min, Date max) throws DAOException {
		final String SQL = String.format("SELECT * FROM %s WHERE account = %d AND (deliveryDate BETWEEN %d AND %d)", getTableName(), 
				account.getId(), toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		System.out.println(SQL);
		List<Outlay> data = new ArrayList<>();
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result, account));
			
			if(data.isEmpty())
				throw new DAOException("Aucune operation pour le compte indexer par: "+account.getId()+" pour la l'intervale de date "+min+" a "+max);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	protected Outlay mapping(ResultSet result) throws SQLException, DAOException {
		Outlay out = new Outlay(result.getLong("id"));
		out.setAccount(result.getLong("account"));
		out.setRecordDate(new Date(result.getLong("recordDate")));
		out.setDeliveryDate(new Date(result.getLong("deliveryDate")));
		out.setWording(result.getString("wording"));
		out.setReference(result.getString("reference"));
		if(result.getLong("lastUpdate") > 0) {
			out.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return out;
	}
	
	protected Outlay mapping(ResultSet result, AnnualSpend account) throws SQLException, DAOException {
		Outlay out = new Outlay(result.getLong("id"));
		out.setAccount(account);
		out.setRecordDate(new Date(result.getLong("recordDate")));
		out.setDeliveryDate(new Date(result.getLong("deliveryDate")));
		out.setWording(result.getString("wording"));
		out.setReference(result.getString("reference"));
		if(result.getLong("lastUpdate") > 0) {
			out.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return out;
	}

	@Override
	protected String getTableName() {
		return Outlay.class.getSimpleName();
	}

}
