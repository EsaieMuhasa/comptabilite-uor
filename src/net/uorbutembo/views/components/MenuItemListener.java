/**
 * 
 */
package net.uorbutembo.views.components;

/**
 * @author Esaie MUHASA
 *
 */
public interface MenuItemListener {
	
	/**
	 * callback lors de l'overture d'un menu
	 * @param item
	 */
	public void onOpen (MenuItem item);
	
	/**
	 * Collback lors la fermeture du menu
	 * @param item
	 */
	public void onClose (MenuItem item);
	
	/**
	 * Lorsque le sous-menu est selectionner
	 * @param item
	 * @param index
	 */
	public void onAction (MenuItem item, int index, MenuItemButton view);
	
	/**
	 * Pour les menus qui n'onp pas de sous-menu
	 * @param item
	 */
	public void onAction (MenuItem item);

}
