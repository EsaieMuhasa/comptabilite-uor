/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButton;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.components.SidebarStudents;
import net.uorbutembo.views.components.SidebarStudents.SidebarListener;
import net.uorbutembo.views.forms.FormInscription;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.InscritTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * 
 */
public class PanelStudents extends DefaultScenePanel implements SidebarListener{
	private static final long serialVersionUID = -356861410803019685L;
	
	private AcademicYear currentYear;
	private InscriptionDao inscriptionDao;
	private Panel panelInscrit = new Panel(new BorderLayout());
	
	private Table table;
	private JLabel title = FormUtil.createSubTitle("");
	private SidebarStudents sidebar;

	public PanelStudents(MainWindow mainWindow) {
		super("Inscription", new ImageIcon(R.getIcon("student")), mainWindow);		
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		this.setTitle("Etudiants");
		
		table = new Table(new InscritTableModel(inscriptionDao, currentYear));
		table.setBackground(FormUtil.BKG_DARK);
		Box top = Box.createHorizontalBox();
		
		sidebar = new SidebarStudents(mainWindow, this);
		
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.sidebar, panelInscrit);
		split.setDividerLocation(300);
		
		//menu secondaire
		this
			.addItemMenu(new NavbarButtonModel("inscrits", "Liste des inscrits"), split)
			.addItemMenu(new NavbarButtonModel("newStudent", "Inscription"), new FormInscription(mainWindow.factory.findDao(InscriptionDao.class)))
			.addItemMenu(new NavbarButtonModel("oldStudent", "Re-inscription"), new Panel());
		
		top.add(title);
		panelInscrit.add(top, BorderLayout.NORTH);
		panelInscrit.add(table, BorderLayout.CENTER);
	}

	@Override
	public String getNikeName() {
		return "students";
	}
	
	@Override
	public void onAction(NavbarButton view) {
		super.onAction(view);
	}
	
	
	//interfacage avec le sidebar
	//=========================================

	@Override
	public void onSelectAcademicYear(AcademicYear year) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSelectFaulty(Faculty faculty, AcademicYear year) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectDepartment(Department department, AcademicYear year) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectPromotion(Promotion promotion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectInscription(Inscription inscription) {
		// TODO Auto-generated method stub
		
	}

}
