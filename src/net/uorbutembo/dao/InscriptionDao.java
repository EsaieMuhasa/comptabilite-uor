/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;

/**
 * @author Esaie MUHASA
 *
 */
public interface InscriptionDao extends DAOInterface<Inscription> {
	
	/**
	 * Verifie si letudiant s'est deja inscrit aumoin une fois
	 * @param studentId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByStudent (long studentId)  throws DAOException {
		return this.check("student", studentId);
	}
	
	/**
	 * Verifie s'il y a aumon un etudiant qui s'ait deja inscrit dans une promotion 
	 * @param promotionId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByPromotion (long promotionId)  throws DAOException {
		return this.check("promotion", promotionId);
	}
	
	/**
	 * Renvoie le parcour universitaire d'un etudiant
	 * @param studentId
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByStudent (long studentId) throws DAOException;
	
	/**
	 * Renvoie l'inscription de l'etudiant en parametre
	 * @param student
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByStudent (Student student) throws DAOException;
	
	/**
	 * Compte le nombre d'inscription dans une promotion
	 * @param promotionId
	 * @return
	 * @throws DAOException
	 */
	int countByPromotion (long promotionId) throws DAOException;

	
	/**
	 * Renvoie la collection des etudiants de la promotion en parametre
	 * @param promotion
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByPromotion (Promotion promotion) throws DAOException;
	default List<Inscription> findByPromotion (long promotionId) throws DAOException{
		return findByPromotion(this.getFactory().findDao(PromotionDao.class).findById(promotionId));
	}
	
	/**
	 * Renvoie la collection des etudiants de la promotions en parametre 
	 * @param promotion
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByPromotion (Promotion promotion, int limit, int offset) throws DAOException;
	default List<Inscription> findByPromotion (long promotionId, int limit, int offset) throws DAOException{
		return findByPromotion(this.getFactory().findDao(PromotionDao.class).findById(promotionId), limit, offset);
	}
	
	/**
	 * Verifie s'il y a amoin un etudiant dans dans la dite faculte pour l'annee academique en 2 eme parametre
	 * @param facultyId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByFaculty (long facultyId, long academicYearId) throws DAOException;
	default boolean checkByFaculty (Faculty faculty, AcademicYear academicYear) throws DAOException{
		return checkByFaculty(faculty.getId(), academicYear.getId());
	}
	
	/**
	 * Renvoie la collection des inscrits pour la faculte en premer parametre, pour l'annee en deuxieme parametre
	 * @param facultyId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByFaculty (long facultyId, long academicYearId) throws DAOException;
	default List<Inscription> findByFaculty (Faculty faculty, AcademicYear academicYear) throws DAOException{
		return findByFaculty(faculty.getId(), academicYear.getId());
	}
	
	/**
	 * comptage des inscriptions dans une faculte pour une annee academique
	 * @param facultyId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	int countByFaculty (long facultyId, long academicYearId) throws DAOException;
	default int countByFaculty (Faculty faculty, AcademicYear academicYear) throws DAOException{
		return countByFaculty(faculty.getId(), academicYear.getId());
	}
	
	/**
	 * Renvoie les etudiants inscrit dans un department pour une annee academique
	 * @param departmentId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByDepartment (long departmentId, long academicYearId) throws DAOException;
	default List<Inscription> findByDepartment (Department department, AcademicYear academicYear) throws DAOException{
		return findByDepartment(department.getId(), academicYear.getId());
	}
	
	/**
	 * verification de l'existance des inscriptions dans un departement pour une annee
	 * @param departmentId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByDepartment (long departmentId, long academicYearId) throws DAOException;
	default boolean checkByDepartment (Department department, AcademicYear academicYear) throws DAOException{
		return checkByDepartment(department.getId(), academicYear.getId());
	}
	
	/**
	 * Comptage de etudiant d'un departement pour une annee academique
	 * @param departmentId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	int countByDepartment (long departmentId, long academicYearId) throws DAOException;
	default int countByDepartment (Department department, AcademicYear academicYear) throws DAOException{
		return countByDepartment(department.getId(), academicYear.getId());
	}
	
	/**
	 * Comptage des inscriptions d'une annee academique
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long academicYearId) throws DAOException;
	default int countByAcademicYear (AcademicYear academicYear) throws DAOException{
		return this.countByAcademicYear(academicYear.getId());
	}
	
	/**
	 * verification s'il y a amoin une inscription pour l'annee academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAcademicYear (long academicYearId) throws DAOException;
	default boolean checkByAcademicYear (AcademicYear academicYear) throws DAOException{
		return this.checkByAcademicYear(academicYear.getId());
	}
	
	/**
	 * renvoie les inscriptions d'une annee academique
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	List <Inscription> findByAcademicYear (AcademicYear academicYear) throws DAOException;
	default List<Inscription> findByAcademicYear (long academicYearId) throws DAOException {
		return this.findByAcademicYear(this.getFactory().findDao(AcademicYearDao.class).findById(academicYearId));
	}
	
	/**
	 * Renvoie les inscriptions d'une annee academique pour l'intervale de selection choisie
	 * @param academicYear
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByAcademicYear (AcademicYear academicYear, int limit, int offset) throws DAOException;
	default List<Inscription> findByAcademicYear (long academicYearId, int limit, int offset) throws DAOException {
		return this.findByAcademicYear(this.getFactory().findDao(AcademicYearDao.class).findById(academicYearId), limit, offset);
	}
	
	/**
	 * Pou effectuer une recherche apoximative dans la table de instription
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> search (String ...value) throws DAOException;
	
	/**
	 * pour effectuer une rechercher.
	 * les inscriptions prise en consideration sont ceux de l'anne academique enpremier parametre
	 * @param year
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> search (AcademicYear year, String ...value) throws DAOException;
	
	
	/**
	 * Effectuer une recherche aproximative
	 * Uniquement les inscriptions de la promotion en premier parametre sons pris en consideration
	 * @param promotion
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> search (Promotion promotion, String ...value) throws DAOException;
}
