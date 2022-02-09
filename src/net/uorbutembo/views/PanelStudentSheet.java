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
public class PanelStudentSheet extends DefaultScenePanel {
	private static final long serialVersionUID = -8436590834731756435L;
	
	public PanelStudentSheet(MainWindow mainWindow) {
		super("Fiche individelle", new ImageIcon(R.getIcon("card")), mainWindow);
	}
	@Override
	public String getNikeName() {
		return "sheets";
	}

}
