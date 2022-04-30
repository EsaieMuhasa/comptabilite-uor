/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface Point extends ChartData{
	
	/**
	 * renvoei la valeur sur l'axe des abs
	 * @return
	 */
	double getX();
	double getRoundX(int decimal);
	
	/**
	 * valeur sur l'axe des ord
	 * @return
	 */
	double getY();
	double getRoundY(int decimal);
	
	
	/**
	 * Valeur sur l'axe Z
	 * @return
	 */
	double getZ();
	double getRoundZ(int decimal);
	
	/**
	 * transalation du point
	 * @param x
	 * @param y
	 * @param z
	 */
	void translate (double x, double y, double z);
	
	/**
	 * translation sur l'axe des X et Y
	 * @param x
	 * @param y
	 */
	default void translateXY (double x, double y) {
		translate(x, y, getZ());
	}
	
	/**
	 * Taille du point
	 * @return
	 */
	int getSize();
	
	/**
	 * Ajout d'un ecouteur
	 * @param listener
	 */
	void addListener(PointListener listener);
	
	/**
	 * @param listener
	 * @return
	 */
	boolean removeListener(PointListener listener);
}
