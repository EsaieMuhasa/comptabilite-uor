/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import javax.swing.ImageIcon;

import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.views.components.DefaultScenePanel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDashboard extends DefaultScenePanel {
	private static final long serialVersionUID = 4525497607858984186L;
	
	public PanelDashboard(MainWindow mainWindow) {
		super("Tableau de board", new ImageIcon(R.getIcon("dashboard")), mainWindow);
		
		DefaultPieModel model = new DefaultPieModel(1000);
		
		model.addPart(new DefaultPiePart(COLORS[0], 100, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[1], 200, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[2], 50, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[3], 70, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[4], 350, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[5], 30, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[6], 90, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[7], 120, "valeur"));
		
		this.getBody().add(new PiePanel(model));
	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
}
