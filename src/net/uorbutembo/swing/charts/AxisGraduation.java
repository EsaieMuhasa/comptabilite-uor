/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Esaie MUHASA
 *
 */
public interface AxisGraduation extends ChartData{
	/**
	 * renvoei le label de la graduation
	 * @return
	 */
	String getLabel ();
	
	/**
	 * Renvoie la taille de la graduation
	 * @return
	 */
	int getSize ();
	
	/**
	 * renvoie la valeur de la graduation
	 * @return
	 */
	double getValue ();
	
	/**
	 * Arrondissement de la valeur reel
	 * @param decimal
	 * @return
	 */
	default double getRoundValue (int decimal) {
		BigDecimal big = new BigDecimal(getValue()).setScale(decimal, RoundingMode.FLOOR);
		return big.doubleValue();
	}
	
	default double getRoundValue () {
		return getRoundValue(0);
	}

}
