/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class Panel extends JPanel {
	private static final long serialVersionUID = 6542681646658354953L;

	/**
	 * 
	 */
	public Panel() {
		super();
		this.setOpaque(false);
	}

	/**
	 * @param layout
	 */
	public Panel(LayoutManager layout) {
		super(layout);
		this.setOpaque(false);
	}

}
