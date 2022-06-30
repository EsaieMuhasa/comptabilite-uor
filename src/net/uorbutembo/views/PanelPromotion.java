/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.forms.FormPromotion;
import net.uorbutembo.views.models.PromotionTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelPromotion extends Panel {
	private static final long serialVersionUID = -5613039570162063491L;
	
	private Button btnNew = new Button(new ImageIcon(R.getIcon("new")), "Ajout des promotions");
	private PromotionDao promotionDao;
	private InscriptionDao inscriptionDao;
	private AcademicYearDao academicYearDao;
	private FormPromotion form;
	private JDialog dialogForm;
	
	private AcademicYear year;//l'annee acdemique acutuelement selectionner
	
	private Panel center = new Panel(new BorderLayout());
	private PromotionTableModel tableModel;
	private TablePanel tablePanel;
	
	private final JMenuItem itemDelete = new  JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private final JPopupMenu popupMenu = new JPopupMenu();

	private final MainWindow mainWindow;
	
	public PanelPromotion(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		tableModel = new PromotionTableModel(promotionDao);
		Table table = new Table(tableModel);
		
		btnNew.setEnabled(false);
		btnNew.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		btnNew.addActionListener(event -> {
			if(!academicYearDao.isCurrent(year))
				return;
			
			if(form == null) 
				createDialog();
			
			dialogForm.setLocationRelativeTo(mainWindow);
			dialogForm.setVisible(true);
		});
		
		tablePanel = new  TablePanel(table, "Liste des promotions", false);
		tablePanel.getHeader().add(btnNew, BorderLayout.EAST);
		tablePanel.getHeader().setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		center.add(tablePanel, BorderLayout.CENTER);
		
		add(center, BorderLayout.CENTER);
		
		//popup
		popupMenu.add(itemDelete);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1 && academicYearDao.isCurrent(year))
					popupMenu.show(table, e.getX(), e.getY());
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemDelete.addActionListener(event -> {
			Promotion pro = tableModel.getRow(table.getSelectedRow());
			try {						
				if(inscriptionDao.checkByPromotion(pro.getId())) {
					JOptionPane.showMessageDialog(null, "Impossible de supprimer la promotion '"+pro.toString()+"', \ncar certains étudiants y sont inscrits", "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				} else {
					int status = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cette promotion?\n=>"+pro.toString(), "Suppression de la promotion", JOptionPane.YES_NO_OPTION);
					if(status == JOptionPane.OK_OPTION) {
						promotionDao.delete(pro.getId());
					}
				}
			} catch (DAOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		});
		
	}
	
	/**
	 * Creation de la boite de dialogue de creation des promotions
	 */
	private void createDialog () {
		if (dialogForm != null)
			return;
		
		form = new FormPromotion(mainWindow);
		form.setCurrentYear(year);
		
		final Panel padding = new Panel(new BorderLayout());
		padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		padding.add(form, BorderLayout.CENTER);
		
		dialogForm = new JDialog(mainWindow, "Création des promotions", true);
		dialogForm.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogForm.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogForm.getContentPane().add(padding, BorderLayout.CENTER);
		dialogForm.pack();
		dialogForm.setMinimumSize(new Dimension(dialogForm.getWidth(), dialogForm.getHeight()));
	}
	
	public void setCurrentYear (AcademicYear year) {
		if(year == null || (this.year != null && year.getId() == this.year.getId())) 
			return;
		
		this.year = year;
		this.tableModel.setAcademicYear(year);
		
		if(form != null)
			this.form.setCurrentYear(year);
		
		btnNew.setEnabled(true);
		btnNew.setVisible(year != null && academicYearDao.isCurrent(year));
		
		for(int i = 0; i <= 2; i += 2) {
			TableColumn col = tablePanel.getTable().getColumnModel().getColumn(i);
			col.setMinWidth(130);
			col.setWidth(140);
			col.setMaxWidth(145);
			col.setResizable(false);
		}
	}

}
