/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.FeePromotion;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormFeePromotion;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFeePromotion extends Panel {
	private static final long serialVersionUID = 5400969854848116850L;
	
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajout une configuration");
	private Button btnList = new Button(new ImageIcon(R.getIcon("menu")), "Voir les configurations");
	
	private FeePromotionDao feePromotionDao;
	
	private Panel center = new Panel(new BorderLayout());
	private FormFeePromotion form;
	private Panel tablePanel = new Panel();
	
	private AcademicYear currentYear;

	/**
	 * 
	 */
	public PanelFeePromotion(MainWindow mainWindow) {
		super(new BorderLayout());
		this.feePromotionDao = mainWindow.factory.findDao(FeePromotionDao.class);
		
		this.feePromotionDao.addListener(new DAOAdapter<FeePromotion>() {
			
		});
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		top.add(btnList);
		
		this.btnNew.addActionListener(event -> {
			this.center.removeAll();
			if(this.form == null) {
				this.form = new FormFeePromotion(this.feePromotionDao);
				form.setCurrentYear(currentYear);
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
		
		this.center.add(tablePanel, BorderLayout.CENTER);
		
		this.add(top, BorderLayout.NORTH);
		this.add(this.center, BorderLayout.CENTER);
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		
		if(form != null)
			this.form.setCurrentYear(currentYear);
	}

}
