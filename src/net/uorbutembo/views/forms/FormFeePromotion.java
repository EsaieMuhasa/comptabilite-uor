/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 * formulaire permetant d'associer un promotion aux frais universitaire
 * Les frais universitaire sont declarer separement des promotions; en plus plusieur promotion peuvent payer le
 * meme frais universitaire:
 */
public class FormFeePromotion extends DefaultFormPanel  {
	private static final long serialVersionUID = 6991790198392841255L;
	
	private AcademicYear currentYear;
	private PromotionDao promotionDao;
	private AcademicFeeDao academicFeeDao;
	private AcademicYearDao academicYearDao;

	private final DefaultListModel<Promotion> modelUnselectedPromotion = new DefaultListModel<>();
	
	private final GridLayout layout = new GridLayout(1, 3, FormUtil.DEFAULT_H_GAP*2, FormUtil.DEFAULT_V_GAP);
	private final Panel container = new Panel(layout);
	private DialogChoosePromotion  dialogPromotion;
	
	
	private List<PanelAcademicFeeConfig> panelsConfig = new ArrayList<>();
	private PanelConfigListener  configListener = new  PanelConfigListener() {//ecouteur du panel de repartition pour chaque frais
		
		@Override
		public void requireAdding(PanelAcademicFeeConfig config) {
			
			if(modelUnselectedPromotion.getSize() == 0) {
				JOptionPane.showMessageDialog(mainWindow, "Toute les promotions sont déjà \nassocier aux frais universitaires.", "Information", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(dialogPromotion == null )
				dialogPromotion = new DialogChoosePromotion(mainWindow);
			
			dialogPromotion.setReceiver(config);
			dialogPromotion.setVisible(true);
		}
		
		@Override
		public void onRemove(PanelAcademicFeeConfig config, List<Promotion> promotions) {
			for (Promotion promotion : promotions) {
				promotionDao.bindToAcademicFee(promotion.getId(), 0);
				promotion.setAcademicFee(null);
				modelUnselectedPromotion.addElement(promotion);
			}
		}
		
		@Override
		public void onAdding(Promotion promotion) {
			promotionDao.bindToAcademicFee(promotion, promotion.getAcademicFee());
		};
		
		@Override
		public void onAdding(Promotion[] promotions, AcademicFee fee) {
			promotionDao.bindToAcademicFee(promotions, fee);
		}
	};
	
	private final DAOAdapter<AcademicFee> feeAdapter = new DAOAdapter<AcademicFee>() {
		@Override
		public void onCreate(AcademicFee fee, int requestId) {
			if(currentYear == null || fee.getAcademicYear().getId() != currentYear.getId())
				return;
			
			PanelAcademicFeeConfig panel = new PanelAcademicFeeConfig(fee, configListener, promotionDao);
			panelsConfig.add(panel);
			container.add(panel);
		}
		
		@Override
		public void onDelete(AcademicFee fee, int requestId) {
			if(currentYear == null || fee.getAcademicYear().getId() != currentYear.getId())
				return;
			
			for (int i = 0, count = panelsConfig.size(); i < count; i++) {
				PanelAcademicFeeConfig p = panelsConfig.get(i);
				if(p.getFee().getId() == fee.getId()) {
					container.remove(p);
					panelsConfig.remove(i);
					container.repaint();
					break;
				}
			}
		}
	};
	
	public FormFeePromotion (MainWindow mainWindow) {
		super(mainWindow);
		
		promotionDao  = mainWindow.factory.findDao(PromotionDao.class);
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		academicFeeDao.addListener(feeAdapter);

		setTitle("Formulaire d'enregistrement");
		getBody().add(container, BorderLayout.CENTER);
		
		init();
		add(getMiddle(), BorderLayout.CENTER);
	}
	
	@Override
	protected boolean createButtonAccept() {
		return false;
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		if(currentYear == null || this.currentYear == null || currentYear.getId() != this.currentYear.getId()) {			
			this.currentYear = currentYear;
			this.loadData();
		}
		
		if(currentYear != null && !academicYearDao.isCurrent(currentYear)) 
			this.setTitle("Classement des promotions");
		else
			this.setTitle(currentYear != null? TITLE_1 : "");
	}
	
	
	/**
	 * Rechargement des donnees
	 * utile lors de la modificationd de l'annee academique via le mutateur setCurrentYear
	 */
	private void loadData() {
		
		container.removeAll();
		panelsConfig.clear();
		modelUnselectedPromotion.clear();
		
		container.revalidate();
		container.repaint();
		
		if(this.currentYear == null)
			return;
		
		final List<Promotion> promotions = this.promotionDao.checkByAcademicYear(this.currentYear.getId())?
				this.promotionDao.findByAcademicYear(this.currentYear.getId()) : new ArrayList<>();
		
		final List<AcademicFee> academicFees = this.academicFeeDao.checkByAcademicYear(this.currentYear.getId())?
				this.academicFeeDao.findByAcademicYear(this.currentYear.getId()) : new ArrayList<>();

		for (AcademicFee fee : academicFees) {
			PanelAcademicFeeConfig panel = new PanelAcademicFeeConfig(fee, configListener, promotionDao);
			panelsConfig.add(panel);
			container.add(panel);
		}
		
		boolean next = false;
		for (Promotion promotion : promotions) {//trie des promotions non associer aux frais univ
			next = false;
			for (PanelAcademicFeeConfig panel : panelsConfig) {
				for (int i = 0, count = panel.getModel().getSize(); i<count; i++) {
					Promotion fee = panel.getModel().get(i);
					
					if(promotion.getId() == fee.getId()) {
						next = true;
						break;
					}
				}
				
				if(next)
					break;
			}
			
			if(!next) 
				modelUnselectedPromotion.addElement(promotion);
		}

		changeStateButtonAdding(!modelUnselectedPromotion.isEmpty());
	}
	
	/**
	 * Changement d'etat des boutons d'association d'une promotion aux frais universitaire
	 * pour chaque panel de configuration
	 * @param enable
	 */
	private void changeStateButtonAdding (boolean enable) {
		for (PanelAcademicFeeConfig panel : panelsConfig) {
			panel.btnAdd.setEnabled(enable);
		}
	}

	/**
	 * Ecoute des evenements du DAO
	 */
	private void init() {
		
		this.promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public void onCreate(Promotion p, int requestId) {
				if(currentYear.getId() == p.getAcademicYear().getId()){
					modelUnselectedPromotion.addElement(p);
					changeStateButtonAdding(!modelUnselectedPromotion.isEmpty());
				}
			}
			
			@Override
			public void onDelete(Promotion e, int requestId) {
				if(e.getAcademicYear().getId() == currentYear.getId()) {
					for (int i = 0, count = modelUnselectedPromotion.getSize(); i < count; i++) {
						if(e.getId() == modelUnselectedPromotion.get(i).getId()) {
							modelUnselectedPromotion.remove(i);
							break;
						}
					}
					changeStateButtonAdding(!modelUnselectedPromotion.isEmpty());
				}
			}
			
			
		});
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {}
	
	/**
	 * @author Esaie MUHASA
	 * Listener 
	 */
	private interface PanelConfigListener {
		
		/**
		 * Evenemet d'ecoute d'ajout de nouveau promotion 
		 * dans la configuration des frais academique
		 * @param config
		 */
		void requireAdding (PanelAcademicFeeConfig config);
		
		/**
		 * Supression de promotions dans la configuration des frais universitaire
		 * @param config
		 * @param fees
		 */
		void onRemove (PanelAcademicFeeConfig config, List<Promotion> fees);
		
		/**
		 * Apres que le paneau de configuration ait instancier la relation entre les frais est la prmotion
		 * @param fee
		 */
		void onAdding (Promotion fee);
		
		/**
		 * apres association d'une collection des promotions au frais universitaires
		 * @param promotions
		 * @param fee
		 */
		void onAdding (Promotion [] promotions, AcademicFee fee);
	}
	
	/**
	 * @author Esaie MUHASA
	 * Panel de configuration des frais universitaire.
	 * facilite l'association d'un groupe des promotion aux frais universitaire
	 */
	private static final class PanelAcademicFeeConfig extends Panel {
		private static final long serialVersionUID = -3831549572169534221L; 
		
		private final AcademicFee fee;
		private final PanelConfigListener listener;
		private final PromotionDao promotionDao;
		private final AcademicYearDao academicYearDao;
		private final DefaultListModel<Promotion> model = new DefaultListModel<>();
		
		private JLabel title = FormUtil.createSubTitle("");
		private final JList<Promotion> list = new JList<>(model);
		private final JButton btnAdd = new JButton(new ImageIcon(R.getIcon("plus")));
		private final JButton btnRemove = new JButton(new ImageIcon(R.getIcon("close")));
		private final LineBorder  border = new LineBorder(FormUtil.BORDER_COLOR), 
				borderActive = new LineBorder(new Color(0xFF0000), 1);
		
		/**
		 * Constructeur d'initialisation.
		 * @param fee, les frais auquel nous voulons associer un nombre x de promotion
		 */
		public PanelAcademicFeeConfig(final AcademicFee fee, final PanelConfigListener listener, final PromotionDao promotionDao) {
			super(new BorderLayout());
			this.fee = fee;
			this.listener = listener;
			this.promotionDao = promotionDao;
			academicYearDao = promotionDao.getFactory().findDao(AcademicYearDao.class);
			title.setText(fee.getAmount()+" "+FormUtil.UNIT_MONEY);
			title.setHorizontalAlignment(JLabel.CENTER);
			
			init();
			load();
		}
		
		/**
		 * @return the fee
		 */
		public AcademicFee getFee() {
			return fee;
		}

		/**
		 * Chargement des prmotions concerner par le frais
		 */
		public void load () {
			if(this.promotionDao.checkByAcademicFee(fee.getId())) {
				List<Promotion> data = promotionDao.findByAcademicFee(fee);
				for (Promotion f : data) {
					model.addElement(f);
				}
			}
		}
		
		/**
		 * Personnalisation des composant graphique
		 */
		private void init() {
			add(title, BorderLayout.NORTH);
			add(FormUtil.createVerticalScrollPane(list), BorderLayout.CENTER);
			
			Panel bottom = new Panel();
			bottom.add(btnAdd);
			bottom.add(btnRemove);
			add(bottom, BorderLayout.SOUTH);
			setBorder(border);
			
			btnAdd.setVisible(academicYearDao.isCurrent(fee.getAcademicYear()));
			btnAdd.addActionListener(event -> {
				listener.requireAdding(this);
			});
			
			btnRemove.addActionListener(event ->  {
				int indexs [] = list.getSelectedIndices();
				
				if(indexs.length == 0) 
					return;
				
				List<Promotion> fees = new ArrayList<>();
				for (int index : indexs) {
					fees.add(model.get(index));
				}
				
				for (int index = indexs.length-1; index != -1; index--) {
					model.remove(indexs[index]);
				}
				
				listener.onRemove(this, fees);
			});
			
			btnRemove.setEnabled(false);
			btnRemove.setVisible(academicYearDao.isCurrent(fee.getAcademicYear()));
			list.addListSelectionListener(event -> {
				btnRemove.setEnabled(list.getSelectedIndex() != -1);
			});
			
		}
		
		public void setActive (boolean active) {
			if(active)
				setBorder(borderActive);
			else 
				setBorder(border);
		}
		
		public void addItem (Promotion promotion) {
			promotion.setAcademicFee(fee);
			model.addElement(promotion);
			listener.onAdding(promotion);
		}
		
		/**
		 * ajout association d'une collection des promotions
		 * @param promotions
		 */
		public void addItems(Promotion [] promotions) {
			for (Promotion promotion : promotions) {
				promotion.setAcademicFee(fee);
				model.addElement(promotion);
			}
			listener.onAdding(promotions, fee);
		}

		/**
		 * @return the model
		 */
		public DefaultListModel<Promotion> getModel() {
			return model;
		}
		
	}
	
	/**
	 * @author Esaie MUHASA
	 * Poite de dialogue permetant de choisir une/plusieur promotions
	 * pour l'/les associer(s) au frais universitarie
	 */
	private class DialogChoosePromotion extends JDialog {
		private static final long serialVersionUID = -4259412319774601929L;
		
		private final JList<Promotion> listPromotion = new JList<>(modelUnselectedPromotion);
		private final JButton btnAccept = new  JButton("Valider");
		private final JButton btnCancel = new JButton("Fermer");
		
		private PanelAcademicFeeConfig receiver;
		
		public DialogChoosePromotion(MainWindow parent) {
			super(parent, "Selectionner une promotion", false);
			
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			this.setSize(300, 400);
			this.setLocationRelativeTo(parent);
			
			//init ihm
			final Panel bottom = new Panel();
			bottom.add(btnAccept);
			bottom.add(btnCancel);
			bottom.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			this.getContentPane().setBackground(FormUtil.BKG_DARK);
			this.getContentPane().add(FormUtil.createSubTitle("Promotion disponible"), BorderLayout.NORTH);
			this.getContentPane().add(FormUtil.createVerticalScrollPane(listPromotion), BorderLayout.CENTER);
			this.getContentPane().add(bottom, BorderLayout.SOUTH);
			//==init ihm
			
			WindowAdapter listener = new WindowAdapter() {

				@Override
				public void windowActivated(WindowEvent e) {
					if(receiver == null) {
						setVisible(false);
					}
				}
				
				@Override
				public void windowClosing(WindowEvent e) {
					if(receiver != null)
						receiver.setActive(false);
					
					receiver = null;
					setVisible(false);
				}
				
			};
			
			this.addWindowStateListener(listener);
			this.addWindowListener(listener);
			
			btnAccept.addActionListener(event -> {
				btnAccept.setEnabled(false);
				Thread t = new Thread(() -> {
					setCursor(FormUtil.WAIT_CURSOR);
					getParent().setCursor(FormUtil.WAIT_CURSOR);
					int [] indexs = listPromotion.getSelectedIndices();
					Promotion [] promotions = new Promotion[indexs.length];
					
					for (int i = indexs.length - 1; i >= 0; i--) {
						promotions[i] = modelUnselectedPromotion.getElementAt(indexs[i]);
						modelUnselectedPromotion.remove(indexs[i]);
					}
					
					if(promotions.length == 1)
						receiver.addItem(promotions[0]);
					else 
						receiver.addItems(promotions);
					
					btnCancel.doClick();
					setCursor(FormUtil.DEFAULT_CURSOR);
					getParent().setCursor(FormUtil.DEFAULT_CURSOR);
					btnAccept.setEnabled(true);
				});
				t.start();
			});
			
			btnCancel.addActionListener(event -> {
				if(modelUnselectedPromotion.getSize() != 0) 
					listPromotion.setSelectedIndex(0);
				receiver.setActive(false);
				this.setVisible(false);
			});
		}
		
		/**
		 * mutateur du panel en ecoute de la selection des promotion
		 * @param panel
		 */
		public void setReceiver (PanelAcademicFeeConfig panel) {
			if(receiver != null)
				receiver.setActive(false);
			receiver = panel;
			panel.setActive(true);
		}
		
	}

}
