/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.User;

/**
 * @author Esaie MUHASA
 *
 */
interface UserDao <T extends User> extends DAOInterface<T> {
	
	/**
	 * mis en jour d'adresse de la photo de profile
	 * @param picture
	 * @param id
	 * @throws DAOException
	 */
	public void updatePicture (String picture, long id) throws DAOException;
	
	/**
	 * 
	 * @param telephone
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByTelephone (String telephone) throws DAOException {
		return this.check("telephone", telephone);
	}
	
	/**
	 * 
	 * @param telephone
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByTelephone (String telephone, long id) throws DAOException {
		return this.check("telephone", telephone, id);
	}
	
	/**
	 * 
	 * @param email
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByEmail (String email) throws DAOException {
		return this.check("email", email);
	}
	
	/**
	 * 
	 * @param email
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByEmail (String email, long id) throws DAOException {
		return this.check("email", email, id);
	}
}
