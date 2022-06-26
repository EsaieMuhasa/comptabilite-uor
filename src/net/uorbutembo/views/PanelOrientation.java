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
public class PanelOrientation extends Panel{
	private static final long serialVersionUID = 1706520118231790169L;
	
	private final PanelFaculty panelFaculty;
	private final PanelStudyClass panelStudyClass;
	
	public PanelOrientation(MainWindow mainWindow) {
		super(new BorderLayout());
		
		panelFaculty=new PanelFaculty(mainWindow);
		panelStudyClass=new PanelStudyClass(mainWindow);
		
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbed.addTab("Facultés", panelFaculty);
		tabbed.addTab("Classe d'étude", panelStudyClass);

		this.add(tabbed, BorderLayout.CENTER);
	}
	
	/**
	 * lecture des donnees
	 */
	public void reload() {
		panelFaculty.load();
		panelStudyClass.load();
	}

}
