/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormStudyClass extends DefaultFormPanel {
	private static final long serialVersionUID = 1236148729398198199L;
	
	private final FormGroup<String> acronym = FormGroup.createTextField("Abbreviation");
	private final FormGroup<String> fullname = FormGroup.createTextField("Appelation complete");
	private StudyClassDao studyClassDao;
	
	public FormStudyClass(MainWindow mainWindow, StudyClassDao studyClassDao) {
		super(mainWindow);
		this.studyClassDao = studyClassDao;
		this.setTitle("Formulaire d'enregistrement");
		this.init();
	}
	
	private void init() {
		final Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.add(this.acronym);
		box.add(this.fullname);

		this.getBody().add(box, BorderLayout.CENTER);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String acronym = this.acronym.getValue();
		String name = this.fullname.getValue();
		StudyClass sc = new StudyClass();
		sc.setName(name);
		sc.setAcronym(acronym);
		sc.setRecordDate(new Date());
		try {
			this.studyClassDao.create(sc);
			this.showMessageDialog("Info", name+"\nEnregistrer avec succes", JOptionPane.INFORMATION_MESSAGE);
			this.acronym.getField().setValue("");
			this.fullname.getField().setValue("");
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
