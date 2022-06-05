/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicYear extends Panel implements YearChooserListener{
	private static final long serialVersionUID = -119042779710985760L;
	
	
	private WorkspaceConfigYear config;
	private Navbar navbar = new Navbar();
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		
		mainWindow.getSidebar().addYearChooserListener(this);

		config = new WorkspaceConfigYear(mainWindow);
		
		final Panel center = new Panel(new BorderLayout());
		final Panel container = new Panel(new BorderLayout());
		
		navbar.createGroup("default", config.getNavbarItems(), config);
		navbar.showGroup("default");
		
		center.add(navbar, BorderLayout.NORTH);
		center.add(config, BorderLayout.CENTER);
		container.add(center, BorderLayout.CENTER);
		add(container, BorderLayout.CENTER);		
	}

	@Override
	public void onChange(AcademicYear year) {
		setCursor(FormUtil.WAIT_CURSOR);
		config.setCursor(FormUtil.WAIT_CURSOR); 
		config.setCurrentYear(year);
		config.setCursor(Cursor.getDefaultCursor());
		setCursor(Cursor.getDefaultCursor());
	}
	
	private static final class WorkspaceConfigYear extends DefaultScenePanel {
		private static final long serialVersionUID = -732369198307883408L;

		private AcademicYear currentYear;
		
		private final PanelPromotion panelPromotion;
		private final PanelFeePromotion panelFeePromotion;
		private final PanelAnnualRecipe panelAnnualRecipe;//configuration de la repartition des recette annuels
		private final PanelAnnualSpend panelAnnualSpend;
		
		/**
		 * @param mainWindow
		 */
		public WorkspaceConfigYear(MainWindow mainWindow) {
			super("Année académique", new ImageIcon(R.getIcon("favorite")), mainWindow, false);
			
			panelPromotion = new PanelPromotion(mainWindow);
			panelFeePromotion = new PanelFeePromotion(mainWindow);
			panelAnnualRecipe = new PanelAnnualRecipe(mainWindow);
			panelAnnualSpend = new PanelAnnualSpend(mainWindow);
			
			JScrollPane scroll = FormUtil.createScrollPane(panelPromotion);
			panelPromotion.setBorder(BODY_BORDER);
			
			this
				.addItemMenu(new NavbarButtonModel("promotions", "Promotions"), scroll)
				.addItemMenu(new NavbarButtonModel("feePromotion", "Frais académique"), panelFeePromotion)
				.addItemMenu(new NavbarButtonModel("autherRecipe", "Autres recettes"), panelAnnualRecipe)
				.addItemMenu(new NavbarButtonModel("annualSpend", "Dépenses annuel"), panelAnnualSpend);
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

}
