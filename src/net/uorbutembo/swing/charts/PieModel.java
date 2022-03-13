/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface PieModel {
	
	void setTitle (String title);
	String getTitle ();	
	
	//adding parts
	void addPart (PiePart part);
	void addPart (int index, PiePart part);
	void addParts (PiePart ...parts);
	// --
	
	/**
	 * Renvoie la valeur max. l'equivalent de 360°
	 * @return
	 */
	double getMax ();
	void setMax (double max);
	
	double getRealMax();
	String getSuffix ();
	
	/**
	 * @param index
	 * @return
	 */
	PiePart getPartAt (int index);
	
	/**
	 * Renvoei les pourcentages de l'indem a l'index
	 * @param index
	 * @return
	 */
	double getPercentOf (int index);
	
	/**
	 * Renvoie les pourcentage du part en parametre, si cel-ci ce trouve dans le model
	 * @param part
	 * @return
	 */
	double getPercentOf (PiePart part);
	
	/**
	 * renvoie l'object le part qui a une reference vers l'object en parametre
	 * @param data
	 * @return
	 */
	PiePart findByData (Object data);
	
	/**
	 * Renvoie le nombre des parts
	 * @return
	 */
	int getCountPart();
	
	/**
	 * recuperation du part dont le nom est en parametre
	 * @param name
	 * @return
	 */
	PiePart getPartByName (String name);
	
	/**
	 * @param index
	 */
	void removePartAt (int index);
	void removeAll ();
	PiePart [] getParts ();
	
	void addListener (PieModelListener listener);
	void removeListener (PieModelListener listener);
}
