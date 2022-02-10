/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormFaculty;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFaculty extends Panel {
	private static final long serialVersionUID = 6683302991865603147L;

	/**
	 * @param mainWindow
	 */
	public PanelFaculty(MainWindow mainWindow) {
		super(new BorderLayout());
		this.add(new FormFaculty(mainWindow.factory.findDao(FacultyDao.class)), BorderLayout.NORTH);
	}

}
