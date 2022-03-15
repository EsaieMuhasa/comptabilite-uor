/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;

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
			this.emitOnCreate(e);
			currentYear = e;
			reload();
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
			this.emitOnUpdate(a);
		} catch (SQLException e) {
			throw new DAOException("Une erreure est survenue lors de la sauvegarde des modifications: \n"+e.getMessage(), e);
		}
	}

	@Override
	public boolean checkCurrent() throws DAOException {
		return true;
	}

	@Override
	public AcademicYear findCurrent() throws DAOException {
		if(this.currentYear == null) {
			this.currentYear = this.findAll().get(0);
		}
		return this.currentYear;
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
		
		return data;
	}
	
	private volatile boolean reloadRunning = false;
	
	@Override
	public boolean isReload() {
		return reloadRunning;
	}
	
	@Override
	public synchronized void reload(int requestId) {
		if(reloadRunning) 
			return;
		
		reloadRunning = true;
		Thread t = new Thread(()-> {
			try {
				if(currentYear == null && checkCurrent())
					findCurrent();
				
				if(currentYear == null){
					reloadRunning = false;
					return;
				}
				
				for (AcademicYearDaoListener ls : yearListeners) {
					ls.onCurrentYear(currentYear);
				}				
			} catch (DAOException e) {
				emitOnError(e);
			} catch (Exception e) {
				emitOnError(new DAOException(e.getMessage(), e));
			}
			reloadRunning = false;
		});
		t.start();
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
