/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

/**
 * @author Esaie MUHASA
 *
 */
class PieCaptionRender extends JComponent implements PieModelListener{
	private static final long serialVersionUID = 124364175204094635L;
	
	private final Font 
			FONT_PERCENT = new Font("Arial", Font.BOLD, 15),
			FONT_LABEL = new Font("Arial", Font.PLAIN, 12),
			FONT_VALUE = new Font("Arial", Font.BOLD, 13);
	
	private final BasicStroke BORDER_STROCK = new BasicStroke(1.0f);
	
	private PieModel model;
	private Color borderColor;
	
	private final List<PiePartInfo> parts = new  ArrayList<>();
	
	private int prefferedHeight;
	private int step = 30;
	
	private int hoverIndex = -1;
	private MouseAdapter mouseAdapter = new MouseListener();
	
	public PieCaptionRender() {
		borderColor = Color.WHITE;
		setBorder(new EmptyBorder(1, 1, 1, 1));
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		setOpaque(false);
	}

	/**
	 * @param part
	 */
	public PieCaptionRender(final PieModel model) {
		super();
		borderColor = Color.WHITE;
		setBorder(new EmptyBorder(1, 1, 1, 1));
		setModel(model);
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final int width = getWidth(), height = getHeight();
		
		if(model == null) {
			g.setColor(getBackground());
			g.fillRect(0, 0, width, height);
			return;
		}
			
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		for (int i = 0, count  = model.getCountPart(); i < count; i++) {
			PiePartInfo info = parts.get(i);
			paintCaption(g2, info, hoverIndex == i, 0,  width);
		}
		
		if (model.getSelectedIndex() != -1) {
			PiePartInfo info = parts.get(model.getSelectedIndex());			
			
			g2.setColor(info.getPart().getBackgroundColor());
			g2.setStroke(BORDER_STROCK);
			g2.draw(info.getShape());
			
		}
		
		super.paintComponent(g);
	}
	
	/**
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void paint (Graphics2D g, double x, double y, int width, int height) {
		setVisible(false);
		prepareRender(false, (int)x, (int)y, width, height);
		for (int i = 0, count  = model.getCountPart(); i < count; i++) {
			PiePartInfo info = parts.get(i);
			paintCaption(g, info, hoverIndex == i, (int)x,  width);
		}
		prepareRender();
		setVisible(true);
		repaint();
	}
	
	private void paintCaption (Graphics2D g2, PiePartInfo info, boolean hover, int translateX, int width) {
		PiePart part = info.getPart();
		
		FontMetrics metricsPercent = g2.getFontMetrics(FONT_PERCENT);
		FontMetrics metricsValue = g2.getFontMetrics(FONT_VALUE);
		FontMetrics metricsLabel = g2.getFontMetrics(hover? FONT_LABEL : FONT_VALUE);

		g2.setColor(part.getBackgroundColor());
		if(hover)
			g2.fill(info.getShape());
		
		int sizeRoundPercent = 60;
		int xRoundPercent = translateX + width - (sizeRoundPercent + 20);
		g2.setColor(part.getBackgroundColor());
		if(hover){
			g2.setColor(getBackground());
			g2.drawRoundRect(xRoundPercent, info.getY(), sizeRoundPercent , (int)info.getShape().getBounds2D().getHeight(), 30, 30);
		} else{
			g2.setColor(part.getBackgroundColor());
			g2.fillRoundRect(xRoundPercent, info.getY(), sizeRoundPercent , (int)info.getShape().getBounds2D().getHeight(), 30, 30);
		}
		
		String percentVal = PieRender.DECIMAL_FORMAT.format(model.getPercentOf(part))+"%";
		String valueVal = PieRender.DECIMAL_FORMAT.format(part.getValue())+model.getSuffix();
		
		//calcult largeur des textes
		int wPercent = metricsPercent.stringWidth(percentVal),
				wValue = metricsValue.stringWidth(valueVal),
				wLabel = metricsLabel.stringWidth(part.getLabel());
		
		int x = translateX + 17, xRect = translateX + 8;
		int	yRect = (int)(info.getY() + info.getShape().getBounds2D().getHeight()/2 - metricsValue.getHeight()/2);
		int y = yRect + (metricsValue.getHeight() - 2); 
		
		g2.setColor(part.getBackgroundColor());
		g2.fillRoundRect(xRect, yRect, wValue + 20, metricsValue.getHeight() + 2, 20, 20);
		
		g2.setColor(getBackground());
		if(hover) {
			g2.drawRoundRect(xRect, yRect, wValue + 20, metricsValue.getHeight() + 2, 20, 20);
		}
		g2.setFont(FONT_VALUE);
		g2.drawString(valueVal, xRect+10, y);
		
		String label = part.getLabel();
		int textLength = 0;
		do {
			wLabel = hover? metricsValue.stringWidth(label) : metricsLabel.stringWidth(label);
			textLength = wLabel + wValue + 20;
			if (textLength > (width - sizeRoundPercent*1.5) && label.length() > 1) {
				label = label.substring(0, label.length() - 1);
			}
		} while (textLength > (width - sizeRoundPercent*1.5) && label.length() > 1);
		
		if (label.length() != part.getLabel().length()) {
			if(label.length() > 2){
				label = label.substring(0, label.length()-2); 
			}
			
			label += "...";
		}
		
		x = translateX + wValue + 30;
		g2.setColor(hover? getBackground() : part.getBackgroundColor());
		g2.setFont(hover? FONT_VALUE : FONT_LABEL);
		g2.drawString(label, x, y);
		
		x = xRoundPercent + sizeRoundPercent/2 - (wPercent/2);
		y = info.getY() + metricsPercent.getHeight() + 2;
		g2.setFont(FONT_PERCENT);
		g2.setColor(getBackground());
		g2.drawString(percentVal, x, y);
		
		g2.setColor(part.getBackgroundColor());
		g2.draw(info.getShape());
	}
	
	@Override
	public void doLayout() {
		prepareRender();
		super.doLayout();
	}
	
	/**
	 * surcharge de l'utilitaire de preparation du rendu
	 */
	private void prepareRender () {
		prepareRender(true, 0, 0, getWidth(), getHeight());
	}
	
	/** 
	 * preparation du rendue graphique des parts
	 * @param revalidate
	 * @param translateX
	 * @param translateY
	 * @param graphicWidth
	 * @param graphicHeight
	 */
	private void prepareRender (boolean revalidate, int translateX, int translateY, int graphicWidth, int graphicHeight) {
		final int space = 10;
		if(revalidate)
			checkPrefferedSize(space);
		final int widht = graphicWidth, height = graphicHeight;
		for (int i = 0; i < parts.size(); i++) 
			parts.get(i).dispose();
		
		parts.clear();
		
		final int count  = model.getCountPart();
		final int padding = height >= prefferedHeight? (height - prefferedHeight) / 2 : space / 2;
		
		int h = padding;
		for (int i = 0 ; i < count; i++) {
			PiePart part = model.getPartAt(i);
			int y = h + translateY;
			RoundRectangle2D rect = new  RoundRectangle2D.Double(translateX, y, widht-10, step, step, step);
			PiePartInfo info = new PiePartInfo(rect, part, translateX, y);
			parts.add(info);
			h += step + space;
		}
		
		if(revalidate)
			revalidate();
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
	 * Determination de la taille requise pour mieux visualiser les elements de la vue
	 * @param space
	 */
	private void checkPrefferedSize (int space) {
		if (model == null)
			return;
		int prefferedHeight = (step + space) * (model.getCountPart()) - space;
		prefferedHeight += (getHeight() >= prefferedHeight? 0 : space * 2);
		if (this.prefferedHeight != prefferedHeight) {
			setPreferredSize(new Dimension(100, prefferedHeight));
			this.prefferedHeight = prefferedHeight;
		}
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
		
		model.addListener(this);
		prepareRender();
		repaint();
	}

	@Override
	public void refresh(PieModel model) {
		prepareRender();
		repaint();
	}
	
	@Override
	public void onSelectedIndex(PieModel model, int oldIndex, int newIndex) {
		repaint();
	}

	@Override
	public void repaintPart(PieModel model, int partIndex) {
		refresh(model);
	}
	
	/**
	 * ecoute des evenement de la sourie
	 * @author Esaie MUHASA
	 */
	protected class MouseListener extends MouseAdapter {

		@Override
		public void mouseExited(MouseEvent e) {
			hoverIndex = -1;
			repaint();
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			int index = -1;
			for (int i = 0; i < parts.size(); i++) {
				if(parts.get(i).match(e.getPoint())) {
					index = i;
					break;
				}
			}
			hoverIndex = index;
			repaint();
		}
		
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	protected static final class PiePartInfo {
		
		private Shape shape;
		private PiePart part;
		private int x;
		private int y;
		
		
		/**
		 * @param shape
		 * @param part
		 * @param x
		 * @param y
		 */
		public PiePartInfo(Shape shape, PiePart part, int x, int y) {
			super();
			this.shape = shape;
			this.part = part;
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Verifie si le moint appartiens au shape
		 * @param M
		 * @return
		 */
		public boolean match (Point M) {
			return shape.contains(M);
		}

		/**
		 * @return the x
		 */
		public int getX() {
			return x;
		}

		/**
		 * @return the y
		 */
		public int getY() {
			return y;
		}

		/**
		 * liberation des resources
		 */
		public void dispose () {
			shape = null;
			part = null;
		}
		
		/**
		 * @return the shape
		 */
		public Shape getShape() {
			return shape;
		}
		/**
		 * @return the part
		 */
		public PiePart getPart() {
			return part;
		}
		
	}

}
