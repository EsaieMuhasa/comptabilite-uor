/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.views.MainWindow;

/**
 * @author Esaie MUHASA
 */
public abstract class DefaultScenePanel extends Panel implements WorkspaceListener, NavbarButtonListener{
	private static final long serialVersionUID = 3779735890284766308L;
	
	public static final EmptyBorder BODY_BORDER = new EmptyBorder(10, 10, 10, 10); 
	
	private ImageIcon icon;
	private String title;
	
	private List<NavbarButtonModel> navbarItems = new ArrayList<>();
	
	private CardLayout card = new CardLayout();//
	private Panel body = new Panel(card);//conteneur
	private Map<String, JComponent> cards = new HashMap<>();
	
	protected MainWindow mainWindow;
	
	
	/**
	 * ce constructeur fait appel au constructeur du ajoute un parmetre boolean
	 * par defaut, la vue devies scrollable
	 * @param title
	 * @param mainWindow
	 */
	public DefaultScenePanel(String title, MainWindow mainWindow) {
		this(title, mainWindow, true);
	}
	
	/**
	 * constructeur d'initialisation
	 * @param title le titlre de la scene
	 * @param mainWindow la fenetre principale
	 * @param scrollable, le corp de la scene est-il scrollable??
	 */
	public DefaultScenePanel(String title, MainWindow mainWindow, boolean scrollable) {
		super(new BorderLayout());
		this.title = title;
		this.mainWindow = mainWindow;
		this.init(scrollable);
	}
	
	/**
	 * 
	 * @param title
	 * @param icon
	 * @param mainWindow
	 */
	public DefaultScenePanel(String title, ImageIcon icon, MainWindow mainWindow) {
		this(title, icon, mainWindow, true);
	}
	
	/**
	 * Constructeur d'initialisation
	 * @param title
	 * @param icon
	 * @param mainWindow
	 * @param scrollable
	 */
	public DefaultScenePanel(String title, ImageIcon icon, MainWindow mainWindow, boolean scrollable) {
		super(new BorderLayout());
		this.title= title;
		this.icon = icon;
		this.mainWindow = mainWindow;
		this.init(scrollable);
	}
	
	/**
	 * visualisation du composant don le menu est a l'index en parametre
	 * @param index
	 */
	public void showAt (final int index) throws IndexOutOfBoundsException {
		body.removeAll();
		body.add(cards.get(navbarItems.get(index).getName()), navbarItems.get(index).getName());
		card.show(body, navbarItems.get(index).getName());
		body.revalidate();
		body.repaint();
	}
	
	/**
	 * @return the mainWindow
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}
	
	/**
	 * modification du titre du paneau
	 * @param title
	 */
	public void setTitle (String title) {
		this.title = title;
	}
	
	public String getTitle () {
		return this.title;
	}
	
	/**
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	/**
	 * initialisation des composants pricipaux de la scene
	 * @param scrollable
	 */
	protected void init(boolean scrollable) {
		final Panel container = new Panel(new BorderLayout());
		
		container.add(body, BorderLayout.CENTER);
		
		if (scrollable) {
			//scrollbar
			final JScrollPane scroll = FormUtil.createVerticalScrollPane(container);
			//--scrollbar
			
			body.setBorder(BODY_BORDER);
			this.add(scroll, BorderLayout.CENTER);
		} else {
			this.add(container, BorderLayout.CENTER);
		}
	}
	
	public CardLayout getCard() {
		return card;
	}

	public Panel getBody() {
		return body;
	}
	
	/**
	 * Permet de confirmer s'il faut prendre en compte l'entete
	 * @return
	 */
	public boolean hasHeader () {
		return true;
	}
	//item-menu
	
	/**
	 * Ajout d'un item dans la base de menu
	 * @param item
	 * @param component
	 * @return
	 */
	public DefaultScenePanel addItemMenu (NavbarButtonModel item, JComponent component) {
		this.navbarItems.add(item);
		if (cards.isEmpty()) {			
			getBody().add(component, item.getName());
		}
		cards.put(item.getName(), component);
		return this;
	}
	//--item-menu
	
	public List<NavbarButtonModel> getNavbarItems() {
		return navbarItems;
	}

	@Override
	public void onAction(NavbarButton view) {
		body.removeAll();
		body.add(cards.get(view.getButtonModel().getName()), view.getButtonModel().getName());
		card.show(body, view.getButtonModel().getName());
		body.revalidate();
		body.repaint();
		view.setCurrent(true);
		view.getGroup().setCurrent(view);
	}
	
	/**
	 * Doit renvoyer le pseudonyme de la scene
	 * @return
	 */
	public abstract String getNikeName ();

	@Override
	public void onShow(MenuItem item) {}

	@Override
	public void onShow (MenuItem item, int index) {
		
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void onHide() {}

}
