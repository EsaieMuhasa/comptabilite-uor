/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.tools.FormUtil.DEFAULT_EMPTY_BORDER;
import static net.uorbutembo.tools.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.tools.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.tools.FormUtil.createScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

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
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.forms.FormAcademicFee;
import net.uorbutembo.views.forms.FormFeePromotion;
import net.uorbutembo.views.forms.FormGroupAllocationCost;
import net.uorbutembo.views.models.PromotionTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFeePromotion extends Panel {
	private static final long serialVersionUID = 5400969854848116850L;
	
	private final JButton btnNewFee = new Button(new ImageIcon(R.getIcon("new")), "Nouveau montant");
	{btnNewFee.setBorder(DEFAULT_EMPTY_BORDER);}
	
	private AllocationCostDao allocationCostDao;
	private AcademicFeeDao academicFeeDao;
	private AcademicYearDao academicYearDao;
	private AnnualSpendDao annualSpendDao;
	private PromotionDao promotionDao;
	
	private Panel center = new Panel(new BorderLayout());
	private FormFeePromotion formFeePromotion;
	private Panel panelShowConfig = new Panel(new BorderLayout());
	private final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	
	
	private DefaultListModel<AcademicFee> listModel = new DefaultListModel<>();//liste des frais universitaire
	private JList<AcademicFee> feeList = new JList<>(listModel);
	private PromotionTableModel tableModel;
	private Table table;
	private TablePanel tablePanel;
	
	private DefaultPieModel pieModel = new DefaultPieModel();
	private FormGroupAllocationCost formCost;
	
	private AcademicYear currentYear;
	private Dialog dialogAcademicFee;
	private FormAcademicFee formAcademicFee;
	
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem itemUpdate = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private final JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if (!listModel.isEmpty() && e.isPopupTrigger())
				popupMenu.show(feeList, e.getX(), e.getY());
		}
	};
	
	private boolean listListenerInited = false;
	private final ListSelectionListener listListener = (event) -> {
		tableModel.clear();
		waiting(true);
		Thread t = new Thread(()-> {				
			updateConfig(false);
			waiting(false);
		});
		
		t.start();
	};

	private final MainWindow mainWindow;
	
	private final DAOAdapter<AcademicFee> feeAdapter = new DAOAdapter<AcademicFee>() {
		@Override
		public void onCreate(AcademicFee e, int requestId) {
			if(academicYearDao.isCurrent(currentYear))
				listModel.addElement(e);
			dialogAcademicFee.setVisible(false);
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
			dialogAcademicFee.setVisible(false);
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
	};
	
	public PanelFeePromotion(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		allocationCostDao = mainWindow.factory.findDao(AllocationCostDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		tableModel = new PromotionTableModel(promotionDao);
		table =  new Table(tableModel);
		tablePanel = new TablePanel(table, "");
		formFeePromotion = new FormFeePromotion(mainWindow);
		
		formCost = new FormGroupAllocationCost(allocationCostDao);
		
		Panel panelForm = new Panel(new BorderLayout());
		panelForm.add(formFeePromotion, BorderLayout.CENTER);
		panelForm.setBorder(DEFAULT_EMPTY_BORDER);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Détailles des répartitions", panelShowConfig);
		tabbedPane.addTab("Association des aux promotions", panelForm);
		
		panelShowConfig.setBorder(new EmptyBorder(5, 5, 5, 0));
		
		btnNewFee.setEnabled(false);
		btnNewFee.addActionListener(event -> {
			createAcademicFee();
		});

		initPanelShowConfig();
		
		center.add(tabbedPane, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);
		setBorder(new EmptyBorder(0, 2, 2, 1));
		
		academicFeeDao.addListener(feeAdapter);
		
		initPopup();//click doit pour la modification et la suppression
	}
	
	/**
	 * construction de la boite de dialogue qui gere la creation et la modification
	 * des frais academique
	 */
	private void buildAcademicFeeDialog () {		
		if(dialogAcademicFee == null) {
			formAcademicFee = new FormAcademicFee(mainWindow);
			final Panel padding = new Panel(new BorderLayout());
			padding.add(formAcademicFee, BorderLayout.CENTER);
			padding.setBorder(DEFAULT_EMPTY_BORDER);
			dialogAcademicFee = new Dialog(mainWindow, padding);
			dialogAcademicFee.setResizable(false);
		}
		dialogAcademicFee.setLocationRelativeTo(mainWindow);
	}
	
	/**
	 * demande d'ouverture de la boite de dialogue de creation
	 * d'une nouvelle occurence pour les frais academiques
	 */
	private void createAcademicFee () {
		buildAcademicFeeDialog();
		dialogAcademicFee.setTitle("Enregistrement des frais univesitaire");
		dialogAcademicFee.setVisible(true);
	}
	
	/**
	 * demande de modification des frais universitaire
	 * @param fee
	 */
	private void updateAcademicFee (AcademicFee fee) {
		buildAcademicFeeDialog();
		dialogAcademicFee.setTitle("Edition des frais univesitaire");
		formAcademicFee.setAcademicFee(fee);
		dialogAcademicFee.setVisible(true);
	}
	
	/**
	 * popup qui contiens l'itemMenu de modification et de suppression
	 */
	private void initPopup() {
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
		
		itemUpdate.addActionListener(event-> {
			AcademicFee fee = listModel.get(feeList.getSelectedIndex());
			updateAcademicFee(fee);
		});
		itemDelete.addActionListener(event -> {
			AcademicFee fee = listModel.get(feeList.getSelectedIndex());
			
			if (promotionDao.checkByAcademicFee(fee)) {
				JOptionPane.showMessageDialog(mainWindow, 
						"Impossible d'effectuer cette operation car \ncertains promotions ont une liaison avec \nles frais que vous voulez supprimer",
						"Erreur", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			int status  = JOptionPane.showConfirmDialog(mainWindow, ""
					+ "Voulez-vous vraiment supprimer le "+fee.toString()+"\nN.B: Cette suppression est definitive",
					"Suppression", JOptionPane.YES_NO_OPTION);
			
			if (status == JOptionPane.OK_OPTION) {
				try {					
					academicFeeDao.delete(fee.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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
		Panel paddingFormCost = new Panel(new BorderLayout());
		
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbed.addTab("Promotions", new ImageIcon(R.getIcon("list")), createScrollPane(centerPanel));
		tabbed.addTab("Répartition", new ImageIcon(R.getIcon("pie")), paddingFormCost);
		
		westPanel.add(createScrollPane(feeList), BorderLayout.CENTER);
		westPanel.add(btnNewFee, BorderLayout.SOUTH);
		westPanel.setBorder(DEFAULT_EMPTY_BORDER);
		
		westBorder.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		westBorder.add(westPanel, BorderLayout.CENTER);

		centerPanel.setBorder(new EmptyBorder(0, 0, 0, 5));
		centerPanel.add(tablePanel, BorderLayout.CENTER);
		
		paddingFormCost.add(formCost, BorderLayout.CENTER);
		paddingFormCost.setBorder(centerPanel.getBorder());
		
		panelShowConfig.add(tabbed, BorderLayout.CENTER);
		panelShowConfig.add(westBorder, BorderLayout.EAST);
		
		feeList.setBackground(FormUtil.BKG_END);
		feeList.setForeground(Color.WHITE);
		feeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	private void updateConfig(boolean loadFees) {
		int index = feeList.getSelectedIndex();
		tablePanel.setTitle("");
		
		if(index == -1)
			return;
		
		AcademicFee fee = listModel.get(index);
		tablePanel.setTitle("Liste des promotions qui doivent payer "+fee.getAmount()+FormUtil.UNIT_MONEY);
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
		formCost.setVisible(false);
		if (loadFees) {			
			List<AnnualSpend> spends = annualSpendDao.checkByAcademicYear(currentYear.getId())? annualSpendDao.findByAcademicYear(currentYear) : new ArrayList<>();
			formCost.init(fee, spends);
		} else 
			formCost.init(fee);
		formCost.setVisible(true);
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		
		listModel.clear();
		tableModel.clear();
		formCost.clear();
		
		if(currentYear == null)
			return;
		
		if(currentYear != null && this.academicFeeDao.checkByAcademicYear(currentYear.getId())) {
			List<AcademicFee> data = this.academicFeeDao.findByAcademicYear(currentYear);
			for (AcademicFee fee : data) {
				listModel.addElement(fee);
			}
			feeList.setSelectedIndex(0);
		}
		
		boolean isCurrent = academicYearDao.isCurrent(currentYear);
		formFeePromotion.setCurrentYear(currentYear);
		btnNewFee.setEnabled(true);
		btnNewFee.setVisible(isCurrent);
		
		if (isCurrent)
			feeList.addMouseListener(mouseAdapter);
		else
			feeList.removeMouseListener(mouseAdapter);
		
		updateConfig(true);
		
		if (!listListenerInited){
			listListenerInited = true;
			feeList.addListSelectionListener(listListener);
		}
	}
	
	private void waiting (boolean wait) {
		btnNewFee.setEnabled(!wait);
		feeList.setEnabled(!wait);
		if (wait) {
			setCursor(WAIT_CURSOR);
			feeList.setCursor(WAIT_CURSOR);
		} else  {
			setCursor(Cursor.getDefaultCursor());
			feeList.setCursor(Cursor.getDefaultCursor());
		}
	}

}
