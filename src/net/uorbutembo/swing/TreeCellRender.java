/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * @author Esaie MUHASA
 *
 */
public class TreeCellRender extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -990642642588128902L;

	/**
	 * 
	 */
	public TreeCellRender() {
		super();
		this.setFont(new Font("Arial", Font.PLAIN, 13));
		this.setTextNonSelectionColor(Color.LIGHT_GRAY);
		this.setTextSelectionColor(Color.LIGHT_GRAY);
	}
	

}
