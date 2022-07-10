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
import net.uorbutembo.beans.Department;
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
			long id = this.insertInTable(
					connection,
					new String[] {
							"promotion", "student", "adress", "recordDate", "picture"
					},
					new Object[] {
							i.getPromotion().getId(),
							i.getStudent().getId(),
							i.getAdress(),
							i.getRecordDate().getTime(),
							i.getPicture()
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
					new String[] {"promotion", "student", "adress", "lastUpdate", "picture"},
					new Object[] {
							i.getPromotion().getId(),
							i.getStudent().getId(),
							i.getAdress(),
							i.getLastUpdate().getTime(),
							i.getPicture()
					}, id);
			this.emitOnUpdate(i);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public void updatePicture(long id, String picture) {
		try {
			this.updateInTable(
					new String[] {"picture", "lastUpdate"},
					new Object[] {picture, System.currentTimeMillis()}, id);
			Inscription i = findById(id);
			this.emitOnUpdate(i);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public boolean checkByStudent(long student, long year) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s INNER JOIN %s ON %s.promotion = %s.id WHERE %s.academicYear = %d AND %s.student = %d LIMIT 1",
				getTableName(), Promotion.class.getSimpleName(), getTableName(), Promotion.class.getSimpleName(),
				Promotion.class.getSimpleName(), year, getTableName(), student);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public Inscription findByStudent(Student student, AcademicYear year) throws DAOException {
		final String SQL_QUERY = String.format("SELECT "
				+ "Inscription.id AS id, Inscription.recordDate AS recordDate, Inscription.lastUpdate AS lastUpdate, Inscription.student AS student,"
				+ "Inscription.promotion AS promotion, Inscription.adress AS adress, Inscription.picture AS picture "
				+ "FROM Inscription INNER JOIN Promotion ON Inscription.promotion = Promotion.id "
				+ "WHERE Promotion.academicYear = %d AND Inscription.student = %d LIMIT 1 OFFSET 0", year.getId(), student.getId());
		Inscription inscription = null;
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY)) {
			if( result.next() ) 
				inscription = fullMapping(result, student, null);
			else
				throw new DAOException("Aucune inscription pour l'etudiant "+student.getMatricul()+" pour l'annee academique "+year);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return inscription;
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
	public int countByPromotion(long promotionId) throws DAOException {
		return this.countAll(new String[] {"promotion"}, new Object[] {promotionId});
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
	public List<Inscription> findByPromotion(Promotion promotion, int limit, int offset) throws DAOException {
		final String sql = String.format("SELECT * FROM %s WHERE promotion = %d LIMIT %d OFFSET %d", 
												this.getTableName(), promotion.getId(), limit, offset);
		final List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next())
				data.add(this.fullMapping(result, null, promotion));
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription pour dans la promotion "+promotion.toString());
		
		return data;
	}

	@Override
	public List<Inscription> findByPromotion(long promotionId, int limit, int offset) throws DAOException {
		return findByPromotion(this.factory.findDao(PromotionDao.class).findById(promotionId), limit, offset);
	}

	@Override
	public List<Inscription> findByPromotion (long promotionId) throws DAOException {
		return findByPromotion(this.factory.findDao(PromotionDao.class).findById(promotionId));
	}

	@Override
	public boolean checkByFaculty(long facultyId, long academicYearId) throws DAOException {
		final String subSql = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), academicYearId, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), facultyId);
		
		final String sql = String.format("SELECT %s.id FROM %s WHERE promotion IN (%s) LIMIT 1 OFFSET 0", getTableName(), getTableName(),subSql);

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
	public List<Inscription> findByFaculty(long facultyId, long academicYearId) throws DAOException {
		
		final String subSql = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), academicYearId, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), facultyId);
		
		final String sql = String.format("SELECT %s.id, %s.student, %s.promotion, %s.recordDate, %s.lastUpdate FROM %s WHERE promotion IN (%s)", 
				getTableName(), getTableName(), getTableName(), getTableName(), getTableName(), getTableName(), subSql);
		
		final List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) 
				data.add(this.fullMapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription ne correspond aux criteres de selection");
		
		return data;
	}


	@Override
	public int countByAcademicYear (long academicYearId) throws DAOException {
		final String SQL_QUERY = String.format("SELECT COUNT(*) AS nombre FROM %s INNER JOIN %s ON %s.promotion = %s.id WHERE %s.academicYear = %d",
				getTableName(), Promotion.class.getSimpleName(), getTableName(), Promotion.class.getSimpleName(),
				Promotion.class.getSimpleName(), academicYearId);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY)) {
			if(result.next())
				return result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public boolean checkByAcademicYear (long academicYearId) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s INNER JOIN %s ON %s.promotion = %s.id WHERE %s.academicYear = %d LIMIT 1",
				getTableName(), Promotion.class.getSimpleName(), getTableName(), Promotion.class.getSimpleName(),
				Promotion.class.getSimpleName(), academicYearId);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY)) {
			return result.next();
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public List<Inscription> findByAcademicYear (long academicYear) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE %s.promotion IN (SELECT %s.id FROM %s WHERE %s.academicYear = %d )",
				getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				Promotion.class.getSimpleName(), academicYear);
		List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY)) {
			while(result.next())
				data.add(this.fullMapping(result));
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty()) {
			throw new DAOException("Auncune inscription pour l'annee academique "+academicYear);
		}
		
		return data;
	}

	@Override
	public List<Inscription> findByAcademicYear (long academicYear, int limit, int offset) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE %s.promotion IN (SELECT %s.id FROM %s WHERE %s.academicYear = %d ) LIMIT %d OFFSET %d",
				getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				Promotion.class.getSimpleName(), academicYear, limit, offset);
		List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY)) {
			while(result.next()) {
				data.add(this.fullMapping(result));
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty()) {
			throw new DAOException("Auncune inscription pour l'annee academique "+academicYear);
		}
		
		return data;
	}

	@Override
	public List<Inscription> findByDepartment(long departmentId, long academicYearId) throws DAOException {
		
		final String sql = String.format("SELECT %s.id, %s.student, %s.promotion, %s.recordDate, %s.lastUpdate FROM %s "
				+ "WHERE %s.promotion IN (SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department = %d)", 
				getTableName(), getTableName(), getTableName(), getTableName(), getTableName(), getTableName(), getTableName(),
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				academicYearId, Promotion.class.getSimpleName(), departmentId);
		
		final List<Inscription> data = new ArrayList<>();
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				data.add(this.fullMapping(result));
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		if(data.isEmpty())
			throw new DAOException("Aucune inscription ne correspond aux criteres de selection");
		
		return data;
	}

	@Override
	public boolean checkByDepartment(long departmentId, long academicYearId) throws DAOException {
		final String sql = String.format("SELECT %s.id FROM %s WHERE %s.promotion IN (SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department = %d) LIMIT 1 OFFSET 0", 
				getTableName(), getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				academicYearId, Promotion.class.getSimpleName(), departmentId);
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
	public int countByDepartment(long departmentId, long academicYearId) throws DAOException {
		final String sql = String.format("SELECT COUNT(%s.id) AS nombre FROM %s WHERE %s.promotion IN (SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department = %d)", 
				getTableName(), getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				academicYearId, Promotion.class.getSimpleName(), departmentId);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if(result.next())
				return result.getInt("nombre");
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int countByFaculty(long faculty, long year) throws DAOException {
		final String subSql = String.format("SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department IN (SELECT %s.id FROM %s WHERE %s.faculty = %d)",
				Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), year, Promotion.class.getSimpleName(),
				Department.class.getSimpleName(), Department.class.getSimpleName(), Department.class.getSimpleName(), faculty);
		final String sql = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE promotion IN (%s)", getTableName(), subSql);
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
	public List<Inscription> findByDepartment(long departmentId, long academicYearId, int limit, int offset)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> findByStudyClass(long studyClassId, long yearId, int limit, int offset)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> findByStudyClass(long studyClassId, long yearId) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByStudyClass (long studyClassId, long yearId) throws DAOException {
		final String sql = String.format("SELECT COUNT(%s.id) AS nombre FROM %s WHERE %s.promotion IN(SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.studyClass = %d)", 
				getTableName(), getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				yearId, Promotion.class.getSimpleName(), studyClassId);
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next())
				return result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<Inscription> findByPromotions(long studyClass, long[] department, long year, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> findByPromotions(long[] prmotions, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByPromotions(long... promotions) {
		String proms [] = new  String [promotions.length];
		for (int i = 0; i < promotions.length; i++)
			proms[i] = promotions[i]+"";
		
		final String sql = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE promotion IN(%s)", getTableName(), String.join(", ", proms));
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
	public boolean checkByPromotions(long... promotions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int countByPromotions(long studyClass, long[] departments, long year) {
		String [] deps = new String [departments.length];
		for (int i = 0; i < deps.length; i++)
			deps [i] = departments[i]+"";
		
		final String sql = String.format("SELECT COUNT(%s.id) AS nombre FROM %s WHERE %s.promotion IN(SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.studyClass = %d AND %s.department IN(%s))", 
				getTableName(), getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				year, Promotion.class.getSimpleName(), studyClass, Promotion.class.getSimpleName(), String.join(", ", deps));
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next())
				return result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return 0;
	}
	
	@Override
	public int countByPromotions(long[] studyClasses, long department, long year) {
		String [] classes = new String [studyClasses.length];
		for (int i = 0; i < classes.length; i++)
			classes [i] = studyClasses[i]+"";
		
		final String sql = String.format("SELECT COUNT(%s.id) AS nombre FROM %s WHERE %s.promotion IN(SELECT %s.id FROM %s WHERE %s.academicYear = %d AND %s.department = %d AND %s.studyClass IN(%s))", 
				getTableName(), getTableName(), getTableName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(), Promotion.class.getSimpleName(),
				year, Promotion.class.getSimpleName(), department, Promotion.class.getSimpleName(), String.join(", ", classes));
		try (
				Connection connection = this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			if (result.next())
				return result.getInt("nombre");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<Inscription> findByPromotions(long[] studyClasses, long department, long year, int limit, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkByPromotions(long[] studyClasses, long department, long year) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkByPromotions(long studyClass, long[] departments, long year) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkByStudyClass(long studyClass, long yearId) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public List<Inscription> findByAcademicYear(long academicYearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByAcademicYear(long academicYearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Inscription> findByFaculty(long faculty, long year, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByFaculty(long faculty, long year, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Inscription> findByDepartment(long departmentId, long academicYearId, Date min, Date max)
			throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByDepartment(long departmentId, long academicYearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Inscription> findByStudyClass(long studyClassId, long yearId, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> findByPromotion(long promotion, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByPromotion(long promotion, Date min, Date max) throws DAOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Inscription> search (String... value) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> search (AcademicYear year, String... value) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Inscription> search (Promotion promotion, String... value) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Inscription baseMapping (ResultSet result) throws SQLException {
		Inscription in = new Inscription(result.getLong("id"));
		in.setRecordDate(new Date(result.getLong("recordDate")));
		in.setAdress(result.getString("adress"));
		in.setPicture(result.getString("picture"));
		if(result.getLong("lastUpdate") != 0) {
			in.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return in;
	}

	@Override
	protected Inscription mapping (ResultSet result) throws SQLException, DAOException {
		Inscription in = baseMapping(result);
		in.setStudent(new Student(result.getLong("student")));
		in.setPromotion(new Promotion(result.getLong("promotion")));
		return in;
	}
	
	protected Inscription mapping (ResultSet result, Student student, Promotion promotion) throws SQLException, DAOException {
		Inscription in = baseMapping(result);
		in.setStudent(student == null ? new Student(result.getLong("student")) : student);
		in.setPromotion(promotion == null ? new Promotion(result.getLong("promotion")) : promotion);
		return in;
	}
	
	@Override
	protected Inscription fullMapping (ResultSet result) throws SQLException, DAOException {
		Inscription in = baseMapping(result);
		in.setStudent(studentDao.findById(result.getLong("student")));
		in.setPromotion(promotionDao.findById(result.getLong("promotion")));
		return in;
	}
	
	protected Inscription fullMapping (ResultSet result, Student student, Promotion promotion) throws SQLException, DAOException {
		Inscription in = baseMapping(result);
		in.setStudent(student == null ? this.studentDao.findById(result.getLong("student")) : student);
		in.setPromotion(promotion == null ? this.promotionDao.findById(result.getLong("promotion")) : promotion);
		return in;
	}

	@Override
	public boolean checkByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Inscription> findByAcademicYearBeforDate(long yearId, Date date) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTableName () {
		return Inscription.class.getSimpleName();
	}

}
