/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 *
 */
public interface PromotionDao extends DAOInterface<Promotion> {
	
	/**
	 * Verifie s'il y a une prmotion
	 * @param academicYearId
	 * @param departmentId
	 * @param studyClassId
	 * @return
	 * @throws DAOException
	 */
	public boolean check (long academicYearId, long departmentId, long studyClassId) throws DAOException;
	
	/**
	 * Renvoie la promotion correspodant aux coordonee en parametre
	 * @param academicYearId
	 * @param departmentId
	 * @param studyClassId
	 * @return
	 * @throws DAOException
	 */
	public Promotion find (long academicYearId, long departmentId, long studyClassId) throws DAOException;
	
	public Promotion find (AcademicYear academicYear, Department department, StudyClass studyClass) throws DAOException;

	/**
	 * verifie le promotion d'un departement en une annee
	 * @param academicYearId
	 * @param departmentId
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByDepartment (long academicYearId, long departmentId) throws DAOException;
	
	/**
	 * Renvoie la collection des promotion d'un departement en une annee
	 * @param academicYearId
	 * @param departmentId
	 * @return
	 * @throws DAOException
	 */
	public List<Promotion> findByDepartment (long academicYearId, long departmentId) throws DAOException;
	
	/**
	 * verifie le promotion d'une classe d'etude en une annee
	 * @param academicYearId
	 * @param studyClassId
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByStudyClass (long academicYearId, long studyClassId) throws DAOException;
	
	/**
	 * Renvoie la collection des promotion de la classe d'etude en une annee
	 * @param academicYearId
	 * @param studyClassId
	 * @return
	 * @throws DAOException
	 */
	public List<Promotion> findByStudyClass (long academicYearId, long studyClassId) throws DAOException;
	
	/**
	 * verifie s'il y a aumoin une prmotion pour l'annee academique en parametre
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYear (long academicYearId) throws DAOException{
		return this.check("academicYear", academicYearId);
	}
	/**
	 * Renvoie la collection des promotions d'une annee acaemique
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	public List<Promotion> findByAcademicYear(long academicYearId) throws DAOException;
	
	/**
	 * 
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	public List<Promotion> findByAcademicYear(AcademicYear academicYear) throws DAOException;
}
