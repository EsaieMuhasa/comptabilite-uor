/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormUniversitySpend extends DefaultFormPanel {
	private static final long serialVersionUID = 6428356255918143364L;
	
	private final FormGroup<String> title = FormGroup.createTextField("Titre");
	private final FormGroup<String> description = FormGroup.createTextArea("Description", 5, 5);
	
	private UniversitySpendDao universitySpendDao;


	public FormUniversitySpend(MainWindow mainWindow, UniversitySpendDao universitySpendDao) {
		super(mainWindow);
		this.universitySpendDao = universitySpendDao;
		this.setTitle("Formultaire d'enregistrement");
		Box box = Box.createVerticalBox();
		box.add(this.title);
		box.add(this.description);
		
		this.getBody().add(box, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		String title = this.title.getValue();
		String description = this.description.getValue();
		
		UniversitySpend spend = new UniversitySpend();
		spend.setTitle(title);
		spend.setDescription(description);
		spend.setRecordDate(new Date());
		
		try {
			this.universitySpendDao.create(spend);
			this.showMessageDialog("Information", "Success d'enregistrement de la rubrique budgetaire", JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

}
