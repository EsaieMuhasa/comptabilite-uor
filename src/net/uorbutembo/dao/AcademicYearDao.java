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
	boolean checkCurrent () throws DAOException;
	
	/**
	 * Est-ce l'annee academique proprietaire de l'id en parametre qui est l'annee academique courente??s
	 * @param id
	 * @return
	 */
	default boolean isCurrent (long id) throws DAOException{
		if(checkCurrent() && id > 0) {
			return findCurrent().getId() == id;
		}
		return false;
	}
	
	/**
	 * Est-ce cette annee qui est l'annee academique courante??
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	default boolean isCurrent (AcademicYear year) throws DAOException{
		if(checkCurrent() && year != null) {
			AcademicYear a = findCurrent();
			return  a == year || a.getId() == year.getId();
		}
		return false;
	}
	
	/**
	 * Renvoie l'annee actuel
	 * @return
	 * @throws DAOException
	 */
	public AcademicYear findCurrent () throws DAOException;
	
	/**
	 * Ajout du dao specifique au dao des annees academique
	 * @param listener
	 */
	void addYearListener (AcademicYearDaoListener listener);
	
	/**
	 * supression du listener specifique au dao de l'annee academique
	 * @param listener
	 */
	void removeYearListener (AcademicYearDaoListener listener);
}
