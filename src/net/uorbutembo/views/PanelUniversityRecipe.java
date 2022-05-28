/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.UniversityRecipe;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.UniversityRecipeDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormUniversityRecipe;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.UniversityRecipeTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelUniversityRecipe extends Panel{
	private static final long serialVersionUID = -6678192465363319784L;
	
	private static final ImageIcon ICO_PLUS  = new ImageIcon(R.getIcon("plus"));
	private static final ImageIcon ICO_CLOSE  = new ImageIcon(R.getIcon("close"));
	
	private final Button btnNew = new Button(ICO_PLUS, "Ajouter");
	private final FormUniversityRecipe form;
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

	/**
	 * 
	 */
	public PanelUniversityRecipe(MainWindow mainWindow) {
		super(new BorderLayout());
		universityRecipeDao =mainWindow.factory.findDao(UniversityRecipeDao.class);
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
		
		final Panel container = new Panel(new BorderLayout());
		final Panel top = new Panel(new BorderLayout());
		final Box box = Box.createHorizontalBox();
		
		form = new FormUniversityRecipe(mainWindow);
		form.setVisible(false);
		btnNew.addActionListener(event -> {
			if (btnNew.getText().equals("Ajouter")) {
				form.setVisible(true);
				btnNew.setText("Annuler");
				btnNew.setIcon(ICO_CLOSE);
			} else {
				btnNew.setText("Ajouter");
				btnNew.setIcon(ICO_PLUS);
				form.setVisible(false);
				form.setRecipe(null);
			}
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		box.add(Box.createHorizontalGlue());
		box.add(btnNew);
		box.setBorder(new EmptyBorder(0, 0, 10, 0));
		top.add(box, BorderLayout.NORTH);
		top.add(form, BorderLayout.CENTER);
		top.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(new TablePanel(table, "Liste des autres recettes", false), BorderLayout.CENTER);
		
		this.add(top, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
	}

}
