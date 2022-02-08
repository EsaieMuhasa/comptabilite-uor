/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.PaymentFee;

/**
 * @author Esaie MUHASA
 *
 */
public interface PaymentFeeDao extends DAOInterface <PaymentFee> {
	
	/**
	 * Verifie si un inscrit a deja payer les fais universitaire aumoin une fois
	 * @param inscriptionId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByInscription (long inscriptionId) throws DAOException {
		return this.check("inscription", inscriptionId);
	}
	
	/**
	 * Renvoie la collection des payements des frais universitaire par un inscrit
	 * @param inscriptionId
	 * @return
	 * @throws DAOException
	 */
	public List<PaymentFee> findByInscription (long inscriptionId)  throws DAOException;
}
