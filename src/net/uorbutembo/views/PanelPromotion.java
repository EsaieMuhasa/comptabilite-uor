/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
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
	private FormPromotion form;
	private TablePanel tablePanel;
	
	private AcademicYear year;//l'annee acdemique acutuelement selectionner
	
	private Panel center = new Panel(new BorderLayout());
	private PromotionTableModel tableModel;


	/**
	 * 
	 */
	public PanelPromotion(MainWindow mainWindow) {
		super(new BorderLayout());
		this.promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		
		tableModel = new PromotionTableModel(this.promotionDao);
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		top.add(btnList);
		
		this.btnNew.addActionListener(event -> {
			this.center.removeAll();
			if(this.form == null) {
				this.form = new FormPromotion(this.promotionDao);
				form.setCurrentYear(year);
			}
			this.center.add(this.form, BorderLayout.CENTER);
			this.center.revalidate();
			this.center.repaint();
			this.btnList.setVisible(true);
			this.btnNew.setVisible(false);
		});
		
		this.btnList.addActionListener(event -> {
			this.center.removeAll();
			this.center.add(this.tablePanel, BorderLayout.CENTER);
			this.center.revalidate();
			this.center.repaint();
			
			this.btnList.setVisible(false);
			this.btnNew.setVisible(true);
		});
		this.btnList.setVisible(false);
		
		Table table = new Table(tableModel);
		center.add(table.getTableHeader(), BorderLayout.NORTH);
		center.add(table, BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		this.add(top, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);
	}
	
	public void setCurrentYear (AcademicYear year) {
		if(this.year != null && year.getId() == this.year.getId()) 
			return;
		
		this.year = year;
		this.tableModel.setAcademicYear(year);
		
		if(form != null)
			this.form.setCurrentYear(year);
	}

}
