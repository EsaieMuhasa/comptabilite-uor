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
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 * Formulaire de manutation des annees academique
 */
public class FormAcademicYear extends DefaultFormPanel {
	private static final long serialVersionUID = -4419809391236771300L;
	
	private AcademicYearDao academicYearDao;
	
	private FormGroup<String> startDate = FormGroup.createTextField("Date d'ouverture");
	private FormGroup<String> closeDate = FormGroup.createTextField("Date de fermeture");
	private FormGroup<String> label = FormGroup.createTextField("Label de l'année");
	
	private AcademicYear academicYear;// != null lors de la modification

	/**
	 * @param mainWindow
	 * @param academicYearDao
	 */
	public FormAcademicYear(MainWindow mainWindow, AcademicYearDao academicYearDao) {
		super(mainWindow);
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
	
	/**
	 * @param academicYear the academicYear to set
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
		label.getField().setValue(academicYear.getLabel());
		startDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(academicYear.getStartDate()));
		if(academicYear.getCloseDate() != null)
			closeDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(academicYear.getCloseDate()));
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
			Date now = new Date();
			try {
				if(academicYear == null){
					year.setRecordDate(now);
					this.academicYearDao.create(year);
				} else {
					year.setLastUpdate(now);
					year.setRecordDate(academicYear.getRecordDate());
					this.academicYearDao.update(year, academicYear.getId());
				}
				academicYear = null;
				this.showMessageDialog("Information", "Année académique enregistrer avec success", JOptionPane.INFORMATION_MESSAGE);
			} catch (DAOException e) {
				this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
