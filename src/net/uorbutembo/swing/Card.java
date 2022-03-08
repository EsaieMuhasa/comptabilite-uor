/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Dimension;
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

import net.uorbutembo.swing.DefaultCardModel.CardType;

/**
 * @author Esaie MUHASA
 *
 */
public class Card extends JComponent {
	private static final long serialVersionUID = 2035862107414961620L;
	private static final Dimension 
			DEFAULT_SIZE = new Dimension(300, 150),
			MIN_SIZE = new Dimension(150, 150),
			MAX_SIZE = new Dimension(1000, 200);
	
	public static final Font 
			FONT_TITLE = new Font("Arial", Font.PLAIN, 22),
			FONT_VALUE = new Font("Arial", Font.BOLD, 35),
			FONT_INFO = new Font("Arial", Font.PLAIN, 13);
	
	private CardModel model;

	public Card() {//default constructor
		super();
		DefaultCardModel model = new DefaultCardModel(CardType.PRIMARY);
		this.model = model;
		model.setView(this);
		this.init();
	}
	
	/**
	 * Consctructeur d'initialisation du model du card
	 * @param model
	 */
	public Card(CardModel model) {
		super();
		this.model = model;
		this.init();
	}
	
	private void init() {
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(MIN_SIZE);
		this.setMaximumSize(MAX_SIZE);
	}
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		model.setBackgroundColor(bg);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		model.setForegroundColor(fg);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = this.getWidth(), height = this.getHeight();
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		g2.setColor(model.getBackgroundColor());
		g2.fillRect(0, 0, width, height);
		
		//icon
		try {
			Image img = ImageIO.read(new File(model.getIcon()));
			
			g2.drawImage(img, 5, 5, 80, 80, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//==icon
		
		FontMetrics metricsInfo = g2.getFontMetrics(FONT_INFO);
		FontMetrics  metricsValue = g2.getFontMetrics(FONT_VALUE);
		FontMetrics metricsTitle = g2.getFontMetrics(FONT_TITLE);
		
		int hI =  height-(metricsInfo.getHeight()+10), wI = width;
		int hV = (height- (metricsInfo.getHeight()+10)) - metricsValue.getHeight() - 5, xV = width - metricsValue.stringWidth(model.getValue()) - 5;
		int hT = hV + metricsValue.getHeight() - 10, xT = width - metricsTitle.stringWidth(model.getTitle()) - 5;
		
		Color darker = model.getBackgroundColor().darker();
		g2.setColor(darker);
		g2.fillRect(0, hI, wI, 50);
		
		g2.setColor(model.getForegroundColor());
		
		//value 
		g2.setFont(FONT_VALUE);
		g2.drawString(model.getValue(), xV, hV);
		//==value
		
		//title
		g2.setFont(FONT_TITLE);
		g2.drawString(model.getTitle(), xT, hT);
		//==title
		
		g2.setFont(FONT_INFO);
		g2.drawString(model.getInfo(), 5, hI+20);
	}

}
