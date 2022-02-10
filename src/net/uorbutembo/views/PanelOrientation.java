/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelOrientation extends DefaultScenePanel {
	private static final long serialVersionUID = 1706520118231790169L;
	
	/**
	 * 
	 */
	public PanelOrientation(MainWindow mainWindow) {
		super("Orientations", new ImageIcon(R.getIcon("database")), mainWindow);
		
		//menu
		this
		.addItemMenu(new NavbarButtonModel("facultys", "Facultés"), new PanelFaculty(mainWindow))
		.addItemMenu(new NavbarButtonModel("departments", "Départements"), new PanelDepartment(mainWindow))
		.addItemMenu(new NavbarButtonModel("studyClass", "Classe d'étude"), new PanelStudyClass(mainWindow));
	}

	@Override
	public String getNikeName() {
		return "orientations";
	}

}
