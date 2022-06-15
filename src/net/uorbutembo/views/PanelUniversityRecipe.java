/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.UniversityRecipe;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.UniversityRecipeDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.forms.FormUniversityRecipe;
import net.uorbutembo.views.models.UniversityRecipeTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelUniversityRecipe extends Panel{
	private static final long serialVersionUID = -6678192465363319784L;
	
	private final Button btnNew = new Button(new ImageIcon(R.getIcon("new")), "Ajouter");
	{btnNew.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);}
	private JDialog dialogForm;
	private FormUniversityRecipe form;
	private final Table table;
	private final UniversityRecipeTableModel tableModel;
	
	private final UniversityRecipeDao universityRecipeDao;
	private final DAOAdapter<UniversityRecipe> recipeAdapter = new DAOAdapter<UniversityRecipe>() {

		@Override
		public synchronized void onCreate(UniversityRecipe e, int requestId) {
			btnNew.doClick();
		}

		@Override
		public synchronized void onUpdate(UniversityRecipe e, int requestId) {
			btnNew.doClick();
		}
		
	};
	
	private final MainWindow mainWindow;

	/**
	 * 
	 */
	public PanelUniversityRecipe(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		universityRecipeDao = mainWindow.factory.findDao(UniversityRecipeDao.class);
		tableModel = new UniversityRecipeTableModel(universityRecipeDao);
		table = new Table(tableModel);
		table.setShowVerticalLines(true);
		final int w = 140;
		for (int i = 1; i <= 2; i++) {			
			table.getColumnModel().getColumn(i).setWidth(w);
			table.getColumnModel().getColumn(i).setMinWidth(w);
			table.getColumnModel().getColumn(i).setMaxWidth(w);
			table.getColumnModel().getColumn(i).setResizable(false);
		}
		
		universityRecipeDao.addListener(recipeAdapter);
		final TablePanel tablePanel = new TablePanel(table, "Liste des autres recettes", false);
		
		final Panel container = new Panel(new BorderLayout());
		
		btnNew.addActionListener(event -> {
			createDialog();
			
			dialogForm.setLocationRelativeTo(mainWindow);
			dialogForm.setVisible(true);
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(tablePanel, BorderLayout.CENTER);

		add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * utilitaire de creation de la boite de dialogue d'enregistrement/modification
	 * des recettes
	 */
	private void createDialog() {
		if (dialogForm != null)
			return;
		
		final Panel padding = new Panel(new BorderLayout());
		form = new FormUniversityRecipe(mainWindow);
		dialogForm = new JDialog(mainWindow, "Recettes de l'université", true);
		dialogForm.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogForm.getContentPane().add(padding, BorderLayout.CENTER);
		dialogForm.getContentPane().setBackground(FormUtil.BKG_DARK);
		
		padding.add(form, BorderLayout.CENTER);
		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		dialogForm.pack();
	}

}
