/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JScrollPane;

import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.forms.FormUniversitySpend;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.UniversitySpendTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelUniversitySpend extends Panel{
	private static final long serialVersionUID = -6678192465363319784L;
	
	private final Button btnNew = new Button("Ajouter");
	private final FormUniversitySpend form;
	private final Table table;
	private final UniversitySpendTableModel tableModel;
	
	private final UniversitySpendDao universitySpendDao;

	/**
	 * 
	 */
	public PanelUniversitySpend(MainWindow mainWindow) {
		super(new BorderLayout());
		universitySpendDao =mainWindow.factory.findDao(UniversitySpendDao.class);
		tableModel = new UniversitySpendTableModel(universitySpendDao);
		table = new Table(tableModel);
		mainWindow.factory.findDao(AcademicYearDao.class).addYearListener( year -> {
			tableModel.reload();
		});
		
		final Panel container = new Panel(new BorderLayout());
		final Panel top = new Panel(new BorderLayout());
		final Box box = Box.createHorizontalBox();
		
		form = new FormUniversitySpend(mainWindow, universitySpendDao);
		form.setVisible(false);
		btnNew.addActionListener(event -> {
			if (btnNew.getText().equals("Ajouter")) {
				form.setVisible(true);
				btnNew.setText("Annuler");
			} else {
				btnNew.setText("Ajouter");
				form.setVisible(false);
			}
		});
		form.getBtnSave().addActionListener(event -> {
			form.setVisible(false);
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		
		box.add(Box.createHorizontalGlue());
		box.add(btnNew);
		top.add(box, BorderLayout.NORTH);
		top.add(form, BorderLayout.CENTER);
		top.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(table, BorderLayout.CENTER);
		
		this.add(top, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
	}

}
