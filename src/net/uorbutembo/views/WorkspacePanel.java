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

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Orientation;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultMenuItemModel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.MenuItem;
import net.uorbutembo.views.components.MenuItemButton;
import net.uorbutembo.views.components.MenuItemListener;
import net.uorbutembo.views.components.MenuItemModel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.Sidebar;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * Ce panieau represente l'epace de travail.
 * chaque element de l'espace de travail doit Heriter de DefaultScenePanel
 */
public class WorkspacePanel extends Panel implements MenuItemListener{
	private static final long serialVersionUID = 3541117443197136392L;
	
	private final Navbar navbar = new Navbar();
	private final CardLayout layout = new CardLayout();
	private final Panel body = new Panel(layout);
	private final Panel head = new Panel(new BorderLayout());
	private final JScrollPane scroll= new JScrollPane();

	private Map<String, DefaultScenePanel> scenes = new HashMap<>();//references vers tout les scenes
	private MainWindow mainWindow;
	private Sidebar sidebar;
	
	private AcademicYear currentYear;

	
	public WorkspacePanel(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		this.setBorder(null);
		
		final Panel container = new Panel(new BorderLayout());
//		this.scroll.setVerticalScrollBar(new ScrollBar());
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		this.scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getViewport().setOpaque(false);
		scroll.setViewportView(this.body);
		scroll.setViewportBorder(null);
		scroll.setBorder(null);
		
		this.add(head, BorderLayout.NORTH);
		container.add(this.navbar, BorderLayout.NORTH);
		container.add(scroll, BorderLayout.CENTER);
		this.add(container, BorderLayout.CENTER);
	}
	
	/**
	 * @return the mainWindow
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}
	

	/**
	 * initialisation des composentants de l'espace de travail
	 * @param sidebar
	 */
	public void init(Sidebar sidebar) {
		this.sidebar = sidebar;
		MenuItemModel<String> dashbord = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("dashboard")), "Tableau de bord");
		
		MenuItemModel<String> years = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("favorite")), "Config "+this.currentYear.getLabel());
		MenuItemModel<String> inscription = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("student")), "Inscription "+this.currentYear.getLabel());
		MenuItemModel<String> sheet = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("card")), "Fiches individuels");
		MenuItemModel<Orientation> orientations = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("database")), "Orientations");

		MenuItemModel<String> config = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("cog")), "Autres configuration");
		
		MenuItemModel<String> exportData = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("export")), "Exporter");
		MenuItemModel<String> importData = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("import")), "Importer");
		MenuItemModel<String> story = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("classeur")), "Palmaresse");
		MenuItemModel<String> help = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("help")), "Manuel d'utilisation");
		MenuItemModel<String> param = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("console")), "Configuration logiciel");
		MenuItemModel<String> journal = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("status")), "Journal d'erreurs");
		
		//sub config
		importData.addItems("Exel 2007 ou plus (.xlsx)", "Exel 2003 (.xls)", "SQL File");
		// --sub config
		
		this
		.add(dashbord, new PanelDashboard(this.mainWindow))
		.add(years, new PanelConfigCurrentYear(this.mainWindow))
		.add(inscription, new PanelInscription(this.mainWindow))
		.add(sheet, new PanelStudentSheet(this.mainWindow))
		.add(orientations, new PanelOrientation(this.mainWindow))
		.add(config, new PanelConfigGlobal(this.mainWindow))
		.add(param, new PanelConfigSoftware(this.mainWindow));
	}

	/**
	 * Ajout d'une scene dans la pile des scenes
	 * @param scene
	 * @return
	 */
	public WorkspacePanel add (MenuItemModel<?> item, DefaultScenePanel scene) {
		item.setName(scene.getNikeName());
		scene.setVisible(false);
		this.sidebar.addItem(item);//item au sidebar
		if(this.scenes.isEmpty()) {//on ajoute le premier item
			this.body.add(scene, scene.getNikeName());//dans le card des scenes
		}
		this.scenes.put(scene.getNikeName(), scene);
		this.navbar.createGroup(scene.getNikeName(), scene.getNavbarItems(), scene);//menu pour la scene
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
		this.onAction(item);
	}

	@Override
	public void onAction(MenuItem item) {
		this.body.removeAll();
		
		if(this.scenes.containsKey(item.getModel().getName())) {	
			DefaultScenePanel scene = this.scenes.get(item.getModel().getName());
			this.body.add(scene, item.getModel().getName());
			this.head.removeAll();
			this.head.add(scene.getHeader(), BorderLayout.CENTER);
			this.head.repaint();
			
			if(scene.getNavbarItems().isEmpty()) {
				this.navbar.setVisible(false);
			} else {
				this.navbar.setVisible(true);
			}
			
			this.scenes.get(item.getModel().getName()).onShow(item);
			this.layout.show(this.body, item.getModel().getName());
			this.navbar.showGroup(item.getModel().getName());
		} else {
			this.navbar.hideItems();
		}
		
		this.body.revalidate();
		this.body.repaint();
	}

}
