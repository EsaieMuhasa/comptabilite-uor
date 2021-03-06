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
	
	private final FormGroup<String> acronym = FormGroup.createTextField("Abbréviation");
	private final FormGroup<String> fullname = FormGroup.createTextField("Appellation complète");
	private StudyClassDao studyClassDao;
	
	private StudyClass studyClass;
	
	public FormStudyClass(MainWindow mainWindow) {
		super(mainWindow);
		studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		setTitle("Formulaire d'enregistrement");
		this.init();
	}
	
	/**
	 * @param studyClass the studyClass to set
	 */
	public void setStudyClass(StudyClass studyClass) {
		this.studyClass = studyClass;
		if(studyClass != null) {
			acronym.getField().setValue(studyClass.getAcronym());
			fullname.getField().setValue(studyClass.getName());
			setTitle(TITLE_2);
		} else {
			acronym.getField().setValue("");
			fullname.getField().setValue("");
			setTitle(TITLE_1);
		}
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
		
		Date now = new Date();
		StudyClass sc = new StudyClass();
		sc.setName(name);
		sc.setAcronym(acronym);
		
		try {
			if (studyClass == null) {
				sc.setRecordDate(now);
				this.studyClassDao.create(sc);
			} else {
				sc.setLastUpdate(now);
				sc.setRecordDate(studyClass.getRecordDate());
				studyClassDao.update(sc, studyClass.getId());
			}
			showMessageDialog("Info", name+"\nEnregistrer avec succes", JOptionPane.INFORMATION_MESSAGE);
			setStudyClass(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
