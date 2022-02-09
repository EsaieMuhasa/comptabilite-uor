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
public class PanelConfigSoftware extends DefaultScenePanel {
	private static final long serialVersionUID = -2515621748287799602L;
	
	public PanelConfigSoftware(MainWindow mainWindow) {
		super("Configuration du logiciel", new ImageIcon(R.getIcon("console")), mainWindow);
	}
	@Override
	public String getNikeName() {
		return "configuration";
	}

}
