/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormAcademicYear;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicYear extends Panel {
	private static final long serialVersionUID = -119042779710985760L;
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		this.add(new FormAcademicYear(mainWindow.factory.findDao(AcademicYearDao.class)), BorderLayout.NORTH);
	}

}
