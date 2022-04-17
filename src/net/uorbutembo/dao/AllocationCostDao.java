/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;

/**
 * @author Esaie MUHASA
 *
 */
public interface AllocationCostDao extends DAOInterface<AllocationCost> {
	
	/**
	 * verifie la validite de l'unicitee
	 * @param annualSpendId
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	boolean check (long annualSpendId, long academicFeeId) throws DAOException;
	
	/**
	 * Renvoie la portion allouer a la rubrique budgetaire d'une annee precise
	 * @param annualSpendId
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	AllocationCost find (long annualSpendId, long academicFeeId) throws DAOException;
	
	/**
	 * Renvoie la l'occurence qui represente la rubrique budgetarie
	 * @param annualSpend
	 * @param academicFee
	 * @return
	 * @throws DAOException
	 */
	AllocationCost find (AnnualSpend annualSpend, AcademicFee academicFee) throws DAOException;
	
	/**
	 * verification de l'existance de 
	 * @param annualSpendId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAnnualSpend (long annualSpendId) throws DAOException{
		return this.check("annualSpend", annualSpendId);
	}
	
	/**
	 * Renvoie une collection des montants affecter une depense annuel
	 * @param annualSpendId
	 * @return
	 * @throws DAOException
	 */
	List<AllocationCost> findByAnnualSpend (long annualSpendId) throws DAOException;
	
	/**
	 * Verifie s'il y a au aumoin une occurence qui fait reference.
	 * Facilte la verification, pour savoir si la repartition des frais a deja eux lieux
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicFee (long academicFeeId) throws DAOException {
		return this.check("academicFee", academicFeeId);
	}
	
	/**
	 * Renvoie la repartion des frais academique
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	List<AllocationCost> findByAcademicFee (long academicFeeId) throws DAOException;
	
	/**
	 * Renvoie la collection de la repartition des frais academique d'une annee
	 * @param academicFee
	 * @return
	 * @throws DAOException
	 */
	List<AllocationCost> findByAcademicFee (AcademicFee academicFee) throws DAOException;
	
	/**
	 * Compte le nombre d'occurence qui font referece aux frais universitaires.
	 * compte le nombre de rubriques qui touche le frais univeritaire
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicFee (long academicFeeId) throws DAOException ;

}
