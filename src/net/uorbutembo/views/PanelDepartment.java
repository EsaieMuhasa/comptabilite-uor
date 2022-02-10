/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormDepartment;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDepartment extends Panel {

	private static final long serialVersionUID = -2959155526810028774L;

	/**
	 * 
	 */
	public PanelDepartment(MainWindow mainWindow) {
		super(new BorderLayout());
		this.add(new FormDepartment(mainWindow.factory.findDao(DepartmentDao.class)), BorderLayout.NORTH);
	}


}
