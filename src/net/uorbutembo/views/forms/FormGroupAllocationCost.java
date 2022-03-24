/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.BKG_END;
import static net.uorbutembo.views.forms.FormUtil.COLORS;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.UNIT_MONEY;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import resources.net.uorbutembo.R;

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
	
	private final GridLayout layout = new GridLayout(1, 1);
	
	//paneau principeau
	private final Panel container = new Panel(layout);
	private final Panel left = new Panel(new BorderLayout());
	private final Box center = Box.createVerticalBox();
	
	private final Button  btnSave = new Button(new ImageIcon(R.getIcon("success")), "Enregistrer");
	
	//pour le grahique
	private final DefaultPieModel pieModel = new DefaultPieModel();
	private final PiePanel piePanel = new PiePanel(pieModel);

	/**
	 * Constructeur d'initialisation
	 * @param feePromotionDao
	 */
	public FormGroupAllocationCost(AllocationCostDao allocationCostDao) {
		super(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		this.allocationCostDao = allocationCostDao;
		this.annualSpendDao= allocationCostDao.getFactory().findDao(AnnualSpendDao.class);
		
		this.initViews();
		this.setBorder(new EmptyBorder(0, 0, DEFAULT_V_GAP*2, 0));
		
		annualSpendDao.addListener(new DAOAdapter<AnnualSpend>() {
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
			public void onDelete(AnnualSpend e, int requestId) {}
			
			@Override
			public void onDelete(AnnualSpend[] e, int requestId) {}
		});
	}
	
	/**
	 * Modification de la repartion des frais encours de vistaalisation
	 * @param academicFee the academicFee to set
	 * @param annualSpends the annualSpends to set
	 */
	public synchronized void init (AcademicFee academicFee, List<AnnualSpend> annualSpends) {
		this.academicFee = academicFee;
		
		if(annualSpends != null)
			this.annualSpends = annualSpends;
		
		//this.pieModel.setTitle("Repartition du "+academicFee.getAmount()+" USD");
		this.pieModel.setMax(academicFee.getAmount());
		this.title.setText("Repartition du "+academicFee.getAmount()+" "+UNIT_MONEY);
		
		this.updateViews();
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
		this.title.setBackground(FormUtil.BKG_START);
		this.title.setOpaque(true);
		this.title.setHorizontalAlignment(JLabel.CENTER);
		
		final Panel bottom = new Panel();
		
		bottom.add(btnSave);
		btnSave.addActionListener(event -> {//lors du click sur le bouton d'enregistrement
			btnSave.setEnabled(false);
			
			/**
			 * le processuce peut prendre +/- du temps, car le operations ci-dessous sont au rendez-vous
			 * -ceparation des proportions a creer a seux a metre en jours
			 * -demande d'enregistrement
			 * -demande de mise en jours
			 * 
			 * => I execute all operations in a new thread
			 */
			Thread t = new Thread(() -> {	
				Date now = new Date();
				List<AllocationCost> toCreate = new ArrayList<>(),
						toUpdate = new ArrayList<>();
				
				try {
					for (AllocationCostField field : fields) {
						AllocationCost cost = field.getCost();
						if(cost.getId() != 0) {
							cost.setLastUpdate(now);
							toUpdate.add(cost);
						} else {
							cost.setRecordDate(now);
							toCreate.add(cost);
						}
					}
					
					if (toCreate.size() != 0){//creation des nouvelles rubriques
						AllocationCost [] tabToCreate = new AllocationCost[toCreate.size()];
						allocationCostDao.create(tabToCreate);
						for (int i = 0; i < tabToCreate.length; i++) {
							tabToCreate[i] = toCreate.get(i);
						}
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
		container.add(piePanel);
		piePanel.setBackground(BKG_END);

		left.add(FormUtil.createScrollPane(center), BorderLayout.CENTER);
		left.setBorder(new EmptyBorder(0, DEFAULT_H_GAP, 0, 0));
		
		this.add(bottom, BorderLayout.SOUTH);
		
		piePanel.setBackground(FormUtil.BKG_DARK);
		
		this.add(this.title, BorderLayout.NORTH);
		this.add(container, BorderLayout.CENTER);
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
				else 
					cost = new AllocationCost();
				
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
		
		if(this.allocationCostDao.check(spend.getId(), this.academicFee.getId())) {
			AllocationCost cost = this.allocationCostDao.find(spend, academicFee);
			field.setCost(cost);
			part.setValue(cost.getAmount());
		} else {
			AllocationCost cost = new AllocationCost();
			cost.setAnnualSpend(spend);
			cost.setAcademicFee(academicFee);
			
			field.setCost(cost);
			fieldPercent.setValue("0");
			fieldAmount.setValue("0");
			part.setValue(0.0);
		}
		
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
	public void dispose() {
		// TODO Auto-generated method stub

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
	
	@Override
	public void doLayout() {
		super.doLayout();
		if(this.getWidth()<=900) {
			this.layout.setRows(2);
		} else {
			this.layout.setRows(1);
		}
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
				this.amount.setValue(cost.getAmount()+"");
				BigDecimal number = new BigDecimal((100.0 / academicFee.getAmount()) * cost.getAmount()).setScale(2, RoundingMode.HALF_UP);
				double percent = number.doubleValue();
				this.percent.setValue(percent+"");
				part = pieModel.getPartByName(cost.getAnnualSpend().getUniversitySpend().getTitle());
				
				part.setValue(cost.getAmount());
			}
		}

		/**
		 * Ajout des evenements au chemps de text
		 */
		private void init() {
			if(cost != null)
				part = pieModel.getPartByName(cost.getAnnualSpend().getUniversitySpend().getTitle());
			
			this.amount.addCaretListener(event -> {
				
				if(cost == null)
					return;
				
				try {
					BigDecimal number = new BigDecimal(Float.parseFloat(amount.getValue())).setScale(2, RoundingMode.HALF_UP);
					cost.setAmount(number.floatValue());
					if(part!=null) {
						part.setValue(cost.getAmount());
					}
				} catch (NumberFormatException e) {
					//amount.setValue(cost.getAmount()+"");
					return;
				}
				
				if(!this.amount.hasFocus()) 
					return;
				
				double amount =cost.getAmount();
				BigDecimal number = new BigDecimal((100.0 / academicFee.getAmount()) * amount).setScale(2, RoundingMode.HALF_UP);
				double percent = number.doubleValue();
				this.percent.setValue(percent+"");
			});
			
			this.amount.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if(amount.getValue()==null || amount.getValue().trim().isEmpty()) {
						amount.setValue("0");
					}
				}
			});
			
			this.percent.addCaretListener(event -> {
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
			});
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
