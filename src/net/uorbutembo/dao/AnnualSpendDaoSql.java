/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;

/**
 * @author Esaie MUHASA
 *
 */
class AnnualSpendDaoSql extends UtilSql<AnnualSpend> implements AnnualSpendDao {
	
	private UniversitySpendDao universitySpendDao;
	private AcademicYearDao academicYearDao;

	public AnnualSpendDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		this.universitySpendDao = factory.findDao(UniversitySpendDao.class);
		this.academicYearDao = factory.findDao(AcademicYearDao.class);
	}

	@Override
	public void create(AnnualSpend a) throws DAOException {
		try {
			long id = this.insertInTable(
					new String[] {"academicYear", "universitySpend", "recordDate"},
					new Object[] {a.getAcademicYear().getId(), a.getUniversitySpend().getId(), a.getRecordDate().getTime()});
			a.setId(id);
			this.emitOnCreate(a);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	@Override
	public void create(AnnualSpend[] t) throws DAOException {
		try (Connection connection = this.factory.getConnection()) {
			connection.setAutoCommit(false);
			for (AnnualSpend a : t) {				
				long id = this.insertInTable(
						connection,
						new String[] {"academicYear", "universitySpend", "recordDate"},
						new Object[] {a.getAcademicYear().getId(), a.getUniversitySpend().getId(), a.getRecordDate().getTime()});
				a.setId(id);
			}
			connection.commit();
			for (AnnualSpend a : t) {				
				this.emitOnCreate(a);
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(AnnualSpend e, long id) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AnnualSpend> findkByAcademicYear(long academicYear) throws DAOException {
		List<AnnualSpend> data = this.findAll(new String[] {"academicYear"}, new Object[] {academicYear});
		AcademicYear academic = this.academicYearDao.findById(academicYear);
		for (AnnualSpend spend : data) {
			spend.setAcademicYear(academic);
			spend.setUniversitySpend(this.universitySpendDao.findById(spend.getUniversitySpend().getId()));
		}
		return data;
	}
	
	@Override
	public List<AnnualSpend> findkByAcademicYear(AcademicYear academicYear) throws DAOException {
		List<AnnualSpend> data = this.findAll(new String[] {"academicYear"}, new Object[] {academicYear.getId()});
		for (AnnualSpend spend : data) {
			spend.setAcademicYear(academicYear);
			spend.setUniversitySpend(this.universitySpendDao.findById(spend.getUniversitySpend().getId()));
		}
		return data;
	}

	@Override
	public int countByAcademicYear(long academicYear) throws DAOException {
		return 0;
	}

	@Override
	protected AnnualSpend mapping(ResultSet result) throws SQLException, DAOException {
		AnnualSpend a = new AnnualSpend(result.getLong("id"));
		a.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			a.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		a.setUniversitySpend(new UniversitySpend(result.getLong("universitySpend")));
		a.setAcademicYear(new AcademicYear(result.getLong("academicYear")));
		return a;
	}
	
	protected AnnualSpend mapping(ResultSet result, boolean year, boolean spend) throws SQLException, DAOException {
		AnnualSpend a = new AnnualSpend(result.getLong("id"));
		a.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			a.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		if (spend){			
			a.setUniversitySpend(this.universitySpendDao.findById(result.getLong("universitySpend")));
		}
		if(year) {			
			a.setAcademicYear(this.academicYearDao.findById(result.getLong("academicYear")));
		}
		return a;
	}
	
	@Override
	protected AnnualSpend fullMapping(ResultSet result) throws SQLException, DAOException {
		AnnualSpend a = new AnnualSpend(result.getLong("id"));
		a.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			a.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		a.setUniversitySpend(this.universitySpendDao.findById(result.getLong("universitySpend")));
		a.setAcademicYear(this.academicYearDao.findById(result.getLong("academicYear")));		
		return a;
	}

	@Override
	protected String getTableName() {
		return AnnualSpend.class.getSimpleName();
	}

}
