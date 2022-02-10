/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigGlobal extends DefaultScenePanel {
	private static final long serialVersionUID = 6023705391758343563L;

	/**
	 * @param title
	 * @param icon
	 * @param mainWindow
	 */
	public PanelConfigGlobal(MainWindow mainWindow) {
		super("Configuration globale", new ImageIcon(R.getIcon("cog")), mainWindow);
		
		this
			.addItemMenu(new NavbarButtonModel("academicYear", "Année Academique"), new PanelAcademicYear(mainWindow))
			.addItemMenu(new NavbarButtonModel("academicFee", "Frais univeritaire"), new Panel())
			.addItemMenu(new NavbarButtonModel("univeritySpend", "Rubrique budgetaire"), new Panel());
	}

	@Override
	public String getNikeName() {
		return "configurationGlobal";
	}

}
