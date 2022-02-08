/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.forms.FormFaculty;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFaculty extends DefaultScenePanel {
	private static final long serialVersionUID = 1706520118231790169L;
	
	/**
	 * 
	 */
	public PanelFaculty() {
		super("Facultés", new ImageIcon(R.getIcon("database")));
		
		//menu
		this
		.addItemMenu(new NavbarButtonModel("facultys", "Facultés"), new FormFaculty())
		.addItemMenu(new NavbarButtonModel("departments", "Départements"), new Panel());
	}

	@Override
	public String getNikeName() {
		return "faculty";
	}

}
