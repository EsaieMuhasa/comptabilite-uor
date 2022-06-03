/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.PaymentLocation;
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
	 * Verifie s'il y a un payment correspondant au numero de recu
	 * @param receiptNumber
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByReceiptNumber (String receiptNumber) throws DAOException {
		if(receiptNumber == null || receiptNumber.trim().length() == 0)
			return false;
		return check("receiptNumber", receiptNumber);
	}
	
	default boolean checkByReceiptNumber (String receiptNumber, long id) throws DAOException {
		if(receiptNumber == null || receiptNumber.trim().length() == 0)
			return false;
		if(id <= 0)
			return check("receiptNumber", receiptNumber);
		return check("receiptNumber", receiptNumber, id);
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
	 * Comptage des operations de payement faite dans un lieux
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	int countByLocation (long location) throws DAOException;
	default int countByLocation (PaymentLocation location) throws DAOException {
		return countByLocation(location.getId());
	}
	
	/**
	 * comptage des operations de payement faite dans un lieux
	 * @param location
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	int countByLocation (long location, long year) throws DAOException;
	default int countByLocation (PaymentLocation location, AcademicYear year) throws DAOException {
		return countByLocation(location.getId(), year.getId());
	}
	
	/**
	 * verification de l'existance des operations dans un lieux
	 * @param location
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLocation (long location, long year) throws DAOException;
	default boolean checkByLocation (PaymentLocation location, AcademicYear year) throws DAOException {
		return checkByLocation(location.getId(), year.getId());
	}
	
	/**
	 * verification des operations faite dans un lieux, en faisant un saut dans les resultat.
	 * @param location
	 * @param year
	 * @param offset (le OFFSET dela clause LIMIT dans une requette SQL)
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLocation (long location, long year, int offset) throws DAOException;
	default boolean checkByLocation (PaymentLocation location, AcademicYear year, int offset) throws DAOException {
		return checkByLocation(location.getId(), year.getId(), offset);
	}
	
	List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year) throws DAOException;
	default List<PaymentFee> findByLocation (long location, long year) throws DAOException{
		return findByLocation(
				getFactory().findDao(PaymentLocationDao.class).findById(location), 
				getFactory().findDao(AcademicYearDao.class).findById(year)
			);
	}
	
	/**
	 * selection d'une partie (intervale de selection) des operations fites dans une localisation (lieux de payement)
	 * @param location
	 * @param year
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, int limit, int offset) throws DAOException;
	
	/**
	 * selelction des operations faite pour une intervale de temps dans un lieux de payement
	 * @param location leux de perception de l'argent
	 * @param year annee academique concerner
	 * @param min la date minimum
	 * @param max la date maximum
	 * @param limit nombre d'element a selectionner
	 * @param offset nombre d'element a sauter dans la requette de selection
	 * @return
	 * @throws DAOException
	 */
	List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, Date min, Date max, int limit, int offset) throws DAOException;
	List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, Date min, Date max) throws DAOException;
	default List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, Date min, Date max, int limit) throws DAOException {
		return findByLocation(location, year, min, max, limit, 0);
	}
	default List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, Date date) throws DAOException {
		return findByLocation(location, year, date, date);
	}
	
	default List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, Date date, int limit, int offset) throws DAOException {
		return findByLocation(location,  year, date, date , limit, offset);
	}
	
	/**
	 * selection des operations faite dans un lieux.
	 * @param location
	 * @param year
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	default List<PaymentFee> findByLocation (PaymentLocation location, AcademicYear year, int limit) throws DAOException {
		return findByLocation(location, year, limit, 0);
	}
	
	/**
	 * selection des operations effectuer dans un lieux de payement
	 * @param location
	 * @param year
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	default List<PaymentFee> findByLocation (long location, long year, int limit) throws DAOException {
		return findByLocation(
				getFactory().findDao(PaymentLocationDao.class).findById(location),
				getFactory().findDao(AcademicYearDao.class).findById(year), limit, 0
			);
	}
	
	/**
	 * aliace de la methode 
	 * <strong>{@link findByLocation(PaymentLocation location, AcadmicYear year, int limit, int offset)}</strong>
	 * @param location
	 * @param year
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	default List<PaymentFee> findByLocation (long location, long year, int limit, int offset) throws DAOException{
		return findByLocation(
				getFactory().findDao(PaymentLocationDao.class).findById(location),
				getFactory().findDao(AcademicYearDao.class).findById(year), limit, offset
			);
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
	 * Renvoie le solde de tout les etudiants pour une annees academique
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByAcademicYear (long year) throws DAOException;
	default double getSoldByAcademicYear (AcademicYear year) throws DAOException {
		return getSoldByAcademicYear(year.getId());
	}
	
	/**
	 * renvoie le solde deja payer en un lieux (prevue pour la preception des frais universitaire)
	 * pour une annee academique
	 * @param location
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	double getSoldByLocation (long location, long year) throws DAOException;
	default double getSoldByLocation (PaymentLocation location, AcademicYear year) throws DAOException {
		return getSoldByLocation(location.getId(), year.getId());
	}
	
}
