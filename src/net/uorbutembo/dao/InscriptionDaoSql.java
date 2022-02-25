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
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;

/**
 * @author Esaie MUHASA
 *
 */
class InscriptionDaoSql extends UtilSql<Inscription> implements InscriptionDao {

	private PromotionDao promotionDao;
	private StudentDaoSql studentDao;
	
	/**
	 * @param factory
	 */
	public InscriptionDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		this.promotionDao = factory.findDao(PromotionDao.class);
		this.studentDao = (StudentDaoSql) factory.findDao(StudentDao.class);
	}

	@Override
	public void create(Inscription i) throws DAOException {
		try (Connection connection = this.factory.getConnection()) {
			connection.setAutoCommit(false);
			if(i.getStudent().getId() == 0) {
				this.studentDao.create(connection, i.getStudent());
			}
			long id = this.insertInTransactionnelTable(
					connection,
					new String[] {
							"promotion", "student", "recordDate"
					},
					new Object[] {
							i.getPromotion().getId(),
							i.getStudent().getId(),
							i.getRecordDate().getTime()
					});
			connection.commit();
			i.setId(id);
			this.emitOnCreate(i);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(Inscription i, long id) throws DAOException {
		try {
			this.updateInTable(
					new String[] {"promotion", "student", "lastUpdate"},
					new Object[] {
							i.getPromotion().getId(),
							i.getStudent().getId(),
							i.getLastUpdate().getTime()
					}, id);
			this.emitOnUpdate(i);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<Inscription> findByStudent(long studentId) throws DAOException {
		return this.findByStudent(this.studentDao.findById(studentId));
	}

	@Override
	public List<Inscription> findByStudent(Student student) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE student = %d", this.getTableName(), student.getId());
		List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				data.add(this.fullMapping(result, student, null));
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription pour l'etudient "+student.getMatricul()+" -> "+student.getFullName());
		
		return data;
	}

	@Override
	public List<Inscription> countByPromotion(long promotionId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> findByPromotion(long promotionId) throws DAOException {
		return this.findByPromotion(this.promotionDao.findById(promotionId));
	}

	@Override
	public List<Inscription> findByPromotion(Promotion promotion) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE promotion = %d", this.getTableName(), promotion.getId());
		List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				data.add(this.fullMapping(result, null, promotion));
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription pour dans la promotion "+promotion.toString());
		
		return data;
	}

	@Override
	public List<Inscription> findByPromotion(long promotionId, int limit, int offset) throws DAOException {
		return this.findByPromotion(this.promotionDao.findById(promotionId), limit, offset);
	}

	@Override
	public List<Inscription> findByPromotion(Promotion promotion, int limit, int offset) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE promotion = %d LIMIT %d OFFSET %d", 
												this.getTableName(), promotion.getId(), limit, offset);
		List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				data.add(this.fullMapping(result, null, promotion));
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription pour dans la promotion "+promotion.toString());
		
		return data;
	}

	@Override
	public boolean checkByFaculty(long facultyId, long academicYearId) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Inscription> findByFaculty(long facultyId, long academicYearId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> findByFaculty(Faculty faculty, AcademicYear academicYear) throws DAOException {
		final String sql = String.format("SELECT * ", getTableName(), faculty.getId(), academicYear);
		return null;
	}

	@Override
	public int countByFaculty(long facultyId, int academicYearId) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Inscription mapping(ResultSet result) throws SQLException, DAOException {
		Inscription in = new Inscription(result.getLong("id"));
		in.setStudent(new Student(result.getLong("student")));
		in.setPromotion(new Promotion(result.getLong("promotion")));
		in.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			in.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return in;
	}
	
	protected Inscription mapping(ResultSet result, Student student, Promotion promotion) throws SQLException, DAOException {
		Inscription in = new Inscription(result.getLong("id"));
		in.setStudent(student == null ? new Student(result.getLong("student")) : student);
		in.setPromotion(promotion == null ? new Promotion(result.getLong("promotion")) : promotion);
		in.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			in.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return in;
	}
	
	@Override
	protected Inscription fullMapping(ResultSet result) throws SQLException, DAOException {
		Inscription in = new Inscription(result.getLong("id"));
		in.setStudent(this.studentDao.findById(result.getLong("student")));
		in.setPromotion(this.promotionDao.findById(result.getLong("promotion")));
		in.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			in.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return in;
	}
	
	protected Inscription fullMapping(ResultSet result, Student student, Promotion promotion) throws SQLException, DAOException {
		Inscription in = new Inscription(result.getLong("id"));
		in.setStudent(student == null ? this.studentDao.findById(result.getLong("student")) : student);
		in.setPromotion(promotion == null ? this.promotionDao.findById(result.getLong("promotion")) : promotion);
		in.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			in.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return in;
	}

	@Override
	protected String getTableName() {
		return Inscription.class.getSimpleName();
	}

}
