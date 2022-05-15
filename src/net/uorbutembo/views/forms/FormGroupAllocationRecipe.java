/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.event.CaretListener;

import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationRecipeDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormGroupAllocationRecipe extends DefaultFormPanel {
	private static final long serialVersionUID = -5999287969817393540L;
	
	private final GridLayout layout = new GridLayout(1, 1);
	
	private final DefaultPieModel pieModel = new DefaultPieModel(100);
	private final PiePanel piePanel = new PiePanel(pieModel);
	private final List<AllocationRecipeField> fields = new ArrayList<>();
	
	private List<AnnualSpend> spends = new ArrayList<>();//depense annuel
	private AnnualRecipe currentRecipe;//recette encours de repartition
	
	private final Box boxFields = Box.createVerticalBox(),
				parentOfBox = Box.createVerticalBox();

	private AllocationRecipeDao allocationRecipeDao;
	private AcademicYearDao academicYearDao;
	
	/**
	 * @param mainWindow
	 */
	public FormGroupAllocationRecipe(MainWindow mainWindow) {
		super(mainWindow);
		allocationRecipeDao = mainWindow.factory.findDao(AllocationRecipeDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		final Panel container = new Panel(layout);
		

		parentOfBox.add(Box.createVerticalGlue());
		parentOfBox.add(boxFields);
		parentOfBox.add(Box.createVerticalGlue());
		
		boxFields.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		container.add(piePanel);
		container.add(parentOfBox);
		piePanel.setCaptionVisibility(false);
		this.getBody().add(container, BorderLayout.CENTER);
	}
	
	/**
	 * mofication de la recette a visualiser
	 * dans ce contexte, les rubrique de repartiton ne sont pas toucher
	 * @param recipe
	 */
	public void reload (AnnualRecipe recipe) {
		if (this.currentRecipe != null && this.currentRecipe.getId() == recipe.getId())
			return;
		
		this.currentRecipe = recipe;
		boolean state = academicYearDao.isCurrent(recipe.getAcademicYear().getId());
		
		getFooter().setVisible(state);
		parentOfBox.setVisible(state);
		
		layout.setColumns(state? 2: 1);
		piePanel.setHorizontalPlacement(!state);
		
		for (AllocationRecipeField field : fields) {
			field.reload(recipe);
		}
		
		setTitle(recipe.toString());
	}
	
	/**
	 * 
	 * @param recipe
	 * @param spends
	 */
	public synchronized void reload (AnnualRecipe recipe, List<AnnualSpend> spends) {
		this.spends = spends;
		
		for (AllocationRecipeField field : fields)
			field.dispose();
		
		pieModel.removeAll();
		boxFields.removeAll();
		fields.clear();
		createFields();
		reload (recipe);
	}
	
	/**
	 * utilitaire de creation de fields
	 * conformement au nombre des depenses
	 */
	private void createFields () {
		
		for (int i=0, count = spends.size(), max = COLORS.length-1, last = count-1; i < count; i++) {
			AnnualSpend spend = spends.get(i);
			Color color = COLORS[i % max];
			DefaultPiePart part = new DefaultPiePart(color, spend.getUniversitySpend().getTitle());
			AllocationRecipeField  field = new AllocationRecipeField(spend, part);
			boxFields.add(field);
			if(i != last)
				boxFields.add(Box.createVerticalStrut(5));
			fields.add(field);
			pieModel.addPart(part);
		}
		
		int maxH = spends.size() * 52 ;
		boxFields.setPreferredSize(new Dimension(150, maxH));
		boxFields.setMaximumSize(new Dimension(1000, maxH));
		
		boxFields.repaint();
	}

	@Override
	public void actionPerformed (ActionEvent event) {
		
		List<AllocationRecipe> 
			toCreateList = new ArrayList<>(), 
			toDeleteList = new ArrayList<>(),
			toUpdateList = new ArrayList<>();
		
		for (AllocationRecipeField field : fields) {
			if(field.getAllocation().getId() == 0 ) {
				if(field.getAllocation().getPercent() != 0.0)
					toCreateList.add(field.getAllocation());
			} else {
				if(field.getAllocation().getPercent() > 0.0){
					toUpdateList.add(field.getAllocation());
				} else{
					toDeleteList.add(field.getAllocation());
				}
			}
		}
		
		AllocationRecipe [] 
				toCreate = new AllocationRecipe[toCreateList.size()],
				toUpdate = new AllocationRecipe[toUpdateList.size()];
		
		long updatableIds [] = new long[toUpdate.length];
		long deletableIds [] = new long[toDeleteList.size()];
		Date now = new Date();
		
		for (int i = 0, count = toCreate.length; i < count; i++){
			toCreateList.get(i).setRecordDate(now);
			toCreate[i] = toCreateList.get(i);
		}
		
		for (int i = 0, count = toUpdate.length; i < count; i++){
			updatableIds[i] = toUpdateList.get(i).getId();
			toUpdateList.get(i).setLastUpdate(now);
			toUpdate[i] = toUpdateList.get(i);
		}
		
		for (int i = 0, count = toDeleteList.size(); i < count; i++)
			deletableIds[i] = toDeleteList.get(i).getId();
		
		try {
			if(toCreate.length != 0)
				allocationRecipeDao.create(toCreate);
			if(toUpdate.length != 0)
				allocationRecipeDao.update(toUpdate, updatableIds);
			if(deletableIds.length != 0)
				allocationRecipeDao.delete(deletableIds);
			
			for (int i = 0, count = toDeleteList.size(); i < count; i++)
				toDeleteList.get(i).setId(0);
			
			System.out.println("create: "+toCreate.length+" => update: "+toUpdate.length+" => delete: "+deletableIds.length);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	class AllocationRecipeField extends Panel{
		private static final long serialVersionUID = -906889312723810849L;
		
		private AnnualRecipe recipe;
		private AnnualSpend spend;
		private PiePart part;
		
		private AllocationRecipe allocation;
		
		private TextField<String> field = new TextField<>("");
		private LineBorder border;
		
		private final FocusListener focusListener = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				pieModel.setSelectedIndex(-1);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				pieModel.setSelectedIndex(pieModel.indexOf(part));
			}
		};
		
		private final CaretListener caretListener = (event) -> {
			float value = 0;
			try {
				BigDecimal big = new BigDecimal(Float.parseFloat(field.getValue())).setScale(2, RoundingMode.HALF_DOWN);
				value = big.floatValue();
			} catch (NumberFormatException e) {}
			part.setValue(value);
			allocation.setPercent(value);
		};
		
		/**
		 * @param spend
		 * @param part reference vers une portion du graphique Pie
		 */
		public AllocationRecipeField(AnnualSpend spend, PiePart part) {
			super(new BorderLayout());
			this.part = part;
			border = new LineBorder(part.getBackgroundColor());
			setSpend(spend);
			init();
		}
		
		/**
		 * mis en jour des donnees qui serons impacter par les modifications
		 * des valeurs du champs
		 * @param recipe
		 * @param spend
		 */
		public synchronized void reload (AnnualRecipe recipe, AnnualSpend spend) {
			this.recipe = recipe;
			if(spend != null)
				this.setSpend(spend);

			if (allocationRecipeDao.check(recipe.getId(), this.spend.getId())) 
				allocation = allocationRecipeDao.find(recipe, this.spend);
			else {
				if (allocation == null)
					allocation = new AllocationRecipe();
				
				allocation.setId(0);
				allocation.setPercent(0);
			}
			
			allocation.setRecipe(recipe);
			part.setValue(allocation.getPercent());
			field.setText(allocation.getPercent()+"");
		}
		
		/**
		 * mis en jour de la configuration de la repartiton de la recette
		 * @param recipe
		 */
		public synchronized void reload (AnnualRecipe recipe ) {
			reload(recipe, null);
		}
		
		/**
		 * -liberation des resources
		 * -deconnexion des listeners
		 */
		public void dispose () {
			field.removeFocusListener(focusListener);
			field.removeCaretListener(caretListener);
		}
		
		private void init() {
			this.add(field, BorderLayout.CENTER);
			this.setBorder(border);
			
			//event
			field.addFocusListener(focusListener);
			field.addCaretListener(caretListener);
		}
		
		/**
		 * @return the recipe
		 */
		public AnnualRecipe getRecipe() {
			return recipe;
		}

		/**
		 * @return the spend
		 */
		public AnnualSpend getSpend() {
			return spend;
		}

		/**
		 * @param spend the spend to set
		 */
		public void setSpend (AnnualSpend spend) {
			this.spend = spend;
			field.setLabel(spend.getUniversitySpend().getTitle());
			if(allocation == null)
				allocation = new AllocationRecipe();
			allocation.setSpend(spend);
		}

		/**
		 * @return the part
		 */
		public PiePart getPart() {
			return part;
		}

		/**
		 * @return the allocation
		 */
		public AllocationRecipe getAllocation() {
			return allocation;
		}
		
	}

}
