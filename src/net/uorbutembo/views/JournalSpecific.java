/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.DefaultRecipePart;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.RecipePart;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
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
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.views.components.JournalMenuItem;
import net.uorbutembo.views.forms.FormOtherRecipe;
import net.uorbutembo.views.forms.FormOutlay;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.OtherRecipeTableModel;
import net.uorbutembo.views.models.OutlayTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalSpecific extends Panel  implements ActionListener, AcademicYearDaoListener{
	private static final long serialVersionUID = 790476345630470695L;
	
	private final AcademicYearDao academicYearDao;
	private final AnnualSpendDao annualSpendDao;
	private final AllocationRecipeDao allocationRecipeDao;
	private final AllocationCostDao allocationCostDao;
	private final OutlayDao outlayDao;
	private final OtherRecipePartDao otherRecipePartDao;
	private final PaymentFeePartDao paymentFeePartDao;
	private final PaymentFeeDao paymentFeeDao;
	private final OtherRecipeDao otherRecipeDao;
	
	private final ListAccount listAccount;
	private final ContainerPanel containerPanel;
	
	private final JButton btnRecipe = new JButton("Nouvelle recette", new ImageIcon(R.getIcon("plus")));
	private final JButton btnSpend = new JButton("Nouveau dépense", new ImageIcon(R.getIcon("moin")));
	
	private FormOutlay formOutlay;
	private FormOtherRecipe formRecipe;
	private JDialog dialogOutlay;
	private JDialog dialogRecipe;
	
	private OtherRecipeTableModel recipeTableModel;
	private Table recipeTable;
	
	private final DAOFactory factory;
	private final MainWindow mainWindow;
	
	private JPopupMenu popupRecipe;
	private JMenuItem itemDeleteRecipe;
	private JMenuItem itemUpdateRecipe;
	
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
		academicYearDao = factory.findDao(AcademicYearDao.class);
		allocationRecipeDao = factory.findDao(AllocationRecipeDao.class);
		allocationCostDao = factory.findDao(AllocationCostDao.class);
		outlayDao = factory.findDao(OutlayDao.class);
		otherRecipePartDao = factory.findDao(OtherRecipePartDao.class);
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		paymentFeePartDao = factory.findDao(PaymentFeePartDao.class);
		otherRecipeDao = factory.findDao(OtherRecipeDao.class);
		
		academicYearDao.addYearListener(this);
		
		listAccount = new ListAccount();
		containerPanel = new ContainerPanel();
		
		add(containerPanel, BorderLayout.CENTER);
		add(listAccount, BorderLayout.EAST);
		
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
	 * demande de modification d'une recette
	 * @param recipe
	 */
	private void updateRecipe (OtherRecipe recipe) {
		formRecipe.setOtherRecipe(recipe);
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
		
		recipeTableModel = new OtherRecipeTableModel(otherRecipeDao);
		recipeTable = new Table(recipeTableModel);
		recipeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		recipeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && recipeTable.getSelectedRow() != -1) {
					createPopupRecipe();
					popupRecipe.show(recipeTable, e.getX(), e.getY());
				}
			}
		});
		
		final Panel panel = new Panel(new BorderLayout());
		panel.add(FormUtil.createVerticalScrollPane(recipeTable), BorderLayout.CENTER);
		
		panel.setBorder(new LineBorder(FormUtil.BORDER_COLOR));
		
		dialogRecipe.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogRecipe.getContentPane().add(formRecipe, BorderLayout.NORTH);
		dialogRecipe.getContentPane().add(panel, BorderLayout.CENTER);
		dialogRecipe.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogRecipe.setSize(800, 550);
		dialogRecipe.setResizable(false);
		recipeTableModel.reload();
		
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
		
		dialogOutlay.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogOutlay.getContentPane().add(formOutlay, BorderLayout.CENTER);
		dialogOutlay.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogOutlay.pack();
		dialogOutlay.setSize(650, dialogOutlay.getHeight());
		dialogOutlay.setResizable(false);
		
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
			Outlay out = containerPanel.outlayModel.getRow(containerPanel.tableOutlays.getSelectedRow());
			int status = JOptionPane.showConfirmDialog(dialogRecipe, "Voulez-vous vraiment supprimer ce dépense??", "Suppression", JOptionPane.OK_CANCEL_OPTION);
			if(status == JOptionPane.OK_OPTION) {
				try {					
					outlayDao.delete(out.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(dialogRecipe, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		itemUpdateOutlay.addActionListener(event -> {
			Outlay out = containerPanel.outlayModel.getRow(containerPanel.tableOutlays.getSelectedRow());
			updateOutlay(out);
		});
	}
	
	/**
	 * creation du popup menu permetant de modifier/supprimer recette (autres, que les frais academique)
	 */
	private void createPopupRecipe() {
		if (popupRecipe != null)
			return;
		
		popupRecipe = new JPopupMenu();
		
		itemDeleteRecipe = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
		itemUpdateRecipe = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
		popupRecipe.add(itemDeleteRecipe);
		popupRecipe.add(itemUpdateRecipe);
		
		itemDeleteRecipe.addActionListener(event -> {
			OtherRecipe recipe = recipeTableModel.getRow(recipeTable.getSelectedRow());
			int status = JOptionPane.showConfirmDialog(dialogRecipe, "Voulez-vous vraiment supprimer \nla dite recette??", "Suppression", JOptionPane.OK_CANCEL_OPTION);
			if(status == JOptionPane.OK_OPTION) {
				try {					
					otherRecipeDao.delete(recipe.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(dialogRecipe, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		itemUpdateRecipe.addActionListener(event -> {
			OtherRecipe recipe = recipeTableModel.getRow(recipeTable.getSelectedRow());
			updateRecipe(recipe);
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		for (JournalMenuItem menu : listAccount.items) 
			menu.setActive(false);
		
		JournalMenuItem item = (JournalMenuItem) e.getSource();
		item.setActive(true);
		containerPanel.setAccount(item.getAccount());
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		containerPanel.wait(true);
		listAccount.reload();
		containerPanel.wait(false);
	}
	
	/**
	 * consultation de la liste des compte et des operations effectuer sur ces comptes
	 * @author Esaie MUHASA
	 */
	private class ListAccount extends Panel{
		private static final long serialVersionUID = 6893735808920207627L;

		private final Box container = Box.createVerticalBox();
		private final DefaultComboBoxModel<AcademicYear> model = new DefaultComboBoxModel<>();
		private final JComboBox<AcademicYear> comboBox = new JComboBox<>(model);
		private final List<JournalMenuItem> items = new ArrayList<>();
		
		/**
		 * Ecouteur de changement de l'annee academique selectionner dans le combobox
		 */
		private final ItemListener itemListener = (event) -> {
			int index = comboBox.getSelectedIndex();
			if (event.getStateChange() == ItemEvent.SELECTED && index != -1) {
				
				AcademicYear year = model.getElementAt(index);
				comboBox.setEnabled(false);
				comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				containerPanel.wait(true);
				Thread t = new Thread(() ->{
					reload(year);
					
					container.revalidate();
					container.repaint();
					comboBox.setCursor(Cursor.getDefaultCursor());
					comboBox.setEnabled(true);
					containerPanel.wait(false);
				});
				
				t.start();
				
			} else if (index == -1) {
				containerPanel.setAccount(null);
			}
			
			container.revalidate();
			container.repaint();
		};
		
		/**
		 * Construction du sidebar
		 */
		public ListAccount() {
			super(new BorderLayout());
			
			setPreferredSize(new Dimension(340, 600));
			final Panel top = new Panel(new BorderLayout());
			final Panel bottom = new Panel();
			final JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
			
			comboBox.addItemListener(itemListener);

			top.add(comboBox, BorderLayout.CENTER);
			top.setBorder(new EmptyBorder(0, 0, 5, 0));
			
			bottom.add(btnRecipe);
			bottom.add(btnSpend);
			
			add(top, BorderLayout.NORTH);
			add(bottom, BorderLayout.SOUTH);
			add(scroll, BorderLayout.CENTER);
			setBorder(new EmptyBorder(10, 10, 10, 10));
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
		 * Mis en jours complete du sidebar
		 * le contenue du combobox est recharger
		 * puis la liste des comptes
		 */
		public void reload () {
			comboBox.removeItemListener(itemListener);
			model.removeAllElements();
			if (academicYearDao.countAll() != 0) {				
				final List<AcademicYear> years = academicYearDao.findAll();
				for (AcademicYear year : years) {
					model.addElement(year);
				}
				comboBox.setSelectedIndex(0);
				reload(model.getElementAt(0));
			}
			comboBox.addItemListener(itemListener);
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
				containerPanel.setAccount(items.get(0).getAccount());
				container.add(Box.createVerticalGlue());
			} else {
				containerPanel.setAccount(null);
			}
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	private class ContainerPanel extends Panel{
		private static final long serialVersionUID = -5154188254710321300L;
		
		private AnnualSpend account;
		private final Panel centerPanel = new Panel(new BorderLayout());
		private final JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.TOP);
		
		private final JLabel title = FormUtil.createSubTitle("");
		
		private final OutlayTableModel outlayModel = new OutlayTableModel(outlayDao);
		private final RecipeTableModel recipesModel = new RecipeTableModel();
		
		private final Table tableOutlays = new Table(outlayModel);
		private final Table tableRecipes = new Table(recipesModel);
		
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel);
		
		{
			pieModel.setSuffix(" $");
			piePanel.getRender().setHovable(false);
		}
		
		private final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && tableOutlays.getSelectedRow() != -1) {
					createPopupOutlay();
					popupOutlay.show(tableOutlays, e.getX(), e.getY());
				}
			}
		};

		public ContainerPanel() {
			super(new BorderLayout());
						
			centerPanel.add(title, BorderLayout.NORTH);
			centerPanel.add(tabbedPanel, BorderLayout.CENTER);
			
			final Panel panelTableOutlay = new Panel(new BorderLayout());
			final Panel panelRecipes = new Panel(new BorderLayout());
			final Panel panelTablesRecipe = new Panel(new BorderLayout());
			final Panel panelChartRecipe = new Panel(new BorderLayout());
			
			panelTablesRecipe.add(FormUtil.createVerticalScrollPane(tableRecipes), BorderLayout.CENTER);
			panelChartRecipe.add(piePanel, BorderLayout.CENTER);
			
			final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelTablesRecipe, panelChartRecipe);
			split.setOneTouchExpandable(true);
			
			tableOutlays.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tableOutlays.addMouseListener(mouseAdapter);
			panelRecipes.add(split, BorderLayout.CENTER);
			panelTableOutlay.add(FormUtil.createVerticalScrollPane(tableOutlays), BorderLayout.CENTER);
			
			tabbedPanel.addTab("Dépense", panelTableOutlay);
			tabbedPanel.addTab("Recettes", panelRecipes);
			
			centerPanel.add(tabbedPanel, BorderLayout.CENTER);
			add(centerPanel, BorderLayout.CENTER);
			setBorder(new EmptyBorder(0, 5, 5, 0));
		}

		/**
		 * @param account the account to set
		 */
		public void setAccount (AnnualSpend account) {
			
			if (this.account != null && account!=null && this.account.getId() == account.getId())
				return;
			
			this.account = account;
			centerPanel.setVisible(account != null);
			outlayModel.setAccount(this.account);
			
			if(account != null) {
				title.setText(account.getUniversitySpend().getTitle());
			}
			
			recipesModel.reload(account);
			
			updatePie();
		}
		
		/**
		 * mise en jour des elements du graphique pie
		 * en fonction du compte actuelement selectionner
		 */
		private void updatePie() {
			pieModel.removeAll();
			
			if(account == null)
				return;
			
			pieModel.setMax(account.getCollectedCost() + account.getCollectedRecipe());
			pieModel.addPart(new DefaultPiePart(FormUtil.COLORS[0], account.getCollectedCost(), "Frais académiques"));
			
			if (allocationRecipeDao.checkBySpend(account)) {
				List<AllocationRecipe> all = allocationRecipeDao.findBySpend(account);
				for (int i = 0, count = all.size(); i < count; i++) {					
					pieModel.addPart(
							new DefaultPiePart(
								FormUtil.COLORS[ (i+1) % (FormUtil.COLORS.length-1) ],
								all.get(i).getCollected(),
								all.get(i).getRecipe().getUniversityRecipe().getTitle()
							)
						);
				}
			} else {
				pieModel.addPart(new DefaultPiePart(FormUtil.COLORS[1], 0d, "Autres recettes"));				
			}
			
		}
		
		/**
		 * pour signaler le panel qu'il y a un traitement lourd encours
		 * @param wait
		 */
		public void wait(boolean wait) {
			tableOutlays.setEnabled(!wait);
			tableRecipes.setEnabled(!wait);
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
				DefaultRecipePart<PaymentFee> part = new DefaultRecipePart<PaymentFee>(e, account, 
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
				DefaultRecipePart<OtherRecipe> part = new DefaultRecipePart<OtherRecipe>(e, account, 
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
			return 3;
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0 : return "Date";
				case 2 : return "libelé";
				case 3 : return "Montant";
			}
			return super.getColumnName(column);
		}


		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0: return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getSource().getRecordDate());
				case 1: return data.get(rowIndex).getLabel();
				case 2: return data.get(rowIndex).getAmount()+" USD";
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}
		
	}
}
