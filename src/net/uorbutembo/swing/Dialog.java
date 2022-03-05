/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JComponent;
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
		this.init(owner);
	}
	
	/**
	 * initialisation de la boite de dialogue et son contentPane
	 * @param owner
	 * @param contentPane
	 */
	public Dialog(Frame owner, JComponent contentPane) {
		super(owner);
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		this.pack();
		this.init(owner);
	}
	
	/**
	 * Initilisation elementaires de la boite de dialogue
	 * @param owner
	 */
	private void init (Frame owner) {
		this.getContentPane().setBackground(FormUtil.BKG_DARK);
		this.setLocationRelativeTo(owner);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setModal(true);

	}

}
