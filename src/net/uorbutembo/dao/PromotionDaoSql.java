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
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 *
 */
class PromotionDaoSql extends UtilSql<Promotion> implements PromotionDao {
	
	private DepartmentDao departmentDao;
	private StudyClassDao studyClassDao;
	private AcademicYearDao academicYearDao;
	private AcademicFeeDao academicFeeDao;

	public PromotionDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		departmentDao = factory.findDao(DepartmentDao.class);
		studyClassDao = factory.findDao(StudyClassDao.class);
		academicYearDao = factory.findDao(AcademicYearDao.class);
		academicFeeDao = factory.findDao(AcademicFeeDao.class);
	}

	@Override
	public void create(Promotion p) throws DAOException {
		try {
			long id = insertInTable(
					new String[] {"academicYear", "studyClass", "department", "recordDate"},
					new Object[] {p.getAcademicYear().getId(), p.getStudyClass().getId(), p.getDepartment().getId(), p.getRecordDate().getTime()});
			p.setId(id);
			this.emitOnCreate(p);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement de la promotion\n"+e.getMessage(), e);
		}
	}
	
	@Override
	public void create( Promotion [] t ) throws DAOException {
		try (Connection connection= this.factory.getConnection();) {
			connection.setAutoCommit(false);
			for (int i=0; i<t.length; i++) {
				Promotion p = t[i];
				long id = insertInTable(
						connection,
						new String[] {"academicYear", "studyClass", "department", "recordDate"},
						new Object[] {p.getAcademicYear().getId(), p.getStudyClass().getId(), p.getDepartment().getId(), p.getRecordDate().getTime()});
				p.setId(id);
			}
			connection.commit();
			
			for (Promotion p : t) {
				this.emitOnCreate(p);
			}
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue en plaine transaction\n"+e.getMessage(), e);
		}
	}

	@Override
	public void update(Promotion p, long id) throws DAOException {
		try {
			updateInTable(
					new String [] {"academicYear", "studyClass", "department", "lastUpdate"},
					new Object[] {p.getAcademicYear().getId(), p.getStudyClass().getId(), p.getDepartment().getId(), p.getLastUpdate().getTime()},
					id);
		} catch (SQLException e) {
			throw new DAOException("Une erreuru est survenue lors de la mise en jour\n"+e.getMessage(), e);
		}
	}

	@Override
	public boolean check(long academicYearId, long departmentId, long studyClassId) throws DAOException {
		return this.check(
				new String[] {"academicYear", "department", "studyClass"},
				new Object[] {academicYearId, departmentId, studyClassId});
	}

	@Override
	public Promotion find(long academicYearId, long departmentId, long studyClassId) throws DAOException {
		return this.find(
				new String[] {"academicYear", "department", "studyClass"},
				new Object[] {academicYearId, departmentId, studyClassId});
	}
	
	@Override
	public Promotion find(AcademicYear academicYear, Department department, StudyClass studyClass) throws DAOException {
		String SQL_QUERY = String.format("SELECT * FROM %s WHERE academicYear = %d AND department = %d AND studyClass = %d", 
				getViewName(), academicYear.getId(), department.getId(), studyClass.getId());
		System.out.println(SQL_QUERY);
		
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if( result.next() ) {
				Promotion pro = new Promotion(result.getLong("id"));
				pro.setRecordDate(new Date(result.getLong("recordDate")));
				pro.setAcademicYear(academicYear);
				pro.setDepartment(department);
				pro.setStudyClass(studyClass);
				if(result.getLong("lastUpdate") != 0) {
					pro.setLastUpdate(new Date(result.getLong("lastUpdate")));
				}
				
				if (result.getLong("academicFee") != 0) {
					pro.setAcademicFee(this.academicFeeDao.findById(result.getLong("academicFee")));
				}
				return pro;
			} 
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		throw new DAOException("Aucune promotion ne correspond aux criteres de selection");
	}

	@Override
	public boolean checkByDepartment(long academicYearId, long departmentId) throws DAOException {
		return this.check(
				new String[] {"academicYear", "department"},
				new Object[] {academicYearId, departmentId});
	}

	@Override
	public List<Promotion> findByDepartment(long academicYearId, long departmentId) throws DAOException {
		return this.findAll(
				new String[] {"academicYear", "department"},
				new Object[] {academicYearId, departmentId});
	}

	@Override
	public boolean checkByStudyClass(long academicYearId, long studyClassId) throws DAOException {
		return this.check(
				new String[] {"academicYear", "studyClass"},
				new Object[] {academicYearId, studyClassId});
	}

	@Override
	public List<Promotion> findByStudyClass(long academicYearId, long studyClassId) throws DAOException {
		return this.findAll(
				new String[] {"academicYear", "studyClass"},
				new Object[] {academicYearId, studyClassId});
	}

	@Override
	public List<Promotion> findByAcademicYear(long academicYearId) throws DAOException {
		List<Promotion> ps = this.findAll(
				new String[] {"academicYear"},
				new Object[] {academicYearId}, "studyClass");
		AcademicYear year = this.factory.findDao(AcademicYearDao.class).findById(academicYearId);
		for (Promotion p : ps) {
			p.setAcademicYear(year);
		}
		return ps;
	}
	
	@Override
	public List<Promotion> findByAcademicYear(AcademicYear academicYear) throws DAOException {
		List<Promotion> ps = this.findAll(
				new String[] {"academicYear"},
				new Object[] {academicYear.getId()}, "studyClass");
		for (Promotion p : ps) {
			p.setAcademicYear(academicYear);
		}
		return ps;
	}

	@Override
	public boolean checkByAcademicFee(long feeId) throws DAOException {
		return check("academicFee", feeId);
	}

	@Override
	public List<Promotion> findByAcademicFee(AcademicFee fee) throws DAOException {
		List<Promotion> data = new ArrayList<>();
		
		final String sql = String.format("SELECT * FROM %s WHERE academicFee = %d", getTableName(), fee.getId());
		System.out.println(sql);
		try (
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql);
				){
			while (result.next()) {
				data.add(fullMapping(result));				
			}
			
			if(data.isEmpty())
				throw new DAOException("Aucune promotion configurer pour les frais universitaire => "+fee.getAmount()+" USD");
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}
	
	@Override
	public void bindToAcademicFee(Promotion promotion, AcademicFee academicFee) throws DAOException {
		try {
			Date now  = new Date();
			promotion.setLastUpdate(now);
			updateInTable(
					new String[] { "academicFee", "lastUpdate" },
					new Object[] { academicFee == null || academicFee.getId() <= 0? null : academicFee, now.getTime() },
					promotion.getId()
				);
			emitOnUpdate(promotion);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public void bindToAcademicFee(Promotion[] promotions, AcademicFee academicFee) throws DAOException {
		try (Connection connection = factory.getConnection()) {
			connection.setAutoCommit(false);
			Date now = new Date();
			for (Promotion promotion : promotions) {
				promotion.setLastUpdate(now);
				updateInTable(connection, 
					new String[] {
						"academicFee",
						"lastUpdate"
					}, 
					new Object[] {
						academicFee == null || academicFee.getId() <= 0? null : academicFee.getId(),
						now.getTime()
					}, promotion.getId()
				);
			}
			connection.commit();
			emitOnUpdate(promotions);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	protected Promotion mapping(ResultSet result) throws SQLException, DAOException {
		Promotion p = new Promotion(result.getLong("id"));
		p.setAcademicYear(result.getLong("academicYear"));
		p.setDepartment(this.departmentDao.findById(result.getLong("department")));
		p.setStudyClass(this.studyClassDao.findById(result.getLong("studyClass")));
		p.setAcademicYear(this.academicYearDao.findById(result.getLong("academicYear")));
		p.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("academicFee") > 0)
			p.setAcademicFee(new AcademicFee(result.getLong("academicFee")));
		if(result.getLong("lastUpdate") != 0) {
			p.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return p;
	}
	
	@Override
	protected Promotion fullMapping(ResultSet result) throws SQLException, DAOException {
		Promotion p = new Promotion(result.getLong("id"));
		p.setAcademicYear(result.getLong("academicYear"));
		p.setDepartment(this.departmentDao.findById(result.getLong("department")));
		p.setStudyClass(this.studyClassDao.findById(result.getLong("studyClass")));
		p.setAcademicYear(this.academicYearDao.findById(result.getLong("academicYear")));
		p.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("academicFee") > 0)
			p.setAcademicFee(academicFeeDao.findById(result.getLong("academicFee")));
		if(result.getLong("lastUpdate") != 0) {
			p.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return p;
	}

	@Override
	protected String getTableName() {
		return Promotion.class.getSimpleName();
	}

}
