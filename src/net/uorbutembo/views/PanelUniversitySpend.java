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

import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.forms.FormUniversitySpend;
import net.uorbutembo.views.models.UniversitySpendTableModel;

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
	
	private final JPopupMenu popup = new JPopupMenu();
	private final JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private final JMenuItem itemUpdate = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	{
		popup.add(itemDelete);
		popup.add(itemUpdate);
	}
	
	private final AnnualSpendDao annualSpendDao;
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
	
	private final MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
				UniversitySpend spend = tableModel.getRow(table.getSelectedRow());
				try {
					boolean delete = !annualSpendDao.checkBySpend(spend);
					itemDelete.setEnabled(delete);
					popup.show(table, e.getX(), e.getY());
				} catch (DAOException ex) {
					JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};
	
	private final MainWindow mainWindow;
	
	public PanelUniversitySpend(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		universitySpendDao = mainWindow.factory.findDao(UniversitySpendDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		tableModel = new UniversitySpendTableModel(universitySpendDao);
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
		
		universitySpendDao.addListener(spendAdapter);
		
		final TablePanel tablePanel = new TablePanel(table, "Liste des dépenses", false);
		final Panel container = new Panel(new BorderLayout());
		
		btnNew.addActionListener(event -> {
			createSpend();
		});
		itemDelete.addActionListener(event -> {
			UniversitySpend spend = tableModel.getRow(table.getSelectedRow());
			int rps = JOptionPane.showConfirmDialog(mainWindow, "Voulez-vous vraiment supprimer\n"+spend.toString(), "Suppression d'une rubrique", JOptionPane.OK_CANCEL_OPTION);
			if(rps == JOptionPane.OK_OPTION) {
				try {
					universitySpendDao.delete(spend.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		itemUpdate.addActionListener(event -> {
			UniversitySpend spend = tableModel.getRow(table.getSelectedRow());
			updateSpend(spend);
		});
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(container);

		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		container.add(tablePanel, BorderLayout.CENTER);
		
		add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * relecture des donnees depuis le DAO
	 */
	public void reload() {
		tableModel.reload();
	}
	
	/**
	 * inersion de la description d'une depense
	 */
	private void createSpend() {
		createDialog();
		
		form.setSpend(null);
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
	}
	
	/**
	 * Mis en jours de la description d'une depense
	 * @param spend
	 */
	private void updateSpend(UniversitySpend spend) {
		createDialog();
		
		form.setSpend(spend);
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
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
