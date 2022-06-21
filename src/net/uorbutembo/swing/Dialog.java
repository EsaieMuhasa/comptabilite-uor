/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JDialog;

import net.uorbutembo.tools.FormUtil;

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
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public Dialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		init(owner);
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
		getContentPane().setBackground(FormUtil.BKG_DARK);
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);

	}

}
