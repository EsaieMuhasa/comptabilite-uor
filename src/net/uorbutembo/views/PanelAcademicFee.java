/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicFee;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormAcademicFee;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicFee extends Panel {
	private static final long serialVersionUID = 5400969854848116850L;
	
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter");
	private Button btnList = new Button(new ImageIcon(R.getIcon("menu")), "voir la liste");
	
	private Panel center = new Panel(new BorderLayout());
	private FormAcademicFee form;
	private Panel panelTable = new Panel();
	
	private AcademicFeeDao academicFeeDao;

	/**
	 * 
	 */
	public PanelAcademicFee(MainWindow mainWindow) {
		super(new BorderLayout());
		this.academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		
		this.academicFeeDao.addListener(new DAOAdapter<AcademicFee>() {
			@Override
			public void onCreate(AcademicFee e, int requestId) {
				if(!btnList.isVisible()) 
					btnList.doClick();
			}
		});
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(this.btnNew);
		top.add(this.btnList);
		
		this.btnNew.addActionListener(event -> {
			this.center.removeAll();
			
			if(this.form == null ) {
				this.form = new FormAcademicFee(this.academicFeeDao);
			}
			
			this.center.add(this.form, BorderLayout.CENTER);
			this.center.revalidate();
			this.center.repaint();
			this.btnNew.setVisible(false);
			this.btnList.setVisible(true);
		});
		
		this.btnList.addActionListener(event -> {
			this.center.removeAll();
			this.center.add(this.panelTable, BorderLayout.CENTER);
			this.center.revalidate();
			this.center.repaint();
			
			this.btnList.setVisible(false);
			this.btnNew.setVisible(true);
		});
		this.btnList.setVisible(false);
		
		this.add(top, BorderLayout.NORTH);
		this.add(this.center, BorderLayout.CENTER);
	}

}
