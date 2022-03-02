/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.DBEntity;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 * Specification pour faciliter le statistique de maniere globale
 */
public interface OverallStatistic <T extends DBEntity> {
	
	//year
	List <T> findByAcademicYear (long academicYearId, int limit, int offset) throws DAOException;
	default List <T> findByAcademicYear (AcademicYear academicYear, int limit, int offset) throws DAOException{
		return findByAcademicYear(academicYear.getId(), limit, offset);
	}
	List <T> findByAcademicYear (long academicYearId) throws DAOException;
	default List <T> findByAcademicYear (AcademicYear academicYear) throws DAOException{
		return findByAcademicYear(academicYear.getId());
	}
	
	default List <T> findByAcademicYear (long academicYearId, Date date) throws DAOException{
		return findByAcademicYear(academicYearId, date, date);
	}
	List <T> findByAcademicYear (long academicYearId, Date min, Date max) throws DAOException;
	default List <T> findByAcademicYear (AcademicYear academicYear, Date min, Date max) throws DAOException{
		return findByAcademicYear(academicYear.getId(), min, max);
	}
	
	boolean checkByAcademicYear (long academicYearId) throws DAOException;
	default boolean checkByAcademicYear (AcademicYear academicYear) throws DAOException{
		return this.checkByAcademicYear(academicYear.getId());
	}
	
	int countByAcademicYear (long academicYearId) throws DAOException;
	default int countByAcademicYear (AcademicYear academicYear) throws DAOException{
		return this.countByAcademicYear(academicYear.getId());
	}
	
	int countByAcademicYear (long academicYearId, Date min, Date max) throws DAOException;
	default int countByAcademicYear (long academicYearId, Date date) throws DAOException{
		return this.countByAcademicYear(academicYearId, date, date);
	}
	default int countByAcademicYear (AcademicYear academicYear, Date date) throws DAOException{
		return this.countByAcademicYear(academicYear.getId(), date, date);
	}
	default int countByAcademicYear (AcademicYear academicYear, Date min, Date max) throws DAOException{
		return this.countByAcademicYear(academicYear.getId(), min, max);
	}
	//== year
	
	
	//faculty
	List <T> findByFaculty (long  faculty, long year) throws DAOException;
	default List <T> findByFaculty (Faculty faculty, AcademicYear year) throws DAOException{
		return findByFaculty(faculty.getId(), year.getId());
	}
	
	List <T> findByFaculty (long  faculty, long year, Date min, Date max) throws DAOException;
	default List <T> findByFaculty (long  faculty, long year, Date date) throws DAOException{
		return findByFaculty(faculty, year, date, date);
	}
	default List <T> findByFaculty (Faculty  faculty, AcademicYear year, Date date) throws DAOException{
		return findByFaculty(faculty.getId(), year.getId(), date, date);
	}
	default List <T> findByFaculty (Faculty  faculty, AcademicYear year, Date min, Date max) throws DAOException{
		return findByFaculty(faculty.getId(), year.getId(), min, max);
	}
	
	int countByFaculty (long faculty, long year) throws DAOException;
	default int countByFaculty (Faculty faculty, AcademicYear year) throws DAOException{
		return countByFaculty(faculty.getId(), year.getId());
	}
	
	int countByFaculty (long  faculty, long year, Date min, Date max) throws DAOException;
	default int countByFaculty (long  faculty, long year, Date date) throws DAOException{
		return countByFaculty(faculty, year, date, date);
	}
	default int countByFaculty (Faculty  faculty, AcademicYear year, Date date) throws DAOException{
		return countByFaculty(faculty.getId(), year.getId(), date, date);
	}
	default int countByFaculty (Faculty  faculty, AcademicYear year, Date min, Date max) throws DAOException{
		return countByFaculty(faculty.getId(), year.getId(), min, max);
	}
	
	boolean checkByFaculty (long faculty, long year) throws DAOException;
	default boolean checkByFaculty (Faculty faculty, AcademicYear year) throws DAOException{
		return checkByFaculty(faculty.getId(), year.getId());
	}
	//== faculty
	
	
	//department
	List <T> findByDepartment (long departmentId, long academicYearId) throws DAOException;
	default List <T> findByDepartment (Department department, AcademicYear academicYear) throws DAOException{
		return findByDepartment(department.getId(), academicYear.getId());
	}
	
	List <T> findByDepartment (long departmentId, long academicYearId, Date min, Date max) throws DAOException;
	default List <T> findByDepartment (long departmentId, long academicYearId, Date date) throws DAOException{
		return findByDepartment(departmentId, academicYearId, date, date);
	}
	default List <T> findByDepartment (Department department, AcademicYear academicYear, Date date) throws DAOException{
		return findByDepartment(department.getId(), academicYear.getId(), date, date);
	}
	
	default List <T> findByDepartment (Department department, AcademicYear academicYear, int limit, int offset) throws DAOException{
		return findByDepartment(department.getId(), academicYear.getId(), limit, offset);
	}
	default List <T> findByDepartment (Department department, AcademicYear academicYear, int limit) throws DAOException{
		return findByDepartment(department, academicYear, limit, 0);
	}
	
	List <T> findByDepartment (long departmentId, long academicYearId, int limit, int offset) throws DAOException;
	default List <T> findByDepartment (long departmentId, long academicYearId, int limit) throws DAOException{
		return findByDepartment(departmentId, academicYearId, limit, 0);
	}
	
	int countByDepartment (long departmentId, long academicYearId) throws DAOException;
	default int countByDepartment (Department department, AcademicYear academicYear) throws DAOException{
		return countByDepartment(department.getId(), academicYear.getId());
	}
	int countByDepartment (long departmentId, long academicYearId, Date min, Date max) throws DAOException;
	default int countByDepartment (long departmentId, long academicYearId, Date date) throws DAOException{
		return countByDepartment(departmentId, academicYearId, date, date);
	}
	default int countByDepartment (Department department, AcademicYear academicYear, Date date) throws DAOException{
		return countByDepartment(department.getId(), academicYear.getId(), date, date);
	}
	
	boolean checkByDepartment (long departmentId, long academicYearId) throws DAOException;
	default boolean checkByDepartment (Department department, AcademicYear academicYear) throws DAOException{
		return checkByDepartment(department.getId(), academicYear.getId());
	}
	//==department
	
	//studyclass
	List <T> findByStudyClass (long studyClassId, long yearId, int limit, int offset) throws DAOException;
	default List <T> findByStudyClass (StudyClass studyClass, AcademicYear year, int limit, int offset) throws DAOException{
		return findByStudyClass(studyClass.getId(), year.getId(), limit, offset);
	}
	
	List <T> findByStudyClass (long studyClassId, long yearId) throws DAOException;
	default List <T> findByStudyClass (StudyClass studyClass, AcademicYear year) throws DAOException{
		return findByStudyClass(studyClass.getId(), year.getId());
	}
	List <T> findByStudyClass (long studyClassId, long yearId, Date min, Date max) throws DAOException;
	default List <T> findByStudyClass (long studyClassId, long yearId, Date date) throws DAOException{
		return findByStudyClass(studyClassId, yearId, date, date);
	}
	
	int countByStudyClass (long studyClassId, long yearId) throws DAOException;
	default int countByStudyClass (StudyClass studyClass, AcademicYear year) throws DAOException{
		return countByStudyClass(studyClass.getId(), year.getId());
	}
	
	boolean checkByStudyClass (long studyClass, long yearId) throws DAOException;
	default boolean checkByStudyClass (StudyClass studyClass, AcademicYear year) throws DAOException {
		return checkByStudyClass(studyClass.getId(), year.getId());
	}
	//==studyclass
	
	//promotion
	List <T> findByPromotion (long promotionId, int limit, int offset) throws DAOException;
	default List <T> findByPromotion (Promotion promotion, int limit, int offset) throws DAOException{
		return findByPromotion(promotion.getId(), limit, offset);
	}
	
	List <T> findByPromotion (long promotionId) throws DAOException;
	default List <T> findByPromotion (Promotion promotion) throws DAOException{
		return findByPromotion(promotion.getId());
	}
	
	List <T> findByPromotion (long promotion, Date min, Date max) throws DAOException;
	default List <T> findByPromotion (Promotion promotion, Date min, Date max) throws DAOException{
		return findByPromotion(promotion.getId(), min, max);
	}
	default List <T> findByPromotion (Promotion promotion, Date date) throws DAOException{
		return findByPromotion(promotion.getId(), date, date);
	}
	default List <T> findByPromotion (long promotion, Date date) throws DAOException{
		return findByPromotion(promotion, date, date);
	}
	
	int countByPromotion (long promotion) throws DAOException;
	default int countByPromotion (Promotion promotion) throws DAOException {
		return countByPromotion(promotion.getId());
	}
	int countByPromotion (long promotion, Date min, Date max) throws DAOException;
	default int countByPromotion (long promotion, Date date) throws DAOException {
		return countByPromotion(promotion, date, date);
	}
	default int countByPromotion (Promotion promotion, Date min, Date max) throws DAOException {
		return countByPromotion(promotion.getId(), min, max);
	}
	
	boolean checkByPromotion (long promotion) throws DAOException;
	default boolean checkByPromotion (Promotion promotion) throws DAOException {
		return checkByPromotion(promotion.getId());
	}
	//==promotion
}
