/**
 * 
 */
package net.uorbutembo.views.components;

/**
 * @author Esaie MUHASA
 *
 */
public interface WorkspaceListener {
	
	/**
	 * @param item
	 */
	public void onShow (MenuItem item);
	
	/**
	 * pour des menus avec sous-menu
	 * @param item
	 * @param index
	 */
	public void onShow (MenuItem item, int index);
	
	/**
	 * renvoie true, si le paneau est deja pret
	 * @return
	 */
	public boolean isReady ();
	
	/**
	 * on informe le le composent qu'il ne sera plus visible
	 */
	public void onHide ();
}
