/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormUniversitySpend;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelUniversitySpend extends Panel {
	private static final long serialVersionUID = -6678192465363319784L;

	/**
	 * 
	 */
	public PanelUniversitySpend(MainWindow mainWindow) {
		super(new BorderLayout());
		
		this.add(new FormUniversitySpend(mainWindow, mainWindow.factory.findDao(UniversitySpendDao.class)), BorderLayout.NORTH);
	}

}
