/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface Axis extends ChartData{
	
	/**
	 * renvoie la graduation qui ce trouve suste apres celuis qui est en parametre
	 * @param graduation
	 * @return
	 */
	AxisGraduation getAfter (AxisGraduation graduation) throws IndexOutOfBoundsException;
	
	boolean checkBefor (AxisGraduation graduation);
	boolean checkAfter (AxisGraduation graduation);
	
	/**
	 * Renvoie la graduation qui est juste avant celui qui est en prametre
	 * @param graduation
	 * @return
	 */
	AxisGraduation getBefor (AxisGraduation graduation) throws IndexOutOfBoundsException;
	
	/**
	 * Renvoie le point zero de l'axe
	 * @return
	 */
	AxisGraduation getFirst ();
	
	/**
	 * Renvoie la derniere gradiation a leurs actuel
	 * @return
	 */
	AxisGraduation getLast ();
	
	/**
	 * renvoie la graduation de la valeur en parametre
	 * @param value
	 * @return
	 */
	AxisGraduation getByValue (double value);
	
	/**
	 * pour fixer l'intervale
	 * @param min
	 * @param max
	 */
	void setInterval (double min, double max);
	
	/**
	 * modification de la plus petite graduation sur l'axe
	 * @param min
	 */
	void setMin (double min);
	
	/**
	 * la plus petite valeur sur l'ax
	 * @return
	 */
	double getMin ();
	
	/**
	 * modification dela plus grande valeur sur l'axe
	 * @param max
	 */
	void setMax (double max);
	
	/**
	 * renvoie la plus grade valeur possible sur l'axe
	 * @return
	 */
	double getMax ();
	
	/**
	 * L'index de la graduation sur l'axe
	 * @param graduation
	 * @return
	 */
	int indexOf (AxisGraduation graduation);
	
	/**
	 * Renvoie la graduation a l'index en parametre
	 * @param index
	 * @return
	 */
	AxisGraduation getAt (int index) throws IndexOutOfBoundsException;
	
	/**
	 * Renvoie la valeur conforme a la ration
	 * @param value
	 * @return
	 */
	double toRation (double value);
	
	/**
	 * Renvoie le nom de l'axe
	 * @return
	 */
	String getName ();
	
	/**
	 * renvoie le nom court de l'axe
	 * @return
	 */
	String getShortName ();
	
	/**
	 * Renvoie le pas d'increment sur l'ax
	 * @return
	 */
	double getStep ();
	
	/**
	 * Renvoie le placement en pixel sur l'axe
	 * @param graduation
	 * @return
	 */
	double getPixelPlacement (AxisGraduation graduation);
	
	/**
	 * Modification du pas d'incrementation pour la numerotation de l'axe
	 * @param step
	 */
	void setStep (double step);
	
	/**
	 * renvoie le ration sur l'axe
	 * @return
	 */
	double getRation ();
	
	/**
	 * Modification de la ration
	 * @param ration
	 */
	void setRation (double ration);
	
	/**
	 * supression de toutes les graduations
	 */
	void clear ();
	
	/**
	 * ajout d'un ecouteur de l'axe
	 * @param listener
	 */
	void addListener (AxisListener listener);
	
	/**
	 * supression d'un ecouteur
	 * @param listener
	 */
	boolean removeListener (AxisListener listener);
}
