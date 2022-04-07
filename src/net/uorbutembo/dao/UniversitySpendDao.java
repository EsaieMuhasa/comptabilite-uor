/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.UniversitySpend;

/**
 * @author Esaie MUHASA
 *
 */
public interface UniversitySpendDao extends DAOInterface<UniversitySpend> {
	
	/**
	 * Verification de l'existance dl'une depance assicier a l'annee academique en parametre
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYear (AcademicYear year) throws DAOException{
		return getFactory().findDao(AnnualSpendDao.class).checkByAcademicYear(year.getId());
	}
	default boolean checkByAcademicYear (long year) throws DAOException{
		return getFactory().findDao(AnnualSpendDao.class).checkByAcademicYear(year);
	}
	
	/**
	 * recuperation des depasse universitaires concerner pour l'annee academique ne parametre
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	List<UniversitySpend> findByAcademicYear (long academicYearId)  throws DAOException;
	default List<UniversitySpend> findByAcademicYear (AcademicYear year)  throws DAOException{
		if(year == null || year.getId() <= 0) {
			throw new DAOException("Impossible de poursuivre les traitements car le parametre year est null");
		}
		return findByAcademicYear(year.getId());
	}
}
