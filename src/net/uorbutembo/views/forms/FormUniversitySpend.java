/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.TextArea;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormUniversitySpend extends DefaultFormPanel {
	private static final long serialVersionUID = 6428356255918143364L;
	
	private final TextField<String> title = new TextField<>("Titre");
	private final TextArea description = new TextArea("Description", 5, 5);
	
	private final FormGroup<String> titleGroup = FormGroup.createTextField(title);
	private final FormGroup<String> descriptionGroup = FormGroup.createTextArea(description);
	
	private final UniversitySpendDao universitySpendDao;
	private UniversitySpend spend;

	public FormUniversitySpend(MainWindow mainWindow) {
		super(mainWindow);
		universitySpendDao = mainWindow.factory.findDao(UniversitySpendDao.class);
		setTitle("Formultaire d'enregistrement");
		Box box = Box.createVerticalBox();
		box.add(this.titleGroup);
		box.add(this.descriptionGroup);
		
		this.getBody().add(box, BorderLayout.CENTER);
		title.addCaretListener(event -> {
			validateFields();
		});
		
		description.addCaretListener(event -> {
			validateFields();
		});
		
		validateFields();
	}
	
	/**
	 * @param spend the spend to set
	 */
	public void setSpend (UniversitySpend spend) {
		this.spend = spend;
		
		if (spend != null) {
			title.setValue(spend.getTitle());
			description.setValue(spend.getDescription());
		} else {
			title.setValue("");
			description.setValue("");
		}
	}

	/**
	 * activation/desativation du bouton d'enregistrement.
	 */
	private void validateFields () {
		boolean enable = (title.getText().trim().length() >= 4) && (description.getText().trim().length() >= 20);
		btnSave.setEnabled(enable);
	}
	
	/**
	 * Renvoie la reference vers le bouton de validation de l'enregistremnt
	 * @return
	 */
	public JButton getBtnSave () {
		return btnSave;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		String title = this.title.getValue();
		String description = this.description.getValue();
		
		UniversitySpend spend = new UniversitySpend();
		spend.setTitle(title);
		spend.setDescription(description);
		
		Date now = new Date();
		
		try {
			if (this.spend == null) {
				spend.setRecordDate(now);
				this.universitySpendDao.create(spend);
			} else {
				spend.setLastUpdate(now);
				spend.setRecordDate(this.spend.getRecordDate());
				universitySpendDao.update(spend, this.spend.getId());
			}
			setSpend(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

}
