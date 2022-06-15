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
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.CheckBox;
import net.uorbutembo.tools.FormUtil;
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
	public FormAnnualSpend(MainWindow mainWindow) {
		super(mainWindow);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		universitySpendDao = annualSpendDao.getFactory().findDao(UniversitySpendDao.class);
		setTitle("Rubriques budgetaire");
		
		this.getBody().add(box, BorderLayout.NORTH);
		this.setVisible(false);
		
		annualSpendDao.addListener(new DAOAdapter<AnnualSpend>() {
			@Override
			public synchronized void onDelete(AnnualSpend e, int requestId) {
				UniversitySpend spend = universitySpendDao.findById(e.getUniversitySpend().getId());
				CheckBox<UniversitySpend> check = FormUtil.createCheckBox(spend.getTitle(), spend);
				checkBoxs.add(check);
				box.add(check);
			}
		});
		
		universitySpendDao.addListener(new DAOAdapter<UniversitySpend>() {
			@Override
			public synchronized void onCreate(UniversitySpend e, int requestId) {
				CheckBox<UniversitySpend> check = FormUtil.createCheckBox(e.getTitle(), e);
				checkBoxs.add(check);
				box.add(check);
			}
			
			@Override
			public synchronized void onUpdate(UniversitySpend e, int requestId) {
				for (CheckBox<UniversitySpend> c : checkBoxs) {
					if(c.getData().getId() == e.getId()) {
						c.setText(e.getTitle());
						c.setData(e);
						break;
					}
				}
			}
			
			@Override
			public synchronized void onDelete(UniversitySpend e, int requestId) {
				for (CheckBox<UniversitySpend> c : checkBoxs) {
					if(c.getData().getId() == e.getId()) {
						box.remove(c);
						break;
					}
				}
			}
			
			@Override
			public synchronized void onDelete(UniversitySpend[] e, int requestId) {
				for (UniversitySpend u : e) {
					onDelete(u, requestId);
				}
			}
		});
	}
	
	/**
	 * rechargement des donnees
	 * cette methode est automatiquement appeler lors de la modification de l'annee academique
	 */
	public void loadData () {
		if(this.currentYear == null || (universitySpendDao.countAll() == 0)) {
			this.setVisible(false);
			return;
		}

		box.removeAll();
		List<UniversitySpend> spends = this.universitySpendDao.findAll();
		checkBoxs.clear();
		
		for (UniversitySpend spend : spends) {
			if(annualSpendDao.check(currentYear, spend))
				continue;
			
			CheckBox<UniversitySpend> check = FormUtil.createCheckBox(spend.getTitle(), spend);
			checkBoxs.add(check);
			box.add(check);
		}
		
		if (!checkBoxs.isEmpty())
			this.setVisible(true);
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		if(this.currentYear == null || this.currentYear.getId() != currentYear.getId()) {
			this.currentYear = currentYear;
			loadData();//mis en jour des composent du paneau
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
			for (CheckBox<UniversitySpend> check : this.checkBoxs) {
				if(check.isSelected())				
					box.remove(check);
			}
			this.showMessageDialog("Success d'enregistrement ","Rubique du buget pour l'annee "+this.currentYear.getLabel()+"\n"+message, JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
