/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 */
public abstract class DefaultScenePanel extends Panel implements WorkspaceListener, NavbarButtonListener{
	private static final long serialVersionUID = 3779735890284766308L;
	
	private ImageIcon icon;
	private JComponent header;
	private JLabel title = FormUtil.createTitle("");
	
	private List<NavbarButtonModel> navbarItems = new ArrayList<>();
	
	private CardLayout card = new CardLayout();//
	private Panel body = new Panel(card);//conteneur
	private Map<String, JComponent> cards = new HashMap<>();
	
	private MainWindow mainWindow;
	
	
	/**
	 * 
	 * @param title
	 * @param mainWindow
	 */
	public DefaultScenePanel(String title, MainWindow mainWindow) {
		super(new BorderLayout());
		this.title.setText(title);
		this.mainWindow = mainWindow;
		this.init();
	}
	
	/**
	 * 
	 * @param title
	 * @param icon
	 * @param mainWindow
	 */
	public DefaultScenePanel(String title, ImageIcon icon, MainWindow mainWindow) {
		super(new BorderLayout());
		this.title.setText(title);
		this.icon = icon;
		this.mainWindow = mainWindow;
		this.init();
	}
	
	/**
	 * @return the mainWindow
	 */
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
//		Graphics2D g2 = (Graphics2D) g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
//        g2.setColor(BORDER_COLOR);
//        g2.drawLine(this.header.getX(), this.header.getHeight()-1, (int)this.header.getBounds().getMaxX(), this.header.getHeight()-1);
	}
	
	/**
	 * modification du titre du paneau
	 * @param title
	 */
	public void setTitle (String title) {
		this.title.setText(title);
	}
	
	public JLabel getTitle () {
		return this.title;
	}
	
	/**
	 * 
	 */
	protected void init() {
		header = new Panel(new BorderLayout());
		header.add(this.title, BorderLayout.CENTER);
		header.setBackground(Color.BLACK);
		this.header.setBorder(new EmptyBorder(0, 10, 0, 10));
		if(this.icon != null) {
			JLabel icon = new JLabel(this.icon);
			header.add(icon, BorderLayout.WEST);
		}
		
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		//this.add(header, BorderLayout.NORTH);
		this.add(body, BorderLayout.CENTER);
	}
	
	public CardLayout getCard() {
		return card;
	}

	public Panel getBody() {
		return body;
	}

	/**
	 * @return the header
	 */
	public JComponent getHeader() {
		return header;
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
		if(this.cards.isEmpty()) {			
			this.getBody().add(component, item.getName());
		}
		this.cards.put(item.getName(), component);
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
	public void onShow(MenuItem item, int index) {}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void onHide() {}

}
