/**
 * 
 */
package net.uorbutembo.views;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButton;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.forms.FormInscription;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelInscription extends DefaultScenePanel {
	private static final long serialVersionUID = -356861410803019685L;
	
	private AcademicYear currentYear;

	public PanelInscription(MainWindow mainWindow) {
		super("Inscription", new ImageIcon(R.getIcon("student")), mainWindow);		
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		this.setTitle("Inscription "+this.currentYear.getLabel());
		//menu secondaire
		this
			.addItemMenu(new NavbarButtonModel("newStudent", "Nouveau"), new FormInscription())
			.addItemMenu(new NavbarButtonModel("oldStudent", "Re-inscription"), new Panel())
			.addItemMenu(new NavbarButtonModel("inscrits", "Inscrits"), new Panel());
	}

	@Override
	public String getNikeName() {
		return "inscription";
	}
	
	@Override
	public void onAction(NavbarButton view) {
		super.onAction(view);
	}

}
