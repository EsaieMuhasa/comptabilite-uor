/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.BKG_DARK;
import static net.uorbutembo.views.forms.FormUtil.BORDER_COLOR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.MenuItem;
import net.uorbutembo.views.components.MenuItemButton;
import net.uorbutembo.views.components.MenuItemListener;
import net.uorbutembo.views.components.Navbar;

/**
 * @author Esaie MUHASA
 * Ce panieau represente l'epace de travail.
 * chaque element de l'espace de travail doit Heriter de DefaultScenePanel
 */
public class WorkspacePanel extends Panel implements MenuItemListener{
	private static final long serialVersionUID = 3541117443197136392L;
	
	private Navbar navbar = new Navbar();
	private CardLayout layout = new CardLayout();
	private JPanel body = new JPanel(layout);
	private Panel emptyPanel = new Panel(); 
	private JScrollPane scroll= new JScrollPane();
	private Map<String, DefaultScenePanel> scenes = new HashMap<>();//references vers tout les scenes
	private MainWindow mainWindow;
	/**
	 * 
	 */
	public WorkspacePanel(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.setBorder(null);
		
//		this.scroll.setVerticalScrollBar(new ScrollBar());
		this.scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.scroll.getViewport().setOpaque(false);
		this.scroll.setViewportView(body);
		this.scroll.setViewportBorder(null);
		this.scroll.setBorder(null);
		this.body.setOpaque(false);
		
		this.add(navbar, BorderLayout.NORTH);
		this.add(scroll, BorderLayout.CENTER);
		this.init();
	}
	
	/**
	 * initialisation des composants graphique
	 */
	private void init() {
		this
		.add(new PanelDashboard(this.mainWindow))
		.add(new PanelAcademicYear(this.mainWindow))
		.add(new PanelInscription(this.mainWindow))
		.add(new PanelFaculty(this.mainWindow))
		.add(new PanelStudentSheet(this.mainWindow))
		.add(new PanelConfigSoftware(this.mainWindow));
		
		this.emptyPanel.setName("defaultEmptyPanel");
		this.body.add(this.emptyPanel);
	}
	
	/**
	 * @return the mainWindow
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	/**
	 * Ajout d'une scene dans la pile des scenes
	 * @param scene
	 * @return
	 */
	public WorkspacePanel add (DefaultScenePanel scene) {
		this.body.add(scene, scene.getNikeName());
		this.scenes.put(scene.getNikeName(), scene);
		this.navbar.createGroup(scene.getNikeName(), scene.getNavbarItems(), scene);
		return this;
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(BORDER_COLOR);
        g2.drawLine(0, 0, 0, this.getHeight());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(BKG_DARK);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	}


	@Override
	public void onOpen(MenuItem item) {
		
	}

	@Override
	public void onClose(MenuItem item) {
		
	}


	@Override
	public void onAction(MenuItem item, int index, MenuItemButton view) {
		if(this.scenes.containsKey(item.getModel().getName())) {
			this.scenes.get(item.getModel().getName()).onShow(item, index);
			this.layout.show(this.body, item.getModel().getName());
			this.navbar.showGroup(item.getModel().getName());
		} else {
			this.navbar.hideItems();
			this.layout.show(this.body, this.emptyPanel.getName());
		}
	}

	@Override
	public void onAction(MenuItem item) {
		if(this.scenes.containsKey(item.getModel().getName())) {	
			this.scenes.get(item.getModel().getName()).onShow(item);
			this.layout.show(this.body, item.getModel().getName());
			this.navbar.showGroup(item.getModel().getName());
		} else {
			this.navbar.hideItems();
			this.layout.show(this.body, this.emptyPanel.getName());
		}
	}

}
