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
import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AllocationRecipeDao;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
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
 * Model du graphique pie. Cette classe inclue les model ci-dessous:
 * + le model du card du bujet jenerale
 * + le model du card du montant deja payer
 * + le model du pie de repartition du montant deja payer, selon la configuration des rubrique budgetaite
 */
public class GeneralBudgetPieModel extends DefaultPieModel {
	
	private DefaultCardModel<Double> cardModel;
	private AcademicYear currentYear;
	private final List<AcademicFee> academicFees = new ArrayList<>();
	private final List<AnnualRecipe> annualRecipes = new ArrayList<>();//autres recette annuel
	
	private final AcademicYearDao academicYearDao;
	private final AcademicFeeDao academicFeeDao;
	private final PaymentFeeDao paymentFeeDao;
	private final PromotionDao promotionDao;
	private final AllocationCostDao allocationCostDao;
	private final InscriptionDao inscriptionDao;
	private final UniversitySpendDao universitySpendDao;
	private final AnnualSpendDao annualSpendDao;
	private final AnnualRecipeDao annualRecipeDao;
	private final AllocationRecipeDao allocationRecipeDao;
	private final OutlayDao outlayDao;
	private final OtherRecipeDao otherRecipeDao;

	//pour le montant deja payer
	private DefaultCardModel<Double> cardModelCaisse;
	private DefaultPieModel pieModelCaisse;
	//==
	
	private final DAOAdapter<Inscription> inscriptionListener = new DAOAdapter<Inscription>() {
		@Override
		public void onCreate(Inscription e, int requestId) {
			if (!academicYearDao.isCurrent(currentYear))
				return;
			
			calculAll();
		}
		
		@Override
		public void onUpdate(Inscription e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)) {
				reload();
				
				if (paymentFeeDao.checkByInscription(e))
					reloadCaisse();
			}
		}
		
		@Override
		public void onDelete(Inscription e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)) 
				reload();
		}
	};
	
	private final DAOAdapter<AnnualSpend> annualSpendListener = new DAOAdapter<AnnualSpend>() {
		@Override
		public synchronized void onDelete(AnnualSpend e, int requestId) {
			PiePart part = findByData(e);
			if(part != null) {
				removePart(part);
				pieModelCaisse.removeByData(part.getData());
			}
		}
		
		@Override
		public synchronized void onDelete(AnnualSpend[] e, int requestId) {
			for (AnnualSpend spend : e) {
				onDelete(spend, requestId);
			}
		}
		
		@Override
		public synchronized void onCreate(AnnualSpend e, int requestId) {
			if(currentYear != null && e.getAcademicYear().getId() == currentYear.getId()) {
				reload();
				reloadCaisse();
			}
		}
		
		@Override
		public synchronized void onCreate(AnnualSpend[] e, int requestId) {
			if(currentYear != null && e[0].getAcademicYear().getId() == currentYear.getId()) {
				reload();
				reloadCaisse();
			}
		}
	};
	
	private final DAOAdapter<AllocationCost> allocationCostListener = new DAOAdapter<AllocationCost>() {
		@Override
		public void onCreate(AllocationCost[] e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)){
				reload();
				reloadCaisse();
			}
		}
		@Override
		public void onCreate(AllocationCost e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)) {					
				reload();
				reloadCaisse();
			}
		}
		@Override
		public void onUpdate(AllocationCost e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)){
				reload();
				reloadCaisse();
			}
		}
		@Override
		public void onUpdate(AllocationCost[] e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)){
				reload();
				reloadCaisse();
			}
		}
		@Override
		public void onDelete(AllocationCost e, int requestId) {
			if(academicYearDao.isCurrent(currentYear)){
				reload();
				reloadCaisse();
			}
		}
	};
	
	private final DAOAdapter<PaymentFee> paymentListener = new DAOAdapter<PaymentFee>() {
		@Override
		public synchronized void onCreate(PaymentFee e, int requestId) {
			reloadCaisse();
		}
		
		@Override
		public synchronized void onCreate(PaymentFee[] payments, int requestId) {
			reloadCaisse();
		}
		
		@Override
		public synchronized void onUpdate(PaymentFee e, int requestId) {
			reloadCaisse();
		}
		
		@Override
		public void onUpdate(PaymentFee[] e, int requestId) {
			reloadCaisse();
		}
		
		@Override
		public void onDelete(PaymentFee e, int requestId) {
			reloadCaisse();
		}
		
		@Override
		public void onDelete(PaymentFee[] payments, int requestId) {
			reloadCaisse();
		}
	};
	
	private final DAOAdapter<OtherRecipe> otherAdapter = new DAOAdapter<OtherRecipe>() {

		@Override
		public synchronized void onCreate(OtherRecipe e, int requestId) {
			if (currentYear != null && e.getAccount().getAcademicYear() != null && 
					e.getAccount().getAcademicYear().getId() == currentYear.getId()) {
				
				reload();
				reloadCaisse();
			}
		}

		@Override
		public synchronized void onUpdate(OtherRecipe e, int requestId) {
			if (currentYear != null && e.getAccount().getAcademicYear() != null && 
					e.getAccount().getAcademicYear().getId() == currentYear.getId()) {				
				reload();
				reloadCaisse();
			}
		}

		@Override
		public synchronized void onDelete(OtherRecipe e, int requestId) {
			if (currentYear != null && e.getAccount().getAcademicYear() != null && 
					e.getAccount().getAcademicYear().getId() == currentYear.getId()) {				
				reload();
				reloadCaisse();
			}
		}
		
	};
	
	private final DAOAdapter<AllocationRecipe> allocationRecipeListener = new DAOAdapter<AllocationRecipe>() {

		@Override
		public synchronized void onCreate(AllocationRecipe e, int requestId) {
			if(indexOf(findByData(e.getSpend())) != -1)
				return;
			
			reload();
			reloadCaisse();
		}

		@Override
		public synchronized void onUpdate(AllocationRecipe e, int requestId) {
			if (e.getRecipe().getCollected() != 0) {
				reload();
				reloadCaisse();
			}
		}

		@Override
		public synchronized void onDelete(AllocationRecipe e, int requestId) {
			if (e.getRecipe().getCollected() != 0) {
				reload();
				reloadCaisse();
			}
		}

		@Override
		public synchronized void onCreate(AllocationRecipe[] e, int requestId) {
			if (e[0].getRecipe().getCollected() != 0) {
				reload();
				reloadCaisse();
			}
		}

		@Override
		public synchronized void onUpdate(AllocationRecipe[] e, int requestId) {
			if (e[0].getRecipe().getCollected() != 0) {
				reload();
				reloadCaisse();
			}
		}

		@Override
		public synchronized void onDelete(AllocationRecipe[] e, int requestId) {
			if (e[0].getRecipe().getCollected() != 0) {
				reload();
				reloadCaisse();
			}
		}
		
	};
	
	private final DAOAdapter<Outlay> outlayListener = new DAOAdapter<Outlay>() {

		@Override
		public synchronized void onCreate(Outlay e, int requestId) {
			int index = indexOf(findByData(e.getAccount()));
			PiePart part = pieModelCaisse.getPartAt(index);
			part.setValue(part.getValue() - e.getAmount());
			cardModelCaisse.setValue(cardModelCaisse.getValue() - e.getAmount());
		}

		@Override
		public synchronized void onUpdate(Outlay e, int requestId) {
			reloadCaisse();
		}

		@Override
		public synchronized void onDelete(Outlay e, int requestId) {
			int index = indexOf(findByData(e.getAccount()));
			PiePart part = pieModelCaisse.getPartAt(index);
			part.setValue(part.getValue() + e.getAmount());
		}

		@Override
		public synchronized void onDelete(Outlay[] e, int requestId) {
			for (Outlay out : e) {
				int index = indexOf(findByData(out.getAccount()));
				PiePart part = pieModelCaisse.getPartAt(index);
				part.setValue(part.getValue() + out.getAmount());
			}
		}
		
	};
	
	public GeneralBudgetPieModel(DAOFactory factory) {
		super();
		academicFeeDao = factory.findDao(AcademicFeeDao.class);
		promotionDao = factory.findDao(PromotionDao.class);
		allocationCostDao = factory.findDao(AllocationCostDao.class);
		inscriptionDao = factory.findDao(InscriptionDao.class);
		universitySpendDao = factory.findDao(UniversitySpendDao.class);
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		academicYearDao = factory.findDao(AcademicYearDao.class);
		annualSpendDao = factory.findDao(AnnualSpendDao.class);
		annualRecipeDao = factory.findDao(AnnualRecipeDao.class);
		allocationRecipeDao = factory.findDao(AllocationRecipeDao.class);
		outlayDao = factory.findDao(OutlayDao.class);
		otherRecipeDao = factory.findDao(OtherRecipeDao.class);
		
		inscriptionDao.addListener(inscriptionListener);
		annualSpendDao.addListener(annualSpendListener);
		allocationCostDao.addListener(allocationCostListener);
		paymentFeeDao.addListener(paymentListener);
		allocationRecipeDao.addListener(allocationRecipeListener);
		outlayDao.addListener(outlayListener);
		otherRecipeDao.addListener(otherAdapter);
		
		promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public void onUpdate(Promotion e, int requestId) {
				reload();
				reloadCaisse();
			}
		});
		
		academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onDelete(AcademicFee e, int requestId) {reload();}
			@Override
			public void onUpdate(AcademicFee e, int requestId) {
				reload();
				reloadCaisse();
			}
		});
		
		//card du budget generale
		cardModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		cardModel.setTitle("Budget global");
		cardModel.setInfo("Montant à ateindre pour l'année courante");
		cardModel.setIcon(R.getIcon("acounting"));
		cardModel.setSuffix("$");
		cardModel.setValue(0d);
		//==
		
		//card de l'etat des payements
		cardModelCaisse = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		cardModelCaisse.setValue(0d);
		cardModelCaisse.setTitle("Caisse");
		cardModelCaisse.setInfo("Montant liquide disponible en caisse");
		cardModelCaisse.setIcon(R.getIcon("caisse"));
		cardModelCaisse.setSuffix("$");
		//==
		
		pieModelCaisse = new DefaultPieModel();
		pieModelCaisse.setTitle("Solde pour chaque compte");
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
	 * @return the cardModelCaisse
	 */
	public DefaultCardModel<Double> getCardModelPayment() {
		return cardModelCaisse;
	}

	/**
	 * @return the pieModelCaisse
	 */
	public DefaultPieModel getPieModelPayment() {
		return pieModelCaisse;
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
		reloadCaisse();
	}
	
	/**
	 * rechargement de donnees 
	 * et on refais tout les calculs du graphique et du card du budget generale
	 */
	public synchronized void reload () {
		academicFees.clear();
		annualRecipes.clear();
		
		if(currentYear != null && academicFeeDao.checkByAcademicYear(currentYear.getId())) {
			List<AcademicFee> fees = this.academicFeeDao.findByAcademicYear(currentYear);
			for (AcademicFee af : fees) {
				academicFees.add(af);
			}
		}
		
		if(annualRecipeDao.checkByAcademicYear(currentYear)) {
			List<AnnualRecipe> recipes  = annualRecipeDao.findByAcademicYear(currentYear);
			for (AnnualRecipe recipe : recipes) {
				annualRecipes.add(recipe);
			}
		}
		this.calculAll();
	}
	
	/**
	 * Rechargement du model du pie des montants deja payer
	 * En plus tout les calculs sont refaite
	 */
	private synchronized void reloadCaisse () {
		
		pieModelCaisse.removeAll();
		pieModelCaisse.setMax(0);
		for (int i = 0, count = getCountPart(); i < count; i++) {//creation des copies de part du model du budget generale
			PiePart part = new DefaultPiePart(getPartAt(i));
			part.setValue(0);
			pieModelCaisse.addPart(part);
		}

		double max = 0;
		for (AcademicFee f: academicFees) {
			if (!allocationCostDao.checkByAcademicFee(f.getId()))
				continue;
			
			List<AllocationCost> costs = allocationCostDao.findByAcademicFee(f);
			for (int i=0, size = costs.size(); i< size; i++) {
				AllocationCost al = costs.get(i);
				PiePart globalPart = findByData(al.getAnnualSpend());
				PiePart part = pieModelCaisse.findByData(globalPart.getData());
				part.setValue(part.getValue() + al.getCollecetd());
				max += al.getCollecetd();
			}
		}
		
		for (AnnualRecipe recipe : annualRecipes) {
			if(recipe.getCollected() == 0)
				continue;
			
			for (int i = 0, count = pieModelCaisse.getCountPart(); i < count; i++) {
				PiePart part = pieModelCaisse.getPartAt(i);
				AnnualSpend spend = (AnnualSpend) part.getData();
				if (allocationRecipeDao.check(recipe.getId(), spend.getId())) {
					AllocationRecipe allocation = allocationRecipeDao.find(recipe, spend);
					double value = (recipe.getCollected() / 100.0) * allocation.getPercent();
					part.setValue(value + part.getValue());
				}
			}
			
			max += recipe.getCollected();
		}
		
		//depences
		for (int i = 0, count = pieModelCaisse.getCountPart(); i < count; i++) {
			PiePart part = pieModelCaisse.getPartAt(i);
			AnnualSpend spend = (AnnualSpend) part.getData();
			if(outlayDao.checkByAccount(spend)) {
				List<Outlay> outs = outlayDao.findByAccount(spend);
				double amount = 0;
				for (Outlay out : outs)
					amount += out.getAmount(); 
				
				part.setValue(part.getValue() - amount);
			}
		}
		
		pieModelCaisse.setMax(max);
		cardModelCaisse.setValue(pieModelCaisse.getRealMax());
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
			
			if (!allocationCostDao.checkByAcademicFee(fee.getId()))
				continue;
			
			List<AllocationCost> costs = this.allocationCostDao.findByAcademicFee(fee);
			for (int i=0, count = costs.size(); i< count; i++) {
				AllocationCost al = costs.get(i);
				PiePart part = this.findByData(al.getAnnualSpend());
				
				if(part == null) {
					Color color = COLORS[i%(COLORS.length-1)];
					UniversitySpend sp = universitySpendDao.findById(al.getAnnualSpend().getUniversitySpend().getId());
					part = new DefaultPiePart(color, 0, sp.getTitle());
					part.setData(al.getAnnualSpend());
					part.setValue(0);
				}
				
				part.setValue(part.getValue()+al.getTotalExpected());
				this.addPart(part);
			}
			max += fee.getAmount();
		}
		
		for (AnnualRecipe recipe : annualRecipes) {
			max += recipe.getCollected();
			
			if (!allocationRecipeDao.checkByRecipe(recipe)) 
				continue;
			
			List<AllocationRecipe> recipes = allocationRecipeDao.findByRecipe(recipe);
			
			for (int i=0, count = recipes.size(); i< count; i++) {
				AllocationRecipe al = recipes.get(i);
				PiePart part = this.findByData(al.getSpend());
				
				if(part == null) {
					Color color = COLORS[i%(COLORS.length-1)];
					UniversitySpend sp = universitySpendDao.findById(al.getSpend().getUniversitySpend().getId());
					part = new DefaultPiePart(color, 0, sp.getTitle());
					part.setData(al.getSpend());
					part.setValue(0);
				}
				
				double value = (al.getRecipe().getCollected() / 100.0) * al.getPercent();
				part.setValue(part.getValue()+value);
				this.addPart(part);
			}
			
		}
		this.setMax(max);
	}
	
}
