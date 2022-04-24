/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.UniversityRecipe;

/**
 * @author Esaie MUHASA
 *
 */
public interface UniversityRecipeDao extends DAOInterface <UniversityRecipe> {

	/**
	 * selection de tout les recete configurer pour l'annee academique
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	List<UniversityRecipe> findByAcademicYear(long yearId) throws DAOException;
	default List<UniversityRecipe> findByAcademicYear(AcademicYear year) throws DAOException {
		return findByAcademicYear(year.getId());
	}

}
