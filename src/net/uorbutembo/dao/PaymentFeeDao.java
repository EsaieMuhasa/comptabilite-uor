/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;

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
	
}
