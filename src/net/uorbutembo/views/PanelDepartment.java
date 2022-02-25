/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormDepartment;
import net.uorbutembo.views.models.DepartmentTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDepartment extends Panel {

	private static final long serialVersionUID = -2959155526810028774L;
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter un département");
	private FormDepartment form;
	private Dialog formDialog;
	private DepartmentDao departmentDao;
	
	private JTabbedPane tabbedPane;
	

	/**
	 * 
	 */
	public PanelDepartment(MainWindow mainWindow) {
		super(new BorderLayout());
		this.departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		
		this.departmentDao.addListener(new DAOAdapter<Department> () {
			@Override
			public void onCreate(Department e, int requestId) {
				formDialog.setVisible(false);
			}
		});
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		this.add(top, BorderLayout.NORTH);
		
		this.btnNew.addActionListener(event -> {
			if(formDialog == null) {
				form = new FormDepartment(this.departmentDao);
				this.formDialog = new Dialog(mainWindow);
				this.formDialog.setTitle("Enregistrement d'un nouveau departement");
				this.formDialog.getContentPane().add(this.form, BorderLayout.CENTER);
				this.formDialog.setSize(600, 320);
			}
			
			this.formDialog.setLocationRelativeTo(mainWindow);
			this.formDialog.setVisible(true);
		});
		
		this.tabbedPane = new JTabbedPane();
		
		final List<Faculty> faculties = mainWindow.factory.findDao(FacultyDao.class).findAll();
		for (Faculty faculty : faculties) {			
			Table table = new Table(new DepartmentTableModel(this.departmentDao, faculty));
			TablePanel tablePanel = new TablePanel(table, faculty.getName());
			tabbedPane.addTab(faculty.getAcronym(), tablePanel);
		}
		this.add(tabbedPane, BorderLayout.CENTER);
		
	}


}
