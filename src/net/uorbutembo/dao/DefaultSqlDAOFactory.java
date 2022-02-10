/**
 * 
 */
package net.uorbutembo.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import resources.net.uorbutembo.Config;

/**
 * @author Esaie MUHASA
 * factory d'acces a la base de donnee
 */
class DefaultSqlDAOFactory implements DAOFactory{
	
	private static final String 
			PROPERTIE_URL = "url",
			PROPERTIE_USER = "user",
			PROPERTIE_PASSWORD= "password",
			PROPERTIE_DRIVER = "driver";
	
	private static DefaultSqlDAOFactory daoFactory;
	private String url;
	private String user;
	private String password;
	
	//contiens les references des instances des implementations des interfaces du DAO
	protected Map<String, DAOInterface<?>> daos = new HashMap<String, DAOInterface<?>>();

	/**
	 * constructeur d'initialisation
	 * @throws DAOConfigException 
	 * @throws ClassNotFoundException 
	 */
	public DefaultSqlDAOFactory () throws DAOConfigException, ClassNotFoundException {
		
		if (daoFactory != null) {
			throw new DAOConfigException("Impossible d'instancier 2 fois le factory du DAO");
		}
		
		this.url = Config.find(PROPERTIE_URL);
		this.user = Config.find(PROPERTIE_USER);
		this.password = Config.find(PROPERTIE_PASSWORD);
		String driver = Config.find(PROPERTIE_DRIVER);
		Class.forName(driver);
	}
	
	/**
	 * Recuperation d'un instance de la connexion vers la base de donnees
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection () throws SQLException {
		if(this.user == null || this.user.trim().isEmpty()) {
			return DriverManager.getConnection(this.url);
		}
		
		Connection connection = DriverManager.getConnection(this.url, this.user, this.password);
		return connection;
	}
	
	/**
	 * @param <T>
	 * @param daoClass
	 * @return
	 * @throws DAOConfigException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T extends DAOInterface<?>> T getDao (Class<T> daoClass) throws DAOConfigException{
		Set<String> daoKeys = this.daos.keySet();
		for (String daoName : daoKeys) {
			if(daoName.equals(daoClass.getSimpleName())) {
				return (T) this.daos.get(daoName);
			}
		}
		
		Set<String> keys = Config.getInstance().getDictionnary().keySet();
		for (String key : keys) {
			if(key.equals(daoClass.getSimpleName())) {
				try {
					Class<?> c = Class.forName(Config.find(key));
					
					Constructor<?> cons = c.getConstructor(this.getClass());
					T instance = (T) cons.newInstance(this);
					this.daos.put(daoClass.getSimpleName(), instance);
					//instance.addListener(this);//on ecoute l'interface de la table
					return instance;
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | ClassCastException |
						InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new DAOConfigException(e.getMessage(), e);
				}
			}
		}
		
		throw new DAOConfigException("Aucune implementation n'a ete specifier dans le fichier de configuration du DAO pour l'interface \""+daoClass.getName()+"\"");
	}
	
}
