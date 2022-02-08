/**
 * 
 */
package net.uorbutembo.dao;

import java.lang.reflect.Constructor;

import resources.net.uorbutembo.Config;

/**
 * @author Esaie MUHASA
 *
 */
final class DAOLoader {
	
	private static DAOFactory dao;

	/**
	 * 
	 */
	private DAOLoader() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Utilitaire d'insatiation du factory DAO en fonction du fichier de configuration
	 * @return
	 * @throws DAOConfigException
	 */
	public static DAOFactory load () throws DAOConfigException {
		
		if(dao == null) {
			String className = Config.find("factory");
			
			try {
				Class<?> cl = Class.forName(className);
				Constructor<?> constructor = cl.getConstructor();
				
				dao = (DAOFactory) constructor.newInstance();
			} catch (Exception e) {
				throw new DAOConfigException(e.getMessage(), e);
			}
		}
		
		return dao;
	}

}
