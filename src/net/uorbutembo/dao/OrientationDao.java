/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.Orientation;

/**
 * @author Esaie MUHASA
 *
 */
interface OrientationDao <T extends Orientation> extends DAOInterface<T> {
	
	/**
	 * @param acronym
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcronym (String acronym) throws DAOException {
		return this.check("acronym", acronym);
	}
	
	/**
	 * @param acronym
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcronym (String acronym, long id) throws DAOException {
		return this.check("acronym", acronym, id);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByName (String name) throws DAOException {
		return this.check("name", name);
	}
	
	/**
	 * 
	 * @param name
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByName (String name, long id) throws DAOException {
		return this.check("name", name, id);
	}
	
	
}
