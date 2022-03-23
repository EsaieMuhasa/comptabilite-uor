/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;

/**
 * @author Esaie MUHASA
 *
 */
public interface AnnualSpendDao extends DAOInterface<AnnualSpend> {
	
	/**
	 * verfication de l'existance de l'unicite des clee
	 * @param yearId identifiant de l'annee academique 
	 * @param spendId identifiant du dep
	 * @return
	 * @throws DAOException
	 */
	boolean check (long yearId, long spendId) throws DAOException;
	default boolean check (AcademicYear year, UniversitySpend spend) throws DAOException{
		return check (year.getId(), spend.getId());
	}
	
	/**
	 * Renvoie la configuration du depasse annuel
	 * @param year
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	AnnualSpend find (AcademicYear year, UniversitySpend spend) throws DAOException;
	default AnnualSpend find (long yearId, long spendId) throws DAOException {
		return find(
				getFactory().findDao(AcademicYearDao.class).findById(yearId),
				getFactory().findDao(UniversitySpendDao.class).findById(spendId));
	}
	
	/**
	 * Renvoie les linge budgetaire pour une annee academique complet
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	List<AnnualSpend> findkByAcademicYear (AcademicYear academicYear) throws DAOException;
	default List<AnnualSpend> findkByAcademicYear (long academicYear) throws DAOException {
		return findkByAcademicYear(getFactory().findDao(AcademicYearDao.class).findById(academicYear));
	}
	
	/**
	 * Verifie s'il y a des linges budgetaire pour une annees academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYear (long academicYear) throws DAOException{
		return this.check("academicYear", academicYear);		
	}
	
	/**
	 * Compte le nombre de ligne budgetaire d'une annee academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long academicYear) throws DAOException ;

}
