/**
 * 
 */
package net.uorbutembo.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Esaie MUHASA
 *
 */
public class Config {

	private static final String CONFIG_FILE = "config.properties";
	private static Config instance;
	private final Map<String, String> dictionnaty;

	/**
	 * constructeur d'initialisation
	 * 
	 * @param dictionnaty
	 */
	private Config(Map<String, String> dictionnaty) {
		this.dictionnaty = dictionnaty;
	}

	/**
	 * renvoie une instance du manager du fichier de configuration
	 * 
	 * @return
	 */
	public static Config getInstance () {
		if (instance == null) {
			final Map<String, String> config = new HashMap<>();
			try (InputStream fileConfig = new FileInputStream(new File(CONFIG_FILE))) {
				Properties properties = new Properties();
				properties.load(fileConfig);
				for (Object key : properties.keySet())
					config.put(key.toString(), properties.getProperty(key.toString()));
			} catch (IOException e) {
				throw new RuntimeException("Erreur survenue lors du chargement du fichier de configuration: " + CONFIG_FILE +" => "+e.getMessage());
			}
			instance = new Config(config);
		}
		return instance;
	}

	/**
	 * Recuperation du dictionnaire de elements du fichier de configuration
	 * 
	 * @return
	 */
	public final Map<String, String> getDictionnary() {
		return this.dictionnaty;
	}

	/**
	 * Renvoie la valeur associer au clee en parametre dans le dictionnaire du
	 * fichier de configuration
	 * 
	 * @param key
	 * @return
	 */
	public static final String find(String key) {
		return getInstance().get(key);
	}

	/**
	 * renvoie la valeur de la clee en parametre
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		return this.dictionnaty.get(key);
	}

}
