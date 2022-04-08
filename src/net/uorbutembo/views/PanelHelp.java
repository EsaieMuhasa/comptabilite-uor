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
public class PanelHelp extends DefaultScenePanel {
	private static final long serialVersionUID = 2668307309635713218L;

	/**
	 * @param mainWindow
	 */
	public PanelHelp (MainWindow mainWindow) {
		super("Aide d'utilisation", new ImageIcon(R.getIcon("help")), mainWindow);
	}

	@Override
	public String getNikeName() {
		return "help";
	}

}
