/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		try (Connection connection = factory.getConnection()) {
			
			connection.setAutoCommit(false);
			
			AcademicYear previous = null;
			if (checkCurrent()){
				previous = findCurrent();
				e.setPrevious(previous);
				if(previous.getCloseDate() == null) {
					updateInTable(
							connection,
							new String[] {"closeDate", "lastUpdate"},
							new Object[] {e.getRecordDate().getTime()-1000, e.getRecordDate().getTime()},
							previous.getId());
				}
			}
			
			long id = insertInTable(
					connection,
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
			connection.commit();
			currentYear = e;
			
			this.emitOnCreate(e);
		} catch (SQLException ex) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement. \n"+ex.getMessage(), ex);
		}
	}

	@Override
	public synchronized void update(AcademicYear a, long id) throws DAOException {
		try {
			this.updateInTable(
					new String[] {"label", "startDate", "closeDate", "recordDate"},
					new Object[] {
							a.getLabel(), 
							a.getStartDate().getTime(),
							a.getCloseDate()!= null? a.getCloseDate().getTime() : null,
							a.getLastUpdate().getTime()
					}, id);
			this.emitOnUpdate(a);
		} catch (SQLException e) {
			throw new DAOException("Une erreure est survenue lors de la sauvegarde des modifications: \n"+e.getMessage(), e);
		}
	}
	
	@Override
	public synchronized void delete(long id) throws DAOException {
		final String SQL_QUERY = String.format("DELETE FROM %s WHERE id = ?", getTableName());
		System.out.println(SQL_QUERY);
		
		AcademicYear t = this.findById(id);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, id);
			) {
			int status = statement.executeUpdate();
			
			if(status == 0) {
				throw new DAOException("Aucune occurence suprimer");
			}
			
			if(currentYear !=null && t.getId() == currentYear.getId()) {
				currentYear = null;
			}
			
			emitOnDelete(t);
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public boolean checkCurrent() throws DAOException {
		if(currentYear != null || countAll() == 1 )
			return true;
		final String SQL = String.format("SELECT id FROM %s WHERE previous IS NOT NULL AND id NOT IN(SELECT previous FROM %s WHERE previous IS NOT NULL)", getTableName(), getTableName());
		System.out.println(SQL);
		try(
				Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL);
			) {
			
			return result.next();
			
		}catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public AcademicYear findCurrent() throws DAOException {
		if(this.currentYear == null) {
			if(countAll() == 1) {
				currentYear = findAll().get(0);
			} else {
				final String SQL = String.format("SELECT * FROM %s WHERE previous IS NOT NULL AND id NOT IN(SELECT previous FROM %s WHERE previous IS NOT NULL)", getTableName(), getTableName());
				System.out.println(SQL);
				try(
						Connection connection = factory.getConnection();
						Statement statement = connection.createStatement();
						ResultSet result = statement.executeQuery(SQL);
						) {
					
					if (result.next())
						currentYear = mapping(result);
					else 
						throw new DAOException("Impossible de determiner l'annee courante");
				}catch (SQLException e) {
					throw new DAOException(e.getMessage(), e);
				}
			}
		}
		return this.currentYear;
	}
	
	@Override
	protected synchronized void emitOnCreate(AcademicYear e, int requestId) {
		Thread t = new Thread(() -> {			
			for (DAOListener<AcademicYear> ls : listeners) {
				ls.onCreate(e, requestId);
			}
			
			reload();
		});
		t.start();
	}
	
	@Override
	protected synchronized void emitOnDelete(AcademicYear e, int requestId) {
		Thread t = new Thread(() -> {			
			for (DAOListener<AcademicYear> ls : listeners) {
				ls.onDelete(e, requestId);
			}
			
			reload();
		});
		t.start();
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
	public synchronized void reload (int requestId) {
		reloadRunning = true;
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
