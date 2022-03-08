/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JComponent;

/**
 * @author Esaie MUHASA
 *
 */
class PiePartCaption extends JComponent implements PieModelListener{
	private static final long serialVersionUID = 124364175204094635L;
	
	private final Font 
			FONT_PERCENT = new Font("Arial", Font.BOLD, 15),
			FONT_LABEL = new Font("Arial", Font.PLAIN, 12);
	
	private PieModel model;
	private Color borderColor;

	/**
	 * @param part
	 */
	public PiePartCaption(final PieModel model) {
		super();
		this.model = model;
		this.model.addListener(this);
		this.borderColor = Color.WHITE;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int widht = this.getWidth(), height = this.getHeight();
		
		if(this.model == null) {
			g.setColor(getBackground());
			g.fillRect(0, 0, widht, height);
			return;
		}
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		int count  = this.model.getCountPart(),
				step = 30;
		int mH = step * count;//hauteur max des items
		int padding = (height - mH) / 2;
		
		int col = 50; //largeur max pour la colone des pourcentages
		int xLabel = col + 5;
		
		FontMetrics metricsPercent = g2.getFontMetrics(FONT_PERCENT);
//		FontMetrics metricsLabel = g2.getFontMetrics(FONT_LABEL);
		
		for (int i = 0 ; i < count; i++) {
			PiePart part = model.getPartAt(i);
			int index = i+1, h = padding + (index*step);
			if(index != count) {				
				g2.setColor(borderColor);
				g2.drawLine(0, h, widht, h);
			}
			
			BigDecimal big = new BigDecimal(model.getPercentOf(i)).setScale(2, RoundingMode.HALF_UP);
			String percentVal = big.doubleValue()+"%", label = part.getLabel();
			
			g2.setFont(FONT_PERCENT);
			int x = col - metricsPercent.stringWidth(percentVal), y = h- metricsPercent.getHeight()/2;
			g2.setColor(part.getBackgroundColor());
			g2.drawString(percentVal, x, y);
			
			
			g2.setFont(FONT_LABEL);
			g2.drawString(label, xLabel, y);
		}
		
	}

	/**
	 * @return the borderColor
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	/**
	 * @return the model
	 */
	public PieModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(PieModel model) {
		if(this.model != null) {
			this.model.removeListener(this);
		}
		this.model = model;
		model.addListener(this);
	}

	@Override
	public void refresh(PieModel model) {
		this.repaint();
	}

	@Override
	public void repaintPart(PieModel model, int partIndex) {
		this.refresh(model);
	}

}
