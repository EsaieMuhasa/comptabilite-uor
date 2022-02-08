/**
 * 
 */
package net.uorbutembo.swing;

import static net.uorbutembo.views.forms.FormUtil.BKG_START;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.views.components.MenuItemButton;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class Button extends MenuItemButton {
	private static final long serialVersionUID = -4646540652206954953L;
	
	public static final Border DEFAULT_BORDER = new EmptyBorder(10, 10, 10, 10);

	/**
	 * @param icon
	 * @param text
	 * @param name
	 */
	public Button(Icon icon, String text, String name) {
		super(icon, text, name);
	}
	
	/**
	 * @param icon
	 * @param text
	 */
	public Button(Icon icon) {
		this(icon, "");
	}

	/**
	 * @param icon
	 * @param text
	 */
	public Button(Icon icon, String text) {
		super(icon, text);
		this.setBorder(DEFAULT_BORDER);
	}

	/**
	 * @param text
	 * @param name
	 */
	public Button(String text, String name) {
		super(text, name);
	}

	/**
	 * @param text
	 */
	public Button(String text) {
		super(text);
		this.setBorder(DEFAULT_BORDER);
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(FormUtil.BORDER_COLOR);
        g2.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, 5, 5);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BKG_START);
        g2.fillRoundRect(1, 1, this.getWidth()-2, this.getHeight()-2, 5, 5);
        
        super.paintComponent(g);
	}
	
}
