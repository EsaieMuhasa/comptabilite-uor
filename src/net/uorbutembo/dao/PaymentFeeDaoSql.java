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

	/**
	 * @param factory
	 */
	public PaymentFeeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
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
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), academicYearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN(%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN(%s) LIMIT %d OFFSET %d", getTableName(), sqlInscrits, limit, offset);
		
		List<PaymentFee> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucun payement pour l'annee academique indexer par  "+academicYearId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return data;
	}

	@Override
	public List<PaymentFee> findByAcademicYear(long academicYearId) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), academicYearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN(%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN(%s)", getTableName(), sqlInscrits);
		
		List<PaymentFee> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(mapping(result));
			
			if(data.isEmpty())
				throw new DAOException("Aucun payement pour l'annee academique indexer par  "+academicYearId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return data;
	}

	@Override
	public List<PaymentFee> findByAcademicYear(long yearId, Date min, Date max) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN(%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN(%s) AND (slipDate BETWEEN %d AND %d)", 
				getTableName(), sqlInscrits, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		List<PaymentFee> data = new ArrayList<>();
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
			throw new DAOException("Aucune operation de payement des fais academique en date du "+min.toString()+" au "+max.toString());
		
		return data;
	}

	@Override
	public boolean checkByAcademicYear(long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN(%s) LIMIT 1 OFFSET 0", getTableName(), sqlInscrits);
		
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
	public int countByAcademicYear(long yearId, Date min, Date max) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN(%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE inscription IN(%s) AND (slipDate BETWEEN %d AND %d)", 
				getTableName(), sqlInscrits, toMinTimestampOfDay(min).getTime(), toMaxTimestampOfDay(max).getTime());
		
		int count  = 0;
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
	public List<PaymentFee> findByFaculty(long faculty, long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), faculty);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		
		final String sql = String.format("SELECT * AS nombre FROM %s WHERE inscription IN (%s) ", getTableName(), sqlInscrits);
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
	public List<PaymentFee> findByPromotions(long studyClass, long[] department, long year, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByPromotions(long studyClass, long[] departments, long year) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByPromotions(long studyClass, long[] departments, long year) {
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
	public List<PaymentFee> findByPromotions(long[] prmotions, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByPromotions(long... promotions) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByPromotions(long... promotions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PaymentFee> findByInscription(Inscription inscription) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE inscription = %d ORDER BY recordDate DESC", this.getTableName(), inscription.getId());
		List<PaymentFee> data = new ArrayList<>();
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
	public boolean checkByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN(%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN(%s) AND slipDate < %d LIMIT 1 OFFSET 0", getTableName(), sqlInscrits, toMinTimestampOfDay(date).getTime());
		
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
	public List<PaymentFee> findByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d ", Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), yearId);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE promotion IN(%s)", Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT * FROM %s WHERE inscription IN(%s) AND slipDate < %d LIMIT 1 OFFSET 0", getTableName(), sqlInscrits, toMinTimestampOfDay(date).getTime());
		
		List<PaymentFee> data = new ArrayList<>();
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
			throw new DAOException("Aucune operation de payement des fais academique en date du "+date.toString());
		
		return data;
	}

	@Override
	public List<PaymentFee> findByPromotions(long[] studyClasses, long department, long year, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByPromotions(long[] studyClasses, long department, long year) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkByPromotions(long[] studyClasses, long department, long year) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected PaymentFee mapping(ResultSet result) throws SQLException, DAOException {
		PaymentFee fee = new PaymentFee(result.getLong("id"));
		fee.setRecordDate(new Date(result.getLong("recordDate")));
		fee.setInscription(new Inscription(result.getLong("inscription")));
		fee.setAmount(result.getFloat("amount"));
		fee.setReceivedDate(new Date(result.getLong("receivedDate")));
		fee.setReceiptNumber(result.getString("receiptNumber"));
		fee.setSlipDate(new Date(result.getLong("slipDate")));
		fee.setSlipNumber(result.getString("slipNumber"));
		fee.setWording(result.getString("wording"));
		return fee;
	}
	
	/**
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

	@Override
	public double getSoldByStudent(long student) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSoldByInscription(long inscription) throws DAOException {
		final String sql = String.format("SELECT SUM(amount) AS sold FROM %s WHERE inscription = %d", getTableName(), inscription);
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByPromotion (long promotion) throws DAOException {
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion = %d", Inscription.class.getSimpleName(), 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), promotion);
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE inscription IN(%s)", getTableName(), getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}
	
	@Override
	public double getSoldByPromotions(long... promotions) throws DAOException {
		String [] proms = new String[promotions.length];
		for (int i = 0; i < proms.length; i++) 
			proms[i] = promotions[i]+"";
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN(%s)", Inscription.class.getSimpleName(), 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), String.join(", ", proms));
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE %s.inscription IN(%s)", getTableName(), getTableName(), getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByPromotions(long studyClass, long[] departments, long year) throws DAOException {
		String [] deps = new String[departments.length];
		for (int i = 0; i < deps.length; i++) 
			deps[i] = departments[i]+"";
		
		String ids = String.join(", ", deps);
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.studyClass = %d AND %s.department IN(%s)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(), studyClass,
				Promotion.class.getSimpleName(), ids);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN(%s)", 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE inscription IN(%s)", getTableName(), getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByPromotions (long[] studyClasses, long department, long year) throws DAOException {
		String [] classes = new String[studyClasses.length];
		for (int i = 0; i < classes.length; i++) 
			classes[i] = studyClasses[i]+"";
		
		String ids = String.join(", ", classes);
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department = %d AND %s.studyClass IN(%s)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(), department,
				Promotion.class.getSimpleName(), ids);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN(%s)", 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE inscription IN(%s)", getTableName(), getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByFaculty(long faculty, long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN(SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), faculty);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN (%s)", 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE inscription IN (%s)", getTableName(), getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByStudyClass(long studyClass, long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.studyClass = %d",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(), studyClass);
		
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN(%s)", 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE inscription IN(%s)", getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByDepartment(long department, long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department = %d",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(), department);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN(%s)", 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT SUM(amount) AS sold FROM %s WHERE inscription IN(%s)", getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}

	@Override
	public double getSoldByAcademicYear(long year) throws DAOException {
		final String sqlPromotion = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year);
		final String sqlInscrits = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN(%s)", 
				Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), Inscription.class.getSimpleName(), sqlPromotion);
		final String sql = String.format("SELECT SUM(%s.amount) AS sold FROM %s WHERE inscription IN(%s)", getTableName(), sqlInscrits);
		
		double sold = 0;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next()) 
				sold  = result.getDouble("sold");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return sold;
	}
	

}
