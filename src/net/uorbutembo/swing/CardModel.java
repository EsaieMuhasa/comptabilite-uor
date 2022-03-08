/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;

/**
 * @author Esaie MUHASA
 * Model des donnees d'un carde
 */
public interface CardModel {
	
	/**
	 * Renvoie la valeur du card
	 * @return
	 */
	String getValue ();
	
	/**
	 * Renvoie le titre principale du card
	 * @return
	 */
	String getTitle ();
	
	/**
	 * Renvoie le sous-titre du card 
	 * @return
	 */
	String getInfo ();
	
	/**
	 * Renvoie l'adresse vers l'icone du carde
	 * @return
	 */
	String getIcon ();
	
	/**
	 * Renvoie la couleur des texte
	 * @return
	 */
	Color getForegroundColor ();
	
	/**
	 * Modification de la coleur de text
	 * @param color
	 */
	void setForegroundColor (Color color);
	
	/**
	 * Renvoie la couleur d'arriere plan du card
	 * @return
	 */
	Color getBackgroundColor ();
	
	/**
	 * modification la couleur d'arriere plan
	 * @param color
	 */
	void setBackgroundColor (Color color);

}
