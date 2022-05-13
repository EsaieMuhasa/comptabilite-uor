/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
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

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOListener;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormOtherRecipe extends DefaultFormPanel {
	private static final long serialVersionUID = -3723343048984497445L;
	
	private final DefaultComboBoxModel<AcademicYear> comboFilterYearModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<AnnualRecipe> comboAccountModel = new DefaultComboBoxModel<>();
	private final Map<AcademicYear, List<AnnualRecipe>> classementRecipes = new HashMap<>();
	
	private final ComboBox<AnnualRecipe> comboAccount = new ComboBox<>("Compte à crediter", comboAccountModel);
	private final ComboBox<AcademicYear> comboAccountYearFilter = new ComboBox<>("Année académique du compte", comboFilterYearModel);
	
	private final FormGroup<String> groupAmount = FormGroup.createTextField("Montant en "+FormUtil.UNIT_MONEY);
	private final FormGroup<String> groupWording = FormGroup.createTextField("Libele de perception");
	private final FormGroup<String> groupDate = FormGroup.createTextField("Date de perception");
	
	private final AnnualRecipeDao annualRecipeDao;
	private final AcademicYearDao academicYearDao;
	private final OtherRecipeDao otherRecipeDao;
	
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

	/**
	 * @param mainWindow
	 */
	public FormOtherRecipe(MainWindow mainWindow) {
		super(mainWindow);
		annualRecipeDao = mainWindow.factory.findDao(AnnualRecipeDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		otherRecipeDao = mainWindow.factory.findDao(OtherRecipeDao.class);
		
		init();
		load();
		
		otherRecipeDao.addListener(daoListener);
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
	}
	
	/**
	 * @param otherRecipe the otherRecipe to set
	 */
	public void setOtherRecipe(OtherRecipe otherRecipe) {
		this.otherRecipe = otherRecipe;
		
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
	}

	/**
	 * initialisation de l'inteface graphique
	 */
	private void init() {
		final Panel container = new Panel(new BorderLayout());
		final Box box = Box.createVerticalBox();
		
		//account
		final Panel boxAccount = new Panel(new GridLayout(1, 1));
		boxAccount.add(comboAccountYearFilter);
		boxAccount.add(comboAccount);
		boxAccount.setBorder(new EmptyBorder(5, 5, 0, 5));
		//==
		
		//amount and date
		final Panel boxAmount = new Panel(new GridLayout());
		boxAmount.add(groupAmount);
		boxAmount.add(groupDate);
		//==
		
		box.setOpaque(false);
		box.add(boxAccount);
		box.add(groupWording);
		box.add(boxAmount);
		
		container.add(box, BorderLayout.CENTER);
		container.setBackground(FormUtil.BKG_END);
		container.setOpaque(false);
		
		getBody().add(container, BorderLayout.NORTH);
		
		//events
		comboAccountYearFilter.addItemListener(event -> {
			List<AnnualRecipe> spends = classementRecipes.get(comboFilterYearModel.getElementAt(comboAccountYearFilter.getSelectedIndex()));
			comboAccount.setEnabled(spends!= null && !spends.isEmpty());
			btnSave.setEnabled(spends!= null && !spends.isEmpty());
			comboAccountModel.removeAllElements();
			if(spends == null)
				return;
			
			for (AnnualRecipe spend : spends) {
				comboAccountModel.addElement(spend);
			}
		});
		//==
	}

	@Override
	public void actionPerformed (ActionEvent event) {
		final float amount = Float.parseFloat(groupAmount.getField().getValue());
		final String label = groupWording.getField().getValue();
		final Date now = new Date();
		
		OtherRecipe recipe = new OtherRecipe();
		
		try {
			final Date collectionDate = FormUtil.DEFAULT_FROMATER.parse(groupDate.getField().getValue());
			if(otherRecipe != null)
				otherRecipe.setCollectionDate(collectionDate);
			else
				recipe.setCollectionDate(collectionDate);
		} catch (ParseException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		AnnualRecipe account = comboAccountModel.getElementAt(comboAccount.getSelectedIndex());
		AcademicYear collectionYear = academicYearDao.findCurrent();
		
		if (otherRecipe == null) {			
			recipe.setRecordDate(now);
			recipe.setLabel(label);
			recipe.setAccount(account);
			recipe.setAmount(amount);
			recipe.setCollectionYear(collectionYear);
		} else {
			otherRecipe.setLastUpdate(now);
			otherRecipe.setLabel(label);
			otherRecipe.setAmount(amount);
			otherRecipe.setAccount(account);
			otherRecipe.setCollectionYear(collectionYear);
		}
		
		try {
			if(otherRecipe == null)
				otherRecipeDao.create(recipe);
			else
				otherRecipeDao.update(otherRecipe, otherRecipe.getId());
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}

	}

	

}
