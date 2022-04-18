/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.views.components.DefaultScenePanel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelJournal extends DefaultScenePanel {
	private static final long serialVersionUID = 5081093701813722107L;

	/**
	 * @param mainWindow
	 */
	public PanelJournal(MainWindow mainWindow) {
		super("Journal", new ImageIcon(R.getIcon("calendar")), mainWindow, false);
	}

	@Override
	public String getNikeName() {
		return "journal";
	}

}
