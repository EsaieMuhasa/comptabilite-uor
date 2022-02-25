/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
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
	
	/**
	 * Verifie s'il y a des orientations qui ont ete configurer pour fonctionner pour l'annee en parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByAcademicYear (AcademicYear year) throws DAOException;
	
	/**
	 * Comptage des orientations qui ont fonctionner pour l'annee en parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public int countByAcademicYear (AcademicYear year) throws DAOException;
	
	/**
	 * renvoie la collection des orientations qui ont fonctionner pour l'annee en paramere
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public List<T> findByAcademicYear (AcademicYear year) throws DAOException;
	
}
