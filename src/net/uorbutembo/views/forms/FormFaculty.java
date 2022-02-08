/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;

import javax.swing.Box;

import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormFaculty extends DefaultFormPanel {
	private static final long serialVersionUID = 1236148729398198199L;
	
	private final FormGroup<String> abbreviation = FormGroup.createEditText("Abbreviation");
	private final FormGroup<String> fullname = FormGroup.createEditText("Appelation complet");
	
	public FormFaculty() {
		super();
		this.setTitle("Formulaire d'enregistrement");
		this.init();
	}
	
	private void init() {
		final Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.add(this.abbreviation);
		box.add(this.fullname);

		this.getBody().add(box, BorderLayout.CENTER);
	}

}
