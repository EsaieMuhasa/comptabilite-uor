/**
 * 
 */
package net.uorbutembo.views.models;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AllocationCost;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
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
	private final List<AcademicFee> academicFees = new ArrayList<>();
	
	private final AcademicFeeDao academicFeeDao;
	private final PromotionDao promotionDao;
	private final AllocationCostDao allocationCostDao;
	private final InscriptionDao inscriptionDao;
	private final UniversitySpendDao universitySpendDao;

	/**
	 * 
	 */
	public GeneralBudgetModel(DAOFactory factory) {
		super();
		this.academicFeeDao = factory.findDao(AcademicFeeDao.class);
		this.promotionDao = factory.findDao(PromotionDao.class);
		this.allocationCostDao = factory.findDao(AllocationCostDao.class);
		this.inscriptionDao = factory.findDao(InscriptionDao.class);
		this.universitySpendDao = factory.findDao(UniversitySpendDao.class);
		
		inscriptionDao.addListener(new DAOAdapter<Inscription>() {
			@Override
			public void onCreate(Inscription e, int requestId) {
				AcademicFee fee = e.getPromotion().getAcademicFee();
				
				setMax(getMax() + fee.getAmount());
				List<AllocationCost> costs = allocationCostDao.findByAcademicFee(fee);
				updateParts(costs, 1);
			}
			@Override
			public void onUpdate(Inscription e, int requestId) {reload();}
			@Override
			public void onDelete(Inscription e, int requestId) {reload();}
		});
		
		allocationCostDao.addListener(new DAOAdapter<AllocationCost>() {
			@Override
			public void onCreate(AllocationCost[] e, int requestId) {reload();}
			@Override
			public void onCreate(AllocationCost e, int requestId) {reload();}
			@Override
			public void onUpdate(AllocationCost e, int requestId) {reload();}
			@Override
			public void onUpdate(AllocationCost[] e, int requestId) {reload();}
			@Override
			public void onDelete(AllocationCost e, int requestId) {reload();}
		});
		
		promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public void onUpdate(Promotion e, int requestId) {reload();}
			@Override
			public void onDelete(Promotion e, int requestId) {reload();}
		});
		
		academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onDelete(AcademicFee e, int requestId) {reload();}
			@Override
			public void onUpdate(AcademicFee e, int requestId) {reload();}
		});
		
		cardModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		cardModel.setTitle("Budget général");
		cardModel.setInfo("Montant que doit payer tout les étudiants");
		cardModel.setIcon(R.getIcon("acounting"));
		cardModel.setSuffix("$");
		cardModel.setValue(0d);
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
	public synchronized void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		reload();
	}
	
	/**
	 * rechargement de donnees 
	 * et on refais tout les calculs
	 */
	public synchronized void reload () {
		Thread t = new Thread(() -> {
			academicFees.clear();
			if(currentYear != null && academicFeeDao.checkByAcademicYear(currentYear.getId())) {
				List<AcademicFee> fees = this.academicFeeDao.findByAcademicYear(currentYear);
				for (AcademicFee af : fees) {
					academicFees.add(af);
				}
			}
			this.calculAll();			
		});
		t.start();
	}
	
	/**
	 * calcult de la repartition des frais universitaire
	 * Cette methode ecrase tout les calculs deja fais bien avant et re-commence tout a zero
	 */
	private synchronized void calculAll() {
		this.removeAll();
		
		removeAll();
		setMax(0);
		
		for (AcademicFee fee : academicFees) {
			List<AllocationCost> costs = this.allocationCostDao.findByAcademicFee(fee);
			List<Promotion> promotions = promotionDao.checkByAcademicFee(fee)? promotionDao.findByAcademicFee(fee) : new ArrayList<>();
			
			//comptage des inscriptions dans chaque promotion
			int count = 0;
			for (Promotion p : promotions) {
				count += this.inscriptionDao.countByPromotion(p.getId());
			}
			
			updateParts(costs, count);
			this.setMax(this.getMax() + (count * fee.getAmount()));
		}
	}
	
	/**
	 * mis en jours des parts selon les repartitions des frais universitaires
	 * @param costs
	 * @param multiplicateur
	 */
	private void updateParts (List<AllocationCost> costs, double multiplicateur) {
		
		for (int i=0, max = costs.size(); i< max; i++) {
			AllocationCost al = costs.get(i);
			PiePart part = this.findByData(al.getAnnualSpend());
			
			if(part == null) {
				Color color = COLORS[i%(COLORS.length-1)];
				UniversitySpend sp = universitySpendDao.findById(al.getAnnualSpend().getUniversitySpend().getId());
				part = new DefaultPiePart(color, 0, sp.getTitle());
				part.setData(al.getAnnualSpend());
				part.setValue(0);
			}
			
			double value = multiplicateur * al.getAmount();
			part.setValue(part.getValue()+value);
			this.addPart(part);
		}
	}
}
