/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Promotion;

/**
 * @author Esaie MUHASA
 *
 */
class AcademicYearDaoSql extends UtilSql<AcademicYear> implements AcademicYearDao {
	
	private AcademicYear currentYear;
	private final List<AcademicYearDaoListener> yearListeners = new ArrayList<>();

	public AcademicYearDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public synchronized void create(AcademicYear e) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			connection.setAutoCommit(false);
			create(connection, e);
			connection.commit();
			currentYear = e;
			emitOnCreate(e);
		} catch (SQLException ex) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement. \n"+ex.getMessage(), ex);
		}
	}
	
	@Override
	public void create (Connection connection, AcademicYear e) throws DAOException, SQLException {
		AcademicYear previous = null;
		if (checkCurrent()){
			previous = findCurrent();
			e.setPrevious(previous);
			if(previous.getCloseDate() == null) {
				updateInTable(
						connection,
						new String[] {"closeDate", "lastUpdate"},
						new Object[] {e.getRecordDate().getTime()-1000, e.getRecordDate().getTime()},
						previous.getId());
			}
		}
		
		long id = insertInTable(
				connection,
				new String [] { "label", "startDate", "closeDate", "recordDate", "previous" }, 
				new Object[] {
						e.getLabel(), 
						e.getStartDate().getTime(),
						e.getCloseDate()!= null? e.getCloseDate().getTime() : null,
						e.getRecordDate().getTime(),
						e.getPrevious()!= null? e.getPrevious().getId() : null
				}
			);
		e.setId(id);
	}
	
	@Override
	public void create (AcademicYear year, AcademicYear importable, int requestId) {
		Thread t = new Thread(() ->{
			try {
				importConfiguration(year, importable, requestId);
			} catch (DAOException e) {
				fireEventProgressError(e, requestId);
			}
		});
		t.start();
	}
	
	/**
	 * imprtation des configuration d'une annee academique
	 * @param year
	 * @param importable
	 * @param requestId
	 * @throws DAOException
	 */
	private synchronized void importConfiguration (AcademicYear year, AcademicYear importable, int requestId) throws DAOException {
		fireEventProgressStart(requestId);
		try (Connection connection = factory.getConnection()) {
			int max = 2, current = 0;
			fireEventProgressProgress(current++, max, "Création de l'excercice académique", requestId);
			connection.setAutoCommit(false);
			create(connection, year);
			fireEventProgressProgress(requestId, current++, max);
			
			final PromotionDaoSql promotionDaoSql = (PromotionDaoSql) factory.findDao(PromotionDao.class);
			final AcademicFeeDaoSql academicFeeDaoSql = (AcademicFeeDaoSql) factory.findDao(AcademicFeeDao.class);
			final AnnualRecipeDaoSql annualRecipeDaoSql = (AnnualRecipeDaoSql) factory.findDao(AnnualRecipeDao.class);
			final AnnualSpendDaoSql annualSpendDaoSql = (AnnualSpendDaoSql) factory.findDao(AnnualSpendDao.class);
			final AllocationCostDaoSql allocationCostDaoSql = (AllocationCostDaoSql) factory.findDao(AllocationCostDao.class);
			final AllocationRecipeDaoSql allocationRecipeDaoSql = (AllocationRecipeDaoSql) factory.findDao(AllocationRecipeDao.class);
			
			Date now = new Date();
			
			//importation des rubriques du budget (depenses annuel)
			fireEventProgressProgress(current++, max, "Importation des rubriques budgetaires", requestId);
			List<AnnualSpend> spends = annualSpendDaoSql.findByAcademicYear(importable);
			AnnualSpend [] spendsCopy = new AnnualSpend[spends.size()];
			max += spendsCopy.length;
			for (int i = 0; i < spendsCopy.length; i++) {
				AnnualSpend spendCopy = new AnnualSpend();
				spendCopy.setAcademicYear(year);
				spendCopy.setUniversitySpend(spends.get(i).getUniversitySpend());
				spendCopy.setRecordDate(now);
				spendsCopy[i] = spendCopy;
				fireEventProgressProgress(current++, max, "("+i+"/"+spendsCopy.length+") Importation des rubriques budgetaires", requestId);
			}
			annualSpendDaoSql.create(connection, spendsCopy);
			//==> end annual spend
			
			//import promotions and academic fees
			List<AcademicFee> fees = academicFeeDaoSql.findByAcademicYear(importable);
			int fCount = fees.size(), fCurrent = 0;
			max += fCount;
			for (AcademicFee fee : fees) {
				fireEventProgressProgress(current++, max, "("+fCurrent+"/"+fCount+") Configuration des frais académiques et promotions", requestId);
				fCurrent++;
				
				if(!promotionDaoSql.checkByAcademicFee(fee))
					continue;
				
				List<Promotion> promotions = promotionDaoSql.findByAcademicFee(fee);
				List<AllocationCost> costs = allocationCostDaoSql.findByAcademicFee(fee);
				
				
				AcademicFee feeCopy = new AcademicFee();
				Promotion[] promotionsCopy = new Promotion[promotions.size()];
				AllocationCost [] costsCopy = new AllocationCost [costs.size()];
				
				fCount += (promotionsCopy.length + costsCopy.length);
				max += (promotionsCopy.length + costsCopy.length);
				
				feeCopy.setAcademicYear(year);
				feeCopy.setRecordDate(now);
				feeCopy.setAmount(fee.getAmount());
				feeCopy.setDescription(fee.getDescription());
				
				for (int i = 0; i < promotionsCopy.length; i++) {//promotions
					Promotion proCopy = new Promotion();
					proCopy.setAcademicFee(feeCopy);
					proCopy.setAcademicYear(year);
					proCopy.setDepartment(promotions.get(i).getDepartment());
					proCopy.setStudyClass(promotions.get(i).getStudyClass());
					proCopy.setRecordDate(now);
					promotionsCopy[i] = proCopy;
					fireEventProgressProgress(current++, max, "("+fCurrent+"/"+fCount+") Configuration des frais académiques et promotions", requestId);
					fCurrent++;
				}
				
				for (int i = 0; i < costsCopy.length; i++) {
					AllocationCost costCopy = new AllocationCost();
					costCopy.setAmount(costs.get(i).getAmount());
					costCopy.setAcademicFee(feeCopy);
					costCopy.setAnnualSpend(findInConfiguration(costs.get(i), spends, spendsCopy));
					costCopy.setRecordDate(now);
					costsCopy[i] = costCopy;
					fireEventProgressProgress(current++, max, "("+fCurrent+"/"+fCount+") Configuration des frais académiques et promotions", requestId);
					fCurrent++;
				}
				
				academicFeeDaoSql.create(connection, feeCopy);
				promotionDaoSql.create(connection, promotionsCopy);
				allocationCostDaoSql.create(connection, costsCopy);
			}
			//==> end promotion, academic fees and allocation cost
			
			//import other recipes
			List<AnnualRecipe> recipes = annualRecipeDaoSql.findByAcademicYear(importable);
			AnnualRecipe [] recipesCopy = new AnnualRecipe[recipes.size()];
			List<AllocationRecipe> allocations = new ArrayList<>();
			fCount = (recipes.size() * spendsCopy.length) + recipes.size();
			max += fCount;
			fCurrent = 0;
			for (int i = 0; i < recipesCopy.length; i++) {
				AnnualRecipe recipeCopy = new AnnualRecipe();
				recipeCopy.setRecordDate(now);
				recipeCopy.setForecasting(recipes.get(i).getForecasting());
				recipeCopy.setAcademicYear(year);
				recipeCopy.setUniversityRecipe(recipes.get(i).getUniversityRecipe());
				recipesCopy[i] = recipeCopy;
				fireEventProgressProgress(current++, max, "("+fCurrent+"/"+fCount+") Configuration des autres recettes", requestId);
				fCurrent++;
				
				for (int j = 0; j < spendsCopy.length; j++) {
					fireEventProgressProgress(current++, max, "("+fCurrent+"/"+fCount+") Configuration des autres recettes", requestId);
					fCurrent++;
					if(!allocationRecipeDaoSql.check(recipes.get(i), spends.get(j)))
						continue;
					
					AllocationRecipe allocationRecipe = allocationRecipeDaoSql.find(recipes.get(i), spends.get(j));
					AllocationRecipe allocationRecipeCopy = new AllocationRecipe();
					allocationRecipeCopy.setPercent(allocationRecipe.getPercent());
					allocationRecipeCopy.setRecipe(recipeCopy);
					allocationRecipeCopy.setSpend(spendsCopy[j]);
					allocationRecipeCopy.setRecordDate(now);
					allocations.add(allocationRecipeCopy);
				}
				
			}
			
			fireEventProgressProgress(max, max, "Sauvegarde des configurations...", requestId);
			
			AllocationRecipe [] allocationsCopy = new AllocationRecipe[allocations.size()];
			for (int i = 0; i < allocationsCopy.length; i++) 
				allocationsCopy[i] = allocations.get(i);
			
			annualRecipeDaoSql.create(connection, recipesCopy);
			allocationRecipeDaoSql.create(connection, allocationsCopy);
			//==> end 
			
			connection.commit();
			currentYear = year;
			fireEventProgressFinish(year, requestId);
			fireEventCreate(year, requestId);
		} catch (SQLException e) {
			throw new DAOException(getTableName());
		}
	}
	
	/**
	 * recherche de la correspondance entre l'encienne configuration et la nouvelle configuration.
	 * N.B: 
	 * <ul>
	 * <li>l'ordre de elements dans la collection et le tableau est d'une importance capital</li>
	 * <li>La taille de la collection doit etre == a la taille du tableau</li>
	 * </ul>
	 * @param cost
	 * @param olds collection de l'encienne configuration
	 * @param news tableau de la nouvelle confiuration
	 * @return
	 */
	private AnnualSpend findInConfiguration (AllocationCost cost, List<AnnualSpend> olds,  AnnualSpend [] news) {
		for (int i = 0; i < olds.size(); i++) {
			if(olds.get(i).getId() == cost.getAnnualSpend().getId())
				return news[i];
		}
		throw new DAOException("Impossible de determiere l'association");
	}

	@Override
	public synchronized void update(AcademicYear a, long id) throws DAOException {
		try {
			this.updateInTable(
					new String[] {"label", "startDate", "closeDate", "recordDate"},
					new Object[] {
							a.getLabel(), 
							a.getStartDate().getTime(),
							a.getCloseDate()!= null? a.getCloseDate().getTime() : null,
							a.getLastUpdate().getTime()
					}, id);
			this.emitOnUpdate(a);
		} catch (SQLException e) {
			throw new DAOException("Une erreure est survenue lors de la sauvegarde des modifications: \n"+e.getMessage(), e);
		}
	}
	
	@Override
	public synchronized void delete(long id) throws DAOException {
		final String SQL_QUERY = String.format("DELETE FROM %s WHERE id = ?", getTableName());
		
		AcademicYear t = this.findById(id);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, id);
			) {
			int status = statement.executeUpdate();
			
			if(status == 0) {
				throw new DAOException("Aucune occurence suprimer");
			}
			
			if(currentYear !=null && t.getId() == currentYear.getId()) {
				currentYear = null;
			}
			
			emitOnDelete(t);
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkCurrent() throws DAOException {
		if(currentYear != null || countAll() == 1 )
			return true;
		final String SQL = String.format("SELECT id FROM %s WHERE previous IS NOT NULL AND id NOT IN(SELECT previous FROM %s WHERE previous IS NOT NULL)", getTableName(), getTableName());
		try(
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			
			return result.next();
			
		}catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public AcademicYear findById(long id) throws DAOException {
		if(currentYear != null && currentYear.getId() == id)
			return currentYear;
		return super.findById(id);
	}

	@Override
	public AcademicYear findCurrent() throws DAOException {
		if(this.currentYear == null) {
			if(countAll() == 1) {
				currentYear = findAll().get(0);
			} else {
				final String SQL = String.format("SELECT * FROM %s WHERE previous IS NOT NULL AND id NOT IN(SELECT previous FROM %s WHERE previous IS NOT NULL)", getTableName(), getTableName());
				try(
						Connection connection = factory.getConnection();
						Statement statement = connection.createStatement();
						ResultSet result = statement.executeQuery(SQL);
						) {
					
					if (result.next())
						currentYear = mapping(result);
					else 
						throw new DAOException("Impossible de determiner l'annee courante");
				}catch (SQLException e) {
					throw new DAOException(e.getMessage(), e);
				}
			}
		}
		return this.currentYear;
	}
	
	@Override
	protected synchronized void emitOnCreate(AcademicYear e, int requestId) {
		Thread t = new Thread(() -> {			
			for (DAOListener<AcademicYear> ls : listeners) {
				ls.onCreate(e, requestId);
			}
			
			reload();
		});
		t.start();
	}
	
	@Override
	protected synchronized void emitOnDelete(AcademicYear e, int requestId) {
		Thread t = new Thread(() -> {			
			for (DAOListener<AcademicYear> ls : listeners) {
				ls.onDelete(e, requestId);
			}
			
			reload();
		});
		t.start();
	}

	@Override
	protected AcademicYear mapping(ResultSet result) throws SQLException, DAOException {
		AcademicYear data = new AcademicYear(result.getLong("id"));
		data.setStartDate(new Date(result.getLong("startDate")));
		data.setRecordDate(new Date(result.getLong("recordDate")));
		data.setLabel(result.getString("label"));
		if(result.getLong("closeDate") != 0) {
			data.setCloseDate(new Date(result.getLong("closeDate")));
		}
		
		if(result.getLong("lastUpdate") != 0) {
			data.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		
		if(result.getLong("previous") > 0) {
			data.setPrevious(new AcademicYear(result.getLong("previous")));
		}
		
		return data;
	}
	
	private volatile boolean reloadRunning = false;
	
	@Override
	public boolean isReload() {
		return reloadRunning;
	}
	
	@Override
	public synchronized void reload (int requestId) {
		reloadRunning = true;
		try {
			if(currentYear == null && checkCurrent())
				findCurrent();
			
			for (AcademicYearDaoListener ls : yearListeners) {
				ls.onCurrentYear(currentYear);
			}				
		} catch (DAOException e) {
			emitOnError(e);
		} catch (Exception e) {
			e.printStackTrace();
			emitOnError(new DAOException(e.getMessage(), e));
		}
		
		reloadRunning = false;
	}

	@Override
	protected String getTableName() {
		return AcademicYear.class.getSimpleName();
	}

	@Override
	public void addYearListener(AcademicYearDaoListener listener) {
		if(!yearListeners.contains(listener))
			yearListeners.add(listener);
	}

	@Override
	public void removeYearListener(AcademicYearDaoListener listener) {
		yearListeners.remove(listener);
	}

}
