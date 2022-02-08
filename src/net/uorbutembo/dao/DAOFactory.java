/**
 * 
 */
package net.uorbutembo.dao;

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
	 * @param <T>
	 * @param daoClass
	 * @return
	 * @throws DAOConfigException
	 */
	public <T extends DAOInterface<?>> T getDao (Class<T> daoClass) throws DAOConfigException;
	
	/**
	 * aliace de la method getDao, son s'il y a erreur lors de la recuperation du DAO,
	 * un NULL pointer est renvoyer
	 * @param <T>
	 * @param daoClass
	 * @return
	 */
	default <T extends DAOInterface<?>> T findDao (Class<T> daoClass) {
		try {
			return this.getDao(daoClass);
		} catch (DAOConfigException e) {
		}
		return null;
	}

}
