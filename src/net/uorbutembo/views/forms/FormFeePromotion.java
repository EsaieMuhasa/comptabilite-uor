/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.CheckBox;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.RadioButton;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormFeePromotion extends DefaultFormPanel {
	private static final long serialVersionUID = 6991790198392841255L;
	
	private AcademicYear currentYear;
	private PromotionDao promotionDao;
	private AcademicFeeDao academicFeeDao;
	private FeePromotionDao feePromotionDao;

	private List<CheckBox<Promotion>> checkBoxs = new ArrayList<>();
	private List<RadioButton<AcademicFee>> radioButtons = new ArrayList<>();
	
	private GridLayout gridCheckBoxPromotion = new GridLayout();
	private Panel panelCheckBoxPromotions = new Panel(gridCheckBoxPromotion);
	private Panel panelRadioButtonFees = new Panel(new FlowLayout(FlowLayout.LEFT));
	private ButtonGroup groupRadioButtonFees = new ButtonGroup();

	/**
	 * 
	 */
	public FormFeePromotion(MainWindow mainWindow, FeePromotionDao feePromotionDao) {
		super(mainWindow);
		this.setTitle("Formulaire d'enregistrement");
		this.feePromotionDao = feePromotionDao;
		this.promotionDao  = feePromotionDao.getFactory().findDao(PromotionDao.class);
		this.academicFeeDao = feePromotionDao.getFactory().findDao(AcademicFeeDao.class);
		
		panelRadioButtonFees.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Montant a payer"));
		panelCheckBoxPromotions.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Promotions"));
		
		Panel panel = new Panel(new BorderLayout(5, 5));
		panel.add(panelCheckBoxPromotions, BorderLayout.NORTH);
		panel.add(panelRadioButtonFees, BorderLayout.SOUTH);
		this.getBody().add(panel, BorderLayout.CENTER);
		
		this.init();
		this.setVisible(false);
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		if(this.currentYear == null || currentYear.getId() != this.currentYear.getId()) {			
			this.currentYear = currentYear;
			this.loadData();
		}
	}
	
	
	/**
	 * Rechargement des donnees
	 * utile lors de la modificationd de l'annee academique via le mutateur setCurrentYear
	 */
	private void loadData() {
		
		if(this.currentYear == null) 
			return;
		
		final List<Promotion> promotions = this.promotionDao.checkByAcademicYear(this.currentYear.getId())?
				this.promotionDao.findByAcademicYear(this.currentYear.getId()) : new ArrayList<>();
		final List<AcademicFee> academicFees = this.academicFeeDao.checkByAcademicYear(this.currentYear.getId())?
				this.academicFeeDao.findByAcademicYear(this.currentYear.getId()) : new ArrayList<>();
		
		final int row = (promotions.size()%4)!=0? (promotions.size()/4)+1 : promotions.size()/4;
		this.gridCheckBoxPromotion.setColumns(3);
		this.gridCheckBoxPromotion.setRows(row);
		
		panelRadioButtonFees.removeAll();
		for (JRadioButton btn : radioButtons) {//supression de enciens composant
			groupRadioButtonFees.remove(btn);
		}
		radioButtons.clear();
		for (AcademicFee fee : academicFees) {
			RadioButton<AcademicFee> r = FormUtil.createRadioButon(fee.getAmount()+" USD", fee);
			panelRadioButtonFees.add(r);
			groupRadioButtonFees.add(r);
			this.radioButtons.add(r);
		}
		
		panelCheckBoxPromotions.removeAll();
		checkBoxs.clear();
		for (Promotion p : promotions) {
			CheckBox<Promotion> c = FormUtil.createCheckBox(p.getStudyClass().getAcronym()+" "+p.getDepartment().getName(), p);
			panelCheckBoxPromotions.add(c);
			this.checkBoxs.add(c);
		}
		
		this.setVisible(true);
	}

	/**
	 * Ecoute des evenements du DAO
	 */
	private void init() {
		
		this.promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public void onCreate(Promotion p, int requestId) {
				CheckBox<Promotion> check = FormUtil.createCheckBox(p.getStudyClass().getAcronym()+" "+p.getDepartment().getName(), p);
				checkBoxs.add(check);
				panelCheckBoxPromotions.add(check);
				
				int row = (checkBoxs.size()%4)!=0? (checkBoxs.size()/4)+1 : checkBoxs.size()/4;
				gridCheckBoxPromotion.setRows(row);
			}
		});
		
		this.academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onCreate(AcademicFee fee, int requestId) {
				RadioButton<AcademicFee> r = FormUtil.createRadioButon(fee.getAmount()+" USD", fee);
				panelRadioButtonFees.add(r);
				groupRadioButtonFees.add(r);
				radioButtons.add(r);
			}
		});
		
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
//		System.out.println("width: "+this.getWidth());
		if(checkBoxs.size() == 0)
			return;
		
		if(this.getWidth() <= 800) {
			int row = (checkBoxs.size()%3)!=0? (checkBoxs.size()/3)+1 : checkBoxs.size()/3;
			gridCheckBoxPromotion.setRows(row);
			gridCheckBoxPromotion.setColumns(3);
		} else {
			int row = (checkBoxs.size()%4)!=0? (checkBoxs.size()/4)+1 : checkBoxs.size()/4;
			gridCheckBoxPromotion.setRows(row);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		List<Promotion> promotions = new ArrayList<>();
		for (CheckBox<Promotion> check : this.checkBoxs) {
			if(check.isSelected()) {
				promotions.add(check.getData());
			}
		}
		
		AcademicFee fee = null;
		for (RadioButton<AcademicFee> radio : this.radioButtons) {
			if(radio.isSelected()) {
				fee = radio.getData();
				break;
			}
		}
		
		if(fee == null || promotions.isEmpty()) {
			this.showMessageDialog("Alert", "Impossible d'effectuer cette requette. \nAssurez-vous d'avoir selectionner aumoin une promotion\n et le fais qui vous voulez y associer!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		FeePromotion [] fees = new FeePromotion[promotions.size()];
		Date recordDate = new Date();
		
		String message = "";
		for (int i=0, max =promotions.size(); i<max; i++) {
			Promotion promotion  = promotions.get(i);
			FeePromotion f = new FeePromotion();
			f.setRecordDate(recordDate);
			f.setPromotion(promotion);
			f.setAcademicFee(fee);
			fees[i] = f;
			message += "\n"+promotion.getStudyClass().getAcronym()+" "+promotion.getDepartment().getName()+" \t-> "+fee.getAmount()+" USD";
		}
		
		try {
			this.feePromotionDao.create(fees);
			this.showMessageDialog("Success d'enregistrement ",""+message, JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
	}

}
