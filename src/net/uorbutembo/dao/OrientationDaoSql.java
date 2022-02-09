/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.SQLException;

import net.uorbutembo.beans.Orientation;

/**
 * @author Esaie MUHASA
 *
 */
abstract class OrientationDaoSql <T extends Orientation> extends UtilSql <T> implements OrientationDao<T> {

	public OrientationDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	

	@Override
	public synchronized void create(T f) throws DAOException {
		try {
			long id = insertInTable(
					new String [] {"acronym", "name", "recordDate"},
					new Object[] {f.getAcronym(), f.getName(), f.getRecordDate().getTime()});
			f.setId(id);
			this.sendOnCreateEvent(f);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public synchronized void update(T f, long id) throws DAOException {
		try {
			updateInTable(
					new String [] {"acronym", "name", "recordDate"},
					new Object[] {f.getAcronym(), f.getName(), f.getRecordDate().getTime()}, id);
			f.setId(id);
			this.sendOnUpdateEvent(f);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de l'enregistrement des modifications: "+e.getMessage(), e);
		}
	}


}
