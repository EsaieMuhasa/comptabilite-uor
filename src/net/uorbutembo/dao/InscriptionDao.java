/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
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
	public List<Inscription> findByStudent (long studentId) throws DAOException;
	
	/**
	 * Renvoie l'inscription de l'etudiant en parametre
	 * @param student
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByStudent (Student student) throws DAOException;
	
	/**
	 * Compte le nombre d'inscription dans une promotion
	 * @param promotionId
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> countByPromotion (long promotionId) throws DAOException;
	
	/**
	 * Renvoie la collection des inscriptiosn deja faitement dans une promotion
	 * @param promotionId
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByPromotion (long promotionId) throws DAOException;
	
	/**
	 * Renvoie la collection des etudiants de la promotion en parametre
	 * @param promotion
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByPromotion (Promotion promotion) throws DAOException;
	
	/**
	 * Renvoie la collection des inscriptions dans une promotions
	 * @param promotionId
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByPromotion (long promotionId, int limit, int offset) throws DAOException;
	
	/**
	 * Renvoie la collection des etudiants de la promotions en parametre 
	 * @param promotion
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByPromotion (Promotion promotion, int limit, int offset) throws DAOException;
	
	/**
	 * Verifie s'il y a amoin un etudiant dans dans la dite faculte pour l'annee academique en 2 eme parametre
	 * @param facultyId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByFaculty (long facultyId, long academicYearId) throws DAOException;
	
	/**
	 * Renvoie la collection des inscrits pour la faculte en premer parametre, pour l'annee en deuxieme parametre
	 * @param facultyId
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByFaculty (long facultyId, long academicYearId) throws DAOException;
	
	/**
	 * Renvoie tout les etudiants de la facultee en parametre, pour l'annee academique en 2 eme parametre
	 * @param faculty
	 * @param academicYear
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByFaculty (Faculty faculty, AcademicYear academicYear) throws DAOException;
	
	/**
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	public int countByFaculty (long facultyId, int academicYearId) throws DAOException;
}
