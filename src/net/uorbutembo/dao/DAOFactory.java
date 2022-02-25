/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
public interface DAOFactory {
	
	/**
	 * renvoie l'instance vers le manager des donnees
	 * selon la configuration du fichier config.properties
	 * @return
	 */
	public static DAOFactory getInstance ()  throws DAOConfigException{
		return DAOLoader.load();
	}
	

	/** 
	 * renvoie l'implemtation de l'interface, du pour l'interface DAO en parametre.
	 * Si aucune implemetation n'existe selon la configuration, alors une exception est lever
	 * @param <H>
	 * @param <T>
	 * @param daoClass
	 * @return
	 * @throws DAOConfigException
	 */
	public <H extends DBEntity, T extends DAOInterface<H>> T getDao (Class<T> daoClass) throws DAOConfigException;
	
	/**
	 * aliace de la method getDao, son s'il y a erreur lors de la recuperation du DAO,
	 * un NULL pointer est renvoyer 
	 * @param <H>
	 * @param <T>
	 * @param daoClass
	 * @return
	 */
	default <H extends DBEntity, T extends DAOInterface<H>> T findDao (Class<T> daoClass) {
		try {
			return this.getDao(daoClass);
		} catch (DAOConfigException e) {
			System.out.printf("\n> Error: %s\n", e.getMessage());
		}
		return null;
	}

}
