/**
 * 
 */
package net.uorbutembo.swing.charts;

import net.uorbutembo.swing.charts.LineChartRender.Interval;

/**
 * @author Esaie MUHASA
 *
 */
public interface LineChartRenderListener {
	
	/**
	 * demande de translation sur l'axe en deuxieme parametre
	 * @param source la source de l'evenement
	 * @param axis
	 * @param interval
	 */
	void onRequireTranslation (LineChartRender source, Axis axis, Interval interval);
	
	/**
	 * require translation on all axis
	 * @param source
	 * @param xInterval
	 * @param yInterval
	 */
	void onRequireTranslation (LineChartRender source, Interval xInterval, Interval yInterval);
}
