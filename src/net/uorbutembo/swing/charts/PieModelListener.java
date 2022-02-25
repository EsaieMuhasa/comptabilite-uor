/**
 * 
 */
package net.uorbutembo.swing.charts;

/**
 * @author Esaie MUHASA
 *
 */
public interface PieModelListener {
	
	/**
	 * method utilitaire appeler pour refrechier la vue
	 * @param model
	 */
	void refresh (PieModel model) ;
	
	/**
	 * pour demander a la vue de redessiner le part en parametre
	 * @param model
	 * @param partIndex
	 */
	void repaintPart (PieModel model, int partIndex);
	
	default void onTitleChange (PieModel  model, String title) {}

}
