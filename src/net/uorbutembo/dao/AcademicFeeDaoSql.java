/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;

/**
 * @author Esaie MUHASA
 *
 */
class AcademicFeeDaoSql extends UtilSql<AcademicFee> implements AcademicFeeDao {
	
	private AcademicYearDao academicYearDao;

	public AcademicFeeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
		this.academicYearDao = factory.findDao(AcademicYearDao.class);
	}
	
	@Override
	protected boolean hasView() {
		return true;
	}
	
	@Override
	public void create(AcademicFee a) throws DAOException {
		try {
			long id = this.insertInTable(
					new String [] {"amount", "description", "academicYear", "recordDate"},
					new Object[] {a.getAmount(), a.getDescription(), a.getAcademicYear().getId(), a.getRecordDate().getTime()});
			a.setId(id);
			this.emitOnCreate(a);
		} catch (SQLException e) {
			throw new DAOException("Une ererur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public void update(AcademicFee a, long id) throws DAOException {
		try {
			this.updateInTable(
					new String [] {"amount", "description", "lastUpdate"},
					new Object[] {a.getAmount(), a.getDescription(), a.getLastUpdate().getTime()}, id);
			a.setId(id);
			this.emitOnUpdate(a);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue dans le processuce de modification\n"+e.getMessage(), e);
		}
	}

	@Override
	public List<AcademicFee> findByAcademicYear(long yearId) throws DAOException {
		AcademicYear year = this.academicYearDao.findById(yearId);
		List<AcademicFee> data = this.findAll(new String[] {"academicYear"}, new Object [] {yearId});
		for (AcademicFee fee : data) {
			fee.setAcademicYear(year);
		}
		return data;
	}
	
	@Override
	public List<AcademicFee> findByAcademicYear(AcademicYear year) throws DAOException {
		List<AcademicFee> data = this.findAll(new String[] {"academicYear"}, new Object [] {year.getId()});
		for (AcademicFee fee : data) {
			fee.setAcademicYear(year);
		}
		return data;
	}

	@Override
	protected AcademicFee mapping(ResultSet result) throws SQLException, DAOException {
		final AcademicFee fee = new AcademicFee(result.getLong("id"));
		fee.setDescription(result.getString("description"));
		fee.setAmount(result.getFloat("amount"));
		fee.setRecordDate(new Date(result.getLong("recordDate")));
		fee.setCollected(result.getDouble("collected"));
		fee.setTotalExpected(result.getDouble("totalExpected"));
		if(result.getLong("lastUpdate") != 0) {
			fee.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return fee;
	}
	
	@Override
	protected AcademicFee fullMapping(ResultSet result) throws SQLException, DAOException {
		final AcademicFee fee = this.mapping(result);
		fee.setAcademicYear(this.academicYearDao.findById(result.getLong("academicYear")));
		return fee;
	}

	@Override
	protected String getTableName() {
		return AcademicFee.class.getSimpleName();
	}

}
