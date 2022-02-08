/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.Inscription;

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
	 * Renvoie la collection des inscriptions dans une promotions
	 * @param promotionId
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	public List<Inscription> findByPromotion (long promotionId, int limit, int offset) throws DAOException;
}
