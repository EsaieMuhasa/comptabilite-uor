/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.tools.FormUtil.ACTIVE_COLOR;
import static net.uorbutembo.tools.FormUtil.BORDER_COLOR;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.border.EmptyBorder;

/**
 * @author Esaie MUHASA
 *
 */
public class NavbarButton extends MenuItemButton{
	private static final long serialVersionUID = 2050181404006868957L;
	
	private boolean current = false;
	private NavbarButtonModel buttonModel;
	private NavbarGroup group;

	/**
	 * 
	 * @param buttonModel
	 * @param listener
	 * @param group
	 */
	public NavbarButton(NavbarButtonModel buttonModel, NavbarButtonListener listener, NavbarGroup group) {
		super(buttonModel.getLabel(), buttonModel.getName());
		this.setBorder(new EmptyBorder(0, 10, 0, 10));
		this.buttonModel = buttonModel;
		this.addActionListener(event -> {
			listener.onAction(this);
		});
		this.group = group;
		this.setMaximumSize(new Dimension((int) this.getMaximumSize().getWidth(), 45));
	}
	
	public NavbarGroup getGroup() {
		return group;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
		this.repaint();
	}

	public NavbarButtonModel getButtonModel() {
		return buttonModel;
	}

	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(this.isCurrent()? ACTIVE_COLOR : BORDER_COLOR);
        g2.fillRect(0, this.getHeight()-4, this.getWidth(), this.getHeight());
	}

}
