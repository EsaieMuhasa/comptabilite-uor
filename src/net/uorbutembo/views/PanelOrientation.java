/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import net.uorbutembo.swing.Panel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelOrientation extends Panel {
	private static final long serialVersionUID = 1706520118231790169L;
	
	/**
	 * 
	 */
	public PanelOrientation(MainWindow mainWindow) {
		super(new BorderLayout());
		
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbed.addTab("Facultés", new PanelFaculty(mainWindow));
		tabbed.addTab("Départements", new PanelDepartment(mainWindow));
		tabbed.addTab("Classe d'étude", new PanelStudyClass(mainWindow));

		this.add(tabbed, BorderLayout.CENTER);
	}

}
