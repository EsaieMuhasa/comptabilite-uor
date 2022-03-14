/**
 * 
 */
package net.uorbutembo.views.models;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.CardModel;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class GeneralBudgetModel extends DefaultPieModel {
	
	private DefaultCardModel<Double> cardModel;
	private AcademicYear currentYear;
	
	private final AcademicFeeDao academicFeeDao;
	private final FeePromotionDao feePromotionDao;
	private final AllocationCostDao allocationCostDao;
	private final InscriptionDao inscriptionDao;
	private final UniversitySpendDao universitySpendDao;

	/**
	 * 
	 */
	public GeneralBudgetModel(DAOFactory factory) {
		super();
		this.academicFeeDao = factory.findDao(AcademicFeeDao.class);
		this.feePromotionDao = factory.findDao(FeePromotionDao.class);
		this.allocationCostDao = factory.findDao(AllocationCostDao.class);
		this.inscriptionDao = factory.findDao(InscriptionDao.class);
		this.universitySpendDao = factory.findDao(UniversitySpendDao.class);
		
		cardModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		cardModel.setTitle("Budget général");
		cardModel.setInfo("Montant que doit payer tout les étudiants");
		cardModel.setIcon(R.getIcon("acounting"));
		cardModel.setSuffix("$");
		this.setTitle("Répartition général du budget");
	}
	
	@Override
	public PiePart findByData(Object data) {
		if(data instanceof AnnualSpend) {
			AnnualSpend dt = (AnnualSpend) data;
			for (PiePart part : parts) {
				if(!(data instanceof AnnualSpend)) 
					continue;
				
				AnnualSpend spend = (AnnualSpend) part.getData();
				if(spend != null && dt.getId() == spend.getId())
					return part;
			}
		}
		return super.findByData(data);
	}
	
	@Override
	public void setMax(double max) {
		super.setMax(max);
		BigDecimal big = new  BigDecimal(max).setScale(2, RoundingMode.HALF_UP);
		cardModel.setValue(big.doubleValue());
	}
	
	@Override
	public String getSuffix() {
		return " "+FormUtil.UNIT_MONEY_SYMBOL;
	}

	/**
	 * @return the cardModel
	 */
	public CardModel<Double> getCardModel() {
		return cardModel;
	}

	/**
	 * @return the currentYear
	 */
	public AcademicYear getCurrentYear() {
		return currentYear;
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		this.calculAll();
	}
	
	private void calculAll() {
		this.removeAll();
		
		List<AcademicFee> fees = this.academicFeeDao.findByAcademicYear(currentYear);
		
		for (AcademicFee fee : fees) {
			List<AllocationCost> costs = this.allocationCostDao.findByAcademicFee(fee);
			List<FeePromotion> feePromotions = this.feePromotionDao.findByAcademicFee(fee);
			
			//comptage des inscriptions dans chaque promotion
			int count = 0;
			for (FeePromotion fp : feePromotions) {
				count += this.inscriptionDao.countByPromotion(fp.getPromotion().getId());
			}
			
			for (int i=0, max = costs.size(); i< max; i++) {
				AllocationCost al = costs.get(i);
				PiePart part = this.findByData(al.getAnnualSpend());
				Color color = COLORS[i%(COLORS.length-1)];
				
				if(part == null){
					UniversitySpend sp = this.universitySpendDao.findById(al.getAnnualSpend().getUniversitySpend().getId());
					part = new DefaultPiePart(color, 0, sp.getTitle());
					part.setData(al.getAnnualSpend());
				}
				
				double value = count * al.getAmount();
				part.setValue(part.getValue()+value);
				this.addPart(part);
			}
			this.setMax(this.getMax() + (count * fee.getAmount()));
		}
		
		
	}
}
