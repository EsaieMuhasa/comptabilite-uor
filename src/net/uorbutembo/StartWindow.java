/**
 * 
 */
package net.uorbutembo;

import static net.uorbutembo.tools.FormUtil.BKG_DARK;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JWindow;

import net.uorbutembo.tools.R;

/**
 * @author Esaie MUHASA
 *
 */
public class StartWindow extends JWindow {
	private static final long serialVersionUID = -3192714715826250151L;
	private static final Font FONT = new Font("Arial", Font.BOLD, 30);
	private static final Cursor WAIT = new Cursor(Cursor.WAIT_CURSOR);

	private ConntentRender render;
	private Image logo;
	
	public StartWindow() {
		super();
		this.setSize(500, 250);
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setBackground(BKG_DARK.darker().darker());
		this.setOpacity(0.95f);
		
		try {
			logo = ImageIO.read(new File(R.getIcon("logo")));
		} catch (IOException e) {}
		
		render = new ConntentRender();
		render.setCursor(WAIT);
		this.setCursor(WAIT);
		this.setContentPane(render);
		
	}
	
	private class ConntentRender extends JComponent {
		private static final long serialVersionUID = -1003829780883748446L;
		public ConntentRender() {
			setOpaque(false);
		}
		
		private String getMessage () {
			return "Chargement...";
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        g2.setColor(BKG_DARK);
	        
	        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 5, 5);
	        
	        g2.setColor(BKG_DARK.darker().darker().darker().darker());
	        int wid = getWidth(), heig = getHeight();
	        g2.fillOval(-wid/2, -heig/2, wid, heig);
	        g2.fillOval(wid/2, heig/2, wid, heig);
	        
	        
	        FontMetrics metrics = g2.getFontMetrics(FONT);
	        g2.setColor(Color.LIGHT_GRAY);
	        g2.setFont(FONT);
	        String message = getMessage();
	        int w = metrics.stringWidth(message), x = getWidth()/2 - w/2, y = getHeight()/2 + metrics.getHeight()/3;
	        g2.drawImage(logo, x-50, y - 35, 50, 50, null);
	        g2.drawString(message, x, y);
		}
	}
}
