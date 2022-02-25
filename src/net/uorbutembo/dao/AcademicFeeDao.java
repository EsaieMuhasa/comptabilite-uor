/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;

/**
 * @author Esaie MUHASA
 * 
 */
public interface AcademicFeeDao extends DAOInterface<AcademicFee> {
	
	/**
	 * Verifie s'il y des frais configurer pour l'annee academique 
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	public default boolean checkByAcademicYear (long yearId) throws DAOException {
		return this.check("academicYear", yearId);
	}
	
	/**
	 * renvoie la configuration des frais pour l'annee academique en parametre
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	public List<AcademicFee> findByAcademicYear (long yearId) throws DAOException;
	
	/**
	 * Renvoie tout les  frais academique qui refrenceie l'annee academique en parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public List<AcademicFee> findByAcademicYear (AcademicYear year)  throws DAOException;
}
