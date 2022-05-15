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
import net.uorbutembo.beans.DBEntity;
import net.uorbutembo.beans.DefaultRecipePart;
import net.uorbutembo.beans.RecipePart;

/**
 * @author Esaie MUHASA
 *
 */
abstract class AbstractRecipePartDao < S extends DBEntity> extends UtilSql<DefaultRecipePart<S>> implements RecipePartDao<RecipePart<S>, DefaultRecipePart<S>, S> {

	/**
	 * @param factory
	 */
	public AbstractRecipePartDao(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}

	@Override
	public DefaultRecipePart<S> findById (long id) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public boolean checkById(long id) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public void create(DefaultRecipePart<S> e) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public void update(DefaultRecipePart<S> e, long id) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public synchronized boolean check(String columnName, Object value, long id) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public synchronized void delete(long id) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public synchronized DefaultRecipePart<S> delete(Connection connection, long id) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public synchronized void delete(long[] ids) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}

	@Override
	public synchronized List<DefaultRecipePart<S>> delete(Connection connection, long[] ids) throws DAOException {
		throw new DAOException("Operation non prise en charge");
	}
	
	@Override
	protected String getTableName() {
		throw new DAOException("Aucune table physique dans la base de donnee: utiliser plutot la vue => "+getViewName());
	}

	@Override
	public List<DefaultRecipePart<S>> findByAcademicYear (long academicYearId, int limit, int offset) throws DAOException {
		List<DefaultRecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d) LIMIT %d OFFSET %d", 
				getViewName(), AnnualSpend.class.getSimpleName(), academicYearId, limit, offset);
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour l'année academique indexer par "+academicYearId+" pour l'intervale specifier");
		return data;
	}

	@Override
	public List<DefaultRecipePart<S>> findByAcademicYear (long academicYearId) throws DAOException {
		List<DefaultRecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d)", getViewName(), AnnualSpend.class.getSimpleName(), academicYearId);
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour l'annee academique indexer par "+academicYearId);
		return data;
	}

	@Override
	public List<DefaultRecipePart<S>> findByAcademicYear (long academicYearId, Date min, Date max) throws DAOException {
		List<DefaultRecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d ) AND (recordDate BETWEEN %d AND %d)",
				getViewName(), AnnualSpend.class.getSimpleName(), academicYearId, toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour l'année academique indexer par "+academicYearId+"pour l'intervale de temps: "+DATE_TIME_FORMAT.format(min)+" => "+DATE_TIME_FORMAT.format(max));
		return data;
	}

	@Override
	public boolean checkByAcademicYear (long academicYearId) throws DAOException {
		final String SQL  = String.format("SELECT * FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d)", getViewName(), AnnualSpend.class.getSimpleName(), academicYearId);
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public int countByAcademicYear (long academicYearId) throws DAOException {
		int count = 0;
		final String SQL  = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d)", getViewName(), AnnualSpend.class.getSimpleName(), academicYearId);
		try (Connection connection = factory.getConnection();
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
	public int countByAcademicYear(long academicYearId, Date min, Date max) throws DAOException {
		int count = 0;
		final String SQL  = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d) AND (recordDate BETWEEN %d AND %d)",
				getViewName(), AnnualSpend.class.getSimpleName(), academicYearId, toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
		try (Connection connection = factory.getConnection();
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
	public boolean checkByAcademicYearBeforDate (long yearId, Date date) throws DAOException {
		final String SQL  = String.format("SELECT * AS nombre FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d) AND recordDate <= %d LIMIT 1",
				getViewName(), AnnualSpend.class.getSimpleName(), yearId, toMinTimestampOfDay(date));
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<DefaultRecipePart<S>> findByAcademicYearBeforDate (long yearId, Date date) throws DAOException {
		final String SQL  = String.format("SELECT * AS nombre FROM %s WHERE spend IN(SELECT id FROM %s WHERE academicYear = %d) AND recordDate <= %d",
				getViewName(), AnnualSpend.class.getSimpleName(), yearId, toMinTimestampOfDay(date));
		List<DefaultRecipePart<S>> data = new ArrayList<>();
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while (result.next())
				data.add(mapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if (data.isEmpty())
			throw new DAOException("Aucune operation effectuer pour l'année academique indexer par "+yearId+" avant la date du "+DATE_FORMAT.format(date));
		return data;
	}

	@Override
	public List<RecipePart<S>> findBySpend(AnnualSpend spend) throws DAOException {
		List<RecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend = %d", getViewName(), spend.getId());
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result, spend));
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour le depense indexer par "+spend.getId());
		return data;
	}
	
	@Override
	public List<RecipePart<S>> findBySource (S source) throws DAOException {
		List<RecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE id = %d", getViewName(), source.getId());
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result, source));
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour la source indexer par "+source.getId()+" => "+source.getClass().getSimpleName());
		return data;
	}

	@Override
	public List<RecipePart<S>> findBySpend (AnnualSpend spend, int limit, int offset) throws DAOException {
		List<RecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend = %d LIMIT %d OFFSET %d", getViewName(), spend.getId(), limit, offset);
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result, spend));
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour le depense indexer par "+spend.getId());
		return data;
	}

	@Override
	public List<RecipePart<S>> findBySpendAtDate (AnnualSpend spend, Date min, Date max) throws DAOException {
		List<RecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend = %d AND (recordDate BETWEEN %d AND %d)", getViewName(), spend.getId(), toMinTimestampOfDay(min), toMaxTimestampOfDay(max));
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result, spend));
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour le depense indexer par "+spend.getId());
		return data;
	}

	@Override
	public List<RecipePart<S>> findBySpendBeforDate(AnnualSpend spend, Date date) throws DAOException {
		List<RecipePart<S>> data = new ArrayList<>();
		final String SQL  = String.format("SELECT * FROM %s WHERE spend = %d AND recordDate <= %d", getViewName(), spend.getId(), toMinTimestampOfDay(date));
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			while(result.next())
				data.add(mapping(result, spend));
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune recete pour le depense indexer par "+spend.getId());
		return data;
	}

	@Override
	public boolean checkBySpend (AnnualSpend spend) throws DAOException {
		return check("spend", spend.getId());
	}

	@Override
	public int countBySpend(AnnualSpend spend) throws DAOException {
		final String SQL  = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE spend = %d", getViewName(), spend.getId());
		int count = 0;
		try (Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL)) {
			if (result.next()) 
				count = result.getInt("nombre");
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return count;
	}

	/**
	 * cartographie des resultats dont la source est connnue
	 * @param result
	 * @param source
	 * @return
	 * @throws SQLException
	 */
	protected RecipePart<S> mapping(ResultSet result, S source) throws SQLException {
		DefaultRecipePart<S> data = new DefaultRecipePart<S>(source,
				factory.findDao(AnnualSpendDao.class).findById(result.getLong("spend")), 
				result.getString("label"), result.getString("title"), result.getDouble("part"));
		return data;
	}

	/**
	 * Cartographique des resultats dont le compte depense est connue d'avance
	 * @param result
	 * @param spend
	 * @return
	 * @throws SQLException
	 */
	protected RecipePart<S> mapping (ResultSet result, AnnualSpend spend) throws SQLException {
		DefaultRecipePart<S> data = new DefaultRecipePart<>(
				mapSource(result),
				spend,
				result.getString("label"), result.getString("title"), result.getDouble("part"));
		return data;
	}

	@Override
	protected DefaultRecipePart<S> mapping (ResultSet result) throws SQLException, DAOException {
		DefaultRecipePart<S> data = new DefaultRecipePart<>(
				mapSource(result),
				factory.findDao(AnnualSpendDao.class).findById(result.getLong("spend")),
				result.getString("label"), result.getString("title"), result.getDouble("amount"));
		return data;
	}
	
	/**
	 * cartographie de la source de la repartition d'une recette
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	protected abstract S mapSource(ResultSet result) throws SQLException;

}
