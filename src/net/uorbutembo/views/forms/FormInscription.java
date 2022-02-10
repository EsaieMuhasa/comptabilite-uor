package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class FormInscription extends DefaultFormPanel{
	
	private static final long serialVersionUID = -7867072774750271198L;
	
	private JLabel picture = new JLabel();
	private JButton pickPicture = new JButton("Photo paceport");
	
	private GridLayout fieldsLayoutLg = new GridLayout(6, 2, DEFAULT_H_GAP, DEFAULT_V_GAP), 
			fieldsLayoutSm = new GridLayout(12, 1, DEFAULT_H_GAP, DEFAULT_V_GAP) ;
	
	private Panel fields;
	private Panel responsiveFileds;
	private Panel panelPicture;
	
	private FormGroup<Faculty> faculty = FormGroup.createComboBox("Faculté");
	private FormGroup<Department> depatement = FormGroup.createComboBox("Département");
	private FormGroup<StudyClass> studyClass = FormGroup.createComboBox("Class d'étude");
	private FormGroup<String> name = FormGroup.createEditText("Nom");
	private FormGroup<String> postName = FormGroup.createEditText("Post-nom");
	private FormGroup<String> lastName = FormGroup.createEditText("Prénom");
	private FormGroup<String> telephone = FormGroup.createEditText("Téléphone");
	private FormGroup<String> email = FormGroup.createEditText("E-mail");
	private FormGroup<String> scool = FormGroup.createEditText("Ecole d'origine");
	private FormGroup<String> birthPlace = FormGroup.createEditText("Leux de naissance");
	private FormGroup<String> birthDate = FormGroup.createEditText("Date de naissance");
	private FormGroup<String> matricul = FormGroup.createEditText("Matricule");
	
	/**
	 * Constructeur par defaut
	 */
	public FormInscription() {
		super();
		this.init();
		this.setTitle("Formulaire d'inscription");
		
		Panel container = new Panel(new BorderLayout());

		container.add(this.fields, BorderLayout.CENTER);
		container.add(this.panelPicture, BorderLayout.EAST);
		
		this.getBody().add(container, BorderLayout.CENTER);
	}
	
	protected void init() {
		responsiveFileds = new Panel(fieldsLayoutLg);
		fields = new Panel(new BorderLayout());
		panelPicture = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		
		
		responsiveFileds.add(depatement);
		responsiveFileds.add(studyClass);
		responsiveFileds.add(this.name);
		responsiveFileds.add(this.postName);
		responsiveFileds.add(this.lastName);
		responsiveFileds.add(this.telephone);
		responsiveFileds.add(this.email);
		responsiveFileds.add(this.scool);
		responsiveFileds.add(this.birthDate);
		responsiveFileds.add(this.birthPlace);
		responsiveFileds.add(this.matricul);
		
		fields.add(faculty, BorderLayout.NORTH);
		fields.add(responsiveFileds, BorderLayout.CENTER);
		
		
		this.panelPicture.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
		this.panelPicture.add(this.pickPicture, BorderLayout.NORTH);
		this.panelPicture.add(this.picture, BorderLayout.CENTER);
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		if(this.getWidth() <= 650) {
			if(this.responsiveFileds.getLayout() != this.fieldsLayoutSm) {
				this.responsiveFileds.setLayout(fieldsLayoutSm);
			}
		} else {
			if(this.responsiveFileds.getLayout() != this.fieldsLayoutLg) {
				this.responsiveFileds.setLayout(this.fieldsLayoutLg);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
	}

}
