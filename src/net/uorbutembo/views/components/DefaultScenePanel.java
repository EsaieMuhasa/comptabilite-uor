/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.views.forms.FormUtil.BORDER_COLOR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class DefaultScenePanel extends Panel implements WorkspaceListener, NavbarButtonListener{
	private static final long serialVersionUID = 3779735890284766308L;
	
	private String title;
	private ImageIcon icon;
	private JComponent header;
	
	private List<NavbarButtonModel> navbarItems = new ArrayList<>();
	
	private CardLayout card = new CardLayout();//
	private Panel body = new Panel(card);//conteneur
//	private Panel emptyPanel = new Panel();
	
	
	/**
	 * @param title
	 */
	public DefaultScenePanel(String title) {
		super(new BorderLayout());
		this.title = title;
		this.init();
	}
	
	/**
	 * @param title
	 * @param icon
	 */
	public DefaultScenePanel(String title, ImageIcon icon) {
		super(new BorderLayout());
		this.title = title;
		this.icon = icon;
		this.init();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(BORDER_COLOR);
        g2.drawLine(this.header.getX(), this.header.getHeight()-1, (int)this.header.getBounds().getMaxX(), this.header.getHeight()-1);
	}
	
	/**
	 * 
	 */
	protected void init() {
		this.header = new JPanel(new BorderLayout());
		JLabel title = FormUtil.createTitle(this.title);
		header.add(title, BorderLayout.CENTER);
		header.setOpaque(false);
		
		Border empty = new EmptyBorder(5, 5, 10, 5);
		if(this.icon != null) {
			JLabel icon = new JLabel(this.icon);
			header.add(icon, BorderLayout.WEST);
//			icon.setBorder(empty);
		}
		
		this.header.setBorder(empty);
		this.setBorder(new EmptyBorder(10, 20, 10, 20));
		this.add(header, BorderLayout.NORTH);
		this.add(this.body, BorderLayout.CENTER);
	}
	
	public CardLayout getCard() {
		return card;
	}

	public Panel getBody() {
		return body;
	}

	//item-menu
	public DefaultScenePanel addItemMenu (NavbarButtonModel item, JComponent component) {
		this.navbarItems.add(item);
		this.getBody().add(component, item.getName());
		return this;
	}
	
	public void addItemMenus (NavbarButtonModel... items) {
		for (NavbarButtonModel item : navbarItems) {
			this.navbarItems.add(item);
		}
	}
	//--item-menu
	
	public List<NavbarButtonModel> getNavbarItems() {
		return navbarItems;
	}

	@Override
	public void onAction(NavbarButton view) {
		this.card.show(this.getBody(), view.getButtonModel().getName());
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
