/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Frame;

import javax.swing.JDialog;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class Dialog extends JDialog {
	private static final long serialVersionUID = -213335211087594814L;

	/**
	 * @param owner
	 */
	public Dialog(Frame owner) {
		super(owner);
		this.getContentPane().setBackground(FormUtil.BKG_DARK);
		this.setLocationRelativeTo(owner);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setModal(true);
	}

}
