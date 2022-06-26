/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.tools.FormUtil.BKG_DARK;
import static net.uorbutembo.tools.FormUtil.BORDER_COLOR;

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
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.components.DefaultMenuItemModel;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.MenuItem;
import net.uorbutembo.views.components.MenuItemButton;
import net.uorbutembo.views.components.MenuItemListener;
import net.uorbutembo.views.components.MenuItemModel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.Sidebar;

/**
 * @author Esaie MUHASA
 * Ce panieau represente l'epace de travail.
 * chaque element de l'espace de travail doit Heriter de DefaultScenePanel
 */
public class WorkspacePanel extends Panel implements MenuItemListener, Sidebar.YearChooserListener{
	private static final long serialVersionUID = 3541117443197136392L;
	
	private final Navbar navbar = new Navbar();
	private final CardLayout layout = new CardLayout();
	private final Panel body = new Panel(layout);
	private final Header head = new Header();

	private Map<String, DefaultScenePanel> scenes = new HashMap<>();//references vers tout les scenes
	private MainWindow mainWindow;
	private Sidebar sidebar;
	
	protected AcademicYear currentYear;
	private MenuItemModel<String> config;
	private PanelDashboard dashboard;

	public WorkspacePanel(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		setBorder(null);
		
		final Panel container = new Panel(new BorderLayout());
		
		add(head, BorderLayout.NORTH);
		container.add(navbar, BorderLayout.NORTH);
		container.add(body, BorderLayout.CENTER);
		add(container, BorderLayout.CENTER);
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
		
		config = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("cog")), "Configurations globales");
		config.addItems("Année academique", "Filières et classes", "Dépenses", "Autres recettes", "Lieux de payement");
		
		dashboard = new PanelDashboard(mainWindow);
		
		this
		.add(dashbord, dashboard)
		.add(students, new PanelStudents(mainWindow))
		.add(journal, new JournalWorkspace(mainWindow))
		.add(config, new PanelConfigGlobal(mainWindow));
		
		head.setVisible(false);
		navbar.hideItems();
		navbar.setVisible(false);
		sidebar.addYearChooserListener(this);
	}
	
	@Override
	public synchronized void onChange(AcademicYear year) {
		currentYear = year;
		config.updateItem(year.toString(), 0);
		
		for (MenuItem item : sidebar.getItems()) {
			if (item.isCurrent()) {
				if(item.getModel().countItems() != 0)
					mainWindow.setTitle(R.getConfig().get("appName")+""+(currentYear == null? "":" ("+currentYear.toString()+") ")+" - "+item.getModel().getLabel()+ " / "+item.getModel().getItem(item.getModel().getCurrentItem()));
				else
					mainWindow.setTitle(R.getConfig().get("appName")+""+(currentYear == null? "":" ("+currentYear.toString()+") ")+" - "+item.getModel().getLabel());
				break;
			}
		}
	}

	/**
	 * @return the dashboard
	 */
	public PanelDashboard getDashboard() {
		return dashboard;
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
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(BKG_DARK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        super.paintComponent(g);
	}
	
	@Override
	protected void paintChildren (Graphics g) {
		super.paintChildren(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        g2.setColor(BORDER_COLOR);
        g2.drawLine(0, 0, 0, getHeight());
	}
	
	public void onItemClicked (MenuItem item) {
		if(item.isCurrent()) {
			return;
		}
		
		for (MenuItem i : sidebar.getItems()) {
			if (i.isCurrent()) {
				i.setCurrent(false);
				break;
			}
		}
		
		item.setCurrent(true);
	}

	@Override
	public void onOpen(MenuItem item) {}

	@Override
	public void onClose(MenuItem item) {}


	@Override
	public void onAction(MenuItem item, int index, MenuItemButton view) {
		
		if(!item.isCurrent()) 
			showContainer(item);
		
		onItemClicked(item);
		
		if(this.scenes.containsKey(item.getModel().getName())) {	
			DefaultScenePanel scene = this.scenes.get(item.getModel().getName());
			scene.onShow(item, index);
			scene.showAt(index);
			item.getModel().setSelectedItem(index);
		}
		mainWindow.setTitle(R.getConfig().get("appName")+""+(currentYear == null? "":" ("+currentYear.toString()+") ")+" - "+item.getModel().getLabel()+ " / "+view.getText());
	}

	@Override
	public void onAction (MenuItem item) {
		
		if(item.getModel().countItems() != 0)
			return;
		
		onItemClicked(item);
		showContainer(item);
		mainWindow.setTitle(R.getConfig().get("appName")+""+(currentYear == null? "":" ("+currentYear.toString()+") ")+" - "+item.getModel().getLabel());
	}
	
	private void showContainer (MenuItem item) {
		body.removeAll();
		
		if(scenes.containsKey(item.getModel().getName())) {	
			DefaultScenePanel scene = scenes.get(item.getModel().getName());
			body.add(scene, item.getModel().getName());
			
			if(scene.hasHeader()) {		
				head.setVisible(true);
				head.setTitle(scene.getTitle());
				head.setIcon(scene.getIcon());
			} else 
				head.setVisible(false);
			
			if (scene.getNavbarItems().isEmpty())
				navbar.setVisible(false);
			else
				navbar.setVisible(true);

			scenes.get(item.getModel().getName()).onShow(item);
			layout.show(body, item.getModel().getName());
			navbar.showGroup(item.getModel().getName());
		} else {
			navbar.hideItems();
		}
		
		body.revalidate();
		body.repaint();		
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
