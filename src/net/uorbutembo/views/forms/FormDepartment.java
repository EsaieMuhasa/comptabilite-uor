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
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormDepartment extends DefaultFormPanel {
	private static final long serialVersionUID = 1236148729398198199L;
	
	private final FormGroup<String> acronym = FormGroup.createEditText("Abbreviation");
	private final FormGroup<String> fullname = FormGroup.createEditText("Appelation complete");
	private final FormGroup<Faculty> faculty = FormGroup.createComboBox("Faculté");
	
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	
	public FormDepartment(DepartmentDao departmentDao) {
		super();
		this.departmentDao = departmentDao;
		this.facultyDao = departmentDao.getFacultyDao();
		this.setTitle("Formulaire d'enregistrement");
		this.init();
	}
	
	private void init() {
		
		List<Faculty> facs = this.facultyDao.findAll();
		ComboBox<Faculty> compo = (ComboBox<Faculty>) this.faculty.getField();
		for (Faculty fac : facs) {
			compo.addItem(fac);
		}
		
		final Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.add(this.faculty);
		box.add(this.acronym);
		box.add(this.fullname);

		this.getBody().add(box, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String acronym = this.acronym.getValue();
		String name = this.fullname.getValue();
		Faculty fac = this.faculty.getValue();
		Department dep = new Department();
		dep.setFaculty(fac);
		dep.setName(name);
		dep.setAcronym(acronym);
		dep.setRecordDate(new Date());
		try {
			this.departmentDao.create(dep);
			this.showMessageDialog("Info", name+"\nEnregistrer avec succes", JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
