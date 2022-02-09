/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.AcademicYear;

/**
 * @author Esaie MUHASA
 *
 */
class AcademicYearDaoSql extends UtilSql<AcademicYear> implements AcademicYearDao {

	public AcademicYearDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public synchronized void create(AcademicYear e) throws DAOException {
		try {
			long id = insertInTable(
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
			this.sendOnCreateEvent(e);
		} catch (SQLException e1) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement. \n"+e1.getMessage(), e1);
		}
	}

	@Override
	public synchronized void update(AcademicYear a, long id) throws DAOException {
		try {
			this.updateInTable(
					new String[] {"label", "startDate", "closeDate", "recordDate", "previous"},
					new Object[] {
							a.getLabel(), 
							a.getStartDate().getTime(),
							a.getCloseDate()!= null? a.getCloseDate().getTime() : null,
							a.getRecordDate().getTime(),
							a.getPrevious()!= null? a.getPrevious().getId() : null
					}, id);
			this.sendOnUpdateEvent(a);
		} catch (SQLException e) {
			throw new DAOException("Une erreure est survenue lors de la sauvegarde des modifications: \n"+e.getMessage(), e);
		}
	}

	@Override
	public boolean checkCurrent() throws DAOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AcademicYear findCurrent() throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AcademicYear maping(ResultSet result) throws SQLException, DAOException {
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
		
		return data;
	}

	@Override
	protected String getTableName() {
		return AcademicYear.class.getSimpleName();
	}

}
