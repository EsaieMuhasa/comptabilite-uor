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
public interface InscriptionDao extends DAOInterface<Inscription>, OverallStatistic<Inscription> {
	
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
