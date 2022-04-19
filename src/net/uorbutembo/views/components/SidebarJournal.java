/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.views.forms.FormUtil.BORDER_COLOR;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.JournalWorkspace;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class SidebarJournal extends Panel{
	private static final long serialVersionUID = -3295426631162619526L;
	
	private final JPanel panelBottom = new Panel(new FlowLayout(FlowLayout.CENTER));
	private final Button btnNewOutlay = new Button(new ImageIcon(R.getIcon("moin")), "Sortie");
	private final Button btnNewRecipe = new Button(new ImageIcon(R.getIcon("plus")), "Entrée");
	
	private final Navbar navbar = new Navbar();
	private final SidebarTabbedPanel tabbedPanel;
	
	private final MainWindow mainWindow;
	private final JournalWorkspace workspace;

	/**
	 * @param mainWindow
	 */
	public SidebarJournal(MainWindow mainWindow, JournalWorkspace workspace) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.workspace = workspace;
		
		tabbedPanel = new SidebarTabbedPanel(mainWindow);
		tabbedPanel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		this.setPreferredSize(new Dimension(300, 400));
		
		init();
	}
	
	/**
	 * Initialisation de l'interface graphique
	 */
	private void init () {
		panelBottom.add(btnNewRecipe);
		panelBottom.add(btnNewOutlay);
		
		NavbarButtonModel item = new NavbarButtonModel("index", "Calendrier");
		NavbarButtonModel item2 = new NavbarButtonModel("bilan", "Filtres");
		tabbedPanel.addItemMenu(item, new Panel());
		tabbedPanel.addItemMenu(item2, new Panel());
		
		navbar.createGroup("default", tabbedPanel.getNavbarItems(), tabbedPanel);
		navbar.showGroup("default");
		
		add(navbar, BorderLayout.NORTH);
		add(tabbedPanel, BorderLayout.CENTER);
		add(panelBottom, BorderLayout.SOUTH);
		
		//events
		btnNewOutlay.addActionListener(event -> {
			workspace.createOutlay();
		});
		btnNewRecipe.addActionListener(event -> {
			JOptionPane.showMessageDialog(mainWindow, "Operation non pris en charge", "Alert", JOptionPane.INFORMATION_MESSAGE);
		});
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(BORDER_COLOR);
        g2.drawLine(0, 0, 0, getHeight());
	}
	
	private static class SidebarTabbedPanel extends DefaultScenePanel implements NavbarButtonListener {
		private static final long serialVersionUID = 981882245167222877L;
		
		/**
		 * @param mainWindow
		 */
		public SidebarTabbedPanel(MainWindow mainWindow) {
			super("Journal", new ImageIcon(R.getIcon("calendar")), mainWindow, false);
		}

		@Override
		public String getNikeName() {
			return "containerJournal";
		}

	}

}
