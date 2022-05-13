/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public interface TimeAxis extends Axis {
	
	/**
	 * Injection de formateur des dates
	 * @param formater
	 */
	void setFormater (final DateFormat formater);
	
	/**
	 * modification de l'intervale
	 * @param min
	 * @param max
	 */
	default void setInterval (final Date min, final Date max) {
		setInterval(min.getTime(), max.getTime());
	}
	
	/**
	 * Renvoie la graduation correspondant a la date en parametre
	 * @param date
	 * @return
	 */
	default AxisGraduation getByValue (final Date date) {
		return getByValue(date.getTime());
	}
	
	/**
	 * Modifiartion de la date max
	 * @param date
	 */
	default void setMax (final Date date) {
		setMax(date.getTime());
	}
	
	/**
	 * Modification de la date min
	 * @param date
	 */
	default void setMin (Date date) {
		setMin(date.getTime());
	}

}
