package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.forms.FormPaymentLocation;
import net.uorbutembo.views.models.PaymentLocationTableModel;

/**
 * panel de configuration des lieux de payement
 * @author Esaie MUHASA
 *
 */
public class PanelPaymentLocation extends Panel {
	private static final long serialVersionUID = 5031377974753784475L;
	
	private final Button btnNew = new Button(new ImageIcon(R.getIcon("new")), "Ajouter");
	{btnNew.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);}
	
	private JDialog dialogForm;
	private FormPaymentLocation form;
	private final Table table;
	private final PaymentLocationTableModel tableModel;
	
	private final PaymentLocationDao paymentLocationDao;
	private final DAOAdapter<PaymentLocation> locationAdapter = new DAOAdapter<PaymentLocation>() {

		@Override
		public synchronized void onCreate(PaymentLocation e, int requestId) {
			btnNew.doClick();
		}

		@Override
		public synchronized void onUpdate(PaymentLocation e, int requestId) {
			btnNew.doClick();
		}
		
	};
	
	private final MainWindow mainWindow;

	/**
	 * 
	 */
	public PanelPaymentLocation (MainWindow mainWindow) {
		super(new BorderLayout());
		
		this.mainWindow = mainWindow;
		
		paymentLocationDao =mainWindow.factory.findDao(PaymentLocationDao.class);
		tableModel = new PaymentLocationTableModel(paymentLocationDao);
		table = new Table(tableModel);
		table.setShowVerticalLines(true);
		final int w = 140;
		for (int i = 1; i <= 2; i++) {			
			table.getColumnModel().getColumn(i).setWidth(w);
			table.getColumnModel().getColumn(i).setMinWidth(w);
			table.getColumnModel().getColumn(i).setMaxWidth(w);
			table.getColumnModel().getColumn(i).setResizable(false);
		}
		
		paymentLocationDao.addListener(locationAdapter);
		
		final Panel container = new Panel(new BorderLayout());
		
		btnNew.addActionListener(event -> {
			createDialog();
			
			dialogForm.setLocationRelativeTo(mainWindow);
			dialogForm.setVisible(true);
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		final TablePanel tablePanel = new TablePanel(table, "Liste des lieux de perceptions des recettes", false);

		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(tablePanel, BorderLayout.CENTER);
		
		add(scroll, BorderLayout.CENTER);
	}
	
	private void createDialog() {
		if (dialogForm != null)
			return;
		
		final Panel padding = new Panel(new BorderLayout());
		form = new FormPaymentLocation(mainWindow);
		
		dialogForm = new JDialog(mainWindow, "Lieux de payement", true);
		dialogForm.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogForm.getContentPane().add(padding, BorderLayout.CENTER);
		dialogForm.getContentPane().setBackground(FormUtil.BKG_DARK);
		
		padding.add(form, BorderLayout.CENTER);
		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		dialogForm.pack();
	}

}
