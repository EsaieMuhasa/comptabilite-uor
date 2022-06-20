/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.UniversityRecipe;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
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
	
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private final JMenuItem itemUpdate = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	{
		popup.add(itemDelete);
		popup.add(itemUpdate);
	}
	
	private final AnnualRecipeDao annualRecipeDao;
	private final UniversityRecipeDao universityRecipeDao;
	private final DAOAdapter<UniversityRecipe> recipeAdapter = new DAOAdapter<UniversityRecipe>() {

		@Override
		public synchronized void onCreate(UniversityRecipe e, int requestId) {
			dialogForm.setVisible(false);
			dialogForm.dispose();
		}

		@Override
		public synchronized void onUpdate(UniversityRecipe e, int requestId) {
			dialogForm.setVisible(false);
			dialogForm.dispose();
		}
		
	};
	
	private final MouseListener mouseListener = new MouseAdapter() {

		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
				UniversityRecipe recipe = tableModel.getRow(table.getSelectedRow());
				try {
					boolean delete = !annualRecipeDao.checkByRecipe(recipe);
					itemDelete.setEnabled(delete);
					popup.show(table, e.getX(), e.getY());
				} catch (DAOException ex) {
					JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
	};
	
	private final MainWindow mainWindow;

	public PanelUniversityRecipe(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		universityRecipeDao = mainWindow.factory.findDao(UniversityRecipeDao.class);
		annualRecipeDao = mainWindow.factory.findDao(AnnualRecipeDao.class);
		tableModel = new UniversityRecipeTableModel(universityRecipeDao);
		table = new Table(tableModel);
		table.setShowVerticalLines(true);
		table.addMouseListener(mouseListener);
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
			createRecipe();
		});
		
		itemDelete.addActionListener(event -> {
			UniversityRecipe recipe = tableModel.getRow(table.getSelectedRow());
			int rps = JOptionPane.showConfirmDialog(mainWindow, "Voulez-vous vraiment supprimer\n"+recipe.toString(), "Suppression d'une recette", JOptionPane.OK_CANCEL_OPTION);
			if(rps == JOptionPane.OK_OPTION) {
				try {
					universityRecipeDao.delete(recipe.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		itemUpdate.addActionListener(event -> {
			UniversityRecipe recipe = tableModel.getRow(table.getSelectedRow());
			updateRecipe(recipe);
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(tablePanel, BorderLayout.CENTER);

		add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * insersion du lable et de la description d'une nouvelle recette
	 */
	private void createRecipe() {
		createDialog();
		form.setRecipe(null);
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
	}
	
	/**
	 * mis en jour du label et de la description d'une recette
	 * @param recipe
	 */
	private void updateRecipe(UniversityRecipe recipe) {
		createDialog();
		form.setRecipe(recipe);
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
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
