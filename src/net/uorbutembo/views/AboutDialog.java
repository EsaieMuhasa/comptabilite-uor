/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class AboutDialog extends JDialog{
	private static final long serialVersionUID = -8984916992661339686L;
	
	private final JEditorPane about;

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	public AboutDialog(MainWindow mainWindow) {
		super(mainWindow, "Apropos - Soft-ACADEMIA", true);
		about = new JEditorPane();
		about.setEditable(false);
		about.setEditorKit(new HTMLEditorKit());
		File file = new File("res/about.html");
		try {
			about.setPage(file.toURL());
		} catch (IOException e) {
			e.printStackTrace();
		};
		
		setSize(mainWindow.getWidth() - mainWindow.getWidth()/4, mainWindow.getHeight() / 2);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(mainWindow);
		
		about.setBackground(FormUtil.BKG_DARK);
		about.setForeground(Color.WHITE);
		about.setOpaque(false);
		about.setBorder(null);
		getContentPane().add(FormUtil.createVerticalScrollPane(about), BorderLayout.CENTER);
		getContentPane().setBackground(FormUtil.BKG_DARK);
		
		try {
			final JPanel cards = new PanelCard();
			getContentPane().add(cards, BorderLayout.SOUTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final class PanelCard extends JPanel {
		private static final long serialVersionUID = -2487476966033036328L;

		public PanelCard () throws IOException {
			super(new GridLayout(1, 2, 5, 5));
			setBackground(FormUtil.BKG_DARK);
			setBorder(new EmptyBorder(15, 10, 10, 10));
			DevCard esaie = new DevCard("res/esaie.jpg", "Ing. Esaie MUHASA", "esaiemuhasa.dev@gmail.com", "Développeur");
			DevCard claude = new DevCard("res/claude.jpg", "Prof. Dr. Ing. Claude TAKENGA", "takenga@yahoo.fr", "Directeur");
			add(claude);
			add(esaie);
		}
		
		@Override
		protected void paintBorder(Graphics g) {
			super.paintBorder(g);
			
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        g2.setColor(FormUtil.BKG_END_2);
	        g2.fillRect(0, 0, getWidth(), 3);
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	private static final class DevCard extends JComponent {
		private static final long serialVersionUID = 4685055886187631591L;
		
		private final Image image;
		private final String names;
		private final String email;
		private final String rol;
		
		
		private Shape shape;
		private final Rectangle2D rect;
		private final Ellipse2D ellipse;
		
		/**
		 * @param img
		 * @param names
		 * @param email
		 * @param rol
		 * @throws IOException
		 */
		public DevCard (String img, String names, String email, String rol) throws  IOException{
			image = ImageIO.read(new File(img));
			this.names = names;
			this.email = email;
			this.rol = rol;
			rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
			ellipse = new Ellipse2D.Double(0, 0, 100, 100);
			setPreferredSize(new Dimension(getWidth(), 100));
			setBackground(FormUtil.BKG_DARK);
			setForeground(Color.LIGHT_GRAY);
		}
		
		@Override
		public void doLayout() {
			super.doLayout();
			rect.setFrame(0, 0, getWidth(), getHeight());
			ellipse.setFrame(0, 0, 100, 100);
			Area area = new Area(rect);
			area.subtract(new Area(ellipse));
			shape = area;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        
	        g2.drawImage(image, 0, 0, 100, 100, null);
	        g2.setColor(getBackground());
	        g2.fill(shape);
	        
	        int y = 30;
	        
	        g2.setColor(getForeground());
	        g2.setFont(getFont().deriveFont(19f));
	        g2.drawString(names, 105, y);
	        g2.setFont(getFont().deriveFont(14f));
	        g2.drawString(email, 105, y+25);
	        g2.setColor(getForeground().darker());
	        g2.setFont(getFont().deriveFont(16f));
	        g2.drawString(rol, 105, y+50);
		}
	}

}
