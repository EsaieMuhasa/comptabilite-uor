/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigCurrentYear extends DefaultScenePanel {
	private static final long serialVersionUID = -732369198307883408L;

	private AcademicYear currentYear;
	
	/**
	 * @param mainWindow
	 */
	public PanelConfigCurrentYear(MainWindow mainWindow) {
		super("Année académique", new ImageIcon(R.getIcon("favorite")), mainWindow);
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		this.setTitle("Paneau de configuration de l'année "+this.currentYear.getLabel());
		this
			.addItemMenu(new NavbarButtonModel("promotions", "Promotions"), new PanelPromotion(mainWindow))
			.addItemMenu(new NavbarButtonModel("academicFee", "Frais univeritaire"), new PanelAcademicFee(mainWindow))
			.addItemMenu(new NavbarButtonModel("feePromotion", "Frais par promotion"), new PanelFeePromotion(mainWindow))
			.addItemMenu(new NavbarButtonModel("annualSpend", "Rubrique budgetaire"), new PanelAnnualSpend(mainWindow))
			.addItemMenu(new NavbarButtonModel("feePlanification", "Repartition des frais"), new PanelAllocationCost(mainWindow));
	}

	@Override
	public String getNikeName() {
		return "configCurrentYear";
	}

}
