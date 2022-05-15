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
public class PanelConfigGlobal extends DefaultScenePanel {
	private static final long serialVersionUID = 6023705391758343563L;
	
	private final PanelAcademicYear panelAcademicYear;

	/**
	 * @param title
	 * @param icon
	 * @param mainWindow
	 */
	public PanelConfigGlobal(MainWindow mainWindow) {
		super("Configuration globale", new ImageIcon(R.getIcon("cog")), mainWindow, false);
		panelAcademicYear =  new PanelAcademicYear(mainWindow);
		this
			.addItemMenu(new NavbarButtonModel("academicYear", "Année Academique"), panelAcademicYear)		
			.addItemMenu(new NavbarButtonModel("orientations", "Orientations"), new PanelOrientation(this.mainWindow))
			.addItemMenu(new NavbarButtonModel("univeritySpend", "Rubriques budgétaire"), new PanelUniversitySpend(mainWindow))
			.addItemMenu(new NavbarButtonModel("univerityRecipe", "Autres recette"), new PanelUniversityRecipe(mainWindow));
	}
	
	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public String getNikeName() {
		return "configurationGlobal";
	}

}
