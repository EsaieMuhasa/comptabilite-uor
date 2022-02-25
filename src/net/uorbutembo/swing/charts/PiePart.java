package net.uorbutembo.swing.charts;

import java.awt.Color;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public interface PiePart {
	
	/**
	 * renvoie la couleur d'arriere plans du Parts
	 * @return
	 */
	Color getBackgroundColor ();
	void setBackgroundColor (Color backgroundColor);
	
	/**
	 * Modification de la couleur du premier plan
	 * @return
	 */
	Color getForegrounColor ();
	void setForegroundColor (Color foregroundColor);
	
	/**
	 * Recuperation de la couleur de bordure
	 * @return
	 */
	Color getBorderColor ();
	void setBorderColor (Color borderColor);	
	
	/**
	 * renvoie la valeur exacte du part
	 * @return
	 */
	double getValue ();
	void setValue (double value);
	
	/**
	 * Renvoie le label du part
	 * @return
	 */
	String getLabel ();
	void setLabel (String label);
	
	/**
	 * Renvoi le nom du part
	 * il est preferable de ce nom sois unique dans la collection des parts dans le model
	 * @return
	 */
	String getName ();
	void setName (String name);
	
	/**
	 * Es-ce que le part est visible???
	 * @return
	 */
	boolean isVisible ();
	void setVisible(boolean visible);
	
	void addListener (PiePartListener listener);
	void removeListener (PiePartListener listener);
}
