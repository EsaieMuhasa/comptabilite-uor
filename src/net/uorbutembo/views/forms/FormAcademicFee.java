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
	private AcademicFee academicFee;//lors de la mise en jours


	public FormAcademicFee (MainWindow mainWindow) {
		super(mainWindow);
		this.academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		this.setTitle("Formultaire d'enregistrement");
		Box box = Box.createVerticalBox();
		box.add(amount);
		box.add(description);
		
		this.getBody().add(box, BorderLayout.CENTER);
	}

	/**
	 * @return the academicFee
	 */
	public AcademicFee getAcademicFee() {
		return academicFee;
	}

	/**
	 * @param academicFee the academicFee to set
	 */
	public void setAcademicFee (AcademicFee academicFee) {
		this.academicFee = academicFee;
		
		if(academicFee != null) {
			amount.getField().setValue(academicFee.getAmount()+"");
			description.getField().setValue(academicFee.getDescription());
		} else {
			amount.getField().setValue("");
			description.getField().setValue("");
		}
		
		setTitle(academicFee == null? TITLE_1 : TITLE_2);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		float amount = Float.parseFloat(this.amount.getValue());
		String description = this.description.getValue();
		Date now  = new Date();
		
		AcademicFee fee = new AcademicFee();
		fee.setAmount(amount);
		fee.setDescription(description);
		fee.setAcademicYear(mainWindow.factory.findDao(AcademicYearDao.class).findCurrent());
		
		try {
			if(academicFee != null) {
				fee.setRecordDate(academicFee.getRecordDate());
				fee.setLastUpdate(now);
				academicFeeDao.update(fee, academicFee.getId());
				showMessageDialog("Information", "Success d'enregistrement des modification", JOptionPane.INFORMATION_MESSAGE);
			} else {				
				fee.setRecordDate(now);
				academicFeeDao.create(fee);
				showMessageDialog("Information", "Success d'enregistrement du frais", JOptionPane.INFORMATION_MESSAGE);
			}
			
			setAcademicFee(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

}
