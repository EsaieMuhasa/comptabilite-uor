/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.DefaultRecipePart;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.beans.RecipePart;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AllocationRecipeDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OtherRecipePartDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PaymentFeePartDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.DefaultCardModel.CardType;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.swing.TableModel.ExportationProgressListener;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.swing.charts.Axis;
import net.uorbutembo.swing.charts.ChartPanel;
import net.uorbutembo.swing.charts.CloudChartRender;
import net.uorbutembo.swing.charts.DateAxis;
import net.uorbutembo.swing.charts.DefaultAxis;
import net.uorbutembo.swing.charts.DefaultCloudChartModel;
import net.uorbutembo.swing.charts.DefaultMaterialPoint;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.DefaultPointCloud;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.swing.charts.PieRender;
import net.uorbutembo.swing.charts.CloudChartRender.ChartRenderTranslationListener;
import net.uorbutembo.swing.charts.CloudChartRender.Interval;
import net.uorbutembo.swing.charts.PointCloud.CloudType;
import net.uorbutembo.tools.Config;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.components.JournalMenuItem;
import net.uorbutembo.views.components.JournalMenuItemListener;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormOutlay;
import net.uorbutembo.views.models.OutlayTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalSpecific extends Panel  implements ActionListener{
	private static final long serialVersionUID = 790476345630470695L;
	
	private final AnnualSpendDao annualSpendDao;
	private final AllocationRecipeDao allocationRecipeDao;
	private final AllocationCostDao allocationCostDao;
	private final OutlayDao outlayDao;
	private final OtherRecipePartDao otherRecipePartDao;
	private final PaymentFeePartDao paymentFeePartDao;
	private final PaymentFeeDao paymentFeeDao;
	private final OtherRecipeDao otherRecipeDao;
	private final PaymentLocationDao paymentLocationDao;
	
	private final ListAccount listAccount;
	private final ContainerPanel containerPanel;
	
	private FormOutlay formOutlay;
	private JDialog dialogOutlay;
	
	private final DAOFactory factory;
	private final MainWindow mainWindow;
	
	private DAOAdapter<Outlay> outlayAdapter;

	public JournalSpecific(MainWindow mainWindow) {
		super(new BorderLayout());
		
		this.mainWindow = mainWindow;
		
		factory = mainWindow.factory;
		annualSpendDao = factory.findDao(AnnualSpendDao.class);
		allocationRecipeDao = factory.findDao(AllocationRecipeDao.class);
		allocationCostDao = factory.findDao(AllocationCostDao.class);
		outlayDao = factory.findDao(OutlayDao.class);
		otherRecipePartDao = factory.findDao(OtherRecipePartDao.class);
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		paymentFeePartDao = factory.findDao(PaymentFeePartDao.class);
		otherRecipeDao = factory.findDao(OtherRecipeDao.class);
		paymentLocationDao = factory.findDao(PaymentLocationDao.class);
		
		listAccount = new ListAccount();
		containerPanel = new ContainerPanel();
		
		add(containerPanel, BorderLayout.CENTER);
		add(listAccount, BorderLayout.EAST);
		
		mainWindow.getSidebar().addYearChooserListener(listAccount);
		
	}
	
	/**
	 * Ouverture de la boiter de dialogue d'enregistrement d'une sortie
	 */
	public void createOutlay () {		
		createOutlayDialog();
		
		dialogOutlay.setLocationRelativeTo(mainWindow);
		dialogOutlay.setVisible(true);
	}
	
	/**
	 * mis enn jour d'un operation sortie dans un compte
	 * @param outlay
	 */
	public void updateOutlay (Outlay outlay) {
		createOutlayDialog();
		
		formOutlay.setOutlay(outlay);
		dialogOutlay.setLocationRelativeTo(mainWindow);
		dialogOutlay.setVisible(true);
	}

	/**
	 * creation de la boite de dialogue de d'enregistrement/modification des depences
	 */
	private void createOutlayDialog () {
		if(dialogOutlay != null)
			return;
		
		formOutlay = new FormOutlay(mainWindow);
		dialogOutlay = new JDialog(mainWindow, "Sortie", true);
		dialogOutlay.setIconImage(mainWindow.getIconImage());
		
		dialogOutlay.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogOutlay.getContentPane().add(formOutlay, BorderLayout.CENTER);
		dialogOutlay.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogOutlay.pack();
		final Dimension size = new Dimension(dialogOutlay.getWidth() < 650? 650 : dialogOutlay.getWidth(), dialogOutlay.getHeight());
		dialogOutlay.setSize(size);
		dialogOutlay.setMinimumSize(size);
		outlayAdapter = new DAOAdapter<Outlay>() {

			@Override
			public synchronized void onCreate(Outlay e, int requestId) {
				dialogOutlay.setVisible(false);
			}

			@Override
			public synchronized void onUpdate(Outlay e, int requestId) {
				dialogOutlay.setVisible(false);
			}			
		};
		
		outlayDao.addListener(outlayAdapter);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for (JournalMenuItem menu : listAccount.items) 
			menu.setActive(false);
		
		JournalMenuItem item = (JournalMenuItem) e.getSource();
		if(item == containerPanel.getAccount())
			return;
		
		listAccount.setEnabled(false);
		item.setActive(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		EventQueue.invokeLater(() -> {});
		Thread t = new Thread(() -> {			
			containerPanel.setAccount(item);
			setCursor(Cursor.getDefaultCursor());
			listAccount.setEnabled(true);
		});
		t.start();		
	}
	
	/**
	 * Injection du listener d'exportation des donnees au format XLSX
	 * @param exportationListener
	 */
	public void setExportationListener(ExportationProgressListener exportationListener) {
		containerPanel.getPanelRecipes().getTableModel().addExportationProgressListener(exportationListener);
		containerPanel.getPanelOutlays().getTableModel().addExportationProgressListener(exportationListener);
	}

	/**
	 * consultation de la liste des compte et des operations effectuer sur ces comptes
	 * @author Esaie MUHASA
	 */
	private class ListAccount extends Panel implements YearChooserListener{
		private static final long serialVersionUID = 6893735808920207627L;

		private final Box container = Box.createVerticalBox();
		private final List<JournalMenuItem> items = new ArrayList<>();
		
		/**
		 * Construction du sidebar
		 */
		public ListAccount() {
			super(new BorderLayout());
			
			setPreferredSize(new Dimension(340, 600));
			final JScrollPane scroll = FormUtil.createVerticalScrollPane(container);

			add(scroll, BorderLayout.CENTER);
			setBorder(new EmptyBorder(10, 10, 10, 10));
		}
		
		@Override
		public void onChange(AcademicYear year) {
			containerPanel.wait(true);
			reload(year);
			container.revalidate();
			container.repaint();
			containerPanel.wait(false);
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			for (JournalMenuItem item : items)
				item.setEnabled(enabled);
			
			repaint();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			
			g2.setColor(isEnabled() ? FormUtil.BORDER_COLOR : Color.RED);
			g2.drawRoundRect(5, 5, getWidth()-10, getHeight()- 10, 10, 10);
			
			super.paintComponent(g);
		}
		
		/**
		 * Rechargement des comptes du sidebar pour l'annee academique en parametre
		 * @param year
		 */
		public synchronized void reload (AcademicYear year) {
			List<AnnualSpend> spends = annualSpendDao.checkByAcademicYear(year.getId()) ?
					annualSpendDao.findByAcademicYear(year) : new ArrayList<>();
			
			for (int i = 0, count = items.size(); i<count; i++)
				items.get(i).removeActionListener(JournalSpecific.this);
			
			items.clear();
			container.removeAll();
			
			if(!spends.isEmpty()) {
				for (int i = 0, count = spends.size(); i<count; i++) {
					AnnualSpend spend = spends.get(i); 
					JournalMenuItem item = new JournalMenuItem(spend, factory);
					container.add(item);
					container.add(Box.createVerticalStrut(5));
					items.add(item);
					item.addActionListener(JournalSpecific.this);
				}
				items.get(0).setActive(true);
				containerPanel.setAccount(items.get(0));
				container.add(Box.createVerticalGlue());
			} else {
				containerPanel.setAccount(null);
			}
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 * panel facilitant la navigation dans toutes les operations des sorties dans une compte
	 */
	private class PanelOutlays extends Panel {
		private static final long serialVersionUID = -1278906633497851001L;
		
		private final OutlayTableModel tableModel = new OutlayTableModel(outlayDao);
		private final Table table = new Table(tableModel);
		private final TablePanel tablePanel = new TablePanel(table, "Liste des operations des sorties");
		
		private AnnualSpend account;
		
		private final JPanel panelBottom = new  JPanel(new BorderLayout());
		private final JLabel labelCount = FormUtil.createSubTitle("");//afiche le nombre doperation
		private final JButton btnNext = new Button( new ImageIcon(R.getIcon("next")));
		private final JButton btnPrev = new Button( new ImageIcon(R.getIcon("prev")));
		
		private final JButton btnToExcel = new Button(new ImageIcon(R.getIcon("export")), "Excel");
		private final ActionListener navigationListener = event -> {
			JButton btn = (JButton) event.getSource();
			boolean next = btn == btnNext;
			if (next && tableModel.hasNext()) 
				tableModel.next();
			else if(!next && tableModel.hasPrevious()) 
				tableModel.previous();
			int opt = tableModel.getCount();
			labelCount.setText(opt+" opération"+(opt > 1? "s":""));
			btnPrev.setEnabled(tableModel.hasPrevious());
			btnNext.setEnabled(tableModel.hasNext());
		};
		private final ActionListener btnExportListener = event -> {
			int rps = Table.XLSX_FILE_CHOOSER.showSaveDialog(mainWindow);
			if(rps == JFileChooser.APPROVE_OPTION) {
				Thread t = new Thread(() -> {
					tableModel.exportToExcel(Table.XLSX_FILE_CHOOSER.getSelectedFile());
				});
				t.start();
			}
		};
		{
			btnNext.addActionListener(navigationListener);
			btnPrev.addActionListener(navigationListener);
			btnToExcel.addActionListener(btnExportListener);
			btnNext.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnPrev.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnToExcel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}
		
		public PanelOutlays () {
			super(new BorderLayout());
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			Box box = Box.createHorizontalBox();
			box.add(btnPrev);
			box.add(Box.createHorizontalStrut(5));
			box.add(btnNext);
			box.add(Box.createHorizontalStrut(10));
			box.add(btnToExcel);
			box.add(Box.createHorizontalStrut(5));
			
			panelBottom.add(box, BorderLayout.EAST);
			panelBottom.add(labelCount, BorderLayout.CENTER);
			panelBottom.setBackground(FormUtil.BKG_END);
			labelCount.setHorizontalAlignment(JLabel.LEFT);
			
			add(tablePanel, BorderLayout.CENTER);
			add(panelBottom, BorderLayout.SOUTH);
		}

		/**
		 * @param account the account to set
		 */
		public void setAccount(AnnualSpend account) {
			this.account = account;
			
			tableModel.setAccount(this.account);
			labelCount.setText(tableModel.getRowCount()+" opération"+(tableModel.getRowCount() > 1? "s":""));
			btnPrev.setEnabled(tableModel.hasPrevious());
			btnNext.setEnabled(tableModel.hasNext());
		}

		/**
		 * @return the tableModel
		 */
		public OutlayTableModel getTableModel() {
			return tableModel;
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	private final class PanelRecipes extends Panel implements JournalMenuItemListener{
		private static final long serialVersionUID = 6642674739813359838L;
		
		private final RecipeTableModel tableModel = new RecipeTableModel();
		private final Table table = new Table(tableModel);
		private final TablePanel tablePanel = new TablePanel(table, "Opérations d'entrées, payements des frais acadédemiques");
		
		private List<PaymentLocation> locations = null;
		private final DefaultPieModel pieModelSource = new DefaultPieModel();
		private final DefaultPieModel pieModelLocation = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModelSource);
		private JournalMenuItem account;
		
		private final JLabel labelCount = FormUtil.createSubTitle("");//afiche le nombre doperation
		private final JButton btnNext = new Button( new ImageIcon(R.getIcon("next")));
		private final JButton btnPrev = new Button( new ImageIcon(R.getIcon("prev")));
		private final JButton btnToExcel = new Button(new ImageIcon(R.getIcon("export")), "Excel");
		private final Box boxRadios = Box.createHorizontalBox();
		private final JRadioButton [] radiosChart = { new JRadioButton("Sources", true), new JRadioButton("Localisation")};
		private final JRadioButton [] radioModels = {
				new JRadioButton("Frais academique", true),
				new JRadioButton("Autres recettes")
		};
		
		private final ChangeListener radionListener = event -> {//ecoute bouton radio permetant de choisir les sorces des donneer a afficher
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if (index == 0) {
				tablePanel.setTitle("Opérations d'entrées, payements des frais acadédemiques");
			} else {
				tablePanel.setTitle("Opérations d'entrées, autres recettes");
			}
			
			tableModel.setFees(index == 0);
			btnNext.setEnabled(tableModel.hasNext());
			btnPrev.setEnabled(tableModel.hasPrevious());
			labelCount.setText(tableModel.getCount()+" opérations");
		};
		
		private final ActionListener btnExportListener = event -> {
			int rps = Table.XLSX_FILE_CHOOSER.showSaveDialog(mainWindow);
			if(rps == JFileChooser.APPROVE_OPTION) {
				Thread t = new Thread(() -> {
					tableModel.exportToExcel(Table.XLSX_FILE_CHOOSER.getSelectedFile());
				});
				t.start();
			}
		};
		
		{
			final ButtonGroup group = new ButtonGroup();
			boxRadios.add(labelCount);
			boxRadios.add(Box.createHorizontalGlue());
			for (int i = 0; i < radioModels.length; i++) {
				boxRadios.add(radioModels[i]);
				radioModels[i].setName(i+"");
				radioModels[i].addChangeListener(radionListener);
				radioModels[i].setForeground(Color.WHITE);
				group.add(radioModels[i]);
			}
			boxRadios.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}
		
		private final Panel panelCommand = new  Panel(new BorderLayout());//conteneur des elements de navigation dans les donnees
		private final Box boxRadiosChart = Box.createHorizontalBox();//filtrage graphique		
		
		private final ChangeListener radioChartListener = (event) ->  {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			int index = Integer.parseInt(radio.getName());
			piePanel.setModel((index == 0)? pieModelSource : pieModelLocation);
		};
		
		private final DAOAdapter<PaymentLocation> locationAdapter = new DAOAdapter<PaymentLocation>() {

			@Override
			public synchronized void onCreate(PaymentLocation e, int requestId) {
				if (locations == null) {
					if (paymentLocationDao.countAll() == 0)
						locations = new  ArrayList<>();
					else 
						locations = paymentLocationDao.findAll();
				}
				
				locations.add(e);
			}

			@Override
			public synchronized void onUpdate(PaymentLocation e, int requestId) {
				if(locations == null)
					return;
				
				for (PaymentLocation location : locations) {
					if (location.getId() == e.getId()){
						PiePart part = pieModelLocation.findByData(location);
						locations.set(locations.indexOf(location), e);
						
						if (part != null){
							part.setData(e);
							part.setLabel(e.toString());
						}
						break;
					}
				}
			}

			@Override
			public synchronized void onDelete(PaymentLocation e, int requestId) {
				if(locations == null)
					return;
				
				for (PaymentLocation location : locations) {
					if (location.getId() == e.getId()){
						locations.remove(location);
						break;
					}
				}
			}
			
		};
		
		private final ActionListener navigationListener = event -> {
			JButton btn = (JButton) event.getSource();
			boolean next = btn == btnNext;
			TableModel<?> model = (TableModel<?>) table.getModel();
			if (next && model.hasNext()) 
				model.next();
			else if(!next && model.hasPrevious()) 
				model.previous();
			btn.setEnabled(next? model.hasNext() : model.hasPrevious());
			int opt = tableModel.getCount();
			labelCount.setText(opt+" opération"+(opt > 1? "s":"")+"/ page "+(tableModel.getOffset()/tableModel.getLimit()));
			btnPrev.setEnabled(tableModel.hasPrevious());
			btnNext.setEnabled(tableModel.hasNext());
		};
		
		{//panel bottom (toolbar du table, avec btn nex, prev, ...). personnalisatio du model du pieChart
			tableModel.setInterval(20, 0);
			
			//chart
			pieModelSource.setRealMaxPriority(true);
			pieModelLocation.setRealMaxPriority(true);
			pieModelSource.setSuffix(" $");
			pieModelLocation.setSuffix(" $");
			pieModelSource.setTitle("Répartition sélon les sources d'alimentation du compte");
			pieModelLocation.setTitle("Répartition sélon les emplassements");
			piePanel.setBorderColor(FormUtil.BKG_END_2);
			final ButtonGroup groupChart = new ButtonGroup();
			for (int i = 0; i < radiosChart.length; i++) {
				JRadioButton radio = radiosChart[i];
				radio.setName(i+"");
				radio.setForeground(Color.WHITE);
				radio.addChangeListener(radioChartListener);
				groupChart.add(radio);
				boxRadiosChart.add(radio);
			}
			boxRadiosChart.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			//==
			 
			final Box box = Box.createHorizontalBox();
			box.add(btnPrev);
			box.add(Box.createHorizontalStrut(5));
			box.add(btnNext);
			btnPrev.addActionListener(navigationListener);
			btnNext.addActionListener(navigationListener);
			btnNext.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnPrev.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnToExcel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnToExcel.addActionListener(btnExportListener);
			 
			labelCount.setFont(new Font("Arial", Font.BOLD, 15));
			labelCount.setHorizontalAlignment(JLabel.LEFT);
			 
			panelCommand.add(box, BorderLayout.EAST);
			panelCommand.add(btnToExcel, BorderLayout.WEST);
			panelCommand.setBackground(FormUtil.BKG_END);
			panelCommand.setOpaque(true);
			 
			panelCommand.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}

		
		public PanelRecipes () {
			super(new BorderLayout());
			
			final Panel panelChart =new Panel(new BorderLayout());
			final Panel panelList = new Panel(new BorderLayout());
			final Panel panelTop = new Panel(new BorderLayout());
			
			panelTop.add(labelCount, BorderLayout.CENTER);
			panelTop.add(boxRadios, BorderLayout.EAST);
			
			panelList.add(tablePanel, BorderLayout.CENTER);
			panelList.add(panelCommand, BorderLayout.SOUTH);
			
			panelChart.add(piePanel, BorderLayout.CENTER);
			panelChart.add(boxRadiosChart, BorderLayout.NORTH);
			
			final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelList, panelChart);
			split.setOneTouchExpandable(true);
			split.setDividerLocation(350);
			
			add(split, BorderLayout.CENTER);
			add(panelTop, BorderLayout.NORTH);
			paymentLocationDao.addListener(locationAdapter);
			
			//table columns width
			final int [] cols = {0, 3};
			final int w = 100;
			for(int i = 0; i < cols.length; i ++) {				
				table.getColumnModel().getColumn(cols[i]).setMinWidth(w);
				table.getColumnModel().getColumn(cols[i]).setMaxWidth(w);
				table.getColumnModel().getColumn(cols[i]).setWidth(w);
				table.getColumnModel().getColumn(cols[i]).setResizable(false);
			}
			//==
			
		}
	
		@Override
		public void onReload(JournalMenuItem item) {
			updatePie();
			tableModel.reload();
			int opt = tableModel.getCount();
			labelCount.setText(opt+" opération"+(opt > 1? "s":"")+"/ page "+(tableModel.getOffset()/tableModel.getLimit()));
			btnPrev.setEnabled(tableModel.hasPrevious());
			btnNext.setEnabled(tableModel.hasNext());
		};


		/**
		 * @param account the account to set
		 */
		public void setAccount (JournalMenuItem account) {
			if(this.account != null)
				this.account.removeItemListener(this);
			this.account = account;
			
			if(account != null) {
				account.addItemListener(this);
				tableModel.reload(account.getAccount());
				int opt = tableModel.getCount();
				labelCount.setText(opt+" Opération"+(opt > 1? "s":"")+"/ page "+(tableModel.getOffset()/tableModel.getLimit()));
				btnPrev.setEnabled(tableModel.hasPrevious());
				btnNext.setEnabled(tableModel.hasNext());
			}
			
			updatePie();
		}
		
		
		/**
		 * mise en jour des elements du graphique pie
		 * en fonction du compte actuelement selectionner
		 */
		private void updatePie() {
			
			if (pieModelLocation.getCountPart() != 0)
				for (PiePart part : pieModelLocation.getParts()) 
					part.setData(null);
			
			pieModelSource.removeAll();
			pieModelLocation.removeAll();
			
			if(account == null)
				return;
			
			int colorIndex = 0;
			pieModelSource.setMax(account.getAccount().getCollectedCost() + account.getAccount().getCollectedRecipe());
			pieModelLocation.setMax(pieModelSource.getMax());
			pieModelSource.addPart(new DefaultPiePart(FormUtil.COLORS[colorIndex++], account.getAccount().getCollectedCost(), "Frais académiques"));
			
			if (allocationRecipeDao.checkBySpend(account.getAccount())) {
				List<AllocationRecipe> all = allocationRecipeDao.findBySpend(account.getAccount());
				for (int i = 0, count = all.size(); i < count; i++) {					
					pieModelSource.addPart(
							new DefaultPiePart(
								FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ],
								all.get(i).getCollected(),
								all.get(i).getRecipe().getUniversityRecipe().getTitle()
							)
						); 
				}
			} else {
				pieModelSource.addPart(new DefaultPiePart(FormUtil.COLORS[1], 0d, "Autres recettes"));				
			}
			
			if (locations == null && paymentLocationDao.countAll() != 0) 
				locations = paymentLocationDao.findAll();
			
			if (locations == null)
				return;
			
			for (int i = 0; i < locations.size(); i++) {
				PaymentLocation location = locations.get(i);
				double sold = otherRecipePartDao.getSoldBySpend(account.getAccount(), location);
				sold += paymentFeePartDao.getSoldBySpend(account.getAccount(), location);
				if (sold > 0.0) {
					DefaultPiePart part = new DefaultPiePart( FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ], sold, location.toString());
					part.setData(location);
					pieModelLocation.addPart(part);
				}
				
			}
			
		}

		/**
		 * @return the tableModel
		 */
		public RecipeTableModel getTableModel() {
			return tableModel;
		}
		
	}
	
	private final class PanelTabDashboard extends Panel implements JournalMenuItemListener, ChartRenderTranslationListener {
		private static final long serialVersionUID = 484389202609625039L;
		
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel, FormUtil.BKG_END_2);
		
		private final Interval interval = new Interval(Integer.parseInt(Config.find("defaultDateMin")), 0);
		private final DefaultAxis yAxis = new DefaultAxis("Y", "Montant", FormUtil.UNIT_MONEY);
		private final DateAxis xAxis = new DateAxis("X", "Date", "");
		private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel(xAxis, yAxis);
		private final ChartPanel chartPanel = new ChartPanel(chartModel);
		private final DefaultPointCloud cloudRecipe = new DefaultPointCloud("Recettes", FormUtil.COLORS_ALPHA[0], Color.WHITE, FormUtil.COLORS[0]);
		private final DefaultPointCloud cloudOutlay = new DefaultPointCloud("Dépenses", FormUtil.COLORS_ALPHA[1], Color.WHITE, FormUtil.COLORS[1]);
		private final DefaultPointCloud cloudSold = new DefaultPointCloud("Solde", FormUtil.COLORS_ALPHA[2], Color.WHITE, FormUtil.COLORS[2]);
		
		private final DefaultCardModel<Double> cardSoldModel = new DefaultCardModel<>(CardType.DARK, R.getIcon("caisse"), "$");
		private final DefaultCardModel<Double> cardOutlayModel = new DefaultCardModel<>(CardType.DARK, R.getIcon("btn-cancel"), "$");
		private final DefaultCardModel<Double> cardRecipeModel = new DefaultCardModel<>(CardType.DARK, R.getIcon("btn-add"), "$");
		
		{
			cardSoldModel.setTitle("Solde");
			cardSoldModel.setInfo("Montant disponible");
			cardSoldModel.setValue(0d);
			
			cardOutlayModel.setTitle("Dépenses");
			cardOutlayModel.setInfo("Déjà utiliser");
			cardOutlayModel.setValue(0d);
			
			cardRecipeModel.setTitle("Recettes");
			cardRecipeModel.setInfo("Totale des recettes");
			cardRecipeModel.setValue(0d);
			
			pieModel.setRealMaxPriority(true);
			pieModel.setSuffix(FormUtil.UNIT_MONEY_SYMBOL);
			
			cloudSold.setFill(true);
			
			chartModel.addChart(cloudOutlay);
			chartModel.addChart(cloudRecipe);
			chartModel.addChart(cloudSold);
		}
		
		private final Card cardSold = new Card(cardSoldModel);
		private final Card cardOutlay = new Card(cardOutlayModel);
		private final Card cardRecipe = new Card(cardRecipeModel);
		
		private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		private List<PaymentLocation> locations = null;
		private JournalMenuItem account;
		
		private final DAOAdapter<PaymentLocation> locationAdapter = new DAOAdapter<PaymentLocation>() {

			@Override
			public synchronized void onCreate(PaymentLocation e, int requestId) {
				
				if (locations == null) {
					if (paymentLocationDao.countAll() == 0)
						locations = new  ArrayList<>();
					else 
						locations = paymentLocationDao.findAll();
				}
				locations.add(e);
			}

			@Override
			public synchronized void onUpdate(PaymentLocation e, int requestId) {
				if(locations == null)
					return;
				
				for (PaymentLocation location : locations) {
					if (location.getId() == e.getId()){
						locations.set(locations.indexOf(location), e);
						reloadPie();
						break;
					}
				}
				
				if (account == null)
					return;
				
				if (paymentFeePartDao.checkBySpend(account.getAccount(), e) || otherRecipePartDao.checkBySpend(account.getAccount(), e))
					reloadPie();
			}

			@Override
			public synchronized void onDelete(PaymentLocation e, int requestId) {
				if(locations == null)
					return;
				
				for (PaymentLocation location : locations) {
					if (location.getId() == e.getId()){
						locations.remove(location);
						break;
					}
				}
			}
			
		};
		
		public PanelTabDashboard () {
			super(new BorderLayout());
			final Panel 
				center = new Panel(new BorderLayout()),
				bottom = new Panel(new GridLayout(1, 3, 10, 0));
			
			final Panel 
				chartContainer = new Panel(new BorderLayout()),
				pieContainer = new Panel(new  BorderLayout());
			
			pieContainer.add(piePanel, BorderLayout.CENTER);
			chartContainer.add(chartPanel, BorderLayout.CENTER);
			
			tabbedPane.addTab("", new ImageIcon(R.getIcon("pie")), pieContainer, "Soldes par emplacement");
			tabbedPane.addTab("", new ImageIcon(R.getIcon("chart")), chartContainer, "Ligne de temps");
			
			center.add(tabbedPane, BorderLayout.CENTER);
			center.setBorder(new EmptyBorder(0, 0, 10, 0));
			
			bottom.add(cardSold);
			bottom.add(cardRecipe);
			bottom.add(cardOutlay);
			
			add(center, BorderLayout.CENTER);
			add(bottom, BorderLayout.SOUTH);
			
			paymentLocationDao.addListener(locationAdapter);
			chartPanel.getChartRender().addTranslationListener(this);
		}
		
		@Override
		public void onReload (JournalMenuItem item) {
			reload();
		}
		
		/**
		 * rechargement complette de toutes les graphiques,
		 * et contenue de cards
		 */
		public void reload () {
			wait(true);
			reloadLine();
			reloadPie();
			wait(false);
		}
		
		private void wait (boolean status) {
			
			if (status) {
				setCursor(FormUtil.WAIT_CURSOR);
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
		}
		
		/**
		 * Rechargement du graphique sous forme tarte
		 * et des cards
		 */
		public synchronized void reloadPie () {
			pieModel.removeAll();
			
			if (locations == null && paymentLocationDao.countAll() != 0) 
				locations = paymentLocationDao.findAll();
			
			if (locations == null || account == null)
				return;
			
			AnnualSpend account = this.account.getAccount();
			int count = locations.size();
			PiePart [] parts = new PiePart[count];
			
			double out = 0, in = 0;
			
			for (int i = 0, colorIndex = 0; i < count; i++) {
				PaymentLocation l = locations.get(i);
				if (paymentFeePartDao.checkBySpend(account, l) || otherRecipePartDao.checkBySpend(account, l)) {
					
					double soldFee = paymentFeePartDao.getSoldBySpend(account, l);
					double soldOther = otherRecipePartDao.getSoldBySpend(account, l);
					double soldOut = outlayDao.getSoldByAccount(account.getId(), l.getId());
					
					double sold = soldFee + soldOther - soldOut;
					in += soldFee + soldOther;
					out += soldOut;
					DefaultPiePart part = new DefaultPiePart(FormUtil.COLORS[(colorIndex) % (FormUtil.COLORS.length-1)], sold, l.toString());
					part.setData(l);
					parts[colorIndex++] = part;
				}
			}
			
			pieModel.addParts(parts);
			pieModel.setTitle(account.toString()+", soldes diponibles par emplacement");
			
			BigDecimal 
				bigSold = new BigDecimal(pieModel.getRealMax()).setScale(3, RoundingMode.FLOOR),
				bigIn = new BigDecimal(in).setScale(3, RoundingMode.FLOOR),
				bigOut = new BigDecimal(out).setScale(3, RoundingMode.FLOOR);
			
			cardOutlayModel.setValue(bigOut.doubleValue());
			cardRecipeModel.setValue(bigIn.doubleValue());
			cardSoldModel.setValue(bigSold.doubleValue());
		}
		
		@Override
		public void onRequireTranslation(CloudChartRender source, Axis axis, Interval interval) {
			double min = interval.getMin(), max = interval.getMax();
			
			if(max > 0) {
				max = 0;
				min = Integer.parseInt(Config.find("defaultDateMin"));
			}
			
			if(this.interval.getMin() == min && this.interval.getMax() == max)
				return;
			
			this.interval.setInterval(min, max);
			wait(true);
			reloadLine();
			wait(false);
		}

		@Override
		public void onRequireTranslation(CloudChartRender source, Interval xInterval, Interval yInterval) {
			onRequireTranslation(source, xAxis, xInterval);
		}
		
		/**
		 * Rechargement du graphique lineaire qui visualise les entrees et les sorties
		 * pour le compte sectionner
		 */
		public synchronized void reloadLine () {
			chartPanel.getChartRender().setVisible(false);
			cloudOutlay.removePoints();
			cloudSold.removePoints();
			cloudRecipe.removePoints();
			
			long min = (long) interval.getMin();
			long max = (long) interval.getMax();

			long time = 0, now = System.currentTimeMillis();
			
			for(long day = min; day <= max; day += 1l) {
				time = now + (day * 60l * 60l * 1000l * 24l);
				Date date = new Date(time);
				double y = 0;
				//recettes
				y = otherRecipePartDao.getSoldBySpendAtDate(account.getAccount(), date);
				y += paymentFeePartDao.getSoldBySpendAtDate(account.getAccount(), date);

				DefaultMaterialPoint pointIn = new DefaultMaterialPoint(day, y);
				pointIn.setLabelX(DateAxis.DEFAULT_DATE_FORMAT.format(date));
				pointIn.setLabelY(y+" USD");
				cloudRecipe.addPoint(pointIn);
				//==
				
				//depense
				y = outlayDao.getSoldByAccountAtDate(account.getAccount(), date);
				
				DefaultMaterialPoint pointOut = new DefaultMaterialPoint(day, -y);
				pointOut.setLabelX(DateAxis.DEFAULT_DATE_FORMAT.format(date));
				pointOut.setLabelY(y+" USD");
				cloudOutlay.addPoint(pointOut);
				//==
				
				//solde
				y = otherRecipePartDao.getSoldBySpendBeforDate(account.getAccount(), date);
				y += paymentFeePartDao.getSoldBySpendBeforDate(account.getAccount(), date);
				y -= outlayDao.getSoldByAccountBeforDate(account.getAccount(), date);
				
				DefaultMaterialPoint pointSold = new DefaultMaterialPoint(day, y, 10f);
				pointSold.setLabelX(DateAxis.DEFAULT_DATE_FORMAT.format(date));
				pointSold.setLabelY(y+" USD");
				pointSold.setBorderColor(cloudSold.getBorderColor().darker());
				cloudSold.addPoint(pointSold);
				//==
			}
			
			cloudRecipe.setType(CloudType.STICK_CHART);
			cloudOutlay.setType(CloudType.STICK_CHART);
			chartPanel.getChartRender().setVisible(true);
		}

		/**
		 * @param account the account to set
		 */
		public void setAccount(JournalMenuItem account) {
			if(this.account != null)
				this.account.removeItemListener(this);
			this.account = account;
			
			if(account != null)
				account.addItemListener(this);
			reload();
		}
		
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	private class ContainerPanel extends Panel{
		private static final long serialVersionUID = -5154188254710321300L;
		
		private JournalMenuItem account;
		private final Panel centerPanel = new Panel(new BorderLayout());
		private final JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.TOP);
		private final PanelOutlays panelOutlays = new PanelOutlays();
		private final PanelRecipes panelRecipes = new PanelRecipes();
		private final PanelTabDashboard panelDashboard = new PanelTabDashboard();
		
		private final JLabel title = FormUtil.createSubTitle("");
		

		public ContainerPanel() {
			super(new BorderLayout());
						
			centerPanel.add(title, BorderLayout.NORTH);
			centerPanel.add(tabbedPanel, BorderLayout.CENTER);

			tabbedPanel.addTab("Etat ", new ImageIcon(R.getIcon("status")), panelDashboard);
			tabbedPanel.addTab("Recettes ", new ImageIcon(R.getIcon("new")), panelRecipes);
			tabbedPanel.addTab("Dépenses ", new ImageIcon(R.getIcon("drop")), panelOutlays);
			
			centerPanel.add(tabbedPanel, BorderLayout.CENTER);
			add(centerPanel, BorderLayout.CENTER);
			setBorder(new EmptyBorder(0, 5, 5, 0));
		}

		/**
		 * @param account the account to set
		 */
		public synchronized void setAccount (JournalMenuItem account) {
			
			if (this.account != null && account!=null && this.account.getAccount().getId() == account.getAccount().getId())
				return;
			
			this.account = account;
			centerPanel.setVisible(account != null);
			
			if(account != null) {
				panelDashboard.setAccount(account);
				panelRecipes.setAccount(this.account);
				panelOutlays.setAccount(this.account.getAccount());
				title.setText(account.getAccount().getUniversitySpend().getTitle());
			}
		}

		
		/**
		 * @return the account
		 */
		public JournalMenuItem getAccount() {
			return account;
		}

		/**
		 * pour signaler le panel qu'il y a un traitement lourd encours
		 * @param wait
		 */
		public void wait (boolean wait) {
			//tableOutlays.setEnabled(!wait);
			//tableRecipes.setEnabled(!wait);
			setCursor(wait? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
		}

		/**
		 * @return the panelOutlays
		 */
		public PanelOutlays getPanelOutlays() {
			return panelOutlays;
		}

		/**
		 * @return the panelRecipes
		 */
		public PanelRecipes getPanelRecipes() {
			return panelRecipes;
		}
		
	}
	
	/**
	 * model du tableau de visualisation de la repartition d'une recete
	 * @author Esaie MUHASA
	 *
	 */
	private class RecipeTableModel extends TableModel<DefaultRecipePart<?>>{
		private static final long serialVersionUID = 5961836886953247067L;
		
		private AnnualSpend account;
		private boolean fees = true; // faut-il charger les parts pour les frais academique??
		
		private final DAOAdapter<PaymentFee> feeAdapter = new DAOAdapter<PaymentFee>() {

			@Override
			public synchronized void onCreate(PaymentFee e, int requestId) {
				if(!fees || account  == null || e.getInscription().getPromotion().getAcademicFee() == null 
						|| !allocationCostDao.check(account.getId(), e.getInscription().getPromotion().getAcademicFee().getId()))
					return;
				reload();
			}

			@Override
			public synchronized void onUpdate(PaymentFee e, int requestId) {
				if(!fees || account  == null)
					return;
				
				reload();
			}

			@Override
			public synchronized void onDelete(PaymentFee e, int requestId) {
				if(!fees || account  == null)
					return;
				reload();
			}
			
		};
		
		private final DAOAdapter<OtherRecipe> recipeAdapter = new DAOAdapter<OtherRecipe>() {

			@Override
			public synchronized void onCreate(OtherRecipe e, int requestId) {
				if(fees || account == null || !allocationRecipeDao.check(e.getAccount().getId(), account.getId()))
					return;
				reload();
			}

			@Override
			public synchronized void onUpdate(OtherRecipe e, int requestId) {
				if(fees || account == null)
					return;
				reload();
			}

			@Override
			public synchronized void onDelete(OtherRecipe e, int requestId) {
				if(fees || account == null)
					return;
				reload();
			}
			
		};

		public RecipeTableModel() {
			super(null);
			paymentFeeDao.addListener(feeAdapter);
			otherRecipeDao.addListener(recipeAdapter);
		}
		
		/**
		 * mis en jour du model des donnees
		 * @param account
		 */
		public void reload (AnnualSpend account) {
			this.account = account;
			offset = 0;
			reload();
		}
		
		public void reload () {
			data.clear();
			if(account != null) {
				if(fees) {					
					if(paymentFeePartDao.checkBySpend(account, offset)){
						List<RecipePart<PaymentFee>>  parts = paymentFeePartDao.findBySpend(account, limit, offset);
						for (RecipePart<PaymentFee> part : parts)
							data.add((DefaultRecipePart<?>)part);
					}
				} else {
					if(otherRecipePartDao.checkBySpend(account, offset)){
						List<RecipePart<OtherRecipe>>  parts = otherRecipePartDao.findBySpend(account, limit, offset);
						for (RecipePart<OtherRecipe> part : parts)
							data.add((DefaultRecipePart<?>)part);
					}					
				}
				
			}
			
			fireTableDataChanged();			
		}

		@Override
		public int getCount() {
			if(fees)
				return paymentFeePartDao.countBySpend(account);
			else
				return otherRecipePartDao.countBySpend(account);
		}
		
		/**
		 * @param fees
		 */
		public void setFees(boolean fees) {
			if(this.fees == fees)
				return;
			
			this.fees = fees;
			offset = 0;
			reload();
		}

		@Override
		public int getColumnCount() {
			return 4;
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0 : return "Date";
				case 1 : return "libelé";
				case 2 : return "Lieux";
				case 3 : return "Montant";
			}
			return super.getColumnName(column);
		}


		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0: return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getSource().getRecordDate());
				case 1: return data.get(rowIndex).getLabel();
				case 2: return data.get(rowIndex).getPaymentLocation();
				case 3: return PieRender.DECIMAL_FORMAT.format(data.get(rowIndex).getAmount())+" USD";
			}
			return null;
		}
	}
}
