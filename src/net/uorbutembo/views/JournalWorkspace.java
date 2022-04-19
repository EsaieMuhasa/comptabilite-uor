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
	private JDialog dialogForm;
	
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
		
		dialogForm.setLocationRelativeTo(mainWindow);
		dialogForm.setVisible(true);
	}
	
	public void updateOutlay (Outlay outlay) {
		
	}
	
	private void createOutlayDialog () {
		if(dialogForm != null)
			return;
		
		formOutlay = new FormOutlay(mainWindow);
		dialogForm = new JDialog(mainWindow, "Sortie", true);
		mainWindow.setIconImage(mainWindow.getIconImage());
		
		dialogForm.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogForm.getContentPane().add(formOutlay, BorderLayout.CENTER);
		dialogForm.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogForm.pack();
		dialogForm.setSize(800, dialogForm.getHeight());
		dialogForm.setResizable(false);
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
