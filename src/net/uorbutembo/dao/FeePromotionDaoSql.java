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
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.beans.Promotion;

/**
 * @author Esaie MUHASA
 *
 */
class FeePromotionDaoSql extends UtilSql<FeePromotion> implements FeePromotionDao {

	private PromotionDao promotionDao;
	private AcademicFeeDao academicFeeDao;
	
	public FeePromotionDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		this.promotionDao = factory.findDao(PromotionDao.class);
		this.academicFeeDao = factory.findDao(AcademicFeeDao.class);
	}

	@Override
	public void create(FeePromotion f) throws DAOException {
		try {
			long id = this.insertInTable(
					new String [] {
							"academicFee", "promotion", "recordDate"
					},
					new Object[] {
							f.getAcademicFee().getId(),
							f.getPromotion().getId(),
							f.getRecordDate().getTime()
					});
			f.setId(id);
			this.emitOnCreate(f);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}
	
	@Override
	public void create(FeePromotion[] t) throws DAOException {
		try (Connection connection = this.factory.getConnection()) {
			connection.setAutoCommit(false);
			for (FeePromotion f : t) {
				
				long id = this.insertInTransactionnelTable(
						connection,
						new String [] {
								"academicFee", "promotion", "recordDate"
						},
						new Object[] {
								f.getAcademicFee().getId(),
								f.getPromotion().getId(),
								f.getRecordDate().getTime()
						});
				f.setId(id);
			}
			connection.commit();
			for (FeePromotion fee : t) {
				this.emitOnCreate(fee);				
			}
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public void update(FeePromotion e, long id) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<FeePromotion> findByAcademicFee(long academicFeeId) throws DAOException {
		AcademicFee academicFee = this.academicFeeDao.findById(academicFeeId);
		List<FeePromotion> data = new ArrayList<>();
		final String sql = String.format("SELECT * FROM %s WHERE academicFee = %d", this.getTableName(), academicFeeId);
		try(
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql);
			) {
			while(result.next()) {				
				FeePromotion  fee = this.mapping(result);
				fee.setAcademicFee(academicFee);
				data.add(fee);
			}			
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la recuperation des donneees dans la BDD. ->" + e.getMessage(), e);
		}
		
		if(data.isEmpty()) 
			throw new DAOException("Aucune promotion n'est configurer pour payer "+academicFee.getAmount()+" USD");
		
		return data;
	}
	
	@Override
	public List<FeePromotion> findByAcademicFee(AcademicFee academicFee) throws DAOException {
		List<FeePromotion> data = new ArrayList<>();
		final String sql = String.format("SELECT * FROM %s WHERE academicFee = %d", this.getTableName(), academicFee.getId());
		try(
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql);
			) {
			while(result.next()) {				
				FeePromotion  fee = this.mapping(result, null, academicFee);
				data.add(fee);
			}			
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la recuperation des donneees dans la BDD. ->" + e.getMessage(), e);
		}
		
		if(data.isEmpty()) 
			throw new DAOException("Aucune promotion n'est configurer pour payer "+academicFee.getAmount()+" USD");
		
		return data;
	}

	@Override
	protected FeePromotion mapping(ResultSet result) throws SQLException, DAOException {
		FeePromotion f = new FeePromotion(result.getLong("id"));
		f.setAcademicFee(this.academicFeeDao.findById(result.getLong("academicFee")));
		f.setPromotion(new Promotion(result.getLong("promotion")));
		f.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			f.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return f;
	}
	
	protected FeePromotion mapping(ResultSet result, Promotion promotion, AcademicFee fee) throws SQLException, DAOException {
		FeePromotion f = new FeePromotion(result.getLong("id"));
		f.setRecordDate(new Date(result.getLong("recordDate")));
		
		f.setAcademicFee(fee == null ? this.academicFeeDao.findById(result.getLong("academicFee") ) : fee);
		f.setPromotion(promotion == null ? this.promotionDao.findById(result.getLong("promotion")) : promotion);

		if(result.getLong("lastUpdate") != 0) {
			f.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return f;
	}
	

	@Override
	protected String getTableName() {
		return FeePromotion.class.getSimpleName();
	}

}
