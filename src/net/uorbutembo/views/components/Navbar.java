/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.views.forms.FormUtil.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author Esaie MUHASA
 *
 */
public class Navbar extends JPanel {
	private static final long serialVersionUID = 7083597139732357437L;
	
	private Box nav = Box.createHorizontalBox();//new MigLayout("filly", "[]5[]5"));//conteneur des items
	//private JPanel profil = new JPanel();//profil de l'utilisateur
	private Map<String, NavbarGroup> groups = new HashMap <> ();
	
	public Navbar() {
		super();
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(400, 50));
		this.setOpaque(false);
		this.add(nav, BorderLayout.CENTER);
		this.nav.setOpaque(false);
		this.nav.setBorder(new EmptyBorder(0, 20, 0, 10));
	}
	
	/**
	 * @param models
	 * @param listener
	 * @return
	 */
	public NavbarGroup createGroup (String name, List<NavbarButtonModel> models, NavbarButtonListener listener) {
		NavbarGroup group = new NavbarGroup(name);
		for (NavbarButtonModel model : models) {
			NavbarButton item = new NavbarButton(model, listener, group);
			Component strut = Box.createHorizontalStrut(5);
			group.addItem(item, strut);
			this.nav.add(item);//, "h 40!");
			this.nav.add(strut);
		}
		this.groups.put(name, group);
		group.setVisible(false);
		return group;
	}
	
	/**
	 * visualisation du menu en parametre
	 * @param name
	 */
	public void showGroup (String name) {
		NavbarGroup group = this.groups.get(name);
		if(group != null) {
			Set<String> keys = this.groups.keySet();
			for (String key : keys) {
				this.groups.get(key).setVisible(false);
			}
			group.setVisible(true);
		}
	}
	
	public void hideItems () {
		Set<String> keys = this.groups.keySet();
		for (String key : keys) {
			this.groups.get(key).setVisible(false);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        Color color = BKG_END;
        g2.setColor(color);
        g2.fillRect(1, 0, getWidth(), getHeight());
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        
        g2.setColor(BORDER_COLOR);
        g2.drawLine(0, this.getHeight()-1, this.getWidth(), this.getHeight()-1);
	}
}
