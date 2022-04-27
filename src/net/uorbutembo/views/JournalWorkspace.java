/**
 * 
 */
package net.uorbutembo.views;


import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.uorbutembo.beans.Outlay;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.NavbarButtonListener;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.components.SidebarJournal;
import net.uorbutembo.views.forms.FormOtherRecipe;
import net.uorbutembo.views.forms.FormOutlay;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalWorkspace extends DefaultScenePanel {
	private static final long serialVersionUID = 5081093701813722107L;
	
	private FormOutlay formOutlay;
	private FormOtherRecipe formRecipe;
	private JDialog dialogOutlay;
	private JDialog dialogRecipe;
	
	private final Navbar navbar = new Navbar();
	private final JPanel sidebar;
	private final Panel container = new Panel(new BorderLayout());
	
	private final JournalTabbedPanel tabbedPanel;

	/**
	 * @param mainWindow
	 */
	public JournalWorkspace(MainWindow mainWindow) {
		super("Journal des opérations financières", new ImageIcon(R.getIcon("calendar")), mainWindow, false);
		
		tabbedPanel = new JournalTabbedPanel(mainWindow);
		sidebar = new SidebarJournal(mainWindow, this);
		
		NavbarButtonModel item = new NavbarButtonModel("index", "Ligne de temps");
		NavbarButtonModel item2 = new NavbarButtonModel("bilan", "Liste des opérations");
		tabbedPanel.addItemMenu(item, new Panel());
		tabbedPanel.addItemMenu(item2, new Panel());
		
		
		navbar.createGroup("default", tabbedPanel.getNavbarItems(), tabbedPanel);
		navbar.showGroup("default");
		
		
		final Panel center = new Panel(new BorderLayout());
		
		center.add(tabbedPanel, BorderLayout.CENTER);
		center.add(navbar, BorderLayout.NORTH);
		
		container.add(sidebar, BorderLayout.EAST);
		container.add(center, BorderLayout.CENTER);
		
		add(container, BorderLayout.CENTER);
	}
	
	/**
	 * Ouverture de la boiter de dialogue d'enregistrement d'une sortie
	 */
	public void createOutlay () {		
		createOutlayDialog();
		
		dialogOutlay.setLocationRelativeTo(mainWindow);
		dialogOutlay.setVisible(true);
	}
	
	public void updateOutlay (Outlay outlay) {
		
	}
	
	/**
	 * creation du boite de dialogue d'ajout d'une valeur
	 */
	public void createRecipe () {		
		createRecipeDialog();
		
		dialogRecipe.setLocationRelativeTo(mainWindow);
		dialogRecipe.setVisible(true);
	}
	
	private void createRecipeDialog () {
		if(dialogRecipe != null)
			return;
		
		formRecipe = new FormOtherRecipe(mainWindow);
		dialogRecipe = new JDialog(mainWindow, "Entrée", true);
		dialogRecipe.setIconImage(mainWindow.getIconImage());
		
		dialogRecipe.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogRecipe.getContentPane().add(formRecipe, BorderLayout.CENTER);
		dialogRecipe.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogRecipe.pack();
		dialogRecipe.setSize(800, dialogRecipe.getHeight());
		dialogRecipe.setResizable(false);
	}
	
	private void createOutlayDialog () {
		if(dialogOutlay != null)
			return;
		
		formOutlay = new FormOutlay(mainWindow);
		dialogOutlay = new JDialog(mainWindow, "Sortie", true);
		dialogOutlay.setIconImage(mainWindow.getIconImage());
		
		dialogOutlay.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogOutlay.getContentPane().add(formOutlay, BorderLayout.CENTER);
		dialogOutlay.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogOutlay.pack();
		dialogOutlay.setSize(800, dialogOutlay.getHeight());
		dialogOutlay.setResizable(false);
	}
	
	@Override
	public boolean hasHeader() {
		return true;
	}

	@Override
	public String getNikeName() {
		return "journal";
	}
	
	/**
	 * @author Esaie MUHASA
	 *
	 */
	private static class JournalTabbedPanel extends DefaultScenePanel implements NavbarButtonListener {
		private static final long serialVersionUID = 981882245167222877L;
		
		/**
		 * @param mainWindow
		 */
		public JournalTabbedPanel(MainWindow mainWindow) {
			super("Journal", new ImageIcon(R.getIcon("calendar")), mainWindow, false);
		}

		@Override
		public String getNikeName() {
			return "containerJournal";
		}

	}

}
