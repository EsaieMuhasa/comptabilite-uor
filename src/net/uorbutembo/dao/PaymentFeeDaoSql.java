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

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;

/**
 * @author Esaie MUHASA
 *
 */
class PaymentFeeDaoSql extends UtilSql<PaymentFee> implements PaymentFeeDao {

	private InscriptionDao inscriptionDao;
	/**
	 * @param factory
	 */
	public PaymentFeeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		inscriptionDao = factory.findDao(InscriptionDao.class);
	}

	@Override
	public void create(PaymentFee p) throws DAOException {
		try {
			long id = insertInTable(
					new String[] {
							"inscription", "amount", "receivedDate", "receiptNumber",
							"slipDate", "slipNumber", "wording", "recordDate"
					},
					new Object[] {
							p.getInscription().getId(),
							p.getAmount(),
							p.getReceivedDate().getTime(),
							p.getReceiptNumber(),
							p.getSlipDate().getTime(),
							p.getSlipNumber(),
							p.getWording(),
							p.getRecordDate().getTime()
					});
			p.setId(id);
			emitOnCreate(p);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(PaymentFee p, long id) throws DAOException {
		try {
			updateInTable(new String[] {
								"amount", "receivedDate", "receiptNumber",
								"slipDate", "slipNumber", "wording", "lastUpdate"
						},
						new Object[] {
								p.getAmount(),
								p.getReceivedDate().getTime(),
								p.getReceiptNumber(),
								p.getSlipDate().getTime(),
								p.getSlipNumber(),
								p.getWording(),
								p.getLastUpdate().getTime()
						}, id);
			p.setId(id);
			emitOnUpdate(p);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<PaymentFee> findByAcademicYear(long academicYearId, int limit, int offset) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByAcademicYear(long academicYearId) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), academicYearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN (%s)", getTableName(), sqlInscrits);
		
		System.out.println(sql);
		List<PaymentFee> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucun payement pour l'annee academique indexer pr  "+academicYearId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return data;
	}

	@Override
	public List<PaymentFee> findByAcademicYear(long academicYearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkByAcademicYear(long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN (%s) LIMIT 1 OFFSET 0", getTableName(), sqlInscrits);
		
		System.out.println(sql);
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
	public int countByAcademicYear(long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE inscription IN (%s)", getTableName(), sqlInscrits);
		
		System.out.println(sql);	
		int count = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				count  = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return count;
	}

	@Override
	public int countByAcademicYear(long academicYearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PaymentFee> findByFaculty(long faculty, long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), faculty);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		
		final String sql = String.format("SELECT * AS nombre FROM %s WHERE inscription IN (%s) ", getTableName(), sqlInscrits);
		System.out.println(sql);
		List<PaymentFee> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				data.add(mapping(result));
			}
			
			if (data.isEmpty())
				throw new DAOException("Aucun payement  dans la faculte indexer par "+faculty+", pour l'annee indexer par "+ year);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return data;
	}

	@Override
	public List<PaymentFee> findByFaculty(long faculty, long year, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByFaculty(long faculty, long year) throws DAOException {
		
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), faculty);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		
		final String sql = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE inscription IN (%s)", getTableName(), sqlInscrits);
		System.out.println(sql);	
		int count = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				count  = result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return count;
	}

	@Override
	public int countByFaculty(long faculty, long year, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByFaculty(long faculty, long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), faculty);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		
		final String sql = String.format("SELECT * AS nombre FROM %s WHERE inscription IN (%s) LIMIT 1 OFFSET 0", getTableName(), sqlInscrits);
		System.out.println(sql);
		
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
	public List<PaymentFee> findByDepartment(long departmentId, long academicYearId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByDepartment(long departmentId, long academicYearId, Date min, Date max)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByDepartment(long departmentId, long academicYearId, int limit, int offset)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByDepartment(long departmentId, long academicYearId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countByDepartment(long departmentId, long academicYearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByDepartment(long departmentId, long academicYearId) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PaymentFee> findByStudyClass(long studyClassId, long yearId, int limit, int offset)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByStudyClass(long studyClassId, long yearId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByStudyClass(long studyClassId, long yearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByStudyClass(long studyClassId, long yearId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByStudyClass(long studyClass, long yearId) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PaymentFee> findByPromotion(long promotionId, int limit, int offset) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByPromotion(long promotionId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PaymentFee> findByPromotion(long promotion, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByPromotion(long promotion) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countByPromotion(long promotion, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByPromotion(long promotion) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PaymentFee> findByInscription(Inscription inscription) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE inscription = %d ORDER BY recordDate DESC", this.getTableName(), inscription.getId());
		List<PaymentFee> data = new ArrayList<>();
		System.out.println(sql);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(this.mapping(result, inscription));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription pour l'etudient "+inscription.getStudent().getMatricul()+" -> "+inscription.getStudent().getFullName());
		
		return data;
	}

	@Override
	protected PaymentFee mapping(ResultSet result) throws SQLException, DAOException {
		return mapping(result, this.inscriptionDao.findById(result.getLong("inscription")));
	}
	
	/**
	 * 
	 * @param result
	 * @param inscription
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected PaymentFee mapping(ResultSet result, Inscription inscription) throws SQLException, DAOException {
		PaymentFee fee = new PaymentFee(result.getLong("id"));
		fee.setRecordDate(new Date(result.getLong("recordDate")));
		fee.setInscription(inscription);
		fee.setAmount(result.getFloat("amount"));
		fee.setReceivedDate(new Date(result.getLong("receivedDate")));
		fee.setReceiptNumber(result.getString("receiptNumber"));
		fee.setSlipDate(new Date(result.getLong("slipDate")));
		fee.setSlipNumber(result.getString("slipNumber"));
		fee.setWording(result.getString("wording"));
		return fee;
	}

	@Override
	protected String getTableName() {
		return PaymentFee.class.getSimpleName();
	}

}
