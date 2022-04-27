/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;

/**
 * @author Esaie MUHASA
 *
 */
public interface AnnualRecipeDao extends DAOInterface<AnnualRecipe> {
	
	/**
	 * verification d'unicite des clee
	 * @param yearId
	 * @param recipeId
	 * @return
	 * @throws DAOException
	 */
	boolean check (long yearId, long recipeId) throws DAOException;
	
	/**
	 * Verification de l'existance d'une configuration pour l'ID de l'annee academique en parametre
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAcademicYear (long yearId) throws DAOException;
	
	/**
	 * Comptage du nombre d'occrence pour la configuration de l'annee academique dont l'id est en parmatre
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long yearId) throws DAOException;
	
	/**
	 * Verification de l'existance d'aumon une configuration pour l'annee academique ene parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYear (AcademicYear year) throws DAOException {
		return checkByAcademicYear(year.getId());
	}
	
	/**
	 * Comptage du nombre d'occurence pour liee a la configuration de l'annee academique en parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	default int countByAcademicYear (AcademicYear year) throws DAOException {
		return countByAcademicYear(year.getId());
	}
	
	/**
	 * Renvoie l'occurence correspondant aux cleee unique en parmatre
	 * @param yearId
	 * @param recipeId
	 * @return
	 * @throws DAOException
	 */
	AnnualRecipe find (long yearId, long recipeId)  throws DAOException;
	
	/**
	 * renvoie la collection des recettes annuel de l'annee dont l'id est en parmetre
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	default List<AnnualRecipe> findByAcademicYear (long yearId) throws DAOException {
		return findByAcademicYear(getFactory().findDao(AcademicYearDao.class).findById(yearId));
	}
	
	/**
	 * renvoie la collection de la configuration annelle
	 * le resultant est une partie des elements corresponant aux criteres de selection
	 * @param yearId
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	default List<AnnualRecipe> findByAcademicYear (long yearId, int limit, int offset) throws DAOException {
		return findByAcademicYear(getFactory().findDao(AcademicYearDao.class).findById(yearId), limit, offset);
	}
	
	/**
	 * Renvoie la collection des configurations associer a l'annee academique en parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	List<AnnualRecipe> findByAcademicYear (AcademicYear year) throws DAOException;
	
	/**
	 * Renvoie ue partie de la configuration annuel de l'annee en parametre
	 * @param year
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<AnnualRecipe> findByAcademicYear (AcademicYear year, int limit, int offset) throws DAOException;
}
