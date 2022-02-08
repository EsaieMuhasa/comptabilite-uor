/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.AcademicYear;

/**
 * @author Esaie MUHASA
 *
 */
public interface AcademicYearDao extends DAOInterface<AcademicYear> {
	
	/**
	 * Verifie si le label existe
	 * @param label
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByLabel (String label) throws DAOException {
		return this.check("label", label);
	}
	
	/**
	 * Verifie s'il y a l'annee qui correspond au label en premier parametre
	 * et dont l'ID est different du 2eme parametre
	 * @param label
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByLabel (String label, long id) throws DAOException {
		return this.check("label", label, id);
	}
	
	/**
	 * renvoie l''annee qui correpond au label en parametre
	 * @param label
	 * @return
	 * @throws DAOException
	 */
	default AcademicYear findByLabel (String label) throws DAOException{
		return this.find("label", label);
	}
	
	/**
	 * verifie s'il y a l'annee actuel
	 * @return
	 * @throws DAOException
	 */
	public boolean checkCurrent () throws DAOException;
	
	/**
	 * Renvoie l'annee actuel
	 * @return
	 * @throws DAOException
	 */
	public AcademicYear findCurrent () throws DAOException;
}
