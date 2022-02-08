/**
 * 
 */
package net.uorbutembo.views.components;

import java.util.List;

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
	public Icon getIcon();

	/**
	 * Modification de l'icone du menu de l'item
	 * @param icon
	 */
    public void setIcon(Icon icon);

    /**
     * renvoie le nom du model
     * @return
     */
    public String getName();
    
    /**
     * Renvoie le label de l'item qui doit etre associer au model
     * @return
     */
    public String getLabel();
    
    /**
     * modification du label
     * @param label
     */
    public void setLabel (String label);

    /**
     * Modification du nom du model
     * @param name
     */
    public void setName(String name);

    /**
     * Renvoie les objects considerer comme sous menu du MenuItem
     * @return
     */
    public List<T> getItems();

    /**
     * Modidication de la collection des objets en sous-menu du MenuItem
     * @param items
     */
    public void setItems(List<T> items);
    
    /**
     * Renvoie le nombre de items en sous-menu
     * @return
     */
    public int countItems ();
    
    /**
     * insersion d'in item dans le model
     * @param item
     * @param index
     * @return
     */
    public MenuItemModel<T> addItem (T item , int index);
    
    /**
     * ajout d'un item
     * @param item
     * @return
     */
    public MenuItemModel<T> addItem (T item);
    
    @SuppressWarnings("unchecked")
	public void addItems (T... items);
}
