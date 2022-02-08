/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.FeePromotion;

/**
 * @author Esaie MUHASA
 *
 */
public interface FeePromotionDao extends DAOInterface<FeePromotion> {
	
	/**
	 * Verifie s'il y a le fais academique pour un promotion
	 * @param promotionId
	 * @return
	 * @throws DAOException 
	 */
	public default boolean checkByPromotion (long promotionId) throws DAOException {
		return this.check("promotion", promotionId);
	}
	
	/**
	 * renvoie le frais qui sera payer par une promotion
	 * @param promotionId
	 * @return
	 * @throws DAOException
	 */
	public default FeePromotion findByPromotion (long promotionId)  throws DAOException {
		return this.find("promotion", promotionId);
	}
	
	/**
	 * verifie s'il y a aumoin une promotion, configurer pour payer les frais en parametre
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicFee (long academicFeeId) throws DAOException {
		return this.check("academicFee", academicFeeId);
	}
	
	/**
	 * Renvoie les promotions qui vont payer le frais academiquee dont l'ID est en parametre
	 * @param academicFeeId
	 * @return
	 * @throws DAOException
	 */
	public List<FeePromotion> findByAcademicFee (long academicFeeId) throws DAOException;
}
