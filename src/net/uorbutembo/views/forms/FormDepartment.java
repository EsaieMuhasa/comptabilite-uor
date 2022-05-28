/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormDepartment extends DefaultFormPanel {
	private static final long serialVersionUID = 1236148729398198199L;
	
	private final FormGroup<String> acronym = FormGroup.createTextField("Abbreviation");
	private final FormGroup<String> fullname = FormGroup.createTextField("Appelation complete");
	
	private final ComboBox<Faculty> comboFaculties = new ComboBox<>("Faculté");
	private final FormGroup<Faculty> faculty = FormGroup.createComboBox(comboFaculties);
	
	private final FacultyDao facultyDao;
	private final DepartmentDao departmentDao;
	private Department department;//lors de la modification, cette reference est != null
	
	private final DAOAdapter<Faculty> facultyAdapter = new DAOAdapter<Faculty>() {
		@Override
		public void onCreate(Faculty e, int requestId) {
			comboFaculties.addItem(e);
		}
		
		@Override
		public void onUpdate(Faculty e, int requestId) {
			for (int i = 0, max = comboFaculties.getItemCount(); i<max; i++) {
				if(comboFaculties.getItemAt(i).getId() == e.getId()) {
					comboFaculties.removeItemAt(i);
					comboFaculties.addItem(e);
					return;
				}
			}
		}
		
		@Override
		public void onDelete(Faculty e, int requestId) {
			for (int i = 0, max = comboFaculties.getItemCount(); i<max; i++) {
				if(comboFaculties.getItemAt(i).getId() == e.getId()) {
					comboFaculties.removeItemAt(i);
					return;
				}
			}
		}
	};
	
	public FormDepartment(MainWindow mainWindow) {
		super(mainWindow);
		departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		facultyDao = departmentDao.getFactory().findDao(FacultyDao.class);
		init();
		
		facultyDao.addListener(facultyAdapter);
	}
	
	private void init() {
		
		List<Faculty> facs = this.facultyDao.findAll();
		for (Faculty fac : facs) {
			comboFaculties.addItem(fac);
		}
		
		final Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.add(this.faculty);
		box.add(this.acronym);
		box.add(this.fullname);

		this.getBody().add(box, BorderLayout.CENTER);
	}
	
	/**
	 * @param department the department to set
	 */
	public void setDepartment(Department department) {
		this.department = department;
		if (department  != null) {
			acronym.getField().setValue(department.getAcronym());
			fullname.getField().setValue(department.getName());

			for (int i = 0, max = comboFaculties.getItemCount(); i<max; i++) {
				if(comboFaculties.getItemAt(i).getId() == department.getFaculty().getId()) {
					comboFaculties.setSelectedIndex(i);
					break;
				}
			}
			setTitle(TITLE_2);
		} else {
			acronym.getField().setValue("");
			fullname.getField().setValue("");
			setTitle(TITLE_1);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String acronym = this.acronym.getValue();
		String name = this.fullname.getValue();
		
		Date now =  new Date();
		Faculty fac = this.faculty.getValue();
		Department dep = new Department();
		dep.setFaculty(fac);
		dep.setName(name);
		dep.setAcronym(acronym);
		
		try {
			if(department == null) {
				dep.setRecordDate(now);			
				this.departmentDao.create(dep);
			} else {
				dep.setLastUpdate(now);
				departmentDao.update(dep, department.getId());
			}
			
			this.showMessageDialog("Info", name+"\nEnregistrer avec succes", JOptionPane.INFORMATION_MESSAGE);
			setDepartment(null);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
