/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigAcademicYear extends DefaultScenePanel {
	private static final long serialVersionUID = -732369198307883408L;

	private AcademicYear currentYear;
	
	private PanelPromotion panelPromotion;
	private PanelAcademicFee panelAcademicFee;
	private PanelFeePromotion panelFeePromotion;
	private PanelAnnualSpend panelAnnualSpend;
	private PanelAllocationCost panelAllocationCost;
	
	/**
	 * @param mainWindow
	 */
	public PanelConfigAcademicYear(MainWindow mainWindow) {
		super("Année académique", new ImageIcon(R.getIcon("favorite")), mainWindow);
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		
		this.panelPromotion = new PanelPromotion(mainWindow);
		this.panelAcademicFee = new PanelAcademicFee(mainWindow);
		this.panelFeePromotion = new PanelFeePromotion(mainWindow);
		this.panelAnnualSpend = new PanelAnnualSpend(mainWindow);
		this.panelAllocationCost = new PanelAllocationCost(mainWindow);
		
		this
			.addItemMenu(new NavbarButtonModel("promotions", "Promotions"), this.panelPromotion)
			.addItemMenu(new NavbarButtonModel("academicFee", "Frais univeritaire"), this.panelAcademicFee)
			.addItemMenu(new NavbarButtonModel("feePromotion", "Frais par promotion"), this.panelFeePromotion)
			.addItemMenu(new NavbarButtonModel("annualSpend", "Rubrique budgetaire"), this.panelAnnualSpend)
			.addItemMenu(new NavbarButtonModel("feePlanification", "Repartition des frais"), this.panelAllocationCost);
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
		this.panelPromotion.setCurrentYear(currentYear);
		this.panelFeePromotion.setCurrentYear(currentYear);
		this.panelAnnualSpend.setCurrentYear(currentYear);
	}

}
