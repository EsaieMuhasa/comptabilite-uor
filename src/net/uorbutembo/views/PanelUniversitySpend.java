/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormUniversitySpend;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.UniversitySpendTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelUniversitySpend extends Panel{
	private static final long serialVersionUID = -6678192465363319784L;
	
	private static final ImageIcon ICO_PLUS  = new ImageIcon(R.getIcon("new"));
	
	private final Button btnNew = new Button(ICO_PLUS, "Ajouter");
	{btnNew.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);}
	
	private JDialog dialogForm;
	private FormUniversitySpend form;
	private final Table table;
	private final UniversitySpendTableModel tableModel;
	
	private final UniversitySpendDao universitySpendDao;
	private final DAOAdapter<UniversitySpend> spendAdapter = new DAOAdapter<UniversitySpend>() {

		@Override
		public synchronized void onCreate(UniversitySpend e, int requestId) {
			dialogForm.setVisible(false);
			dialogForm.dispose();
		}

		@Override
		public synchronized void onUpdate(UniversitySpend e, int requestId) {
			dialogForm.setVisible(false);
			dialogForm.dispose();
		}
		
	};
	
	private final MainWindow mainWindow;
	
	public PanelUniversitySpend(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		universitySpendDao = mainWindow.factory.findDao(UniversitySpendDao.class);
		tableModel = new UniversitySpendTableModel(universitySpendDao);
		table = new Table(tableModel);
		table.setShowVerticalLines(true);
		final int w = 140;
		for (int i = 1; i <= 2; i++) {			
			table.getColumnModel().getColumn(i).setWidth(w);
			table.getColumnModel().getColumn(i).setMinWidth(w);
			table.getColumnModel().getColumn(i).setMaxWidth(w);
			table.getColumnModel().getColumn(i).setResizable(false);
		}
		
		universitySpendDao.addListener(spendAdapter);
		
		final TablePanel tablePanel = new TablePanel(table, "Liste des dépenses", false);
		final Panel container = new Panel(new BorderLayout());
		
		btnNew.addActionListener(event -> {
			if (dialogForm == null)
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
	 * Utilitaire de creation de la boite de dialogue qui permet d'enregistrer
	 * et de modifier un depense
	 */
	private void createDialog() {
		if (dialogForm != null)
			return;
		
		final Panel padding = new Panel(new BorderLayout());
		form = new FormUniversitySpend(mainWindow);
		dialogForm = new JDialog(mainWindow, "Ajout d'une nouvelle dépense", true);
		dialogForm.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogForm.getContentPane().add(padding, BorderLayout.CENTER);
		dialogForm.getContentPane().setBackground(FormUtil.BKG_DARK);
		
		padding.add(form, BorderLayout.CENTER);
		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		dialogForm.pack();
	}

}
