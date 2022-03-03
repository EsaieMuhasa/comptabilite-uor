/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormAnnualSpend;
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
	private Panel panelTable = new Panel();
	
	private AnnualSpendDao annualSpendDao;
	
	private AcademicYear currentYear;

	/**
	 * @param mainWindow
	 */
	public PanelAnnualSpend(MainWindow mainWindow) {
		super(new BorderLayout());
		this.annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		
		this.annualSpendDao.addListener(new DAOAdapter<AnnualSpend>() {
			@Override
			public void onCreate(AnnualSpend e, int requestId) {
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
				this.form = new FormAnnualSpend(this.annualSpendDao);
				form.setCurrentYear(currentYear);
			}
			
			this.center.add(this.form, BorderLayout.NORTH);
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

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		
		if(form!= null) 
			form.setCurrentYear(currentYear);
	}


}
