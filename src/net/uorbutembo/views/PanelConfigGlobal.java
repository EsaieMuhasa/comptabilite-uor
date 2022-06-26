/**
 * 
 */
package net.uorbutembo.views;

import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigGlobal extends DefaultScenePanel implements YearChooserListener{
	private static final long serialVersionUID = 6023705391758343563L;
	
	private PanelAcademicYear yearPanel;
	private PanelOrientation orientationPanel;
	private PanelUniversityRecipe recipePanel;
	private PanelUniversitySpend spendPanel;
	private PanelPaymentLocation locationPanel;
	
	private AcademicYear currentYear;
	private final JPanel [] containers = new JPanel[5];

	/**
	 * @param title
	 * @param icon
	 * @param mainWindow
	 */
	public PanelConfigGlobal(MainWindow mainWindow) {
		super("Configuration globale", new ImageIcon(R.getIcon("cog")), mainWindow, false);
		mainWindow.getSidebar().addYearChooserListener(this);
	}
	
	@Override
	public void showAt (int index) throws IndexOutOfBoundsException {
		Thread t = new Thread(() -> {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			mainWindow.setCursor(getCursor());
			JPanel body = getBody();
			body.removeAll();
			
			chooseContainer(index);
			setCursor(Cursor.getDefaultCursor());
			mainWindow.setCursor(getCursor());
			
			body.add(containers[index]);
			body.revalidate();
			body.repaint();
		});
		t.start();
	}
	
	/**
	 * choix  du conteneur a afficher.
	 * si l'instance du conteneur en question n'existe pas, alors on l'instacie
	 * @param index
	 */
	private synchronized void chooseContainer (int index) {
		switch (index) {
			case 0:{
				if (yearPanel == null){
					yearPanel = new PanelAcademicYear(mainWindow);
					containers[index] = yearPanel;
					yearPanel.reload(currentYear);
				}
			}break;
			case 1:{
				orientationPanel = new PanelOrientation(this.mainWindow);
				containers[index] = orientationPanel;
				orientationPanel.reload();
			}break;
			case 2:{
				spendPanel = new PanelUniversitySpend(mainWindow);
				containers[index] = spendPanel;
				spendPanel.reload();
			}break;
			case 3:{
				recipePanel = new PanelUniversityRecipe(mainWindow);
				containers[index] = recipePanel;
				recipePanel.reload();
			}break;
			case 4:{
				locationPanel = new PanelPaymentLocation(mainWindow);
				containers[index] = locationPanel;
				locationPanel.reload();
			}break;
	
			default :
				throw new IndexOutOfBoundsException("Index out of bounds : "+index);
		}
	
	}

	@Override
	public void onChange(AcademicYear year) {
		currentYear = year;
		if(yearPanel != null) {
			yearPanel.reload(year);
		}
	}
	
	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public String getNikeName() {
		return "configurationGlobal";
	}

}
