/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;

/**
 * @author Esaie MUHASA
 *
 */
public interface InscriptionDao extends DAOInterface<Inscription>, OverallStatistic<Inscription> {
	
	/**
	 * Mise en jour la photo d'un inscrit
	 * @param id
	 * @param picture
	 */
	void updatePicture(long id, String picture);
	
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
	 * verifie si l'etudiant est deja inscrit pour l'annee academique
	 * @param student
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	boolean checkByStudent(long student, long year) throws DAOException;
	default boolean checkByStudent(Student student, AcademicYear year) throws DAOException {
		return checkByStudent(student.getId(), year.getId());
	}
	
	/**
	 * Renvoie l'inscription d'un etudiant pour une excercice academique
	 * @param student
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	Inscription findByStudent (Student student, AcademicYear year) throws DAOException;
	
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
	 * Renvoie le parcours universitarie d'un etudiant
	 * @param student
	 * @return
	 * @throws DAOException
	 */
	List<Inscription> findByStudent (Student student) throws DAOException;
	default List<Inscription> findByStudent (long studentId) throws DAOException {
		return findByStudent(getFactory().findDao(StudentDao.class).findById(studentId));
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
