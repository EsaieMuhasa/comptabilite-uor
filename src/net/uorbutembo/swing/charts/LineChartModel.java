/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface LineChartModel extends ChartData{

	/**
	 * Renvoie l'axe des X
	 * @return
	 */
	Axis getXAxis ();
	
	/**
	 * Renvoie l'axe des Y
	 * @return
	 */
	Axis getYAxis ();
	
	/**
	 * renvoie le max sur l'axe des X
	 * @return
	 */
	Point getXMax ();
	
	/**
	 * Renvoie le max sur l'axe des Y
	 * @return
	 */
	Point getYMax ();
	
	/**
	 * renvoie le point le plus bas sur l'axe des 
	 * @return
	 */
	Point getXMin ();
	
	/**
	 * renvoie le point le plus bas sur l'axe des Y
	 * @return
	 */
	Point getYMin ();
	
	/**
	 * Ajout d'un nuage des point au model des graphique lineaire
	 * @param cloud
	 */
	void addChart (PointCloud cloud);
	
	/**
	 * Insersion du graphique
	 * @param cloud
	 * @param index
	 */
	void addChart (PointCloud cloud, int index);
	
	/**
	 * compte le nombre de graphiques ce trouvant dans le modele
	 * @return
	 */
	int countChars ();
	
	/**
	 * Renvoie le graphique a l'index en parametre
	 * @param index
	 * @return
	 */
	PointCloud getChartAt (int index);
	
	/**
	 * Suppression du nuage de point a l'index en parametre
	 * @param index
	 */
	void removeChartAt (int index);
	
	/**
	 * supression de tout les nuages des points dans le model
	 */
	void removeAll();
	
	/**
	 * Renvoie l'index du cloud dans le model ou -1 dans le cas où le cloud n'appartiens 
	 * pas au model
	 * @param cloud
	 * @return
	 */
	int indexOf (PointCloud cloud);
	
	/**
	 * Abonnement d'un ecouteur 
	 * @param listener
	 */
	void addListener (LineChartModelListener listener);
	
	/**
	 * Desabonnement d'un ecouteur
	 * @param listener
	 * @return
	 */
	boolean removeListener (LineChartModelListener listener);
}
