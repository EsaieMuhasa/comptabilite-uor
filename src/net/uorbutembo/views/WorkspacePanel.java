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
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultMenuItemModel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.MenuItem;
import net.uorbutembo.views.components.MenuItemButton;
import net.uorbutembo.views.components.MenuItemListener;
import net.uorbutembo.views.components.MenuItemModel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.Sidebar;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * Ce panieau represente l'epace de travail.
 * chaque element de l'espace de travail doit Heriter de DefaultScenePanel
 */
public class WorkspacePanel extends Panel implements MenuItemListener, AcademicYearDaoListener{
	private static final long serialVersionUID = 3541117443197136392L;
	
	private final Navbar navbar = new Navbar();
	private final CardLayout layout = new CardLayout();
	private final Panel body = new Panel(layout);
	private final Header head = new Header();

	private Map<String, DefaultScenePanel> scenes = new HashMap<>();//references vers tout les scenes
	private MainWindow mainWindow;
	private Sidebar sidebar;
	
	protected AcademicYear currentYear;
	private AcademicYearDao academicYearDao;

	
	public WorkspacePanel(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicYearDao.addYearListener(this);
		this.setBorder(null);
		
		final Panel container = new Panel(new BorderLayout());
		
		this.add(head, BorderLayout.NORTH);
		container.add(this.navbar, BorderLayout.NORTH);
		container.add(this.body, BorderLayout.CENTER);
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
	public void init (Sidebar sidebar) {
		this.sidebar = sidebar;
		MenuItemModel<String> dashbord = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("dashboard")), "Tableau de bord");
		MenuItemModel<String> students = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("student")), "Etudiants ");
		MenuItemModel<String> journal = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("calendar")), "Journal ");
		MenuItemModel<String> config = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("cog")), "Configurations globales");
		MenuItemModel<String> help = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("help")), "Manuel d'utilisation");
		
		this
		.add(dashbord, new PanelDashboard(mainWindow))
		.add(students, new PanelStudents(mainWindow))
		.add(journal, new JournalWorkspace(mainWindow))
		.add(config, new PanelConfigGlobal(mainWindow))
		.add(help, new PanelHelp(mainWindow));
		
		head.setVisible(false);
		navbar.hideItems();
		navbar.setVisible(false);
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
	public void onCurrentYear(AcademicYear year) {
		currentYear = year;
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
			if(scene.hasHeader()) {		
				head.setVisible(true);
				head.setTitle(scene.getTitle());
				head.setIcon(scene.getIcon());
			}else {
				head.setVisible(false);
			}
			
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
	
	/**
	 * @author Esaie MUHASA
	 * En-tete de l'espace de travaille
	 */
	public static class Header extends Panel {
		private static final long serialVersionUID = -1499973340320709177L;
		
		private JLabel title = FormUtil.createTitle("");
		private JLabel icon = new JLabel();
		
		public Header() {
			super(new BorderLayout());
			add(title, BorderLayout.CENTER);
			add(icon, BorderLayout.WEST);
			setBorder(new EmptyBorder(5, 10, 5, 10));
			icon.setBorder(new EmptyBorder(0, 0, 0, 10));
		}
		
		/**
		 * @param title
		 */
		public void setTitle (String title) {
			this.title.setText(title);
		}
		
		/**
		 * @param icon
		 */
		public void setIcon (ImageIcon icon) {
			this.icon.setIcon(icon);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        g2.setColor(BORDER_COLOR);
	        g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
		}
		
		@Override
		protected void paintBorder(Graphics g) {
			super.paintBorder(g);
		}
		
	}

}
