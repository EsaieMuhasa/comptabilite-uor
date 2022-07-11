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

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;

/**
 * @author Esaie MUHASA
 *
 */
class AllocationCostDaoSql extends UtilSql<AllocationCost> implements AllocationCostDao {

	private AnnualSpendDao annualSpendDao;
	private AcademicFeeDao academicFeeDao;
	
	/**
	 * @param factory
	 */
	public AllocationCostDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		this.academicFeeDao = factory.findDao(AcademicFeeDao.class);
		this.annualSpendDao = factory.findDao(AnnualSpendDao.class);
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}

	@Override
	public synchronized void create(AllocationCost a) throws DAOException {
		try {
			long id = insertInTable(
					new String[] {"academicFee", "amount", "annualSpend", "recordDate"},
					new Object[] {
							a.getAcademicFee().getId(),
							a.getAmount(),
							a.getAnnualSpend().getId(),
							a.getRecordDate().getTime() });
			a.setId(id);
			emitOnCreate(a);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public synchronized void create(AllocationCost[] t) throws DAOException {
		try (final Connection connection = factory.getConnection()) {
			connection.setAutoCommit(false);
			connection.commit();
			emitOnCreate(t);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public void create (Connection connection, AllocationCost[] t) throws DAOException, SQLException {
		for (AllocationCost a : t) {
			long id = insertInTable(
					connection,
					new String[] {"academicFee", "amount", "annualSpend","recordDate" },
					new Object[] {
							a.getAcademicFee().getId(),
							a.getAmount(),
							a.getAnnualSpend().getId(),
							a.getRecordDate().getTime() });
			a.setId(id);
		}		
	}
	
	@Override
	public List<AllocationCost> findByAnnualSpend(long annualSpendId) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE annualSpend = %d", getViewName(), annualSpendId);
		
		List<AllocationCost> data = new ArrayList<>();
		try(
				final Connection connection = factory.getConnection();
				final Statement statement = connection.createStatement();
				final ResultSet result = statement.executeQuery(sql)) {
			
			AnnualSpend annualSpend = annualSpendDao.findById(annualSpendId);
			while (result.next()) {
				AllocationCost c = this.mapping(result, true, false);
				c.setAnnualSpend(annualSpend);
				data.add(c);
			}
			
			if(data.isEmpty())
				throw new DAOException("Aucunne reference pour la rubrique \""+annualSpend.getUniversitySpend().getTitle()+"\"");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}

		return data;
	}

	@Override
	public synchronized void update(AllocationCost a, long id) throws DAOException {
		try {
			updateInTable(
					new String[] {"amount","lastUpdate" },
					new Object[] {a.getAmount(), a.getLastUpdate().getTime()}, id);
			emitOnUpdate(a);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public synchronized void update(AllocationCost[] es, long[] id) throws DAOException {
		try (Connection connection = factory.getConnection()){
			connection.setAutoCommit(false);
			for (int i = 0; i < id.length; i++) {
				AllocationCost a = es[i];		
				updateInTable(
						connection,
						new String[] {"amount","lastUpdate" },
						new Object[] {a.getAmount(), a.getLastUpdate().getTime()}, id[i]);
				a.setId(id[i]);
			}
			connection.commit();
			emitOnUpdate(es);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public AllocationCost find(AnnualSpend annualSpend, AcademicFee academicFee) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE annualSpend = %d AND academicFee = %d", getViewName(), annualSpend.getId(), academicFee.getId());

		try(
				final Connection connection = factory.getConnection();
				final Statement statement = connection.createStatement();
				final ResultSet result = statement.executeQuery(sql)) {
			
			if(result.next()) {
				AllocationCost c = this.mapping(result, false, false);
				c.setAcademicFee(academicFee);
				c.setAnnualSpend(annualSpend);
				return c;
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		throw new DAOException("Aucunne reference unique pour "+annualSpend+" et "+academicFee);
	}
	
	@Override
	public AllocationCost find(long annualSpendId, long academicFeeId) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE annualSpend = %d AND academicFee = %d", getViewName(), annualSpendId, academicFeeId);
		try(
				final Connection connection = factory.getConnection();
				final Statement statement = connection.createStatement();
				final ResultSet result = statement.executeQuery(sql)) {
			
			if(result.next()) {
				return this.mapping(result, true, true);
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		throw new DAOException("Aucunne reference unique pour "+annualSpendId+" et "+academicFeeId);
	}
	
	@Override
	public boolean check(long annualSpendId, long academicFeeId) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE annualSpend = %d AND academicFee = %d", getViewName(), annualSpendId, academicFeeId);
		try(
				final Connection connection = factory.getConnection();
				final Statement statement = connection.createStatement();
				final ResultSet result = statement.executeQuery(sql)) {
			
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<AllocationCost> findByAcademicFee(long academicFeeId) throws DAOException {
		return this.findAll(new String[] {"academicFee"}, new Object[] {academicFeeId});
	}

	@Override
	public List<AllocationCost> findByAcademicFee(AcademicFee academicFee) throws DAOException {
		List<AllocationCost>  costs = new ArrayList<>();
		final String sql = String.format("SELECT * FROM %s WHERE academicFee = %d", getViewName(), academicFee.getId());
		try(
				final Connection connection = factory.getConnection();
				final Statement statement = connection.createStatement();
				final ResultSet result = statement.executeQuery(sql)) {
			
			while(result.next()) {
				AllocationCost cost = this.mapping(result, false, true);
				cost.setAcademicFee(academicFee);
				costs.add(cost);
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(costs.isEmpty()) {
			throw new DAOException("Aucune configuration, pour la repartiton de fais academique -> "+academicFee.getAmount()+" USD");
		}
		return costs;
	}

	@Override
	public int countByAcademicFee(long academicFeeId) throws DAOException {
		return count("academicFee", academicFeeId);
	}

	@Override
	protected AllocationCost mapping(ResultSet result) throws SQLException, DAOException {
		AllocationCost c = new AllocationCost(result.getLong("id"));
		c.setRecordDate(new Date(result.getLong("recordDate")));
		c.setAmount(result.getFloat("amount"));
		c.setCollecetd(result.getDouble("collected"));
		c.setPercent(result.getFloat("percent"));
		c.setTotalExpected(result.getDouble("totalExpected"));
		if (result.getLong("lastUpdate") != 0)
			c.setLastUpdate(new Date(result.getLong("lastUpdate")));
		c.setAcademicFee(this.academicFeeDao.findById(result.getLong("academicFee")));
		c.setAnnualSpend(this.annualSpendDao.findById(result.getLong("annualSpend")));
		return c;
	}
	
	/**
	 * surcharge du mapping
	 * @param result
	 * @param fee
	 * @param spend
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected AllocationCost mapping(ResultSet result, boolean fee, boolean spend) throws SQLException, DAOException {
		AllocationCost c = new AllocationCost(result.getLong("id"));
		c.setRecordDate(new Date(result.getLong("recordDate")));
		c.setAmount(result.getFloat("amount"));
		c.setCollecetd(result.getDouble("collected"));
		c.setPercent(result.getFloat("percent"));
		c.setTotalExpected(result.getDouble("totalExpected"));
		if(result.getLong("lastUpdate") != 0)
			c.setLastUpdate(new Date(result.getLong("lastUpdate")));
		if(fee)
			c.setAcademicFee(this.academicFeeDao.findById(result.getLong("academicFee")));
		if(spend)
			c.setAnnualSpend(this.annualSpendDao.findById(result.getLong("annualSpend")));
		return c;
	}

	@Override
	protected String getTableName() {
		return AllocationCost.class.getSimpleName();
	}

}
