/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
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

	private InscriptionDao inscriptionDao;
	
	private Table table;
	private InscritTableModel tableModel;
	private JLabel title = FormUtil.createSubTitle("");
	private SidebarStudents sidebar;
	private Panel workspace = new Panel(new BorderLayout());

	public PanelStudents(MainWindow mainWindow) {
		super("Inscription", new ImageIcon(R.getIcon("student")), mainWindow, false);//la scene gere les scrollbars	
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		this.setTitle("Etudiants");
		
		tableModel = new InscritTableModel(inscriptionDao);
		table = new Table(tableModel);
		table.setBackground(FormUtil.BKG_DARK);
		
		sidebar = new SidebarStudents(mainWindow, this, tableModel);
		
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.sidebar, workspace);
		split.setDividerLocation(300);
		
		final Panel formPanel = new Panel(new BorderLayout());
		final Panel listPanel = new Panel(new BorderLayout());
		
		final FormInscription form = new FormInscription(mainWindow.factory.findDao(InscriptionDao.class));
		formPanel.add(form, BorderLayout.CENTER);
		
		formPanel.setBorder(BODY_BORDER);
		listPanel.setBorder(BODY_BORDER);
		
		final JScrollPane scroll = FormUtil.createVerticalScrollPane(formPanel);
		
		listPanel.add(split, BorderLayout.CENTER);
		
		//menu secondaire
		this
			.addItemMenu(new NavbarButtonModel("inscrits", "Liste des inscrits"), listPanel)
			.addItemMenu(new NavbarButtonModel("newStudent", "Inscription"), scroll)
			.addItemMenu(new NavbarButtonModel("oldStudent", "Re-inscription"), new Panel());
		
		this.initWorkspace();
	}
	
	/**
	 * Personnalisaion de l'espce de travaille
	 */
	private void initWorkspace() {
		workspace.add(title, BorderLayout.NORTH);
		workspace.add(table, BorderLayout.CENTER);
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
		this.title.setText("Etudiants inscrit pour l'année " + year.getLabel());
	}
	
	@Override
	public void onSelectFaulty(Faculty faculty, AcademicYear year) {
		this.title.setText(year.toString()+" / "+faculty);
	}

	@Override
	public void onSelectDepartment(Department department, AcademicYear year) {
		this.title.setText(year.toString()+" / "+department.getFaculty().getAcronym()+" / "+department);
	}

	@Override
	public void onSelectPromotion(Promotion promotion) {
		this.title.setText(promotion.getAcademicYear().toString()+" / "+promotion.getDepartment().getFaculty().getAcronym()+" / "+promotion);
	}

	@Override
	public void onSelectInscription(Inscription inscription) {
		// TODO Auto-generated method stub
		
	}

}
