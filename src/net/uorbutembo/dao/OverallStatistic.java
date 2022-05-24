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
public interface OverallStatistic <T extends DBEntity> extends BaseStatistic <T> {
	
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

	List<T> findByPromotions (long studyClass, long [] departments, long year, int limit, int offset);
	default List<T> findByPromotions (StudyClass studyClass, Department [] departments, AcademicYear year, int limit, int offset) {
		long [] deps = new long[departments.length]; 
		for (int i = 0; i < deps.length; i++) 
			deps[i] = departments[i].getId();
		return findByPromotions(studyClass.getId(), deps, year.getId(), limit, offset);
	}
	
	List<T> findByPromotions (long [] studyClasses, long department, long year, int limit, int offset);
	default List<T> findByPromotions (StudyClass [] studyClasses, Department department, AcademicYear year, int limit, int offset) {
		long [] scs = new long[studyClasses.length]; 
		for (int i = 0; i < scs.length; i++) 
			scs[i] = studyClasses[i].getId();
		return findByPromotions(scs, department.getId(), year.getId(), limit, offset);
	}
	
	List<T> findByPromotions (long [] prmotions, int limit, int offset);
	default List<T> findByPromotions (Promotion [] promotions, int limit, int offset) {
		long [] proms = new long[promotions.length]; 
		for (int i = 0; i < proms.length; i++) 
			proms[i] = promotions[i].getId();
		return findByPromotions(proms, limit, offset);
	}
	
	int countByPromotions (long studyClass, long [] departments, long year);
	default int countByPromotions (StudyClass studyClass, Department [] departments, AcademicYear year) {
		long [] deps = new long[departments.length]; 
		for (int i = 0; i < deps.length; i++) 
			deps[i] = departments[i].getId();
		return countByPromotions(studyClass.getId(), deps, year.getId());
	}
	
	int countByPromotions (long [] studyClasses, long department, long year);
	default int countByPromotions (StudyClass [] studyClasses, Department department, AcademicYear year) {
		long [] scs = new long[studyClasses.length]; 
		for (int i = 0; i < scs.length; i++) 
			scs[i] = studyClasses[i].getId();
		return countByPromotions(scs, department.getId(), year.getId());
	}
	
	int countByPromotions (long... promotions);
	default int countByPromotions (Promotion... promotions) {
		long [] proms = new long[promotions.length]; 
		for (int i = 0; i < proms.length; i++) 
			proms[i] = promotions[i].getId();
		return countByPromotions(proms);
	}
	
	boolean checkByPromotions (long studyClass, long [] departments, long year);
	default boolean checkByStudyClass (StudyClass studyClass, Department [] departments, AcademicYear year) {
		long [] deps = new long[departments.length]; 
		for (int i = 0; i < deps.length; i++) 
			deps[i] = departments[i].getId();
		return checkByPromotions(studyClass.getId(), deps, year.getId());
	}
	
	boolean checkByPromotions (long [] studyClasses, long department, long year);
	default boolean checkByPromotions (StudyClass [] studyClasses, Department department, AcademicYear year) {
		long [] scs = new long[studyClasses.length]; 
		for (int i = 0; i < scs.length; i++) 
			scs[i] = studyClasses[i].getId();
		return checkByPromotions(scs, department.getId(), year.getId());
	}
	
	boolean checkByPromotions (long... promotions);
	default boolean checkByPromotions (Promotion... promotions) {
		long [] proms = new long[promotions.length]; 
		for (int i = 0; i < proms.length; i++) 
			proms[i] = promotions[i].getId();
		return checkByPromotions(proms);
	}
	//==promotion
}
