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
	boolean check (long academicYearId, long departmentId, long studyClassId) throws DAOException;
	default boolean check (AcademicYear year, Department department, StudyClass study) throws DAOException{
		return check(year.getId(), department.getId(), study.getId());
	}
	
	/**
	 * Renvoie la promotion correspodant aux coordonee en parametre
	 * @param academicYear
	 * @param department
	 * @param studyClass
	 * @return
	 * @throws DAOException
	 */
	Promotion find (AcademicYear academicYear, Department department, StudyClass studyClass) throws DAOException;
	default Promotion find (long academicYearId, long departmentId, long studyClassId) throws DAOException{
		return find (
				getFactory().findDao(AcademicYearDao.class).findById(academicYearId),
				getFactory().findDao(DepartmentDao.class).findById(departmentId),
				getFactory().findDao(StudyClassDao.class).findById(studyClassId));
	}

	/**
	 * verifie le promotion d'un departement en une annee
	 * @param academicYearId
	 * @param departmentId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByDepartment (long academicYearId, long departmentId) throws DAOException;
	default boolean checkByDepartment (AcademicYear year, Department department) throws DAOException{
		return checkByDepartment(year.getId(), department.getId());
	}
	
	/**
	 * Renvoie la collection des promotion d'un departement en une annee
	 * @param academicYearId
	 * @param departmentId
	 * @return
	 * @throws DAOException
	 */
	List<Promotion> findByDepartment (long academicYearId, long departmentId) throws DAOException;
	default List<Promotion> findByDepartment (AcademicYear year, Department department) throws DAOException{
		return findByDepartment(year.getId(), department.getId());
	}
	
	/**
	 * verifie le promotion d'une classe d'etude en une annee
	 * @param academicYearId
	 * @param studyClassId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByStudyClass (long academicYearId, long studyClassId) throws DAOException;
	
	/**
	 * Renvoie la collection des promotion de la classe d'etude en une annee
	 * @param academicYearId
	 * @param studyClassId
	 * @return
	 * @throws DAOException
	 */
	List<Promotion> findByStudyClass (long academicYearId, long studyClassId) throws DAOException;
	
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
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	List<Promotion> findByAcademicYear(AcademicYear academicYear) throws DAOException;
	default List<Promotion> findByAcademicYear(long academicYearId) throws DAOException{
		return findByAcademicYear(getFactory().findDao(AcademicYearDao.class).findById(academicYearId));
	}
}
