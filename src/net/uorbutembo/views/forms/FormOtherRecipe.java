/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretListener;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOListener;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 */
public class FormOtherRecipe extends DefaultFormPanel {
	private static final long serialVersionUID = -3723343048984497445L;
	
	private final DefaultComboBoxModel<AcademicYear> comboFilterYearModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<AnnualRecipe> comboAccountModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<PaymentLocation> comboLocationModel = new DefaultComboBoxModel<>();
	private final Map<AcademicYear, List<AnnualRecipe>> classementRecipes = new HashMap<>();
	
	private final ComboBox<AnnualRecipe> comboAccount = new ComboBox<>("Compte à créditer", comboAccountModel);
	private final ComboBox<AcademicYear> comboAccountYearFilter = new ComboBox<>("Année académique du compte", comboFilterYearModel);
	private final ComboBox<PaymentLocation> comboLocation = new  ComboBox<>("Lieux de perception", comboLocationModel);
	
	private final TextField<String> fieldAmount =  new TextField<>("Montant en "+FormUtil.UNIT_MONEY);
	private final FormGroup<String> groupAmount = FormGroup.createTextField(fieldAmount);
	private final FormGroup<String> groupWording = FormGroup.createTextField("Libellé de perception");
	private final FormGroup<String> groupReceived = FormGroup.createTextField("N° du reçu en caisse");
	private final FormGroup<String> groupDate = FormGroup.createTextField("Date de perception (jj-mm-aaaa)");
	
	private final AnnualRecipeDao annualRecipeDao;
	private final AcademicYearDao academicYearDao;
	private final OtherRecipeDao otherRecipeDao;
	private final PaymentLocationDao paymentLocationDao;
	
	private OtherRecipe otherRecipe;

	private final DAOListener<OtherRecipe> daoListener = new DAOAdapter<OtherRecipe>() {
		@Override
		public synchronized void onCreate(OtherRecipe e, int requestId) {
			groupAmount.getField().setValue("");
			groupWording.getField().setValue("");
			groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(e.getRecordDate()));
		}

		@Override
		public synchronized void onUpdate(OtherRecipe e, int requestId) {
			groupAmount.getField().setValue("");
			groupWording.getField().setValue("");
			groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(e.getLastUpdate()));
			otherRecipe = null;
		}
	};
	
	
	private final DAOAdapter<PaymentLocation> locationAdapter = new DAOAdapter<PaymentLocation>() {

		@Override
		public synchronized void onCreate(PaymentLocation e, int requestId) {
			comboLocationModel.addElement(e);
		}

		@Override
		public synchronized void onUpdate(PaymentLocation e, int requestId) {
			for (int i = 0; i < comboLocationModel.getSize(); i++) {
				if(comboLocationModel.getElementAt(i).getId() == e.getId()){
					comboLocationModel.removeElementAt(i);
					comboLocationModel.insertElementAt(e, i);
					break;
				}
			}
		}

		@Override
		public synchronized void onDelete(PaymentLocation e, int requestId) {
			for (int i = 0; i < comboLocationModel.getSize(); i++) {
				if(comboLocationModel.getElementAt(i).getId() == e.getId()){
					comboLocationModel.removeElementAt(i);
					break;
				}
			}
		}
	};
	
	/**
	 * ecoute de changement de la valeur (du montant de la requectte) recue en recette
	 */
	private CaretListener amountListener = event -> {
		validateAmount();
	};
	
	/**
	 * ecoute evenement de changement de l'annee academique selectionner
	 */
	private final ItemListener comboAccountYearFilterListener = event -> {
		List<AnnualRecipe> spends = classementRecipes.get(comboFilterYearModel.getElementAt(comboAccountYearFilter.getSelectedIndex()));
		comboAccount.setEnabled(spends!= null && !spends.isEmpty());
		btnSave.setEnabled(spends!= null && !spends.isEmpty());
		comboAccountModel.removeAllElements();
		if(spends != null) {
			for (AnnualRecipe spend : spends) 
				comboAccountModel.addElement(spend);
		}
		
		validateAmount();
	};

	/**
	 * @param mainWindow
	 */
	public FormOtherRecipe(MainWindow mainWindow) {
		super(mainWindow);
		annualRecipeDao = mainWindow.factory.findDao(AnnualRecipeDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		otherRecipeDao = mainWindow.factory.findDao(OtherRecipeDao.class);
		paymentLocationDao = mainWindow.factory.findDao(PaymentLocationDao.class);
		
		init();
		
		otherRecipeDao.addListener(daoListener);
		paymentLocationDao.addListener(locationAdapter);
		
		final Panel panel = new Panel(new BorderLayout());
		
		panel.setBorder(new EmptyBorder(10, 0, 0, 0));
		add(panel, BorderLayout.CENTER);
		setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		load();
	}
	
	
	/**
	 * validation du montant a retirer
	 * tant que le montant est invalide, le bouton enregistrer reste desactiver
	 */
	private void validateAmount () {
		String amount = groupAmount.getField().getValue();
		boolean enable = false;
		if (!amount.trim().isEmpty() && amount.matches(RGX_NUMBER)) {
			double value = Double.parseDouble(amount);
			enable = value > 0;
		}
		
		if (comboAccountModel.getSize() == 0 || comboLocationModel.getSize() == 0)
			enable = false;
		
		btnSave.setEnabled(enable);
	}
	
	/**
	 * Chargement des donnees
	 */
	private void load() {
		List<AcademicYear> years = academicYearDao.countAll() == 0 ? new ArrayList<>() : academicYearDao.findAll();
		for (AcademicYear year : years) {
			if (annualRecipeDao.checkByAcademicYear(year.getId())) 
				classementRecipes.put(year, annualRecipeDao.findByAcademicYear(year));
			else
				classementRecipes.put(year, new ArrayList<>());
			
			comboFilterYearModel.addElement(year);
		}
		
		List<PaymentLocation> locations = paymentLocationDao.countAll() == 0? new ArrayList<>() : paymentLocationDao.findAll();
		for (PaymentLocation l : locations) 
			comboLocationModel.addElement(l);
		
		if (locations.isEmpty() || years.isEmpty())
			btnSave.setEnabled(false);
		
		groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(new Date()));
	}
	
	/**
	 * @param otherRecipe the otherRecipe to set
	 */
	public void setOtherRecipe(OtherRecipe otherRecipe) {
		this.otherRecipe = otherRecipe;
		
		if (otherRecipe == null) {
			groupAmount.getField().setValue("");
			groupWording.getField().setValue("");
			groupReceived.getField().setValue("");
			groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(new Date()));
			return;
		}
		
		groupAmount.getField().setValue(otherRecipe.getAmount()+"");
		groupWording.getField().setValue(otherRecipe.getLabel());
		groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(otherRecipe.getCollectionDate()));
		
		for (int i = 0, count = comboFilterYearModel.getSize(); i < count; i++) {
			
			AcademicYear year = comboFilterYearModel.getElementAt(i);
			if(year.getId() == otherRecipe.getAccount().getAcademicYear().getId()) {
				comboAccountYearFilter.setSelectedIndex(i);
				break;
			}
		}
		
		for (int i = 0, count = comboAccountModel.getSize(); i < count; i++) {
			AnnualRecipe recipe = comboAccountModel.getElementAt(i);
			if(recipe.getId() == otherRecipe.getAccount().getId()) {
				comboAccount.setSelectedIndex(i);
				break;
			}
		}
		
		//location
		for (int i = 0, count = comboLocationModel.getSize(); i < count; i++) {
			PaymentLocation l = comboLocationModel.getElementAt(i);
			
			if (otherRecipe.getLocation().getId() == l.getId()) {
				comboLocation.setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * initialisation de l'inteface graphique
	 */
	private void init() {
		final Panel container = new Panel(new BorderLayout());
		final Box box = Box.createVerticalBox();
		
		//account
		final Panel boxAccount = new Panel(new GridLayout(1, 3, 5, 5));
		boxAccount.add(comboAccountYearFilter);
		boxAccount.add(comboAccount);
		boxAccount.add(comboLocation);
		boxAccount.setBorder(new EmptyBorder(5, 5, 0, 5));
		//==
		
		//amount and date
		final Panel boxAmount = new Panel(new GridLayout());
		boxAmount.add(groupAmount);
		boxAmount.add(groupDate);
		//==
		
		//wording and recipe in accout service data
		final Panel boxWording = new Panel (new GridLayout());
		boxWording.add(groupWording);
		boxWording.add(groupReceived);
		//
		
		box.setOpaque(false);
		box.add(boxAccount);
		box.add(boxWording);
		box.add(boxAmount);
		
		container.add(box, BorderLayout.CENTER);
		container.setBackground(FormUtil.BKG_END);
		container.setOpaque(false);
		
		getBody().add(container, BorderLayout.NORTH);
		
		//events
		comboAccountYearFilter.addItemListener(comboAccountYearFilterListener);
		fieldAmount.addCaretListener(amountListener);
		//==
	}

	@Override
	public void actionPerformed (ActionEvent event) {
		final float amount = Float.parseFloat(groupAmount.getField().getValue());
		final String label = groupWording.getField().getValue();
		final Date now = new Date();
		final String number = groupReceived.getField().getValue();
		
		OtherRecipe recipe = new OtherRecipe();
		String message = "";
		
		try {
			final Date collectionDate = FormUtil.DEFAULT_FROMATER.parse(groupDate.getField().getValue());
			if(otherRecipe != null)
				otherRecipe.setCollectionDate(collectionDate);
			else
				recipe.setCollectionDate(collectionDate);
		} catch (ParseException e) {
			message += "Entrez la date au format valide\n";
		}
		
		if (number.trim().length() != 0) {
			try {
				recipe.setReceivedNumber(Integer.parseInt(number));
			} catch (NumberFormatException e) {
				message += "Le numéro du réçu doit être une valeur numérique entière";
			}
		}
		
		if (message.length() != 0) {
			showMessageDialog("Erreur", message, JOptionPane.ERROR_MESSAGE);
			return;			
		}
		
		AnnualRecipe account = comboAccountModel.getElementAt(comboAccount.getSelectedIndex());
		PaymentLocation lo = comboLocationModel.getElementAt(comboLocation.getSelectedIndex());
		AcademicYear collectionYear = academicYearDao.findCurrent();
		
		recipe.setLabel(label);
		recipe.setAccount(account);
		recipe.setAmount(amount);
		recipe.setCollectionYear(collectionYear);
		recipe.setLocation(lo);
		
		try {
			if(otherRecipe == null){
				recipe.setRecordDate(now);
				otherRecipeDao.create(recipe);
			} else{
				recipe.setLastUpdate(now);
				recipe.setRecordDate(otherRecipe.getRecordDate());
				otherRecipeDao.update(recipe, otherRecipe.getId());
			}
			
			setOtherRecipe(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

}
