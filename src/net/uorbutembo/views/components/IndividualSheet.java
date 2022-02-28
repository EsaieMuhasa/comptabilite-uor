/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class IndividualSheet extends Panel {

	private static final long serialVersionUID = 4342531142487279020L;
	public static final Dimension A4_LAND = new Dimension(1123, 794);
	
	private Panel page = new Panel(new BorderLayout());
	private JScrollPane scroll = FormUtil.createScrollPane(page);
	private JPanel container = new JPanel();
	
	private Inscription inscription;

	/**
	 * 
	 */
	public IndividualSheet(MainWindow mainWindow) {
		super(new BorderLayout());
		page.setPreferredSize(A4_LAND);
		page.setSize(A4_LAND);
		page.setMaximumSize(A4_LAND);
		page.setMinimumSize(A4_LAND);
		page.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		this.add(scroll, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		
		page.add(container, BorderLayout.CENTER);
	}

	/**
	 * @return the inscription
	 */
	public Inscription getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(Inscription inscription) {
		this.inscription = inscription;
	}
	
	


}
