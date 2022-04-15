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
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.CardModel;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PieModel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * Model du graphique pie. Cette classe inclue les model ci-dessous:
 * + le model du card du bujet jenerale
 * + le model du card du montant deja payer
 * + le model du pie de repartition du montant deja payer, selon la configuration des rubrique budgetaite
 */
public class GeneralBudgetPieModel extends DefaultPieModel {
	
	private DefaultCardModel<Double> cardModel;
	private AcademicYear currentYear;
	private final List<AcademicFee> academicFees = new ArrayList<>();
	
	private final AcademicYearDao academicYearDao;
	private final AcademicFeeDao academicFeeDao;
	private final PaymentFeeDao paymentFeeDao;
	private final PromotionDao promotionDao;
	private final AllocationCostDao allocationCostDao;
	private final InscriptionDao inscriptionDao;
	private final UniversitySpendDao universitySpendDao;

	//pour le montant deja payer
	private DefaultCardModel<Double> cardModelPayment;
	private DefaultPieModel pieModelPayment;
	//==
	

	public GeneralBudgetPieModel(DAOFactory factory) {
		super();
		academicFeeDao = factory.findDao(AcademicFeeDao.class);
		promotionDao = factory.findDao(PromotionDao.class);
		allocationCostDao = factory.findDao(AllocationCostDao.class);
		inscriptionDao = factory.findDao(InscriptionDao.class);
		universitySpendDao = factory.findDao(UniversitySpendDao.class);
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		academicYearDao = factory.findDao(AcademicYearDao.class);
		
		inscriptionDao.addListener(new DAOAdapter<Inscription>() {
			@Override
			public void onCreate(Inscription e, int requestId) {
				if (!academicYearDao.isCurrent(currentYear))
					return;
				
				AcademicFee fee = e.getPromotion().getAcademicFee();
				
				if (fee != null) {					
					setMax(getMax() + fee.getAmount());
					List<AllocationCost> costs = allocationCostDao.findByAcademicFee(fee);
					updateParts(costs, 1);
				}
			}
			@Override
			public void onUpdate(Inscription e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)) {
					reload();
					reloadPayment();
				}
			}
			@Override
			public void onDelete(Inscription e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)) 
					reload();
			}
		});
		
		allocationCostDao.addListener(new DAOAdapter<AllocationCost>() {
			@Override
			public void onCreate(AllocationCost[] e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)){
					reload();
					reloadPayment();
				}
			}
			@Override
			public void onCreate(AllocationCost e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)) {					
					reload();
					reloadPayment();
				}
			}
			@Override
			public void onUpdate(AllocationCost e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)){
					reload();
					reloadPayment();
				}
			}
			@Override
			public void onUpdate(AllocationCost[] e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)){
					reload();
					reloadPayment();
				}
			}
			@Override
			public void onDelete(AllocationCost e, int requestId) {
				if(academicYearDao.isCurrent(currentYear)){
					reload();
					reloadPayment();
				}
			}
		});
		
		promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public void onUpdate(Promotion e, int requestId) {
				reload();
				reloadPayment();
			}
		});
		
		academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onDelete(AcademicFee e, int requestId) {reload();}
			@Override
			public void onUpdate(AcademicFee e, int requestId) {
				reload();
				reloadPayment();
			}
		});
		
		//card du budget generale
		cardModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		cardModel.setTitle("Budget général");
		cardModel.setInfo("Montant que doit payer tout les étudiants");
		cardModel.setIcon(R.getIcon("acounting"));
		cardModel.setSuffix("$");
		cardModel.setValue(0d);
		//==
		
		//card de l'etat des payements
		cardModelPayment = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		cardModelPayment.setValue(0d);
		cardModelPayment.setTitle("Payer par les etudiants");
		cardModelPayment.setInfo("Montant déjà payer par tout les étudiants");
		cardModelPayment.setIcon(R.getIcon("caisse"));
		cardModelPayment.setSuffix("$");
		paymentFeeDao.addListener(new DAOAdapter <PaymentFee>() {
			@Override
			public void onCreate(PaymentFee e, int requestId) {
				cardModelPayment.setValue(cardModelPayment.getValue()+e.getAmount());
			}
			
			@Override
			public void onCreate(PaymentFee[] payments, int requestId) {
				double amount = 0d;
				for (PaymentFee e : payments) {
					amount += e.getAmount();
				}
				cardModelPayment.setValue(cardModelPayment.getValue()+amount);
			}
			
			@Override
			public void onUpdate(PaymentFee e, int requestId) {reload();}
			
			@Override
			public void onUpdate(PaymentFee[] e, int requestId) {reload();}
			
			@Override
			public void onDelete(PaymentFee e, int requestId) {cardModelPayment.setValue(cardModelPayment.getValue() - e.getAmount());}
			
			@Override
			public void onDelete(PaymentFee[] payments, int requestId) {
				double amount = 0d;
				for (PaymentFee e : payments) {
					amount += e.getAmount();
				}
				cardModelPayment.setValue(cardModelPayment.getValue() - amount);
			}
		});
		//==
		
		pieModelPayment = new DefaultPieModel();
		pieModelPayment.setTitle("Payement par faculté");
		this.setTitle("Répartition général du budget");
	}
	
	@Override
	public PiePart findByData(Object data) {
		if(data instanceof AnnualSpend) {
			AnnualSpend dt = (AnnualSpend) data;
			for (PiePart part : parts) {
				if(!(part.getData() instanceof AnnualSpend)) 
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
	 * @return the cardModelPayment
	 */
	public DefaultCardModel<Double> getCardModelPayment() {
		return cardModelPayment;
	}

	/**
	 * @return the pieModelPayment
	 */
	public DefaultPieModel getPieModelPayment() {
		return pieModelPayment;
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
		reloadPayment();
	}
	
	/**
	 * rechargement de donnees 
	 * et on refais tout les calculs du graphique et du card du budget generale
	 */
	public synchronized void reload () {
		academicFees.clear();
		if(currentYear != null && academicFeeDao.checkByAcademicYear(currentYear.getId())) {
			List<AcademicFee> fees = this.academicFeeDao.findByAcademicYear(currentYear);
			for (AcademicFee af : fees) {
				academicFees.add(af);
			}
		}
		this.calculAll();
	}
	
	/**
	 * Rechargement du model du pie des montants deja payer
	 * En plus tout les calculs sont refaite
	 */
	private synchronized void reloadPayment () {
		
		pieModelPayment.removeAll();
		pieModelPayment.setMax(0);
		for (int i = 0, count = getCountPart(); i < count; i++) {//creation des copies de part du model du budget generale
			PiePart part = new DefaultPiePart(getPartAt(i));
			part.setValue(0);
			pieModelPayment.addPart(part);
		}
		
		if (!inscriptionDao.checkByAcademicYear(currentYear))
			return;
		
		List<Inscription> inscriptions = inscriptionDao.findByAcademicYear(currentYear);
		double max = 0;
		for (Inscription i : inscriptions) {
			if (!paymentFeeDao.checkByInscription(i)) 
				continue;
			
			List<PaymentFee> payments = paymentFeeDao.findByInscription(i);
			List<AllocationCost> costs = allocationCostDao.findByAcademicFee(i.getPromotion().getAcademicFee());
			max += updateParts(pieModelPayment, costs, payments);
		}
		
		pieModelPayment.setMax(max);
		cardModelPayment.setValue(max);
		
	}
	
	/**
	 * mise en jours des parts du model des payements
	 * @param model
	 * @param costs
	 * @param payments
	 */
	private double updateParts (PieModel model, List<AllocationCost> costs, List<PaymentFee> payments) {
		double sold = 0, percent;
		for (PaymentFee fee : payments) {
			for (int i=0, max = costs.size(); i< max; i++) {
				AllocationCost al = costs.get(i);
				PiePart globalPart = findByData(al.getAnnualSpend());
				PiePart part = model.findByData(globalPart.getData());
				percent = (fee.getAmount() / 100.0) * getPercentOf(globalPart);
				part.setValue(part.getValue() + percent);
			}
			sold += fee.getAmount();
		}
		return sold;
	}
	
	/**
	 * calcult de la repartition des frais universitaire
	 * Cette methode ecrase tout les calculs deja fais bien avant et re-commence tout a zero
	 */
	private synchronized void calculAll() {
		
		removeAll();
		setMax(0);
		
		double max = 0;
		for (AcademicFee fee : academicFees) {
			List<AllocationCost> costs = allocationCostDao.checkByAcademicFee(fee.getId())? this.allocationCostDao.findByAcademicFee(fee) : new ArrayList<>();
			List<Promotion> promotions = promotionDao.checkByAcademicFee(fee)? promotionDao.findByAcademicFee(fee) : new ArrayList<>();
			
			//comptage des inscriptions dans chaque promotion
			int count = 0;
			for (Promotion p : promotions) {
				count += this.inscriptionDao.countByPromotion(p.getId());
			}
			
			updateParts(costs, count);
			max += count * fee.getAmount();
		}
		this.setMax(max);
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
