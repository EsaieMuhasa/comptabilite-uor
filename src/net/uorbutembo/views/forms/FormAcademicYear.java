/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JPanel;

import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 * Formulaire de manutation des annees academique
 */
public class FormAcademicYear extends DefaultFormPanel {
	private static final long serialVersionUID = -4419809391236771300L;
	
	private AcademicYearDao academicYearDao;
	
	private FormGroup<String> startDate = FormGroup.createEditText("Date d'ouverture");
	private FormGroup<String> closeDate = FormGroup.createEditText("Date de fermeture");
	private FormGroup<String> label = FormGroup.createEditText("Label de l'année");

	public FormAcademicYear(AcademicYearDao academicYearDao) {
		super();
		this.academicYearDao = academicYearDao;
		this.setTitle("Formulaire de decalration d'une année academique");
		this.init();
	}
	
	/**
	 * 
	 */
	private void init() {		
		JPanel center = new JPanel(new BorderLayout());
		Box form = Box.createHorizontalBox();
		center.setOpaque(false);
		form.setOpaque(false);
		
		center.add(this.label, BorderLayout.NORTH);
		form.add(this.startDate);
		form.add(this.closeDate);
		
		center.add(form, BorderLayout.CENTER);
		this.getBody().add(center, BorderLayout.CENTER);
	}

}
