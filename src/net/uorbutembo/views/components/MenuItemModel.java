/**
 * 
 */
package net.uorbutembo.views.components;

import javax.swing.Icon;

/**
 * @author Esaie MUHASA
 *
 */
public interface MenuItemModel <T> {
	
	/**
	 * Renvoie un reference vers le menu de l'item
	 * @return
	 */
	Icon getIcon();

	/**
	 * Modification de l'icone du menu de l'item
	 * @param icon
	 */
    void setIcon(Icon icon);

    /**
     * renvoie le nom du model
     * @return
     */
    String getName();
    
    /**
     * Renvoie le label de l'item qui doit etre associer au model
     * @return
     */
    String getLabel();
    
    /**
     * modification du label
     * @param label
     */
    void setLabel (String label);

    /**
     * Modification du nom du model
     * @param name
     */
    void setName(String name);

    /**
     * Renvoie les objects considerer comme sous menu du MenuItem
     * @return
     */
    T[] getItems();
    
    /**
     * Renvoie le nombre de items en sous-menu
     * @return
     */
    int countItems ();
    
    /**
     * renovie l'index active
     * -1 dans le cas où aucun menu n'est active
     * @return
     */
    int getCurrentItem ();
    
    /**
     * Modification de l'index active
     * @param index
     * @throws IndexOutOfBoundsException
     */
    void setSelectedItem (final int index) throws IndexOutOfBoundsException;
    
    /**
     * insersion d'in item dans le model
     * @param item
     * @param index
     * @return
     */
    MenuItemModel<T> addItem (T item , int index);
    
    /**
     * mise en jours d'un item
     * @param item
     * @param index
     */
    void updateItem (T item, int index);
    
    /**
     * Renvoie l'item a l'index
     * @param index
     * @return
     */
    T getItem (int index);
    
    /**
     * Suppresion d'une item
     * @param index
     */
    void removeItem (int index);
    
    /**
     * ajout d'un item
     * @param item
     * @return
     */
    MenuItemModel<T> addItem (T item);
    
    @SuppressWarnings("unchecked")
	public void addItems (T... items);
    
    /**
     * ecoute des changements du model
     * @param ls
     */
    void addModelListener (MenuItemModelListener ls);
    
    /**
     * Desabonnement d'ecoute des changement du model
     * @param ls
     */
    void removeModelListener (MenuItemModelListener ls);
    
    /**
     * @author Esaie MUHASA
     *	Interface d'ecoute de changement du model
     */
    public static interface MenuItemModelListener {
    	
    	/**
    	 * changement du model globale
    	 * @param model
    	 */
    	void onChange (MenuItemModel<?> model);
    	
    	/**
    	 * Changement d'un element du model
    	 * @param model
    	 * @param index
    	 */
    	void onChange (MenuItemModel<?> model, int index);
    }
}
