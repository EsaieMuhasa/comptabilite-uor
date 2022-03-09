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
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.CheckBox;
import net.uorbutembo.views.MainWindow;
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
	Box box = Box.createVerticalBox();

	/**
	 * 
	 */
	public FormAnnualSpend(MainWindow mainWindow, AnnualSpendDao annualSpendDao) {
		super(mainWindow);
		this.annualSpendDao = annualSpendDao;
		this.setTitle("Rubriques budgetaire");
		this.universitySpendDao = this.annualSpendDao.getFactory().findDao(UniversitySpendDao.class);
		
		this.getBody().add(box, BorderLayout.NORTH);
		this.setVisible(false);
	}
	
	/**
	 * rechargement des donnees
	 * cette methode est automatiquement appeler lors de la modification de l'annee academique
	 */
	private void loadData() {
		if(this.currentYear == null) {
			this.setVisible(false);
			return;
		}

		box.removeAll();
		List<UniversitySpend> spends = this.universitySpendDao.findAll();
		checkBoxs.clear();
		
		for (UniversitySpend spend : spends) {
			CheckBox<UniversitySpend> check = FormUtil.createCheckBox(spend.getTitle(), spend);
			checkBoxs.add(check);
			box.add(check);
		}
		
		this.setVisible(true);
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		if(this.currentYear == null || this.currentYear.getId() != currentYear.getId()) {
			this.currentYear = currentYear;
			this.loadData();//mis en jour des composent du paneau
		}
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
