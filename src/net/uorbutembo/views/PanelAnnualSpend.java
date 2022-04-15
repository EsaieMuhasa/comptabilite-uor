/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.forms.FormAnnualSpend;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.UniversitySpendTableModel;
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
	private Panel panelTable = new Panel(new BorderLayout());
	private Table table;
	private UniversitySpendTableModel tableModel;
	
	private AnnualSpendDao annualSpendDao;
	private UniversitySpendDao universitySpendDao;
	private AcademicYearDao academicYearDao;
	private AcademicYear currentYear;

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
		
		universitySpendDao.addListener(new DAOAdapter<UniversitySpend>() {
			@Override
			public void onCreate(UniversitySpend e, int requestId) { reload(); }
			
			@Override
			public void onCreate(UniversitySpend[] e, int requestId) { reload(); }
			
			@Override
			public void onUpdate(UniversitySpend e, int requestId) { reload(); }
			
			@Override
			public void onUpdate(UniversitySpend[] e, int requestId) { reload(); }
			
			@Override
			public void onDelete(UniversitySpend e, int requestId) { reload(); }
			
			@Override
			public void onDelete(UniversitySpend[] e, int requestId) { reload(); }
		});
		
		tableModel = new UniversitySpendTableModel(mainWindow.factory.findDao(UniversitySpendDao.class));
		table = new Table(tableModel);
		Panel panel = new Panel(new BorderLayout());
		panel.add(table, BorderLayout.CENTER);
		panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		panelTable.add(FormUtil.createVerticalScrollPane(panel), BorderLayout.CENTER);
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(this.btnNew);
		top.add(this.btnList);
		
		this.btnNew.addActionListener(event -> {
			this.center.removeAll();
			
			if(this.form == null ) {
				this.form = new FormAnnualSpend(mainWindow, this.annualSpendDao);
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
		
		this.center.add(this.panelTable, BorderLayout.CENTER);
		this.add(this.center, BorderLayout.CENTER);
	}
	
	/**
	 * rechargement des donnees
	 */
	private void reload() {
		if(form != null)
			form.loadData();
		
		if(universitySpendDao.countAll() == annualSpendDao.countByAcademicYear(currentYear.getId())) {
			this.btnList.setVisible(false);
			this.btnNew.setVisible(false);
		}
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		tableModel.setCurrentYear(currentYear);
		
		if(form!= null) 
			form.setCurrentYear(currentYear);
		
		btnList.doClick();
		btnNew.setVisible(currentYear != null && academicYearDao.isCurrent(currentYear));//0975612604
		
		reload();
	}


}
