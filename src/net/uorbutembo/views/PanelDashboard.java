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
		
		DefaultPieModel model = new DefaultPieModel(360);
		
		model.addPart(new DefaultPiePart(COLORS[0], COLORS[6], 60, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[8], COLORS[5], 60, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[2], COLORS[4], 60, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[3], COLORS[3], 30, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[4], COLORS[2], 30, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[5], COLORS[8], 30, "valeur"));
		model.addPart(new DefaultPiePart(COLORS[6], COLORS[0], 90, "valeur"));
		
		this.getBody().add(new PiePanel(model));
	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
}
