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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PieModel;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;

/**
 * @author Esaie MUHASA
 *
 */
public class FormGroupAllocationCost extends Panel {
	private static final long serialVersionUID = -6416909862130925182L;
	
	private JLabel title = FormUtil.createSubTitle("");
	private AcademicFee academicFee;
	private List<AnnualSpend> annualSpends;
	private List<FeePromotion> feePromotions;
	private List<AllocationCostField> fields = new ArrayList<>();
	private FeePromotionDao feePromotionDao;
	private AllocationCostDao allocationCostDao;
	
	private DefaultListModel<FeePromotion> listFeePromotionModel = new DefaultListModel<>();
	private JList<FeePromotion> listFeePromotion = new JList<>(listFeePromotionModel);
	private GridLayout layout = new GridLayout(1, 1);
	
	
	//pour le grahique
	private PieModel pieModel = new DefaultPieModel();
	private PiePanel piePanel = new PiePanel(pieModel);

	/**
	 * Constructeur d'initialisation
	 * @param feePromotionDao
	 * @param academicFee
	 * @param annualSpends
	 */
	public FormGroupAllocationCost(FeePromotionDao feePromotionDao, AcademicFee academicFee, List<AnnualSpend> annualSpends) {
		super(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		this.feePromotionDao = feePromotionDao;
		this.allocationCostDao = feePromotionDao.getFactory().findDao(AllocationCostDao.class);
		this.academicFee = academicFee;
		this.annualSpends = annualSpends;
		this.title.setText(academicFee.getAmount()+" "+UNIT_MONEY);
		
		this.pieModel.setMax(academicFee.getAmount());
		this.pieModel.setTitle("Repartition du "+academicFee.getAmount()+" USD");
		
		if(this.feePromotionDao.checkByAcademicFee(academicFee.getId())) {
			feePromotions = feePromotionDao.findByAcademicFee(academicFee);
		} else {
			feePromotions = new ArrayList<>();
		}
		this.init();
		this.setBorder(new EmptyBorder(0, 0, DEFAULT_V_GAP*2, 0));
	}
	
	/**
	 * initialisation de l'interface graphique
	 */
	private void init () {
		
		//le promotions conserner
		for (FeePromotion fee : feePromotions) {
			listFeePromotionModel.addElement(fee);
		}
		
		final Panel container = new Panel(layout);
		final Panel left = new Panel(new BorderLayout());
		final Box center = Box.createVerticalBox();
		
		//les champs de text
		int i = 0;
		for (AnnualSpend spend : annualSpends) {
			
			i++;
			int c = i % (COLORS.length-1);
			Color color = COLORS[c];
			DefaultPiePart part = new DefaultPiePart(color, spend.getUniversitySpend().getTitle());
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
				fieldPercent.setValue("0");
				fieldAmount.setValue("0");
				part.setValue(0.0);
			}
			
			
			label.setForeground(Color.LIGHT_GRAY);
			label.setOpaque(true);
			label.setBackground(FormUtil.BORDER_COLOR);
			label.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			panelPadding.add(label, BorderLayout.NORTH);
			panel.add(box, BorderLayout.CENTER);
			
			box.add(fieldAmount);
			box.add(Box.createHorizontalStrut(DEFAULT_H_GAP));
			box.add(fieldPercent);
			
			this.fields.add(field);
			
			panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			panelPadding.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
			panelPadding.add(panel, BorderLayout.CENTER);
			center.add(panelPadding);
			center.add(Box.createVerticalStrut(DEFAULT_H_GAP));
			
		}
		
		center.add(Box.createVerticalGlue());
		
		container.add(left);
		container.add(piePanel);
		piePanel.setBackground(BKG_END);
		
		left.add(new JScrollPane(this.listFeePromotion), BorderLayout.WEST);
		left.add(center, BorderLayout.CENTER);
		
		this.add(container, BorderLayout.CENTER);
		this.add(this.title, BorderLayout.NORTH);
		this.title.setBackground(FormUtil.BKG_START);
		this.title.setOpaque(true);
		this.title.setHorizontalAlignment(JLabel.CENTER);

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
			}
		}

		/**
		 * Ajout des evenements au chemps de text
		 */
		private void init() {
			if(cost != null)
				part = pieModel.getPartByName(cost.getAnnualSpend().getUniversitySpend().getTitle());
			
			this.amount.addCaretListener(event -> {
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
