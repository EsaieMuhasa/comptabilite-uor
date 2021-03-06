/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicYearDao;
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
import net.uorbutembo.views.forms.FormAnnualSpend;
import net.uorbutembo.views.models.AnnualSpendTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAnnualSpend extends Panel {
	private static final long serialVersionUID = -1871314050736755404L;
	private Button btnNew = new Button(new ImageIcon(R.getIcon("new")), "Ajouter");
	{btnNew.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);}
	
	
	private JDialog dialogForm;
	private FormAnnualSpend form;
	private Panel center = new Panel(new BorderLayout());
	private TablePanel tablePanel;
	private JScrollPane scroll;
	private Table table;
	private AnnualSpendTableModel tableModel;
	
	private AnnualSpendDao annualSpendDao;
	private UniversitySpendDao universitySpendDao;
	private AcademicYearDao academicYearDao;
	private AcademicYear currentYear;
	
	private JPopupMenu popupMenu = new JPopupMenu();
	private JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	
	private DAOAdapter<UniversitySpend> univSpendAdapter = new DAOAdapter<UniversitySpend>() {

		@Override
		public synchronized void onCreate(UniversitySpend e, int requestId) {
			
		}

		@Override
		public synchronized void onDelete(UniversitySpend e, int requestId) {
			if (tableModel.getRowCount() == universitySpendDao.countAll()) {
				
			}
		}
		
	};
	
	private final DAOAdapter<AnnualSpend> annualSpendAdapter = new DAOAdapter<AnnualSpend>() {
		@Override
		public void onCreate(AnnualSpend e, int requestId) {
			hiddeDialog();
			reload();
		}
		
		@Override
		public void onCreate(AnnualSpend[] e, int requestId) {
			hiddeDialog();
			reload();
		}
		
		/**
		 * on cache la boite de dilogue et on libere les ressources qu'elle occupe
		 */
		private void hiddeDialog () {
			if(dialogForm  != null){
				dialogForm.setVisible(false);
				dialogForm.dispose();
			}
		}
		
		@Override
		public void onDelete(AnnualSpend e, int requestId) { reload(); }
		
	};
	
	private final MainWindow mainWindow;
	
	/**
	 * @param mainWindow
	 */
	public PanelAnnualSpend (MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		universitySpendDao = mainWindow.factory.findDao(UniversitySpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		annualSpendDao.addListener(annualSpendAdapter);
		universitySpendDao.addListener(univSpendAdapter);
		
		final Panel padding = new Panel(new BorderLayout());
		tableModel = new AnnualSpendTableModel(annualSpendDao);
		table = new Table(tableModel);
		tablePanel = new TablePanel(table, "", false);
		padding.add(tablePanel, BorderLayout.CENTER);
		scroll = FormUtil.createVerticalScrollPane(padding);
		table.setShowVerticalLines(true);
		final int w = 140;
		for (int i = 1; i <= 4; i++) {			
			table.getColumnModel().getColumn(i).setWidth(w);
			table.getColumnModel().getColumn(i).setMinWidth(w);
			table.getColumnModel().getColumn(i).setMaxWidth(w);
			table.getColumnModel().getColumn(i).setResizable(false);
		}
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
					popupMenu.show(table, e.getX(), e.getY());
				}
			}
		});
		
		//popup
		popupMenu.add(itemDelete);
		itemDelete.addActionListener(event -> {
			AnnualSpend spend = tableModel.getRow(table.getSelectedRow());
			String message = "Voulez-vous vraiment supprimer la rubrique \n\""+spend.getUniversitySpend().getTitle()+"\"\n";
			message += "pour le budget de l'ann??e acad??mique "+currentYear.getLabel()+" ?";
			message += "\nN.B: Cette suppression est definitive!";
			int status = JOptionPane.showConfirmDialog(mainWindow, message, "Suppression", JOptionPane.YES_NO_OPTION);
			if(status == JOptionPane.OK_OPTION) {
				try {
					annualSpendDao.delete(spend.getId());
				} catch (DAOException e) {
					JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		//==
		
		btnNew.setEnabled(false);
		btnNew.addActionListener(event -> {
			createDialog();
			
			dialogForm.setLocationRelativeTo(mainWindow);
			dialogForm.setVisible(true);
		});
		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);

		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		center.add(scroll, BorderLayout.CENTER);
		add(center, BorderLayout.CENTER);
	}
	
	/**
	 * unilitaire de creation du boite de dialogue permetant de selectionner certatins depanse qu niveau globel
	 * pour en prendre en compte pour l'annee courtante
	 */
	private void createDialog() {
		if (dialogForm != null)
			return;
		
		final Panel padding = new Panel(new BorderLayout());
		form = new FormAnnualSpend(mainWindow);
		form.setCurrentYear(currentYear);
		dialogForm = new JDialog(mainWindow, "R??cup??ration d??penses annuelles", true);
		dialogForm.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogForm.getContentPane().add(FormUtil.createVerticalScrollPane(padding), BorderLayout.CENTER);
		dialogForm.getContentPane().setBackground(FormUtil.BKG_DARK);
		
		padding.add(form, BorderLayout.CENTER);
		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		dialogForm.pack();
		dialogForm.setSize(dialogForm.getWidth() + 50, dialogForm.getHeight());
	}
	
	/**
	 * rechargement des donnees
	 */
	private void reload () {
		if(form != null)
			form.loadData();
		
		if(universitySpendDao.countAll() == annualSpendDao.countByAcademicYear(currentYear.getId())) {
			btnNew.setVisible(false);
		}
		
		btnNew.setEnabled(true);
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		tableModel.setCurrentYear(currentYear);
		
		if(form!= null) 
			form.setCurrentYear(currentYear);
		
		if(currentYear != null)
			tablePanel.setTitle("D??penses pr??vue pour l'ann??e "+currentYear.getLabel());
		else 
			tablePanel.setTitle("");
		
		btnNew.setVisible(currentYear != null && academicYearDao.isCurrent(currentYear));
		
		reload();
	}

}
