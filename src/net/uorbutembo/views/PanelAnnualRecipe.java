/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversityRecipe;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversityRecipeDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormGroupAllocationRecipe;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAnnualRecipe extends Panel {
	private static final long serialVersionUID = -6487804127168576480L;
	
	protected final MainWindow mainWindow;
	private AnnualRecipeDao annualRecipeDao;
	private AnnualSpendDao annualSpendDao;
	private AcademicYearDao academicYearDao;
	
	private AcademicYear currentYear;//l'annee academique actuel
	
	private DefaultListModel<AnnualRecipe> modelRecipe = new DefaultListModel<>();
	private JList<AnnualRecipe> listRecipe = new JList<>(modelRecipe);
	private JButton btnAdd = new JButton("Nouvelle rubrique");
	private DialogChooseRecipe dialog;
	
	private final Panel panelRight = new  Panel(new BorderLayout());
	private final Panel panelCenter = new Panel(new BorderLayout());
	private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCenter, panelRight); 
	
	private FormGroupAllocationRecipe formRecipe;

	/**
	 * @param mainWindow
	 */
	public PanelAnnualRecipe (MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		annualRecipeDao = mainWindow.factory.findDao(AnnualRecipeDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		formRecipe = new FormGroupAllocationRecipe(mainWindow);
		init();
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public synchronized void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		List<AnnualSpend> spends = annualSpendDao.checkByAcademicYear(currentYear.getId())?
				annualSpendDao.findByAcademicYear(currentYear) : new ArrayList<>();
		List<AnnualRecipe> recipes = annualRecipeDao.checkByAcademicYear(currentYear)?
				annualRecipeDao.findByAcademicYear(currentYear) : new ArrayList<>();
		
		modelRecipe.clear();
		for (AnnualRecipe recipe : recipes) 
			modelRecipe.addElement(recipe);
		
		panelCenter.setVisible(!modelRecipe.isEmpty());
		if(!modelRecipe.isEmpty()) {
			formRecipe.reload(modelRecipe.get(0), spends);
			listRecipe.setSelectedIndex(0);
			split.setDividerLocation(getWidth() - 250);
		}
		
		btnAdd.setVisible(academicYearDao.isCurrent(currentYear));
		
		if(dialog != null)
			dialog.setCurrentYear(currentYear);
	}

	/**
	 * initialisation de l'interface graphique
	 */
	private void init () {
		final Panel panelBottom = new Panel();
		
		panelBottom.setBorder(new EmptyBorder(5, 0, 0, 0));
		panelBottom.add(btnAdd);
		//left
		listRecipe.setBackground(FormUtil.BKG_END);
		listRecipe.setForeground(FormUtil.BORDER_COLOR);
		panelRight.add(panelBottom, BorderLayout.SOUTH);
		panelRight.add(FormUtil.createScrollPane(listRecipe), BorderLayout.CENTER);
		panelRight.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		//==left
		
		panelCenter.add(FormUtil.createVerticalScrollPane(formRecipe), BorderLayout.CENTER);
		panelCenter.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		this.add(split, BorderLayout.CENTER);
		
		//event
		btnAdd.addActionListener(event -> {
			
			if(dialog == null) {
				dialog = new DialogChooseRecipe(mainWindow);
				dialog.setCurrentYear(currentYear);
			}
			
			if (dialog.hasChoosableRecipe())
				dialog.setVisible(true);
			else 
				JOptionPane.showMessageDialog(mainWindow, "Impossible d'effectuer cette opérations\n car "
						+ "tout les depense sont déjà répris!", "Avertissement", JOptionPane.ERROR_MESSAGE);
		});
		
		listRecipe.addListSelectionListener(event -> {
			int index = listRecipe.getSelectedIndex();
			if(index != -1){
				formRecipe.reload(modelRecipe.getElementAt(index));
				formRecipe.setVisible(true);
			} else 
				formRecipe.setVisible(false);
		});
	}

	/**
	 * @author Esaie MUHASA
	 * boite de dialogue d'assossiation des recettes a l'annee academique encours
	 */
	static class DialogChooseRecipe extends JDialog {
		private static final long serialVersionUID = 1767602689224449886L;
		
		private AcademicYear currentYear;
		private AnnualRecipeDao annualRecipeDao;
		private UniversityRecipeDao universityRecipeDao;
		
		private final DefaultListModel<UniversityRecipe> listModel = new DefaultListModel<>();
		private final JList<UniversityRecipe> listRecipes = new JList<>(listModel);
		private final JButton btnValidate = new JButton("Valider", new ImageIcon("success"));
		private final JButton btnCancel = new JButton("Annuler", new ImageIcon("close"));
		
		
		private final DAOAdapter<UniversityRecipe> daoAdapter = new DAOAdapter<UniversityRecipe>() {
			
			@Override
			public synchronized void onCreate(UniversityRecipe e, int requestId) {
				listModel.addElement(e);
			};
			
			@Override
			public synchronized void onDelete(UniversityRecipe e, int requestId) {
				for (int i = 0, count = listModel.getSize(); i < count; i++) {
					if(listModel.get(i).getId() == e.getId()) {
						listModel.remove(i);
						break;
					}
				}
			};
			
			@Override
			public synchronized void onUpdate(UniversityRecipe e, int requestId) {
				for (int i = 0, count = listModel.getSize(); i < count; i++) {
					if(listModel.get(i).getId() == e.getId()) {
						listModel.set(i, e);
						break;
					}
				}
			};
		};
		
		private final DAOAdapter<AnnualRecipe> daoAdapterRecipe = new DAOAdapter<AnnualRecipe>() {

			@Override
			public synchronized void onDelete(AnnualRecipe e, int requestId) {
				if(e.getAcademicYear().getId() == currentYear.getId())
					listModel.addElement(universityRecipeDao.findById(e.getUniversityRecipe().getId()));
			}
			
		};
		
		public DialogChooseRecipe(MainWindow mainWindow) {
			super(mainWindow, "Choisir les recetes", true);
			annualRecipeDao = mainWindow.factory.findDao(AnnualRecipeDao.class);
			universityRecipeDao = mainWindow.factory.findDao(UniversityRecipeDao.class);
			
			init();
			
			setSize(600, 350);
			setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
			setLocationRelativeTo(mainWindow);
			
			universityRecipeDao.addListener(daoAdapter);
			annualRecipeDao.addListener(daoAdapterRecipe);
		}
		
		/**
		 * y a-il des elements a choisir
		 * @return
		 */
		public boolean hasChoosableRecipe () {
			return !listModel.isEmpty();
		}
		
		/**
		 * @param currentYear the currentYear to set
		 */
		public synchronized void setCurrentYear(AcademicYear currentYear) {
			this.currentYear = currentYear;
			
			listModel.clear();
			if(universityRecipeDao.countAll() != 0) {
				List<UniversityRecipe> recipes = universityRecipeDao.findAll();
				
				if (annualRecipeDao.checkByAcademicYear(currentYear)) {
					List<UniversityRecipe> byYear = universityRecipeDao.findByAcademicYear(currentYear);
					
					for (UniversityRecipe recipe : byYear) {
						for (UniversityRecipe r : recipes)
							if(r.getId() == recipe.getId()) {
								recipes.remove(r);
								break;
							}
					}
				}
				
				for (UniversityRecipe recipe : recipes) 
					listModel.addElement(recipe);
			}
			
			btnValidate.setEnabled(!listModel.isEmpty() && listRecipes.getSelectedIndex() != -1);
		}

		private void init() {
			final Panel panelCenter = new Panel(new BorderLayout());
			final Panel panelBottom = new Panel();
			
			panelCenter.add(FormUtil.createScrollPane(listRecipes), BorderLayout.CENTER);
						
			panelBottom.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			panelBottom.add(btnValidate);
			panelBottom.add(btnCancel);
			
			getContentPane().setBackground(FormUtil.BKG_DARK);
			getContentPane().add(panelCenter, BorderLayout.CENTER);
			getContentPane().add(panelBottom, BorderLayout.SOUTH);
			
			listRecipes.setFixedCellHeight(30);
			listRecipes.setBackground(FormUtil.BKG_END);
			listRecipes.addListSelectionListener(event -> {
				btnValidate.setEnabled(listRecipes.getSelectedIndex() != -1);
			});
			
			btnValidate.setEnabled(false);
			btnValidate.addActionListener(event -> {
				btnValidate.setEnabled(false);
				btnCancel.setEnabled(false);
				Thread t = new Thread(() -> {
					setCursor(FormUtil.WAIT_CURSOR);
					listRecipes.setCursor(getCursor());
					
					int [] indexs = listRecipes.getSelectedIndices();
					AnnualRecipe [] recipes = new AnnualRecipe[indexs.length];
					Date now = new Date();
					
					for (int i = indexs.length-1; i >= 0 ; i--) {
						AnnualRecipe recipe = new AnnualRecipe();
						recipe.setAcademicYear(currentYear);
						recipe.setUniversityRecipe(listModel.getElementAt(indexs[i]));
						recipe.setRecordDate(now);
						recipes[i] = recipe;
					}
					
					try {
						annualRecipeDao.create(recipes);
						for (int i = indexs.length-1; i >= 0 ; i--)
							listModel.remove(indexs[i]);
					} catch (DAOException e) {
						JOptionPane.showMessageDialog(getParent(), e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					}
					
					setCursor(Cursor.getDefaultCursor());
					listRecipes.setCursor(Cursor.getDefaultCursor());
					setVisible(false);
					
					btnValidate.setEnabled(true);
					btnCancel.setEnabled(true);
				});
				
				t.start();
			});
			
			btnCancel.addActionListener(event->{
				setVisible(false);
			});
		}
		
		@Override
		public void setVisible(boolean b) {
			setLocationRelativeTo(getParent());
			super.setVisible(b);
		}
	}

}