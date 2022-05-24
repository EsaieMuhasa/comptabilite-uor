/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 *
 */
public interface PaymentFeeDao extends DAOInterface <PaymentFee>, OverallStatistic<PaymentFee>{
	
	/**
	 * Verifie si un inscrit a deja payer les fais universitaire aumoin une fois
	 * @param inscriptionId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByInscription (long inscriptionId) throws DAOException {
		return this.check("inscription", inscriptionId);
	}
	default boolean checkByInscription (Inscription inscription) throws DAOException {
		return this.check("inscription", inscription.getId());
	}
	
	/**
	 * Renvoie la collection des payements des frais universitaire par un inscrit
	 * @param inscription
	 * @return
	 * @throws DAOException
	 */
	List<PaymentFee> findByInscription (Inscription inscription)  throws DAOException;
	default List<PaymentFee> findByInscription (long inscriptionId)  throws DAOException {
		return findByInscription(getFactory().findDao(InscriptionDao.class).findById(inscriptionId));
	}
	
	/**
	 * renvoie le solde du parcours universitaire d'un etudiant
	 * @param student
	 * @return
	 * @throws DAOException
	 */
	double getSoldByStudent (long student) throws DAOException;
	default double getSoldByStudent (Student student) throws DAOException {
		return getSoldByStudent(student.getId());
	}
	
	/**
	 * Renvoie le solde d'une etudant deja inscrit dans une promotion
	 * @param inscription
	 * @return
	 * @throws DAOException
	 */
	double getSoldByInscription (long inscription) throws DAOException;
	default double getSoldByInscription (Inscription inscription) throws DAOException {
		return getSoldByInscription(inscription.getId());
	}
	
	/**
	 * Renvoie le sold des etudiant d'une promotion
	 * @param promotion
	 * @return
	 * @throws DAOException
	 */
	double getSoldByPromotion (long promotion) throws DAOException;
	default double getSoldByPromotion (Promotion promotion) throws DAOException {
		return getSoldByPromotion(promotion.getId());
	}
	
	/**
	 * Renvoie le solde des etudiants d'un ensemble des promotions
	 * @param studyClass
	 * @param departments
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByPromotions (long studyClass, long [] departments, long year) throws DAOException;
	default double getSoldByPromotions (StudyClass studyClass, Department [] departments, AcademicYear year) throws DAOException {
		long [] deps = new long [departments.length];
		for (int i = 0; i < deps.length; i++)
			deps[i] = departments[i].getId();
		return getSoldByPromotions(studyClass.getId(), deps, year.getId());
	}
	
	/**
	 * Renvoie le solde des toutes les classes d'etudes d'une promotion x, pour un departement y
	 * en une annee academique
	 * @param studyClasses
	 * @param department
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByPromotions (long [] studyClasses, long department, long year) throws DAOException;
	default double getSoldByPromotions (StudyClass [] studyClasses, Department department, AcademicYear year) throws DAOException {
		long [] scs = new long [studyClasses.length];
		for (int i = 0; i < scs.length; i++)
			scs[i] = studyClasses[i].getId();
		return getSoldByPromotions(scs, department.getId(), year.getId());
	}
	
	/**
	 * Renvoie le solde des promotions selectionner
	 * @param promotions
	 * @return
	 * @throws DAOException
	 */
	double getSoldByPromotions (long ...promotions)  throws DAOException;
	default double getSoldByPromotions (Promotion ...promotions)  throws DAOException {
		long [] proms = new long [promotions.length];
		for (int i = 0; i < proms.length; i++)
			proms[i] = promotions[i].getId();
		return getSoldByPromotions(proms);
	}
	
	/**
	 * renvoie le solde des etudiant d'une faculte en une annees academique
	 * @param faculty
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByFaculty (long faculty, long year) throws DAOException;
	default double getSoldByFaculty (Faculty faculty, AcademicYear year) throws DAOException {
		return getSoldByFaculty(faculty.getId(), year.getId());
	}
	
	/**
	 * Renvoie le solde des etudiants d'une classe d'etude en une annee academique
	 * @param studyClass
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByStudyClass (long studyClass, long year) throws DAOException;
	default double getSoldByStudyClass (StudyClass studyClass, AcademicYear year) throws DAOException {
		return getSoldByStudyClass(studyClass.getId(), year.getId());
	}
	
	/**
	 * renvoie le solde des etudiants d'un department en une annee academique
	 * @param department
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByDepartment (long department, long year) throws DAOException;
	default double getSoldByDepartment (Department department, AcademicYear year) throws DAOException {
		return getSoldByDepartment(department.getId(), year.getId());
	}
	
	/**
	 * Renvoie le solde de tout les etudiant pour une annees academique
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByAcademicYear (long year) throws DAOException;
	default double getSoldByAcademicYear (AcademicYear year) throws DAOException {
		return getSoldByAcademicYear(year.getId());
	}
	
}
