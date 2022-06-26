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

import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
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
	
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private final JMenuItem itemUpdate = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	{
		popup.add(itemDelete);
		popup.add(itemUpdate);
	}
	private final OutlayDao outlayDao;
	private final OtherRecipeDao otherRecipeDao;
	private final PaymentFeeDao paymentFeeDao;
	private final PaymentLocationDao paymentLocationDao;
	private final DAOAdapter<PaymentLocation> locationAdapter = new DAOAdapter<PaymentLocation>() {

		@Override
		public synchronized void onCreate(PaymentLocation e, int requestId) {
			dialogForm.setVisible(false);
			dialogForm.dispose();
		}

		@Override
		public synchronized void onUpdate(PaymentLocation e, int requestId) {
			dialogForm.setVisible(false);
			dialogForm.dispose();
		}
		
	};
	
	private final MouseListener mouseListener = new MouseAdapter() {

		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
				PaymentLocation location = tableModel.getRow(table.getSelectedRow());
				try {
					boolean delete = !outlayDao.checkByLocation(location) || !otherRecipeDao.checkByLocation(location) || !paymentFeeDao.checkByLocation(location);
					itemDelete.setEnabled(delete);
					popup.show(table, e.getX(), e.getY());
				} catch (DAOException ex) {
					JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
	};
	
	private final MainWindow mainWindow;

	public PanelPaymentLocation (MainWindow mainWindow) {
		super(new BorderLayout());
		
		this.mainWindow = mainWindow;
		
		paymentLocationDao =mainWindow.factory.findDao(PaymentLocationDao.class);
		outlayDao = mainWindow.factory.findDao(OutlayDao.class);
		otherRecipeDao = mainWindow.factory.findDao(OtherRecipeDao.class);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		tableModel = new PaymentLocationTableModel(paymentLocationDao);
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
		
		paymentLocationDao.addListener(locationAdapter);
		
		final Panel container = new Panel(new BorderLayout());
		
		btnNew.addActionListener(event -> {
			createLocation();
		});
		
		itemDelete.addActionListener(event -> {
			PaymentLocation location = tableModel.getRow(table.getSelectedRow());
			
			int rps = JOptionPane.showConfirmDialog(mainWindow, "Voulez-vous vraiment supprimer\n"+location.toString(), "Suppression d'un lieux de payeemnt", JOptionPane.OK_CANCEL_OPTION);
			if(rps == JOptionPane.OK_OPTION) {
				try {
					paymentLocationDao.delete(location.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		itemUpdate.addActionListener(event -> {
			PaymentLocation location = tableModel.getRow(table.getSelectedRow());
			updateLocation(location);
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
		final TablePanel tablePanel = new TablePanel(table, "Liste des lieux de perceptions des recettes", false);

		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(tablePanel, BorderLayout.CENTER);
		
		add(scroll, BorderLayout.CENTER);
	}
	
	
	/**
	 * rechargement des donnees 
	 */
	public void reload() {
		tableModel.reload();
	}
	
	/**
	 * enregistrement d'un nouveau lieux de payement
	 */
	private void createLocation() {
		createDialog();
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
	}
	
	/**
	 * mis en jours d'un leux de payement
	 * @param location
	 */
	private void updateLocation(PaymentLocation location) {
		createDialog();
		form.setPaymentLocation(location);
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
	}
	
	/**
	 * creation dela boite de dialogue d'insersion/modification du label 
	 * d'un lieux de payement 
	 */
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
