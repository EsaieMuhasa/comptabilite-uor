/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_FROMATER;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
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
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String start  = this.startDate.getValue();
		String close = this.closeDate.getValue();
		String label = this.label.getValue();
		
		AcademicYear year = new AcademicYear();
		year.setLabel(label);
		try {
			year.setStartDate(DEFAULT_FROMATER.parse(start));
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(this, "Entrez la date au format valide\n"+DEFAULT_FROMATER.toPattern(), "Format de date invalide", JOptionPane.ERROR_MESSAGE);
		}
		
		if(close != null && !close.trim().isEmpty()) {				
			try {
				year.setStartDate(DEFAULT_FROMATER.parse(close));
			} catch (ParseException e) {
				JOptionPane.showMessageDialog(this, "Entrez la date au format valide\n"+DEFAULT_FROMATER.toPattern(), "Format de date invalide", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		if(year.getStartDate() != null && year.getLabel() != null && !year.getLabel().isEmpty()) {
			year.setRecordDate(new Date());
			try {
				this.academicYearDao.create(year);
				JOptionPane.showMessageDialog(this, "Année académique enregistrer avec success", "Success d'enregistrement", JOptionPane.ERROR_MESSAGE);
			} catch (DAOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Echec d'enregistrement", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
