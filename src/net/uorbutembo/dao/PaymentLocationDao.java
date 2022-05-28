/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.PaymentLocation;

/**
 * @author Esaie MUHASA
 *
 */
public interface PaymentLocationDao extends DAOInterface<PaymentLocation> {
	
	/**
	 * Verifie si le nom est deja utiliser par un autre lieu de payement
	 * @param name
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByName (String name) throws DAOException {
		return check("name", name);
	}
}
