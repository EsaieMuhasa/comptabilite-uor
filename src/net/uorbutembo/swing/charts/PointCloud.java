/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface PointCloud extends ChartData{
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
	 * Faut il replir la surface limiter par la courbe et l'ax des X
	 * @return
	 */
	boolean isFill ();
	
	/**
	 * ajout d'un point au nuage des points
	 * @param point
	 */
	void addPoint (Point point);
	
	/**
	 * Insersion d'un point au nuage des points
	 * @param point
	 * @param index
	 */
	void addPoint(Point point, int index);
	
	/**
	 * Renvoie l'index d'un point dans le model
	 * @param point
	 */
	int indexOf (Point point);
	
	/**
	 * renvoie le point a l'index en parametre
	 * @param index
	 * @return
	 */
	Point getPointAt (int index);
	
	/**
	 * suprime le point a l'index en parametre dans le nuage des points
	 * @param index
	 */
	void removePointAt(int index);
	
	/**
	 * suppression d'un point dans le nuage
	 * @param point
	 */
	void removePoint (Point point);
	
	/**
	 * comptage des points dans le nuage
	 * @return
	 */
	int countPoint ();
	
	/**
	 * Renvoie la collection des point dans le nuage
	 * @return
	 */
	Point[] getPoints ();
	
	/**
	 * Ajout d'un ecouteur du cloud
	 * @param listener
	 */
	void addListener (PointCloudListener listener);
	
	/**
	 * supression d'un ecouteur du cloud
	 * @param listener
	 */
	void removeListener (PointCloudListener listener);
}
