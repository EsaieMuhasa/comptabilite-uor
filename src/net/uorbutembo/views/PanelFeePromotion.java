/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_EMPTY_BORDER;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.createScrollPane;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.PromotionDao;
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

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFeePromotion extends Panel {
	private static final long serialVersionUID = 5400969854848116850L;
	
	private JButton btnNewFee = new JButton("Ajout un montant");
	
	private AllocationCostDao allocationCostDao;
	private AcademicFeeDao academicFeeDao;
	private AcademicYearDao academicYearDao;
	private AnnualSpendDao annualSpendDao;
	private PromotionDao promotionDao;
	
	private Panel center = new Panel(new BorderLayout());
	private FormFeePromotion formFeePromotion;
	private Panel panelShowConfig = new Panel(new BorderLayout());
	private final Cursor cursorWait = new Cursor(Cursor.WAIT_CURSOR);
	
	
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
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		allocationCostDao = mainWindow.factory.findDao(AllocationCostDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		tableModel = new PromotionTableModel(promotionDao);
		table =  new Table(tableModel);
		formFeePromotion = new FormFeePromotion(mainWindow, promotionDao);
		
		formCost = new FormGroupAllocationCost(allocationCostDao);
		
		Panel panelForm = new Panel(new BorderLayout());
		panelForm.add(formFeePromotion, BorderLayout.CENTER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Détailles des répartitions", panelShowConfig);
		tabbedPane.addTab("Association des aux promotions", panelForm);
		
		panelShowConfig.setBorder(new EmptyBorder(5, 5, 5, 0));
		
		btnNewFee.setEnabled(false);
		btnNewFee.addActionListener(event -> {
			if(dialogAcademicFee == null) {
				dialogAcademicFee = new Dialog(mainWindow, new FormAcademicFee(mainWindow, this.academicFeeDao));
			}
			
			dialogAcademicFee.setVisible(true);
		});

		this.initPanelShowConfig();
		
		center.add(tabbedPane, BorderLayout.CENTER);
		this.add(center, BorderLayout.CENTER);
		
		academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onCreate(AcademicFee e, int requestId) {
				if(academicYearDao.isCurrent(currentYear))
					listModel.addElement(e);
			}
			
			@Override
			public void onUpdate (AcademicFee e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)) {					
					for (int i = 0, count = listModel.getSize(); i < count; i++) {						
						if(listModel.get(i).getId() == e.getId()){
							listModel.set(i, e);
							break;
						}
					}
				}
			}
			
			@Override
			public void onDelete(AcademicFee e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)) {					
					for (int i = 0, count = listModel.getSize(); i < count; i++) {						
						if(listModel.get(i).getId() == e.getId()){
							listModel.remove(i);
							setCurrentYear(currentYear);
							break;
						}
					}
				}
			}
		});
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
		tabbed.addTab("Répartition", formCost);
		
		westPanel.add(createScrollPane(feeList), BorderLayout.CENTER);
		westPanel.add(btnNewFee, BorderLayout.SOUTH);
		westPanel.setBorder(DEFAULT_EMPTY_BORDER);
		
		westBorder.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		westBorder.add(westPanel, BorderLayout.CENTER);

		centerPanel.setBorder(DEFAULT_EMPTY_BORDER);
		centerPanel.add(table, BorderLayout.NORTH);
		
		panelShowConfig.add(tabbed, BorderLayout.CENTER);
		panelShowConfig.add(westBorder, BorderLayout.EAST);
		
		feeList.setBackground(FormUtil.BKG_END);
		feeList.addListSelectionListener(event -> {
			int index = feeList.getSelectedIndex();
			if (index == -1)
				return;
			
			AcademicFee fee = listModel.get(index);
			
			tableModel.clear();
			waiting(true);
			Thread t = new Thread(()-> {				
				if(promotionDao.checkByAcademicFee(fee.getId())) {				
					List<Promotion> promotions = promotionDao.findByAcademicFee(fee);
					for (Promotion p : promotions) {
						tableModel.addRow(p);
					}
				}
				
				pieModel.removeAll();
				if(allocationCostDao.checkByAcademicFee(fee.getId())) {
					List<AllocationCost> costs = this.allocationCostDao.findByAcademicFee(fee);
					for (int i=0, max=costs.size(); i<max;i++) {
						AllocationCost cost = costs.get(i);
						int color = i%(FormUtil.COLORS.length-1);
						DefaultPiePart part = new DefaultPiePart(FormUtil.COLORS[color], cost.getAmount(), cost.getAmount()+"");
						pieModel.addPart(part);
					}
				}
				
				List<AnnualSpend> spends = annualSpendDao.checkByAcademicYear(currentYear.getId())? annualSpendDao.findByAcademicYear(currentYear) : new ArrayList<>();
				
				formCost.init(fee, spends);
				waiting(false);
			});
			
			t.start();
		});
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		
		listModel.clear();
		tableModel.clear();
		formCost.clear();
		
		if(currentYear != null && this.academicFeeDao.checkByAcademicYear(currentYear.getId())) {
			List<AcademicFee> data = this.academicFeeDao.findByAcademicYear(currentYear);
			for (AcademicFee fee : data) {
				listModel.addElement(fee);
			}
			feeList.setSelectedIndex(0);
		}
		
		formFeePromotion.setCurrentYear(currentYear);
		btnNewFee.setEnabled(true);
		btnNewFee.setVisible(academicYearDao.isCurrent(currentYear));
	}
	
	private void waiting (boolean wait) {
		btnNewFee.setEnabled(!wait);
		feeList.setEnabled(!wait);
		if (wait) {
			setCursor(cursorWait);
			feeList.setCursor(cursorWait);
		} else  {
			setCursor(Cursor.getDefaultCursor());
			feeList.setCursor(Cursor.getDefaultCursor());
		}
	}

}
