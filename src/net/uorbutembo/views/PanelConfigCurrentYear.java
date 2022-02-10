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
import net.uorbutembo.views.forms.FormPromotion;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigCurrentYear extends DefaultScenePanel {
	private static final long serialVersionUID = -732369198307883408L;

	private AcademicYear currentYear;
	
	/**
	 * 
	 * @param mainWindow
	 */
	public PanelConfigCurrentYear(MainWindow mainWindow) {
		super("Année académique", new ImageIcon(R.getIcon("favorite")), mainWindow);
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		this.setTitle("Paneau de configuration de l'année "+this.currentYear.getLabel());
		this
			.addItemMenu(new NavbarButtonModel("promotions", "Promotions"), new FormPromotion())
			.addItemMenu(new NavbarButtonModel("feePromotion", "Frais par promotion"), new Panel())
			.addItemMenu(new NavbarButtonModel("feePlanification", "Repartition des frais"), new Panel());
	}

	@Override
	public String getNikeName() {
		return "configCurrentYear";
	}

}
