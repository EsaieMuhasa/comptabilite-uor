/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormFaculty extends DefaultFormPanel {
	private static final long serialVersionUID = 1236148729398198199L;
	
	private final FormGroup<String> acronym = FormGroup.createTextField("Abbreviation");
	private final FormGroup<String> fullname = FormGroup.createTextField("Appelation complete");
	private FacultyDao facultyDao;
	private Faculty faculty;//lors de la modification
	
	private JDialog dialog;
	
	public FormFaculty(MainWindow mainWindow, FacultyDao facultyDao) {
		super(mainWindow);
		this.facultyDao = facultyDao;
		this.setTitle(TITLE_1);
		this.init();
	}
	
	/**
	 * @param dialog the dialog to set
	 */
	public void setDialog(JDialog dialog) {
		this.dialog = dialog;
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setFaculty(null);
			};
		});
	}

	private void init() {
		final Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.add(this.acronym);
		box.add(this.fullname);

		this.getBody().add(box, BorderLayout.CENTER);
	}
	
	/**
	 * @param faculty the faculty to set
	 */
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
		if(faculty  != null) {
			acronym.getField().setValue(faculty.getAcronym());
			fullname.getField().setValue(faculty.getName());
			setTitle(TITLE_2);
		} else {
			setTitle(TITLE_1);
			this.acronym.getField().setValue("");
			this.fullname.getField().setValue("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String acronym = this.acronym.getValue();
		String name = this.fullname.getValue();
		
		Date now = new Date();
		Faculty fac = new Faculty();
		fac.setName(name);
		fac.setAcronym(acronym);

		try {
			
			if(faculty == null) {
				fac.setRecordDate(now);
				this.facultyDao.create(fac);
			}
			else {
				fac.setLastUpdate(now);
				facultyDao.update(fac, faculty.getId());
			}
			setFaculty(null);			
			this.showMessageDialog("Info", name+"\nEnregistrer avec succes", JOptionPane.INFORMATION_MESSAGE);
			if(dialog != null)
				dialog.setVisible(false);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
