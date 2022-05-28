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

import net.uorbutembo.beans.UniversityRecipe;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversityRecipeDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.TextArea;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormUniversityRecipe extends DefaultFormPanel {
	private static final long serialVersionUID = 6428356255918143364L;
	
	private final TextField<String> title = new TextField<>("Titre");
	private final TextArea description = new TextArea("Description", 3, 5);
	
	private final FormGroup<String> titleGroup = FormGroup.createTextField(title);
	private final FormGroup<String> descriptionGroup = FormGroup.createTextArea(description);
	
	private final UniversityRecipeDao universityRecipeDao;
	
	private UniversityRecipe recipe;

	public FormUniversityRecipe (MainWindow mainWindow) {
		super(mainWindow);
		universityRecipeDao = mainWindow.factory.findDao(UniversityRecipeDao.class);
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
	 * @param recipe the recipe to set
	 */
	public void setRecipe (UniversityRecipe recipe) {
		this.recipe = recipe;
		
		if (recipe != null) {
			title.setValue(recipe.getTitle());
			description.setValue(recipe.getDescription());
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
		
		UniversityRecipe r = new UniversityRecipe();
		r.setTitle(title);
		r.setDescription(description);
		
		Date now = new Date();
		
		try {
			if (recipe == null) {				
				r.setRecordDate(now);
				universityRecipeDao.create(r);
			} else {
				r.setLastUpdate(now);
				r.setRecordDate(recipe.getRecordDate());
				universityRecipeDao.update(r, recipe.getId());
			}
			setRecipe(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

}
