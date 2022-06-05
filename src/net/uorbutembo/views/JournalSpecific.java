/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
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
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OtherRecipePartDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PaymentFeePartDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.DefaultCardModel.CardType;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.swing.charts.DefaultLineChartModel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.LineChartRender;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.components.JournalMenuItem;
import net.uorbutembo.views.components.JournalMenuItemListener;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormOtherRecipe;
import net.uorbutembo.views.forms.FormOutlay;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.OutlayTableModel;
import resources.net.uorbutembo.R;

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
	
	private final JButton btnRecipe = new JButton("Nouvelle recette", new ImageIcon(R.getIcon("plus")));
	private final JButton btnSpend = new JButton("Nouveau dépense", new ImageIcon(R.getIcon("moin")));
	
	private FormOutlay formOutlay;
	private FormOtherRecipe formRecipe;
	private JDialog dialogOutlay;
	private JDialog dialogRecipe;
	
	private final DAOFactory factory;
	private final MainWindow mainWindow;
	
	private JPopupMenu popupOutlay;
	private JMenuItem itemDeleteOutlay;
	private JMenuItem itemUpdateOutlay;
	
	private DAOAdapter<Outlay> outlayAdapter;
	private DAOAdapter<OtherRecipe> recipeAdapter;

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
		
		btnRecipe.addActionListener(event -> {
			createRecipe();
		});
		
		btnSpend.addActionListener(event -> {
			createOutlay();
		});
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
	 * Ajout d'une nouvelle recette (autre que les frais acadmique)
	 */
	public void createRecipe () {		
		createRecipeDialog();
		
		dialogRecipe.setLocationRelativeTo(mainWindow);
		dialogRecipe.setVisible(true);
	}
	
	/**
	 * creation du boite de dialogue d'ajout d'une recete
	 */
	private void createRecipeDialog () {
		if(dialogRecipe != null)
			return;
		
		formRecipe = new FormOtherRecipe(mainWindow);
		dialogRecipe = new JDialog(mainWindow, "Entrée", true);
		dialogRecipe.setIconImage(mainWindow.getIconImage());		
		
		
		dialogRecipe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogRecipe.getContentPane().add(formRecipe, BorderLayout.CENTER);
		dialogRecipe.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogRecipe.pack();
		Dimension size = new Dimension(dialogRecipe.getWidth() < 800? 800 : dialogRecipe.getWidth() , dialogRecipe.getHeight() < 550 ? 550 : 600);
		dialogRecipe.setSize(size);
		dialogRecipe.setMinimumSize(size);
		
		recipeAdapter = new DAOAdapter<OtherRecipe>() {

			@Override
			public synchronized void onCreate(OtherRecipe e, int requestId) {
				dialogRecipe.setVisible(false);
			}

			@Override
			public synchronized void onUpdate(OtherRecipe e, int requestId) {
				dialogRecipe.setVisible(false);
			}
			
		};
		
		otherRecipeDao.addListener(recipeAdapter);
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
	
	/**
	 * creation du popup menu qui permet de modifier/supprimer une sortie
	 */
	private void createPopupOutlay() {
		if (popupOutlay != null)
			return;
		popupOutlay = new JPopupMenu();
		
		itemDeleteOutlay = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
		itemUpdateOutlay = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
		popupOutlay.add(itemDeleteOutlay);
		popupOutlay.add(itemUpdateOutlay);
		
		itemDeleteOutlay.addActionListener(event -> {
			Outlay out = containerPanel.panelOutlays.tableModel.getRow(containerPanel.panelOutlays.table.getSelectedRow());
			int status = JOptionPane.showConfirmDialog(dialogRecipe, "Voulez-vous vraiment supprimer ce dépense??", "Suppression", JOptionPane.OK_CANCEL_OPTION);
			if(status == JOptionPane.OK_OPTION) {
				try {					
					outlayDao.delete(out.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(dialogRecipe, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		itemUpdateOutlay.addActionListener (event -> {
			Outlay out = containerPanel.panelOutlays.tableModel.getRow(containerPanel.panelOutlays.table.getSelectedRow());
			updateOutlay(out);
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for (JournalMenuItem menu : listAccount.items) 
			menu.setActive(false);
		
		JournalMenuItem item = (JournalMenuItem) e.getSource();
		item.setActive(true);
		containerPanel.setAccount(item);
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
			final Panel bottom = new Panel();
			final JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
			
			bottom.add(btnRecipe);
			bottom.add(btnSpend);
			
			add(bottom, BorderLayout.SOUTH);
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
		protected void paintComponent(Graphics g) {
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			
			g2.setColor(FormUtil.BORDER_COLOR);
			g2.drawRoundRect(5, 5, getWidth()-10, getHeight()- 10, 10, 10);
			
			g2.drawLine(6, getHeight() - 48, getWidth()-6, getHeight() - 48);
			
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
		
		private final Panel panelBottom = new  Panel(new BorderLayout());
		private final JLabel labelCount = FormUtil.createSubTitle("");//afiche le nombre doperation
		private final JButton btnNext = new JButton( new ImageIcon(R.getIcon("next")));
		private final JButton btnPrev = new JButton( new ImageIcon(R.getIcon("prev")));
		
		private final JButton btnToExcel = new JButton("Excel", new ImageIcon(R.getIcon("export")));
		private final JButton btnToPdf = new JButton("PDF", new ImageIcon(R.getIcon("pdf")));
		private final JButton btnToPrint = new JButton("Imprimer", new ImageIcon(R.getIcon("print")));
		
		private final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
					createPopupOutlay();
					popupOutlay.show(table, e.getX(), e.getY());
					
				}
			}
		};
		
		public PanelOutlays () {
			super(new BorderLayout());
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.addMouseListener(mouseAdapter);
			
			Box box = Box.createHorizontalBox();
			box.add(btnPrev);
			box.add(btnNext);
			box.add(Box.createHorizontalStrut(20));
			box.add(btnToExcel);
			box.add(btnToPdf);
			box.add(btnToPrint);
			
			panelBottom.add(box, BorderLayout.WEST);
			panelBottom.add(labelCount, BorderLayout.CENTER);
			labelCount.setHorizontalAlignment(JLabel.RIGHT);
			
			add(tablePanel, BorderLayout.CENTER);
			add(panelBottom, BorderLayout.SOUTH);
		}

		/**
		 * @param account the account to set
		 */
		public void setAccount(AnnualSpend account) {
			this.account = account;
			
			tableModel.setAccount(this.account);
			labelCount.setText(tableModel.getRowCount()+" Opération"+(tableModel.getRowCount() > 1? "s":""));
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	private final class PanelRecipes extends Panel implements JournalMenuItemListener{
		private static final long serialVersionUID = 6642674739813359838L;
		
		private final RecipeTableModel tableModel = new RecipeTableModel();
		private final Table table = new Table(tableModel);
		private final TablePanel tablePanel = new TablePanel(table, "Liste des opérations d'entrées");
		
		private List<PaymentLocation> locations = null;
		private final DefaultPieModel pieModelSource = new DefaultPieModel();
		private final DefaultPieModel pieModelLocation = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModelSource);
		private JournalMenuItem account;
		
		private final JLabel labelCount = FormUtil.createSubTitle("");//afiche le nombre doperation
		private final JButton btnNext = new JButton( new ImageIcon(R.getIcon("next")));
		private final JButton btnPrev = new JButton( new ImageIcon(R.getIcon("prev")));
		private final JRadioButton [] radiosChart = { new JRadioButton("Sources", true), new JRadioButton("Localisation")};
		private final JButton btnFilter = new JButton("Filtrer", new ImageIcon(R.getIcon("normalize")));
		
		private final Panel panelCommand = new  Panel(new BorderLayout());//conteneur des elements de navigation dans les donnees
		private final Box boxRadiosChart = Box.createHorizontalBox();//filtrage graphique
		private final Box boxFilter = Box.createHorizontalBox();
		
		private final JButton btnToExcel = new JButton("Excel", new ImageIcon(R.getIcon("export")));
		private final JButton btnToPdf = new JButton("PDF", new ImageIcon(R.getIcon("pdf")));
		private final JButton btnToPrint = new JButton("Imprimer", new ImageIcon(R.getIcon("print")));
		
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
		
		{
			//chart
			pieModelSource.setRealMaxPriority(true);
			pieModelLocation.setRealMaxPriority(true);
			pieModelSource.setSuffix(" $");
			pieModelLocation.setSuffix(" $");
			pieModelSource.setTitle("Répartition sélon les sources d'alimentation du compte");
			pieModelLocation.setTitle("Répartition sélon les emplassements");
			piePanel.getRender().setHovable(false);
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

			boxFilter.add(btnFilter);
			 
			final Box box = Box.createHorizontalBox();
			box.add(btnPrev);
			box.add(btnNext);
			 
			labelCount.setFont(new Font("Arial", Font.BOLD, 15));
			labelCount.setHorizontalAlignment(JLabel.LEFT);
			 
			panelCommand.add(box, BorderLayout.EAST);
			panelCommand.add(boxFilter, BorderLayout.WEST);
			 
			panelCommand.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}
		
		public PanelRecipes () {
			super(new BorderLayout());
			
			final Panel panelChart =new Panel(new BorderLayout());
			final Panel panelList = new Panel(new BorderLayout());
			final Panel panelTop = new Panel(new BorderLayout());
			final Box box = Box.createHorizontalBox();
			
			box.add(btnToExcel);
			box.add(btnToPdf);
			box.add(btnToPrint);
			
			panelTop.add(labelCount, BorderLayout.CENTER);
			panelTop.add(box, BorderLayout.EAST);
			
			panelList.add(tablePanel, BorderLayout.CENTER);
			panelList.add(panelCommand, BorderLayout.SOUTH);
			
			panelChart.add(piePanel, BorderLayout.CENTER);
			panelChart.add(boxRadiosChart, BorderLayout.NORTH);
			
			final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelList, panelChart);
			split.setOneTouchExpandable(true);
			
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
			}
			
			labelCount.setText(tableModel.getRowCount()+" Opération"+(tableModel.getRowCount() > 1? "s":""));
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
		
	}
	
	private final class PanelTabDashboard extends Panel implements JournalMenuItemListener {
		private static final long serialVersionUID = 484389202609625039L;
		
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel, FormUtil.BKG_END_2);
		
		private final DefaultLineChartModel lineChartModel = new DefaultLineChartModel();
		private final LineChartRender lineChartRender = new LineChartRender(lineChartModel);
		
		private final DefaultCardModel<Double> cardSoldModel = new DefaultCardModel<>(CardType.DARK, R.getIcon("caisse"), "$");
		private final DefaultCardModel<Double> cardOutlayModel = new DefaultCardModel<>(CardType.DARK, R.getIcon("btn-cancel"), "$");
		private final DefaultCardModel<Double> cardRecipeModel = new DefaultCardModel<>(CardType.DARK, R.getIcon("btn-add"), "$");
		
		{
			cardSoldModel.setTitle("Solde");
			cardSoldModel.setInfo("Montant disponible");
			cardSoldModel.setValue(0d);
			
			cardOutlayModel.setTitle("Depenses");
			cardOutlayModel.setInfo("Déjà utiliser");
			cardOutlayModel.setValue(0d);
			
			cardRecipeModel.setTitle("Recettes");
			cardRecipeModel.setInfo("Totale des recettes");
			cardRecipeModel.setValue(0d);
			
			pieModel.setRealMaxPriority(true);
			pieModel.setSuffix("$");
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
				lineContainer = new Panel(new BorderLayout()),
				pieContainer = new Panel(new  BorderLayout());
			
			pieContainer.add(piePanel, BorderLayout.CENTER);
			lineContainer.add(lineChartRender, BorderLayout.CENTER);
			
			tabbedPane.addTab("", new ImageIcon(R.getIcon("pie")), pieContainer, "Soldes par emplacement");
			tabbedPane.addTab("", new ImageIcon(R.getIcon("chart")), lineContainer, "Ligne de temps");
			
			center.add(tabbedPane, BorderLayout.CENTER);
			center.setBorder(new EmptyBorder(0, 0, 10, 0));
			
			bottom.add(cardSold);
			bottom.add(cardRecipe);
			bottom.add(cardOutlay);
			
			add(center, BorderLayout.CENTER);
			add(bottom, BorderLayout.SOUTH);
			
			paymentLocationDao.addListener(locationAdapter);
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
			reloadLine();
			reloadPie();
		}
		
		/**
		 * Rechargement du graphique sous forme tarte
		 * et des cards
		 */
		public void reloadPie () {
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
		
		/**
		 * Rechargement du graphique lineaire qui visualise les entrees et les sorties
		 * pour le compte sectionner
		 */
		public void reloadLine () {
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
		public void setAccount (JournalMenuItem account) {
			
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
		 * pour signaler le panel qu'il y a un traitement lourd encours
		 * @param wait
		 */
		public void wait (boolean wait) {
			//tableOutlays.setEnabled(!wait);
			//tableRecipes.setEnabled(!wait);
			setCursor(wait? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
		}
		
	}
	
	/**
	 * model du tableau de visualisation de la repartition d'une recete
	 * @author Esaie MUHASA
	 *
	 */
	private class RecipeTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 5961836886953247067L;
		
		private List<RecipePart<?>> data = new ArrayList<>();
		private AnnualSpend account;
		
		
		private final DAOAdapter<PaymentFee> feeAdapter = new DAOAdapter<PaymentFee>() {

			@Override
			public synchronized void onCreate(PaymentFee e, int requestId) {
				if(account  == null || e.getInscription().getPromotion().getAcademicFee() == null 
						|| !allocationCostDao.check(account.getId(), e.getInscription().getPromotion().getAcademicFee().getId()))
					return;
				
				AllocationCost allocationCost = allocationCostDao.find(account, e.getInscription().getPromotion().getAcademicFee());
				
				double amount = (e.getAmount()/100) * allocationCost.getPercent();
				DefaultRecipePart<PaymentFee> part = new DefaultRecipePart<PaymentFee>(e, account, e.getLocation(), 
						e.getInscription().getStudent().getFullName(), account.getUniversitySpend().getTitle(), amount);
				data.add(part);
				fireTableRowsDeleted(data.size()-1, data.size()-1);
			}

			@Override
			public synchronized void onUpdate(PaymentFee e, int requestId) {
				if(account  == null)
					return;
				
				reload(account);
			}

			@Override
			public synchronized void onDelete(PaymentFee e, int requestId) {
				if(account  == null)
					return;
				
				reload(account);
			}
			
		};
		
		private final DAOAdapter<OtherRecipe> recipeAdapter = new DAOAdapter<OtherRecipe>() {

			@Override
			public synchronized void onCreate(OtherRecipe e, int requestId) {
				if(account == null || !allocationRecipeDao.check(e.getAccount().getId(), account.getId()))
					return;
				
				AllocationRecipe allocationRecipe = allocationRecipeDao.find(e.getAccount(), account);
				
				double amount = (e.getAmount()/100) * allocationRecipe.getPercent();
				DefaultRecipePart<OtherRecipe> part = new DefaultRecipePart<OtherRecipe>(e, account, e.getLocation(),
						e.getLabel(), account.getUniversitySpend().getTitle(), amount);
				data.add(part);
				fireTableRowsDeleted(data.size()-1, data.size()-1);
			}

			@Override
			public synchronized void onUpdate(OtherRecipe e, int requestId) {
				if(account == null)
					return;
				
				reload(account);
			}

			@Override
			public synchronized void onDelete(OtherRecipe e, int requestId) {
				if(account == null)
					return;
				
				reload(account);
			}
			
		};

		public RecipeTableModel() {
			paymentFeeDao.addListener(feeAdapter);
			otherRecipeDao.addListener(recipeAdapter);
		}
		
		public void reload (AnnualSpend account) {
			data.clear();
			this.account = account;
			
			if(account != null) {
				if(paymentFeePartDao.checkBySpend(account)){
					List<RecipePart<PaymentFee>>  parts = paymentFeePartDao.findBySpend(account);
					for (RecipePart<PaymentFee> part : parts) {
						data.add(part);
					}
				}
				
				if(otherRecipePartDao.checkBySpend(account)){
					List<RecipePart<OtherRecipe>>  parts = otherRecipePartDao.findBySpend(account);
					for (RecipePart<OtherRecipe> part : parts) {
						data.add(part);
					}
				}
			}
			
			fireTableDataChanged();
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
				case 3: return data.get(rowIndex).getAmount()+" USD";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}
		
	}
}
