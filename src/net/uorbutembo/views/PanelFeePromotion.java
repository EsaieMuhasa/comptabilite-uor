/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_EMPTY_BORDER;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.createScrollPane;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.views.forms.FormAcademicFee;
import net.uorbutembo.views.forms.FormFeePromotion;
import net.uorbutembo.views.forms.FormGroupAllocationCost;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PromotionTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFeePromotion extends Panel {
	private static final long serialVersionUID = 5400969854848116850L;
	
	private Button btnNewFee = new Button(new ImageIcon(R.getIcon("plus")), "Ajout un montant");
	
	private FeePromotionDao feePromotionDao;
	private AllocationCostDao allocationCostDao;
	private AcademicFeeDao academicFeeDao;
	private AnnualSpendDao annualSpendDao;
	private PromotionDao promotionDao;
	
	private Panel center = new Panel(new BorderLayout());
	private FormFeePromotion form;
	private FormFeePromotion formFeePromotion;
	private Panel panelShowConfig = new Panel(new BorderLayout());
	
	
	private DefaultListModel<AcademicFee> listModel = new DefaultListModel<>();//liste des frais universitaire
	private JList<AcademicFee> feeList = new JList<>(listModel);
	private PromotionTableModel tableModel;
	private Table table;
	
	private DefaultPieModel pieModel = new DefaultPieModel();
	private FormGroupAllocationCost formCost;
	
	private AcademicYear currentYear;
	
	private Dialog dialogAcademicFee;

	/**
	 * 
	 */
	public PanelFeePromotion(MainWindow mainWindow) {
		super(new BorderLayout());
		this.feePromotionDao = mainWindow.factory.findDao(FeePromotionDao.class);
		this.academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		this.promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		this.allocationCostDao = mainWindow.factory.findDao(AllocationCostDao.class);
		this.annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		
		tableModel = new PromotionTableModel(promotionDao);
		table =  new Table(tableModel);
		formFeePromotion = new FormFeePromotion(feePromotionDao);
		
		formCost = new FormGroupAllocationCost(feePromotionDao);
		
		Panel panelForm = new Panel(new BorderLayout());
		panelForm.add(formFeePromotion, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Mode lecture", panelShowConfig);
		tabbedPane.addTab("Mode ecriture", panelForm);
		
		panelShowConfig.setBorder(new EmptyBorder(5, 5, 5, 0));
		
		btnNewFee.addActionListener(event -> {
			if(dialogAcademicFee == null) {
				dialogAcademicFee = new Dialog(mainWindow, new FormAcademicFee(this.academicFeeDao));
			}
			
			dialogAcademicFee.setVisible(true);
		});

		this.initPanelShowConfig();
		
		center.add(tabbedPane, BorderLayout.CENTER);
		this.add(center, BorderLayout.CENTER);
	}
	
	/**
	 * int
	 */
	private void initPanelShowConfig() {
		Panel westPanel = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP)),
				westBorder = new Panel(new BorderLayout());
		Panel centerPanel = new  Panel(new BorderLayout());
		
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbed.addTab("Promotions", createScrollPane(centerPanel));
		tabbed.addTab("Repartition", formCost);
		
		westPanel.add(createScrollPane(feeList), BorderLayout.CENTER);
		westPanel.add(btnNewFee, BorderLayout.SOUTH);
		westPanel.setBorder(DEFAULT_EMPTY_BORDER);
		
		westBorder.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		westBorder.add(westPanel, BorderLayout.CENTER);

		centerPanel.setBorder(DEFAULT_EMPTY_BORDER);
		centerPanel.add(table, BorderLayout.NORTH);
		
		panelShowConfig.add(tabbed, BorderLayout.CENTER);
		panelShowConfig.add(westBorder, BorderLayout.WEST);
		
		feeList.setBackground(FormUtil.BKG_END);
		feeList.addListSelectionListener(event -> {
			AcademicFee fee = listModel.get(feeList.getSelectedIndex());
			
			tableModel.clear();
			if(this.feePromotionDao.checkByAcademicFee(fee.getId())) {				
				List<FeePromotion> fes = this.feePromotionDao.findByAcademicFee(fee);
				for (FeePromotion f : fes) {
					tableModel.addRow(f.getPromotion());
				}
			}
			
			pieModel.removeAll();
			if(this.allocationCostDao.checkByAcademicFee(fee.getId())) {
				List<AllocationCost> costs = this.allocationCostDao.findByAcademicFee(fee);
				for (int i=0, max=costs.size(); i<max;i++) {
					AllocationCost cost = costs.get(i);
					int color = i%(FormUtil.COLORS.length-1);
					DefaultPiePart part = new DefaultPiePart(FormUtil.COLORS[color], cost.getAmount(), cost.getAmount()+"");
					pieModel.addPart(part);
				}
			}
			
			List<AnnualSpend> spends = this.annualSpendDao.findkByAcademicYear(currentYear);
			
			formCost.init(fee, spends);
		});
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		
		listModel.clear();
		
		if(this.academicFeeDao.checkByAcademicYear(currentYear.getId())) {
			List<AcademicFee> data = this.academicFeeDao.findByAcademicYear(currentYear);
			for (AcademicFee fee : data) {
				listModel.addElement(fee);
			}
			feeList.setSelectedIndex(0);
		}
		
		
		if(form != null)
			this.form.setCurrentYear(currentYear);
		
		formFeePromotion.setCurrentYear(currentYear);
		
	}

}
