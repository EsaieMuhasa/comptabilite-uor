/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.tools.FormUtil.COLORS;
import static net.uorbutembo.tools.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.tools.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.tools.FormUtil.UNIT_MONEY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretListener;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PieModel;
import net.uorbutembo.swing.charts.PieModelListener;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;

/**
 * @author Esaie MUHASA
 * Formulaire de repartition des frais universitaire.
 * Contiens entre autre un groupe de champs de texte pour chaque rubrique du budget, 
 * et un graphique Pie, permetant la visualisation de graphique.
 */
public class FormGroupAllocationCost extends Panel {
	private static final long serialVersionUID = -6416909862130925182L;
	
	private final JLabel title = FormUtil.createSubTitle("");
	private AcademicFee academicFee;
	private List<AnnualSpend> annualSpends = new ArrayList<>();
	private List<AllocationCostField> fields = new ArrayList<>();
	
	private AllocationCostDao allocationCostDao;
	private AnnualSpendDao annualSpendDao;
	private AcademicYearDao academicYearDao;
	
	private final GridLayout layout = new GridLayout(1, 2);
	
	//paneau principeau
	private final Panel container = new Panel(layout);
	private final Panel left = new Panel(new BorderLayout());
	private final Panel right = new  Panel(new BorderLayout());
	private final Panel bottom = new Panel();
	private final Box center = Box.createVerticalBox();
	private final JScrollPane fieldScroll = FormUtil.createScrollPane(center);
	
	private final Button  btnSave = new Button(new ImageIcon(R.getIcon("success")), "Enregistrer");
	
	//pour le grahique
	private final DefaultPieModel pieModel = new DefaultPieModel();
	private final PiePanel piePanel = new PiePanel(pieModel);
	private final JLabel 
		labelPercentUsed = FormUtil.createSubTitle(""),
		labelAmountUsed = FormUtil.createSubTitle("");
	
	private PieModelListener pieListener = new PieModelListener() {
		
		@Override
		public void repaintPart(PieModel model, int partIndex) {
			refresh(model);
		}
		
		@Override
		public void refresh (PieModel model) {
			double percent = model.getSumPercent();
			BigDecimal bigPercent = new BigDecimal(percent).setScale(4, RoundingMode.FLOOR);
			BigDecimal bigAmount = new BigDecimal(model.getRealMax()).setScale(4, RoundingMode.FLOOR);
			labelPercentUsed.setText(bigPercent.doubleValue()+" %");
			labelAmountUsed.setText(bigAmount.doubleValue()+" "+FormUtil.UNIT_MONEY+"");
			btnSave.setEnabled(percent == 100.0);
		}
		
		@Override
		public void onSelectedIndex(PieModel model, int oldIndex, int newIndex) {}
	};
	
	private final DAOAdapter<AnnualSpend> annualAdapter = new DAOAdapter<AnnualSpend>() {
		@Override
		public void onCreate(AnnualSpend e, int requestId) {
			if(academicFee != null && academicFee.getAcademicYear().getId() == e.getAcademicYear().getId()) 
				createFieldGroup(e);
		}
		
		@Override
		public void onCreate(AnnualSpend[] e, int requestId) {
			for (AnnualSpend spend : e) {
				if(academicFee != null && academicFee.getAcademicYear().getId() == spend.getAcademicYear().getId()) 
					createFieldGroup(spend);
			}
		}
		
		@Override
		public void onDelete(AnnualSpend e, int requestId) {
			for (int i = 0; i < annualSpends.size(); i++) {
				if(annualSpends.get(i).getId() ==e.getId()) {
					annualSpends.remove(i);
					init(academicFee, annualSpends);
					return;
				}
			}
		}
	};

	/**
	 * Constructeur d'initialisation
	 * @param feePromotionDao
	 */
	public FormGroupAllocationCost(AllocationCostDao allocationCostDao) {
		super(new BorderLayout());
		this.allocationCostDao = allocationCostDao;
		this.annualSpendDao= allocationCostDao.getFactory().findDao(AnnualSpendDao.class);
		this.academicYearDao = allocationCostDao.getFactory().findDao(AcademicYearDao.class);
		this.initViews();
		setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		
		piePanel.getModel().addListener(pieListener);
		piePanel.getRender().setHovable(false);
		annualSpendDao.addListener(annualAdapter);
		btnSave.setEnabled(false);
		pieModel.setSuffix(FormUtil.UNIT_MONEY_SYMBOL);
		
		//syncronisation des scroll bars
		fieldScroll.getVerticalScrollBar().addAdjustmentListener(event -> {
			if (event.getSource() != fieldScroll.getVerticalScrollBar())
				return;
			
			JScrollBar bar = piePanel.getScroll().getVerticalScrollBar();
			BigDecimal big = new BigDecimal((( 100.0 / event.getAdjustable().getMaximum()) * event.getValue()) * (bar.getMaximum() / 100)).setScale(0, RoundingMode.HALF_UP);
			int value =  big.intValue();
			bar.setValue(value - bar.getModel().getExtent()/4);
		});
		
	}
	
	/**
	 * Modification de la repartion des frais encours de visualisation
	 * @param academicFee the academicFee to set
	 * @param annualSpends the annualSpends to set
	 */
	public synchronized void init (AcademicFee academicFee, List<AnnualSpend> annualSpends) {
		this.academicFee = academicFee;
		
		if(annualSpends != null){
			this.annualSpends = annualSpends;
			pieModel.removeAll();
			fields.clear();
			center.removeAll();
		}
		
		pieModel.setMax(academicFee.getAmount());
		title.setText("Répartition du "+academicFee.getAmount()+" "+UNIT_MONEY);
		pieModel.setTitle(title.getText());
		
		updateViews();
		
		//la repartiton est modificable uniquement pour l'annee courante
		//les archives ne sont plus modifiable
		if(academicYearDao.isCurrent(academicFee.getAcademicYear())) {	
			container.add(left, 0);
			layout.setColumns(2);
			bottom.setVisible(true);
		} else {
			bottom.setVisible(false);
			container.remove(left);
			layout.setColumns(1);
		}
		
		container.repaint();
	}
	
	/**
	 * @param academicFee
	 */
	public void init (AcademicFee academicFee) {
		this.init(academicFee, null);
	}

	/**
	 * initialisation de l'interface graphique
	 */
	private void initViews() {
		title.setBackground(FormUtil.BKG_END);
		title.setOpaque(true);
		title.setHorizontalAlignment(JLabel.CENTER);

		bottom.add(btnSave);
		bottom.setVisible(false);
		btnSave.addActionListener(event -> {//lors du click sur le bouton d'enregistrement
			btnSave.setEnabled(false);
			
			/**
			 * le processuce peut prendre +/- du temps, car le operations ci-dessous sont au rendez-vous
			 * - ceparation des proportions a creer oua metre en jours
			 * - demande d'enregistrement
			 * - demande de mise en jours
			 * - suppression de operation qui n'ont pas un repartion != 0
			 * => I execute all operations in a new thread
			 */
			Thread t = new Thread(() -> {	
				Date now = new Date();
				List<AllocationCost> toCreate = new ArrayList<>(),
						toUpdate = new ArrayList<>(), 
						toDelete = new ArrayList<>();
				
				try {
					for (AllocationCostField field : fields) {
						AllocationCost cost = field.getCost();
						if(cost.getId() != 0) {
							cost.setLastUpdate(now);
							if(cost.getAmount() != 0.0)
								toUpdate.add(cost);
							else 
								toDelete.add(cost);
						} else {
							if (cost.getAmount() != 0.0) {								
								cost.setRecordDate(now);
								toCreate.add(cost);
							}
						}
					}
					
					if (toCreate.size() != 0){//creation des nouvelles rubriques
						AllocationCost [] tabToCreate = new AllocationCost[toCreate.size()];
						for (int i = 0; i < tabToCreate.length; i++) {
							tabToCreate[i] = toCreate.get(i);
						}
						allocationCostDao.create(tabToCreate);
					}

					if (toUpdate.size() != 0) {//mis en jour
						AllocationCost [] tabToUpdate = new AllocationCost[toUpdate.size()];
						long [] tabId = new long [toUpdate.size()];
						for (int i = 0; i < tabToUpdate.length; i++) {
							tabToUpdate[i] = toUpdate.get(i);
							tabId[i] = toUpdate.get(i).getId();
						}
						allocationCostDao.update(tabToUpdate, tabId);				
					}
					
					if (toDelete.size() != 0) {
						long [] tab = new long [toDelete.size()];
						for (int i = 0; i < tab.length; i++)
							tab[i] = toDelete.get(i).getId();
						allocationCostDao.delete(tab);
						for (int i = 0; i < tab.length; i++)
							toDelete.get(i).setId(0);
					}
					
					JOptionPane.showMessageDialog(null, "Succès d'enregistrement de \nla répartiton du "+academicFee.getAmount()+" USD", "Alert", JOptionPane.INFORMATION_MESSAGE);
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				btnSave.setEnabled(true);
			});
			t.start();
		});
		
		center.add(Box.createVerticalGlue());
		center.setBorder(new EmptyBorder(0, 0, 0, DEFAULT_V_GAP));
		
		container.add(left);
		container.add(right);
		container.setBorder(new LineBorder(FormUtil.BORDER_COLOR));
		
		final Panel labels = new  Panel(new GridLayout(1, 2, DEFAULT_H_GAP, DEFAULT_H_GAP));
		final Panel padding = new  Panel(new BorderLayout());

		padding.add(fieldScroll, BorderLayout.CENTER);
		padding.setBorder(new EmptyBorder(0, DEFAULT_H_GAP, 0, 0));
		
		labels.add(labelAmountUsed);
		labels.add(labelPercentUsed);
		labels.setBackground(FormUtil.BORDER_COLOR);
		labels.setOpaque(true);
		
		right.add(piePanel, BorderLayout.CENTER);
		left.add(labels, BorderLayout.SOUTH);
		left.add(padding, BorderLayout.CENTER);
		
		piePanel.setBackground(FormUtil.BKG_DARK);
		piePanel.setBorderColor(FormUtil.BKG_END);
		
		add(title, BorderLayout.NORTH);
		add(bottom, BorderLayout.SOUTH);
		add(container, BorderLayout.CENTER);
	}
	
	/**
	 * mise en jours des composant graphiques
	 */
	private synchronized void updateViews () {
		
		if (!fields.isEmpty()) {//reutilisation des champs deja creer
			for ( int i= 0, max= annualSpends.size(); i<max; i++) {
				AllocationCostField field = fields.get(i);
				AnnualSpend spend = annualSpends.get(i);
				AllocationCost cost = null ;
				if(allocationCostDao.check(spend.getId(), academicFee.getId()))
					cost = allocationCostDao.find(spend, academicFee);
				else {
					cost = new AllocationCost();
					cost.setAcademicFee(academicFee);
					cost.setAnnualSpend(spend);
				}
				
				field.updateModels(spend, cost);
			}
			return;
		}
		
		pieModel.removeAll();
		fields.clear();
		center.removeAll();
		
		//les champs de text
		for ( int i= 0, max= annualSpends.size(); i<max; i++) {
			AnnualSpend spend = annualSpends.get(i);
			createFieldGroup(spend);
		}

		center.repaint();
		//btnSave.setEnabled(academicFee != null && pieModel.getCountPart() != 0 && academicYearDao.isCurrent(academicFee.getAcademicYear()));
	}
	
	/**
	 * utilitaire de creation du group des champs qui permet de cofigurer
	 * la portion d'une rubique du budjet
	 * @param spend
	 */
	private void createFieldGroup (AnnualSpend spend) {
		int c = pieModel.getCountPart() % (COLORS.length-1);
		Color color = COLORS[c];
		
		DefaultPiePart part = new DefaultPiePart(color, spend.getUniversitySpend().getTitle());
		part.setLabel(spend.getUniversitySpend().getTitle());
		part.setData(spend);
		pieModel.addPart(part);
		
		final TextField<String> fieldAmount = new TextField<>("Montant en USD");
		final TextField<String> fieldPercent = new TextField<>("Valeur en %");
		final Panel panel = new Panel(new BorderLayout(DEFAULT_V_GAP, DEFAULT_H_GAP));
		final Panel panelPadding = new Panel(new BorderLayout());
		final JLabel label = new JLabel(spend.getUniversitySpend().getTitle());
		final Box box = Box.createHorizontalBox();
		final AllocationCostField field = new AllocationCostField(fieldAmount, fieldPercent, spend);
		
		AllocationCost cost = null;
		if(this.allocationCostDao.check(spend.getId(), academicFee.getId())) {//pour ceux qui existe deja
			cost = this.allocationCostDao.find(spend, academicFee);
		} else {//pour ceux qui n'existe pas
			cost = new AllocationCost();
			cost.setAnnualSpend(spend);
			cost.setAcademicFee(academicFee);
		}
		
		field.setCost(cost);
		part.setValue(cost.getAmount());
		
		label.setForeground(color);
		label.setOpaque(true);
		label.setBackground(color.darker().darker().darker());
		Border border = new LineBorder(label.getBackground());
		label.setBorder(border);
		
		panelPadding.add(label, BorderLayout.NORTH);
		panel.add(box, BorderLayout.CENTER);
		
		box.add(fieldAmount);
		box.add(Box.createHorizontalStrut(DEFAULT_H_GAP));
		box.add(fieldPercent);
		
		panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		panelPadding.setBorder(border);
		panelPadding.add(panel, BorderLayout.CENTER);
		
		fields.add(field);
		center.add(panelPadding);
		center.add(Box.createVerticalStrut(DEFAULT_H_GAP));
	}
	
	/**
	 * Liberation des resources utiliser par le groupe
	 */
	public void clear() {
		for (AllocationCostField field : fields) {
			field.dispose();
		}
		fields.clear();
		btnSave.setEnabled(false);
		title.setText(" ");
		pieModel.removeAll();
		center.removeAll();
		center.repaint();
		academicFee = null;
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
      g2.setColor(FormUtil.BKG_DARK);
      g2.fillRect(0, this.getHeight()-5, this.getWidth(), 5);
      super.paintBorder(g);
	}
	
	
	/**
	 * @return the academicFee
	 */
	public AcademicFee getAcademicFee() {
		return academicFee;
	}

	/**
	 * @return the fields
	 */
	public List<AllocationCostField> getFields() {
		return fields;
	}

	/**
	 * @author Esaie MUHASA
	 * utilitaire qui surveille le modification des champs d'une rubique budgetaire
	 */
	protected class AllocationCostField {
		
		private TextField<String> amount;
		private TextField<String> percent;
		private AnnualSpend spend;
		private AllocationCost cost;
		private PiePart part;
		
		private FocusListener focusAmount = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(amount.getValue()==null || amount.getValue().trim().isEmpty()) {
					amount.setValue("0");
				}
				
				pieModel.setSelectedIndex(-1);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				pieModel.setSelectedIndex(pieModel.indexOf(part));
			}
		};
		
		private FocusListener focusPercent = new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				pieModel.setSelectedIndex(-1);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				pieModel.setSelectedIndex(pieModel.indexOf(part));
			}
		};
		
		private CaretListener caretAmount = (event) -> {
			if(cost == null)
				return;
			
			try {
				BigDecimal number = new BigDecimal(Float.parseFloat(amount.getValue())).setScale(2, RoundingMode.HALF_UP);
				cost.setAmount(number.floatValue());
				if(part!=null) {
					part.setValue(cost.getAmount());
				}
			} catch (NumberFormatException e) {
				return;
			}
			
			if(!this.amount.hasFocus()) 
				return;
			
			double amount =cost.getAmount();
			BigDecimal number = new BigDecimal((100.0 / academicFee.getAmount()) * amount).setScale(2, RoundingMode.HALF_UP);
			double percent = number.doubleValue();
			this.percent.setValue(percent+"");
		};
		
		private CaretListener caretPercent = (event) -> {
			if(!this.percent.hasFocus()) 
				return;
			
			double percent = 0;
			try {
				BigDecimal number = new BigDecimal(Float.parseFloat(this.percent.getValue())).setScale(2, RoundingMode.HALF_UP);
				percent = number.doubleValue();
			} catch (NumberFormatException e) {
				this.amount.setValue(cost.getAmount()+"");
				return;
			}
			
			BigDecimal number = new BigDecimal((academicFee.getAmount() / 100.0) * percent).setScale(2, RoundingMode.HALF_UP);
			double amount = number.doubleValue();
			this.amount.setValue(amount+"");
		};
		
		/**
		 * constructeur d'initialisation
		 * @param amount
		 * @param percent
		 * @param spend
		 */
		public AllocationCostField(TextField<String> amount, TextField<String> percent, AnnualSpend spend) {
			this.amount = amount;
			this.percent = percent;
			this.spend = spend;
			this.init();
		}
		
		public AllocationCostField(TextField<String> amount, TextField<String> percent, AllocationCost cost) {
			this.amount = amount;
			this.percent = percent;
			this.spend = cost.getAnnualSpend();
			this.cost = cost;
			this.init();
		}
		
		/**
		 * Mis en jours du model des donnes
		 * @param soend
		 * @param cost
		 */
		public synchronized void updateModels (AnnualSpend spend, AllocationCost cost) {
			this.spend = spend;
			this.setCost(cost);
		}
		
		/**
		 * @return the cost
		 */
		public AllocationCost getCost() {
			return cost;
		}

		/**
		 * @param cost the cost to set
		 */
		public void setCost(AllocationCost cost) {
			this.cost = cost;
			if(cost != null) {				
				amount.setValue(cost.getAmount()+"");
				double percent = 0;
				if ((academicFee.getAmount() * cost.getAmount()) != 0) {
					BigDecimal number = new BigDecimal((100.0 / academicFee.getAmount()) * cost.getAmount()).setScale(2, RoundingMode.HALF_UP);
					percent = number.doubleValue();
				} 
				this.percent.setValue(percent+"");
				part = pieModel.getPartByName(cost.getAnnualSpend().getUniversitySpend().getTitle());
				
				part.setValue(cost.getAmount());
				
				if (cost.getAcademicFee() == null || cost.getAcademicFee().getId() <= 0)
					cost.setAcademicFee(academicFee);
			}
		}
		
		public void dispose() {
			amount.removeFocusListener(focusAmount);
			percent.removeFocusListener(focusAmount);
			amount.removeCaretListener(caretAmount);
			percent.removeCaretListener(caretPercent);
		}

		/**
		 * Ajout des evenements au chemps de text
		 */
		private void init() {
			if(cost != null)
				part = pieModel.getPartByName(cost.getAnnualSpend().getUniversitySpend().getTitle());
			
			//lors du changemet du montant, on recalcule les pourcentage 
			//=> seuelement dans le cas où le champ amount est la source de l'evenement
			this.amount.addCaretListener(caretAmount);
			
			this.amount.addFocusListener(focusAmount);
			this.percent.addFocusListener(focusPercent);
			
			//dans le cas où le pourcentage change, on recalcule montant
			//=> operation faite, uniquement dans le cas où, le field percenage est source du changement
			this.percent.addCaretListener(caretPercent);
		}

		/**
		 * @return the amount
		 */
		public TextField<String> getAmount() {
			return amount;
		}

		/**
		 * @return the percent
		 */
		public TextField<String> getPercent() {
			return percent;
		}

		/**
		 * @return the spend
		 */
		public AnnualSpend getSpend() {
			return spend;
		}
	}

}
