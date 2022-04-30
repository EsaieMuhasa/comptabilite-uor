/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface AxisListener {
	/**
	 * lors du changement de la configuration de l'axe
	 * @param axis
	 */
	void onChange (Axis axis);
	
	/**
	 * Lors de l'insersion d'une graduation
	 * @param axis
	 * @param index
	 */
	void graduationInserted (Axis axis, int index);
	
	/**
	 * Lors du changement de la configuration d'une graduation
	 * @param axis
	 * @param index
	 */
	void graduationChange (Axis axis, int index);
}
