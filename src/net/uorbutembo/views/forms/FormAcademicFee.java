/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormAcademicFee extends DefaultFormPanel {
	private static final long serialVersionUID = 6428356255918143364L;
	
	private final FormGroup<String> amount = FormGroup.createTextField("Montant (en USD)");
	private final FormGroup<String> description = FormGroup.createTextArea("Description", 5, 5);
	
	private AcademicFeeDao academicFeeDao;

	/**
	 * 
	 */
	public FormAcademicFee(MainWindow mainWindow, AcademicFeeDao academicFeeDao) {
		super(mainWindow);
		this.academicFeeDao = academicFeeDao;
		this.setTitle("Formultaire d'enregistrement");
		Box box = Box.createVerticalBox();
		box.add(this.amount);
		box.add(this.description);
		
		this.getBody().add(box, BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		
		float amount = Float.parseFloat(this.amount.getValue());
		String description = this.description.getValue();
		
		AcademicFee fee = new AcademicFee();
		fee.setAmount(amount);
		fee.setDescription(description);
		fee.setRecordDate(new Date());
		fee.setAcademicYear(this.academicFeeDao.getFactory().findDao(AcademicYearDao.class).findCurrent());
		
		try {
			this.academicFeeDao.create(fee);
			this.showMessageDialog("Information", "Success d'enregistrement du frais", JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

}
