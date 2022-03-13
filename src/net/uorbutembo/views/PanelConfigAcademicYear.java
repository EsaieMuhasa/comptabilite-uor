/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigAcademicYear extends DefaultScenePanel {
	private static final long serialVersionUID = -732369198307883408L;

	private AcademicYear currentYear;
	
	private PanelPromotion panelPromotion;
	private PanelFeePromotion panelFeePromotion;
	private PanelAnnualSpend panelAnnualSpend;
//	private PanelAllocationCost panelAllocationCost;
	
	/**
	 * @param mainWindow
	 */
	public PanelConfigAcademicYear(MainWindow mainWindow) {
		super("Année académique", new ImageIcon(R.getIcon("favorite")), mainWindow, false);
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		
		this.panelPromotion = new PanelPromotion(mainWindow);
		this.panelFeePromotion = new PanelFeePromotion(mainWindow);
		this.panelAnnualSpend = new PanelAnnualSpend(mainWindow);
		
		JScrollPane scroll = FormUtil.createScrollPane(panelPromotion);
		panelPromotion.setBorder(BODY_BORDER);
		
		this
			.addItemMenu(new NavbarButtonModel("promotions", "Promotions"), scroll)
			.addItemMenu(new NavbarButtonModel("feePromotion", "Frais par promotion"), this.panelFeePromotion)
			.addItemMenu(new NavbarButtonModel("annualSpend", "Rubrique budgetaire"), this.panelAnnualSpend);
	}

	@Override
	public String getNikeName() {
		return "configCurrentYear";
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		this.reload();
	}
	
	protected void reload () {
		this.panelPromotion.setCurrentYear(currentYear);
		this.panelFeePromotion.setCurrentYear(currentYear);
		this.panelAnnualSpend.setCurrentYear(currentYear);	
	}

}
