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
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class FormFeePromotion extends DefaultFormPanel  {
	private static final long serialVersionUID = 6991790198392841255L;
	
	private AcademicYear currentYear;
	private PromotionDao promotionDao;
	private AcademicFeeDao academicFeeDao;
	private FeePromotionDao feePromotionDao;

	private final DefaultListModel<Promotion> modelUnselectedPromotion = new DefaultListModel<>();
	
	private final GridLayout layout = new GridLayout(1, 3, FormUtil.DEFAULT_H_GAP*2, FormUtil.DEFAULT_V_GAP);
	private final Panel container = new Panel(layout);
	private DialogChoosePromotion  dialogPromotion;
	
	
	private List<PanelAcademicFeeConfig> panelsConfig = new ArrayList<>();
	private PanelConfigListener  configListener = new  PanelConfigListener() {//ecouteur du panel de repartition pour chaque frais
		
		@Override
		public void requireAdding(PanelAcademicFeeConfig config) {
			if(dialogPromotion == null )
				dialogPromotion = new DialogChoosePromotion(mainWindow);
			
			dialogPromotion.setReceiver(config);
			dialogPromotion.setVisible(true);
		}
		
		@Override
		public void onRemove(PanelAcademicFeeConfig config, List<FeePromotion> fees) {
			for (FeePromotion fee : fees) {
				if(fee.getId() > 0)
					feePromotionDao.delete(fee.getId());
				
				modelUnselectedPromotion.addElement(fee.getPromotion());
			}
		}
		
		@Override
		public void onAdding(FeePromotion fee) {
			if(fee.getId() == 0)
				feePromotionDao.create(fee);
		};
	};
	/**
	 * 
	 */
	public FormFeePromotion(MainWindow mainWindow, FeePromotionDao feePromotionDao) {
		super(mainWindow);
		this.setTitle("Formulaire d'enregistrement");
		
		this.feePromotionDao = feePromotionDao;
		this.promotionDao  = feePromotionDao.getFactory().findDao(PromotionDao.class);
		this.academicFeeDao = feePromotionDao.getFactory().findDao(AcademicFeeDao.class);

		this.getBody().add(container, BorderLayout.CENTER);
		
		this.init();
		this.btnSave.setVisible(false);
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

		for (AcademicFee fee : academicFees) {
			PanelAcademicFeeConfig panel = new PanelAcademicFeeConfig(fee, configListener, feePromotionDao);
			panelsConfig.add(panel);
			container.add(panel);
		}
		
		boolean next = false;
		for (Promotion promotion : promotions) {//trie des promotions non associer aux frais univ
			next = false;
			for (PanelAcademicFeeConfig panel : panelsConfig) {
				for (int i = 0, count = panel.getModel().getSize(); i<count; i++) {
					FeePromotion fee = panel.getModel().get(i);
					
					if(promotion.getId() == fee.getPromotion().getId()) {
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

	}

	/**
	 * Ecoute des evenements du DAO
	 */
	private void init() {
		
		this.promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public void onCreate(Promotion p, int requestId) {
				if(currentYear.getId() == p.getAcademicYear().getId())
					modelUnselectedPromotion.addElement(p);
			}
		});
		
		this.academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onCreate(AcademicFee fee, int requestId) {
				if(currentYear.getId() != fee.getAcademicYear().getId()) 
					return;
				
				PanelAcademicFeeConfig panel = new PanelAcademicFeeConfig(fee, configListener, feePromotionDao);
				panelsConfig.add(panel);
				container.add(panel);
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
		void onRemove (PanelAcademicFeeConfig config, List<FeePromotion> fees);
		
		/**
		 * Apres que le paneau de configuration ait instancier la relation entre les frais est la prmotion
		 * @param fee
		 */
		void onAdding (FeePromotion fee);
	}
	
	/**
	 * @author Esaie MUHASA
	 * Panel de de configuration des frais universitaire
	 */
	private static final class PanelAcademicFeeConfig extends Panel {
		private static final long serialVersionUID = -3831549572169534221L; 
		
		private final AcademicFee fee;
		private final PanelConfigListener listener;
		private final FeePromotionDao feePromotionDao;
		private final DefaultListModel<FeePromotion> model = new DefaultListModel<>();
		
		private JLabel title = FormUtil.createSubTitle("");
		private final JList<FeePromotion> list = new JList<>(model);
		private final Button btnAdd = new Button(new ImageIcon(R.getIcon("success")));
		private final Button btnRemove = new Button(new ImageIcon(R.getIcon("close")));
		private final LineBorder  border = new LineBorder(FormUtil.BORDER_COLOR), 
				borderActive = new LineBorder(new Color(0xFF0000), 1);
		
		/**
		 * Constructeur d'initialisation.
		 * @param fee, les frais auquel nous voulons associer un nombre x de promotion
		 */
		public PanelAcademicFeeConfig(final AcademicFee fee, final PanelConfigListener listener, final FeePromotionDao feePromotionDao) {
			super(new BorderLayout());
			this.fee = fee;
			this.listener = listener;
			this.feePromotionDao = feePromotionDao;
			title.setText(fee.getAmount()+" "+FormUtil.UNIT_MONEY);
			title.setHorizontalAlignment(JLabel.CENTER);
			
			init();
			load();
		}
		
		/**
		 * Chargement des prmotions concerner par le frais
		 */
		public void load () {
			if(this.feePromotionDao.checkByAcademicFee(fee.getId())) {
				List<FeePromotion> data = feePromotionDao.findByAcademicFee(fee);
				for (FeePromotion f : data) {
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
			
			btnAdd.addActionListener(event -> {
				listener.requireAdding(this);
			});
			
			btnRemove.addActionListener(event ->  {
				int indexs [] = list.getSelectedIndices();
				
				if(indexs.length == 0) 
					return;
				
				List<FeePromotion> fees = new ArrayList<>();
				for (int index : indexs) {
					fees.add(model.get(index));
				}
				
				for (int i : indexs) {
					model.remove(i);
				}
				
				listener.onRemove(this, fees);
			});
		}
		
		public void setActive (boolean active) {
			if(active)
				setBorder(borderActive);
			else 
				setBorder(border);
		}
		
		public void addItem (Promotion promotion) {
			FeePromotion fee = null;
			if(feePromotionDao.checkByPromotion(promotion.getId())) {
				fee = feePromotionDao.findByPromotion(promotion.getId());
			} else {
				fee = new FeePromotion();
				fee.setAcademicFee(getFee());
				fee.setPromotion(promotion);
				fee.setRecordDate(new Date());
			}
			model.addElement(fee);
			listener.onAdding(fee);
		}

		/**
		 * @return the fee
		 */
		public AcademicFee getFee() {
			return fee;
		}

		/**
		 * @return the model
		 */
		public DefaultListModel<FeePromotion> getModel() {
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
			
			this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
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
			
			this.addWindowStateListener(new WindowAdapter() {

				@Override
				public void windowActivated(WindowEvent e) {
					if(receiver == null) {
						setVisible(false);
					}
				}
				
			});
			
			btnAccept.addActionListener(event -> {
				int [] indexs = listPromotion.getSelectedIndices();
				for (int i : indexs) {
					receiver.addItem(modelUnselectedPromotion.getElementAt(i));
				}
				
				for (int i = indexs.length; i > 0; i--) {
					modelUnselectedPromotion.remove(indexs[i-1]);
				}
				
				btnCancel.doClick();
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
