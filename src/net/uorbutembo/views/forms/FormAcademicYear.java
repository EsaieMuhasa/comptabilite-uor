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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * Formulaire de manutation des annees academique
 */
public class FormAcademicYear extends DefaultFormPanel {
	private static final long serialVersionUID = -4419809391236771300L;
	
	private AcademicYearDao academicYearDao;
	
	private FormGroup<String> startDate = FormGroup.createTextField("Date d'ouverture  (jj-mm-aaaa)");
	private FormGroup<String> closeDate = FormGroup.createTextField("Date de fermeture  (jj-mm-aaaa)");
	private FormGroup<String> label = FormGroup.createTextField("Label de l'année");
	private Button btnCancel = new Button(new ImageIcon(R.getIcon("close")), "Annuler");
	
	private AcademicYear academicYear;// != null lors de la modification

	/**
	 * @param mainWindow
	 */
	public FormAcademicYear(MainWindow mainWindow) {
		super(mainWindow);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		setTitle(TITLE_1);
		init();
	}
	
	/**
	 * initalisation d'interface graphique
	 */
	private void init() {		
		JPanel center = new JPanel(new BorderLayout());
		Box form = Box.createHorizontalBox();
		center.setOpaque(false);
		form.setOpaque(false);
		
		center.add(label, BorderLayout.NORTH);
		form.add(startDate);
		form.add(closeDate);
		
		center.add(form, BorderLayout.CENTER);
		getBody().add(center, BorderLayout.CENTER);
		getFooter().add(btnCancel);
		btnCancel.setVisible(false);
		
		btnCancel.addActionListener(event -> {
			setAcademicYear(null);
		});
	}
	
	/**
	 * @param academicYear the academicYear to set
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
		boolean isnull = academicYear == null;
		setTitle(isnull? TITLE_1 : TITLE_2);
		btnCancel.setVisible(!isnull);
		
		if (!isnull) {
			label.getField().setValue(academicYear.getLabel());
			startDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(academicYear.getStartDate()));
			if(academicYear.getCloseDate() != null)
				closeDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(academicYear.getCloseDate()));
		} else {
			label.getField().setValue("");
			startDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(new Date()));
			closeDate.getField().setValue("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String start  = this.startDate.getValue();
		String close = this.closeDate.getValue();
		String label = this.label.getValue();
		
		long id = academicYear == null? 0 : academicYear.getId();
		AcademicYear year = new AcademicYear();
		year.setLabel(label);
		String message = "";
		
		if (label.trim().length() == 0)
			message += "Entrez le labele de l'annee academique\n";
		else if (academicYearDao.checkByLabel(label, id))
			message += "le label "+label+", est déjà utiliser\n";
		
		try {
			year.setStartDate(DEFAULT_FROMATER.parse(start));
		} catch (ParseException e) {
			message += "Entrez la date d'ouverture de l'année academique au format valide\n";
		}
		
		if(close != null && !close.trim().isEmpty()) {				
			try {
				year.setStartDate(DEFAULT_FROMATER.parse(close));
			} catch (ParseException e) {
				message += "Entrez la date de fermeture de l'année academique au format valide\n";
			}
		}
		
		if (message.length() != 0){
			JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
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
				showMessageDialog("Information", "Année académique enregistrer avec success", JOptionPane.INFORMATION_MESSAGE);
			} catch (DAOException e) {
				showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
