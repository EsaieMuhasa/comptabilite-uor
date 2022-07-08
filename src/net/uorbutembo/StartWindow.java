/**
 * 
 */
package net.uorbutembo;

import static net.uorbutembo.tools.FormUtil.BKG_DARK;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import net.uorbutembo.tools.Config;

/**
 * @author Esaie MUHASA
 *
 */
public class StartWindow extends JFrame {
	private static final long serialVersionUID = -3192714715826250151L;
	private static final Font FONT = new Font("Arial", Font.BOLD, 25);
	private static final Cursor WAIT = new Cursor(Cursor.WAIT_CURSOR);
	private static final Color COLOR = new Color(0xEE5010);

	private ConntentRender render;
	private Image logo;
	private boolean run = true;
	private Thread thread;
	private int count = 0;
	
	public StartWindow() {
		super();
		
		setSize(400, 250);
		setUndecorated(true);
		setLocationRelativeTo(null);
		setBackground(new Color(0xAA000000, true));
		setAlwaysOnTop(true);
		setOpacity(0.90f);
		
		try {
			logo = ImageIO.read(new File(Config.find("appLaucherIcon")));
			setIconImage(logo);
		} catch (IOException e) {}
		
		render = new ConntentRender();
		render.setCursor(WAIT);
		setCursor(WAIT);
		setContentPane(render);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (thread != null) {
			run = false;
			thread = null;
		}
	}
	
	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			run = true;
			thread = new Thread(() -> {
				while (run) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {}
					repaint();
					count++;
				}
			});
			thread.start();
		} else {
			run = false;
		}
	}
	
	private class ConntentRender extends JComponent {
		private static final long serialVersionUID = -1003829780883748446L;
		public ConntentRender() {
			setOpaque(false);
		}
		
		private String getMessage () {
			int point = count % 4;
			String msg = "Chargement";
			for (int i = 0; i < point; i++) 
				msg += ".";
			return msg;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        
	        g2.setColor(BKG_DARK);
	        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 5, 5);
	        
	        g2.setColor(Color.WHITE.darker());
	        g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
	        
	        g2.drawImage(logo, getWidth() / 2 - 75, 20, 150, 150, null);
	        g2.setColor(COLOR);
	        g2.setFont(FONT);
	        final int wi = g2.getFontMetrics(FONT).stringWidth("FINANCE");
	        int x = getWidth() / 2 - wi / 2;
	        g2.drawString("FINANCE", x, 200);

	        g2.setColor(Color.LIGHT_GRAY);
	        g2.setFont(FONT.deriveFont(16f));
	        g2.drawString(getMessage (), x, 225);
		}
	}
}
