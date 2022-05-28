/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormAnnualSpend;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.AnnualSpendTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAnnualSpend extends Panel {
	private static final long serialVersionUID = -1871314050736755404L;
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter");
	private Button btnList = new Button(new ImageIcon(R.getIcon("menu")), "voir la liste");
	
	private Panel center = new Panel(new BorderLayout());
	private FormAnnualSpend form;
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

	/**
	 * @param mainWindow
	 */
	public PanelAnnualSpend(MainWindow mainWindow) {
		super(new BorderLayout());
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		universitySpendDao = mainWindow.factory.findDao(UniversitySpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		annualSpendDao.addListener(new DAOAdapter<AnnualSpend>() {
			@Override
			public void onCreate(AnnualSpend e, int requestId) {
				if(!btnList.isVisible()) {
					btnList.doClick();
					reload();
				}
			}
			
			@Override
			public void onCreate(AnnualSpend[] e, int requestId) {
				btnList.doClick();
				reload();
			}
			
			@Override
			public void onDelete(AnnualSpend e, int requestId) { reload(); }
			
			@Override
			public void onDelete(AnnualSpend[] e, int requestId) { reload(); }
		});
		
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
			message += "pour le budget de l'année academique "+currentYear.getLabel()+" ?";
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
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		top.add(btnList);
		
		btnNew.setEnabled(false);
		btnNew.addActionListener(event -> {
			center.removeAll();
			
			if(form == null ) {
				form = new FormAnnualSpend(mainWindow);
				form.setCurrentYear(currentYear);
			}
			
			center.add(form, BorderLayout.NORTH);
			center.revalidate();
			center.repaint();
			btnNew.setVisible(false);
			btnList.setVisible(true);
		});
		
		btnList.setVisible(false);
		btnList.addActionListener(event -> {
			center.removeAll();
			center.add(scroll, BorderLayout.CENTER);
			center.revalidate();
			center.repaint();
			
			btnList.setVisible(false);
			btnNew.setVisible(true);
		});
		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		center.add(scroll, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
	}
	
	/**
	 * rechargement des donnees
	 */
	private void reload() {
		if(form != null)
			form.loadData();
		
		if(universitySpendDao.countAll() == annualSpendDao.countByAcademicYear(currentYear.getId())) {
			btnList.setVisible(false);
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
			tablePanel.setTitle("Dépenses prévue pour l'année "+currentYear.getLabel());
		else 
			tablePanel.setTitle("");
		
		btnList.doClick();
		btnNew.setVisible(currentYear != null && academicYearDao.isCurrent(currentYear));//0975612604
		
		reload();
	}


}
