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
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.forms.FormPromotion;
import net.uorbutembo.views.models.PromotionTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelPromotion extends Panel {
	private static final long serialVersionUID = -5613039570162063491L;
	
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajout des promotions");
	private Button btnList = new Button(new ImageIcon(R.getIcon("menu")), "Voir les promotions");
	private PromotionDao promotionDao;
	private InscriptionDao inscriptionDao;
	private AcademicYearDao academicYearDao;
	private FormPromotion form;
	
	private AcademicYear year;//l'annee acdemique acutuelement selectionner
	
	private Panel center = new Panel(new BorderLayout());
	private PromotionTableModel tableModel;
	
	private final JMenuItem itemDelete = new  JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private final JPopupMenu popupMenu = new JPopupMenu();


	/**
	 * 
	 */
	public PanelPromotion(MainWindow mainWindow) {
		super(new BorderLayout());
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		tableModel = new PromotionTableModel(promotionDao);
		Table table = new Table(tableModel);
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		top.add(btnList);
		
		btnNew.setEnabled(false);
		btnNew.addActionListener(event -> {
			center.removeAll();
			if(form == null) {
				form = new FormPromotion(mainWindow);
				form.setCurrentYear(year);
			}
			center.add(form, BorderLayout.CENTER);
			center.revalidate();
			center.repaint();
			btnList.setVisible(true);
			btnNew.setVisible(false);
		});
		
		btnList.addActionListener(event -> {
			center.removeAll();
			center.add(table.getTableHeader(), BorderLayout.NORTH);
			center.add(table, BorderLayout.CENTER);
			center.revalidate();
			center.repaint();
			
			btnList.setVisible(false);
			btnNew.setVisible(true);
		});
		btnList.setVisible(false);
		
		center.add(table.getTableHeader(), BorderLayout.NORTH);
		center.add(table, BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		this.add(top, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);
		
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
	
	public void setCurrentYear (AcademicYear year) {
		if(year == null || (this.year != null && year.getId() == this.year.getId())) 
			return;
		
		this.year = year;
		this.tableModel.setAcademicYear(year);
		
		if(form != null)
			this.form.setCurrentYear(year);
		
		btnList.doClick();
		btnNew.setEnabled(true);
		btnNew.setVisible(year != null && academicYearDao.isCurrent(year));
	}

}
