/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.UniversityRecipeDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
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

	/**
	 * 
	 */
	public PanelUniversityRecipe(MainWindow mainWindow) {
		super(new BorderLayout());
		universityRecipeDao =mainWindow.factory.findDao(UniversityRecipeDao.class);
		tableModel = new UniversityRecipeTableModel(universityRecipeDao);
		table = new Table(tableModel);
		mainWindow.factory.findDao(AcademicYearDao.class).addYearListener( year -> {
			tableModel.reload();
		});
		
		final Panel container = new Panel(new BorderLayout());
		final Panel top = new Panel(new BorderLayout());
		final Box box = Box.createHorizontalBox();
		
		form = new FormUniversityRecipe(mainWindow, universityRecipeDao);
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
			}
		});
		form.getBtnSave().addActionListener(event -> {
			form.setVisible(false);
			btnNew.doClick();
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		box.add(Box.createHorizontalGlue());
		box.add(btnNew);
		box.setBorder(new EmptyBorder(0, 0, 10, 0));
		top.add(box, BorderLayout.NORTH);
		top.add(form, BorderLayout.CENTER);
		top.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(table, BorderLayout.CENTER);
		
		this.add(top, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
	}

}
