/**
 * 
 */
package net.uorbutembo.views.components;

/**
 * @author Esaie MUHASA
 *
 */
public class NavbarButtonModel {
	
	/**
	 * Le nom du model, doit respecter le norme de nomage des variable
	 */
	private String name;
	
	/**
	 * Utiliser lors de la creation d'une vue pour le model
	 */
	private String label;
	
	private boolean enable;//activation / desactivation du menu


	/**
	 * @param name
	 * @param label
	 */
	public NavbarButtonModel(String name, String label) {
		super();
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	

}
