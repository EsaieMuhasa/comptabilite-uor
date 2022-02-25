/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.CheckBox;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormAnnualSpend extends DefaultFormPanel {
	private static final long serialVersionUID = 7676660372298228089L;
	
	private AnnualSpendDao annualSpendDao;
	private UniversitySpendDao universitySpendDao;
	private AcademicYear currentYear;
	
	private List<CheckBox<UniversitySpend>> checkBoxs = new ArrayList<>();

	/**
	 * 
	 */
	public FormAnnualSpend(AnnualSpendDao annualSpendDao) {
		super();
		this.annualSpendDao = annualSpendDao;
		this.setTitle("Rubriques budgetaire");
		this.currentYear = annualSpendDao.getFactory().findDao(AcademicYearDao.class).findCurrent();
		this.universitySpendDao = this.annualSpendDao.getFactory().findDao(UniversitySpendDao.class);
		
		List<UniversitySpend> spends = this.universitySpendDao.findAll();
		Box box = Box.createVerticalBox();
		
		for (UniversitySpend spend : spends) {
			CheckBox<UniversitySpend> check = FormUtil.createCheckBox(spend.getTitle(), spend);
			checkBoxs.add(check);
			box.add(check);
		}
		
		this.getBody().add(box, BorderLayout.NORTH);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		List<UniversitySpend> spends =new ArrayList<>();
		
		String message = "";
		for (CheckBox<UniversitySpend> check : this.checkBoxs) {
			if(check.isSelected()) {				
				spends.add(check.getData());
				message += check.getData().getTitle()+"\n";
			}
		}
		
		if(spends.isEmpty()) {
			this.showMessageDialog("Alert", "Impossible d'effectuer cette requette. \nAssurez-vous d'avoir selectionner aumoin une case!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		AnnualSpend all [] = new AnnualSpend[spends.size()];
		Date recordDate = new Date();
		for (int i=0, max = spends.size(); i< max; i++) {
			UniversitySpend spend = spends.get(i);
			AnnualSpend as = new AnnualSpend();
			as.setUniversitySpend(spend);
			as.setAcademicYear(this.currentYear);
			as.setRecordDate(recordDate);
			all[i] = as;
		}
		try {
			this.annualSpendDao.create(all);
			this.showMessageDialog("Success d'enregistrement ","Rubique du buget pour l'annee "+this.currentYear.getLabel()+"\n"+message, JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
