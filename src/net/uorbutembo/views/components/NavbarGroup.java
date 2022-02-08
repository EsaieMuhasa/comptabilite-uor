/**
 * 
 */
package net.uorbutembo.views.components;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class NavbarGroup {
	
	/**
	 * Le bouton du menu de navigation
	 */
	private List<NavbarButton> items = new ArrayList<>();
	private String name;

	/**
	 * @param name
	 */
	public NavbarGroup(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * @param item
	 */
	public void setCurrent (NavbarButton item) {
		for (NavbarButton i : items) {
			if(i != item) {
				i.setCurrent(false);
			}
		}
		item.setCurrent(true);
	}
	
	public void setCurrent (int index) {
		for (NavbarButton item : items) {
			item.setCurrent(false);
		}
		this.items.get(index).setCurrent(true);
	}
	
	public void setCurrent (String name) {
		for (NavbarButton item : items) {
			if(item.getName() != name) {
				item.setCurrent(false);
			} else {
				item.setCurrent(true);
			}
		}
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Ajout d'une item au group du menu
	 * @param item
	 * @return
	 */
	public NavbarGroup addItem (NavbarButton item) {
		this.items.add(item);
		return this;
	}
	
	/**
	 * changer la visibilite des elements du menu
	 * @param visible
	 */
	public void setVisible (boolean visible) {
		for (NavbarButton item : items) {
			item.setVisible(visible);
		}
	}
	
	/**
	 * modification de la visibilite d'un element du menu
	 * @param visible
	 * @param index
	 */
	public void setVisible (boolean visible, int index) {
		this.items.get(index).setVisible(visible);
	}
	
	/**
	 * Change la visibilite de items dont leurs indexs sont en parmetres
	 * @param visible
	 * @param indexs
	 */
	public void setVisible (boolean visible, int ...indexs) {
		for (int i : indexs) {			
			this.items.get(i).setVisible(visible);
		}
	}

}
