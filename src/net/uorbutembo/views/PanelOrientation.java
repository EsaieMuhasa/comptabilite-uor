/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.swing.Panel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelOrientation extends Panel implements AcademicYearDaoListener{
	private static final long serialVersionUID = 1706520118231790169L;
	
	private boolean loaded = false;
	private final PanelFaculty panelFaculty;
	private final PanelDepartment panelDepartment;
	private final PanelStudyClass panelStudyClass;
	
	/**
	 * 
	 */
	public PanelOrientation(MainWindow mainWindow) {
		super(new BorderLayout());
		
		mainWindow.factory.findDao(AcademicYearDao.class).addYearListener(this);
		
		panelFaculty=new PanelFaculty(mainWindow);
		panelDepartment=new PanelDepartment(mainWindow);
		panelStudyClass=new PanelStudyClass(mainWindow);
		
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbed.addTab("Facultés", panelFaculty);
		tabbed.addTab("Départements", panelDepartment);
		tabbed.addTab("Classe d'étude", panelStudyClass);

		this.add(tabbed, BorderLayout.CENTER);
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		if(loaded)
			return;
		
		panelFaculty.load();
		panelDepartment.load();
		panelStudyClass.load();
	}

}
