/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.awt.Dimension;
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
			FONT_LABEL = new Font("Arial", Font.PLAIN, 12),
			FONT_VALUE = new Font("Arial", Font.BOLD, 13);
	
	private PieModel model;
	private Color borderColor;
	private boolean paddingLeft = false;
	
	private int prefferedHeight;
	private int step = 30;
	
	public PiePartCaption() {
		this.borderColor = Color.WHITE;
	}

	/**
	 * @param part
	 */
	public PiePartCaption(final PieModel model) {
		super();
		this.borderColor = Color.WHITE;
		setModel(model);
	}
	
	public void setPaddingLeft (boolean left) {
		paddingLeft = left;
		this.repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		int widht = this.getWidth(), height = this.getHeight();
		
		if(this.model == null) {
			g.setColor(getBackground());
			g.fillRect(0, 0, widht, height);
			return;
		}
		
		int prefferedWidth = 0;		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		final int count  = this.model.getCountPart();
		final int mH = (step + 18) * count;//hauteur max des items
		final int padding = ((height - mH) / 2) - step/3;
		
		final int col = 50 + (paddingLeft? step/2 : 0); //largeur max pour la colone des pourcentages
		int xLabel = col + 5 + step/3;
		
		FontMetrics metricsPercent = g2.getFontMetrics(FONT_PERCENT);
		FontMetrics metricsValue = g2.getFontMetrics(FONT_VALUE);
		FontMetrics metricsLabel = g2.getFontMetrics(FONT_LABEL);
		
		int h = padding + step;
		for (int i = 0 ; i < count; i++) {
			PiePart part = model.getPartAt(i);

			g2.setColor(part.getBackgroundColor().darker().darker().darker().darker());
			g2.fillRoundRect(0, h-step, widht-10, step, step, step);
			
			if(model.getSelectedIndex() == model.indexOf(part)) {
				g2.setColor(part.getBackgroundColor().darker());
				g2.drawRoundRect(0, h-step, widht-10, step, step, step);
			}
			
			g2.setColor(part.getBackgroundColor());
			g2.fillOval(widht-col-5, h-step-10, col, col);
			
			BigDecimal big = new BigDecimal(model.getPercentOf(i)).setScale(2, RoundingMode.HALF_UP);
			BigDecimal bigValue = new BigDecimal(part.getValue()).setScale(2, RoundingMode.HALF_UP);
			String percentVal = big.doubleValue()+"%", label = part.getLabel();
			
			int intVal = (int) bigValue.doubleValue();
			String valueVal = "";
			if (intVal == bigValue.doubleValue())
				valueVal = intVal +""+model.getSuffix();
			else 
				valueVal = bigValue.doubleValue()+""+model.getSuffix();
			
			//calcult largeur des textes
			int wPercent = metricsPercent.stringWidth(percentVal),
					wValue = metricsValue.stringWidth(valueVal),
					wLabel = metricsLabel.stringWidth(label);
			
			int wMax = col + xLabel + wLabel + 10;//prefferedWidth
			if (wMax > prefferedWidth)
				prefferedWidth = wMax;
			
			g2.setFont(FONT_VALUE);
			int x = col - wValue + step/3, y = h- metricsValue.getHeight()/2;
			g2.drawString(valueVal, x, y);
			
			g2.setFont(FONT_LABEL);
			g2.drawString(label, xLabel, y);
			
			g2.setFont(FONT_PERCENT);
			g2.setColor(getBackground());
			x = (widht - col-5 + col/2) - wPercent + (wPercent/2);
			y = ( h-step-10 + col/2) + metricsPercent.getHeight()/3;
			g2.drawString(percentVal, x, y);
			g2.fillRect(widht-5, h-step-10, 4, col);
			
			h += step + 25;
		}
		//prefferedHeight = mH + col;
		super.paintComponent(g);
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
	public void setModel (PieModel model) {
		if(this.model != null) {
			this.model.removeListener(this);
		}
		this.model = model;
		if (model == null)
			return;

		int prefferedHeight = (step + 18) * model.getCountPart() + 65;
		if (this.prefferedHeight != prefferedHeight) {
			this.setPreferredSize(new Dimension(100, prefferedHeight));
			this.prefferedHeight = prefferedHeight;
		}
		
		model.addListener(this);
	}

	@Override
	public void refresh(PieModel model) {
		int prefferedHeight = (step + 18) * model.getCountPart() + 65;
		if (this.prefferedHeight != prefferedHeight) {
			this.setPreferredSize(new Dimension(100, prefferedHeight));
			this.prefferedHeight = prefferedHeight;
		}
		
		repaint();
	}
	
	@Override
	public void onSelectedIndex(PieModel model, int oldIndex, int newIndex) {
		repaint();
	}

	@Override
	public void repaintPart(PieModel model, int partIndex) {
		this.refresh(model);
	}

}
