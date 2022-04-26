/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.AcademicYear;
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
	
	private final PanelPromotion panelPromotion;
	private final PanelFeePromotion panelFeePromotion;
	private final PanelAnnualRecipe panelAnnualRecipe;//configuration de la repartition des recette annuels
	private final PanelAnnualSpend panelAnnualSpend;
	
	/**
	 * @param mainWindow
	 */
	public PanelConfigAcademicYear(MainWindow mainWindow) {
		super("Année académique", new ImageIcon(R.getIcon("favorite")), mainWindow, false);
		
		panelPromotion = new PanelPromotion(mainWindow);
		panelFeePromotion = new PanelFeePromotion(mainWindow);
		panelAnnualRecipe = new PanelAnnualRecipe(mainWindow);
		panelAnnualSpend = new PanelAnnualSpend(mainWindow);
		
		JScrollPane scroll = FormUtil.createScrollPane(panelPromotion);
		panelPromotion.setBorder(BODY_BORDER);
		
		this
			.addItemMenu(new NavbarButtonModel("promotions", "Promotions"), scroll)
			.addItemMenu(new NavbarButtonModel("feePromotion", "Frais acadmique"), panelFeePromotion)
			.addItemMenu(new NavbarButtonModel("autherRecipe", "Autres recetes"), panelAnnualRecipe)
			.addItemMenu(new NavbarButtonModel("annualSpend", "Rubrique budgetaire"), panelAnnualSpend);
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
		panelPromotion.setCurrentYear(currentYear);
		panelFeePromotion.setCurrentYear(currentYear);
		panelAnnualSpend.setCurrentYear(currentYear);	
		panelAnnualRecipe.setCurrentYear(currentYear);
	}

}
