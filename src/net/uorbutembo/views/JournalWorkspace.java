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
public class JournalWorkspace extends DefaultScenePanel {
	private static final long serialVersionUID = 5081093701813722107L;
	

	/**
	 * @param mainWindow
	 */
	public JournalWorkspace(MainWindow mainWindow) {
		super("Journal des opérations financières", new ImageIcon(R.getIcon("calendar")), mainWindow, false);

		NavbarButtonModel itemIndex = new NavbarButtonModel("index", "Général");
		NavbarButtonModel itemAccounts = new NavbarButtonModel("bilan", "Spécifiques");
		
		final JournalGeneral panelChart = new JournalGeneral(mainWindow);
		final JournalSpecific panelAccounts = new JournalSpecific(mainWindow);
		
		addItemMenu(itemIndex, panelChart);
		addItemMenu(itemAccounts, panelAccounts);
	}
	
	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public String getNikeName() {
		return "journal";
	}
	
}
