/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface LineChartModelListener{
	
	/**
	 * changement total de la configuration du model
	 * @param model
	 */
	void onChange (LineChartModel model);
	
	/**
	 * changement total de la configuration d'un cloud du model
	 * @param model
	 * @param index
	 */
	void onCloudChange (LineChartModel model, int index);
	
	/**
	 * Changement de la configuration d'un point
	 * @param model
	 * @param cloudIndex
	 * @param pointIndex
	 */
	void onPointChange (LineChartModel model, int cloudIndex, int pointIndex);
	
	/**
	 * insrsion d'un nouveau cloud dans le model
	 * @param model
	 * @param cloudIndex
	 */
	void onInsertCloud (LineChartModel model, int cloudIndex);
	
	/**
	 * suppression d'un cloud dans le model des donnees
	 * @param model
	 * @param cloudIndex
	 */
	void onRemoveCloud (LineChartModel model, int cloudIndex);
	
	/**
	 * insersion d'un point dans un cloud du model
	 * @param model
	 * @param cloudIndex
	 * @param pointIndex
	 */
	void onInsertPoint (LineChartModel model, int cloudIndex, int pointIndex);
	
	/**
	 * suppression d'un point dansun cloud du model
	 * @param model
	 * @param cloudIndex
	 * @param pointIndex
	 * @param point
	 */
	void onRemovePoint (LineChartModel model, int cloudIndex, int pointIndex, Point point);
}
