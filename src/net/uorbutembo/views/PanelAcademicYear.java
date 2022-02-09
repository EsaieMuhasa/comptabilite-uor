/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.forms.FormAcademicYear;
import net.uorbutembo.views.forms.FormPromotion;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicYear extends DefaultScenePanel {
	private static final long serialVersionUID = -732369198307883408L;

	public PanelAcademicYear(MainWindow mainWindow) {
		super("Année académique", new ImageIcon(R.getIcon("events")), mainWindow);
		this
			.addItemMenu(new NavbarButtonModel("newStudent", "Annee academique"), new FormAcademicYear(mainWindow.factory.findDao(AcademicYearDao.class)))
			.addItemMenu(new NavbarButtonModel("oldStudent", "Promotions"), new FormPromotion())
			.addItemMenu(new NavbarButtonModel("academicSpend", "Frais cademique"), new Panel());
	}

	@Override
	public String getNikeName() {
		return "year";
	}

}
