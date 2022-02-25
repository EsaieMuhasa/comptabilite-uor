/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;

/**
 * @author Esaie MUHASA
 *
 */
public interface AnnualSpendDao extends DAOInterface<AnnualSpend> {
	
	/**
	 * renvoie le ligne budgetaire pour une annee academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	public List<AnnualSpend> findkByAcademicYear (long academicYear) throws DAOException;
	
	
	/**
	 * Renvoie les linge budgetaire pour une annee academique complet
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	public List<AnnualSpend> findkByAcademicYear (AcademicYear academicYear) throws DAOException;
	
	/**
	 * Verifie s'il y a des linges budgetaire pour une annees academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	public default boolean checkByAcademicYear (long academicYear) throws DAOException{
		return this.check("academicYear", academicYear);		
	}
	
	/**
	 * Compte le nombre de ligne budgetaire d'une annee academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	public int countByAcademicYear (long academicYear) throws DAOException ;

}
