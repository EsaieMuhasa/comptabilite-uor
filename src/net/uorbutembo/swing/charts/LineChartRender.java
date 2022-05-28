/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * @author Esaie MUHASA
 *
 */
public class LineChartRender extends JComponent{
	private static final long serialVersionUID = -7566892662084935440L;
	
	/**
	 * une intervale numerique
	 * @author Esaie MUHASA
	 *
	 */
	public static final class Interval {
		private final double min;//la plus petite valeur de l'intervale
		private final double max;//la plus grande valeur de l'intervale
		
		/**
		 * @param min
		 * @param max
		 */
		public Interval(double min, double max) {
			super();
			this.min = min;
			this.max = max;
		}

		/**
		 * @return the min
		 */
		public double getMin() {
			return min;
		}

		/**
		 * @return the max
		 */
		public double getMax() {
			return max;
		}
		
		@Override
		public boolean equals (Object obj) {
			if (obj instanceof Interval) {
				Interval i = (Interval) obj;
				return (i.min == min && i.max == max);
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return "Interval [min=" + min + ", max=" + max + "]";
		}
		
	};
	
	private LineChartModel model;
	private final LineChartModelListener chartListener = new LineChartModelListener() {
		
		@Override
		public void onRemovePoint(LineChartModel model, int cloudIndex, int pointIndex, Point point) {
			repaint();
		}
		
		@Override
		public void onRemoveCloud(LineChartModel model, int cloudIndex) {
			repaint();
		}
		
		@Override
		public void onPointChange(LineChartModel model, int cloudIndex, int pointIndex) {
			repaint();
		}
		
		@Override
		public void onInsertPoint(LineChartModel model, int cloudIndex, int pointIndex) {
			repaint();
			
		}
		
		@Override
		public void onInsertCloud(LineChartModel model, int cloudIndex) {
			repaint();
		}

		@Override
		public void onCloudChange(LineChartModel model, int index) {
			repaint();
		}
		
		@Override
		public void onChange(LineChartModel model) {
			repaint();
		}
	};
	
	private final MouseAdapter mouseAdapter = new MouseAdapter() {
		
		private java.awt.Point A, B, C;

		@Override
		public void mousePressed(MouseEvent e) {
			if(A == null)
				A = e.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(B == null && A != null){
				B = e.getPoint();
				C = new java.awt.Point(B.x, A.y);
				
				emitRequireTranslation();
				mouseExited(e);
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			if (A != null || B != null || C != null) {
				A = null;
				B = null;
				C = null;
			}
		};
		
		/**
		 * to emit event when translation is needed
		 */
		private synchronized void emitRequireTranslation () {
			
			double dAC = Math.sqrt( Math.pow((C.getX() - A.getX()), 2) + Math.pow((C.getY() - A.getY()), 2));
			double dBC = Math.sqrt( Math.pow((C.getX() - B.getX()), 2) + Math.pow((C.getY() - B.getY()), 2));
			
			final boolean translateToX = dAC >= model.getXAxis().getRation() * model.getXAxis().getStep();
			final boolean translateToY = dBC >= model.getYAxis().getRation() * model.getYAxis().getStep();
			
			if (translateToX && translateToY) {
				//double stepsX = dAC / model.getXAxis().getRation();
				//double stepsY = dBC / model.getYAxis().getRation();
				double stepsX = (A.getX() < B.getX())? -draggXsteps : draggXsteps;
				double stepsY = (A.getY() > B.getY())? draggYsteps : draggYsteps;
				
				double minX = model.getXAxis().getMin() + stepsX;
				double maxX = model.getXAxis().getMax() + stepsX;
				//double minX = model.getXMin() != null? model.getXMin().getX() + stepsX : -Math.abs(stepsX);
				//double maxX = model.getXMax() != null? model.getXMax().getX() + stepsX : Math.abs(stepsX);
				
				double minY = model.getYAxis().getMin() + stepsY;
				double maxY = model.getYAxis().getMax() + stepsY;
				//double minY = model.getYMin() != null? model.getYMin().getY() + stepsY : -Math.abs(stepsY);
				//double maxY = model.getYMax() != null? model.getYMax().getY() + stepsY : Math.abs(stepsY);
				
				Interval xInterval = new Interval(minX, maxX);
				Interval yInterval = new Interval(minY, maxY);
				
				lastxInterval = xInterval;
				lastyInterval = yInterval;
				
				for (LineChartRenderListener ls : listeners)
					ls.onRequireTranslation(LineChartRender.this, xInterval, yInterval);
				
			} else if (translateToX) {
				double steps = (A.getX() < B.getX())? -draggXsteps : draggXsteps;
				
				//double min = model.getXAxis().getMin() + steps;
				//double max = model.getXAxis().getMax() + steps;
				double min = model.getXMin().getX() + steps;
				double max = model.getXMax().getX() + steps;
				Interval interval = new Interval(min, max);
				lastxInterval = interval;
				
				for (LineChartRenderListener ls : listeners)
					ls.onRequireTranslation(LineChartRender.this, model.getXAxis(), interval);
				
			} else if (translateToY) {
				double steps = (A.getY() > B.getY())? -draggYsteps : draggYsteps;
				
				double min = model.getYAxis().getMin() + steps;
				double max = model.getYAxis().getMax() + steps;
				Interval interval = new Interval(min, max);
				lastyInterval = interval;
				
				for (LineChartRenderListener ls : listeners)
					ls.onRequireTranslation(LineChartRender.this, model.getYAxis(), interval);
			}
		}
	};
	
	private List<LineChartRenderListener> listeners = new ArrayList<>();
	
	private int widhtRender;
	private int heightRender;
	
	private Color gridColor;
	private boolean responsive = true;
	private boolean visibleGrid = true;
	private boolean draggable = false;
	private double draggXsteps = 2;
	private double draggYsteps = 2;
	private Font fontGraduation = new Font("Arial", Font.PLAIN, 10);
	
	//last intervals
	private Interval lastxInterval;
	private Interval lastyInterval;
	//==
	
	/**
	 * 
	 */
	public LineChartRender() {
		super();
		init();
	}

	/**
	 * @param model
	 */
	public LineChartRender(LineChartModel model) {
		super();
		this.model = model;
		model.addListener(chartListener);
		init();
	}

	/**
	 * @param model
	 * @param draggable
	 */
	public LineChartRender(LineChartModel model, boolean draggable) {
		super();
		this.model = model;
		model.addListener(chartListener);
		setDraggable(draggable);
		init();
	}

	/**
	 * @param model
	 * @param draggXsteps
	 * @param draggYsteps
	 */
	public LineChartRender(LineChartModel model, double draggXsteps, double draggYsteps) {
		super();
		this.model = model;
		this.draggXsteps = draggXsteps;
		this.draggYsteps = draggYsteps;
		model.addListener(chartListener);
		setDraggable(true);
		init();
	}

	/**
	 * @return the draggXsteps
	 */
	public double getDraggXsteps() {
		return draggXsteps;
	}

	/**
	 * @param draggXsteps the draggXsteps to set
	 */
	public void setDraggXsteps (double draggXsteps) {
		this.draggXsteps = draggXsteps;
	}

	/**
	 * @return the draggYsteps
	 */
	public double getDraggYsteps() {
		return draggYsteps;
	}

	/**
	 * @param draggYsteps the draggYsteps to set
	 */
	public void setDraggYsteps(double draggYsteps) {
		this.draggYsteps = draggYsteps;
	}

	/**
	 * @return the gridColor
	 */
	protected Color getGridColor() {
		return gridColor;
	}

	/**
	 * @param gridColor the gridColor to set
	 */
	protected void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
	}

	/**
	 * @return the responsive
	 */
	protected boolean isResponsive() {
		return responsive;
	}

	/**
	 * @param responsive the responsive to set
	 */
	protected void setResponsive(boolean responsive) {
		this.responsive = responsive;
	}

	/**
	 * @return the visibleGrid
	 */
	public boolean isVisibleGrid() {
		return visibleGrid;
	}

	/**
	 * @param visibleGrid the visibleGrid to set
	 */
	public void setVisibleGrid(boolean visibleGrid) {
		if(this.visibleGrid == visibleGrid)
			return;
		
		this.visibleGrid = visibleGrid;
		repaint();
	}

	/**
	 * @return the draggable
	 */
	public boolean isDraggable() {
		return draggable;
	}

	/**
	 * @param draggable the draggable to set
	 */
	public void setDraggable(boolean draggable) {
		if(this.draggable == draggable)
			return;
		
		this.draggable = draggable;
		if (draggable) {
			addMouseListener(mouseAdapter);
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		} else {
			removeMouseListener(mouseAdapter);
			setCursor(Cursor.getDefaultCursor());
		}
		repaint();
	}
	
	/**
	 * @return the lastxInterval
	 */
	public Interval getLastxInterval() {
		return lastxInterval;
	}

	/**
	 * @return the lastyInterval
	 */
	public Interval getLastyInterval() {
		return lastyInterval;
	}

	/**
	 * Ajout d'un ecouteur du dragg du rendue du graphique
	 * @param listener
	 */
	public void addRenderListener (LineChartRenderListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	/**
	 * Detachement de l'ecouteur au rendue graphique 
	 * @param listener
	 */
	public void removeRenderListener (LineChartRenderListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the model
	 */
	public LineChartModel getModel() {
		return model;
	}
	
	private void init() {
		setOpaque(false);
		gridColor = new Color(0x50DDAAAA, true);
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(LineChartModel model) {
		if(this.model == model)
			return;
		
		if(this.model != null)
			this.model.removeListener(chartListener);
		
		this.model = model;
		
		if(model != null)
			model.addListener(chartListener);
		
		repaint();
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		if (model.getBorderColor() != null) {
			g2.setStroke(new BasicStroke(model.getBorderWidth()));
			g2.setColor(model.getBorderColor());
			g2.drawRect(model.getBorderWidth()/2, model.getBorderWidth()/2, getWidth()-(model.getBorderWidth()), getHeight()-(model.getBorderWidth()));
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		widhtRender = getWidth();
		heightRender = getHeight();
		
		Axis xAxis = model.getXAxis();
		Axis yAxis = model.getYAxis();
		
		xAxis.clear();
		yAxis.clear();
		
		final int padding = 50;
		final int stepsWidth = 10;
		int paddingX = padding;//padding horizontal
		int paddingY = padding;//padding vertical
		
		double xMax = model.hasVisibleChart()? model.getXMax().getX() : 5;
		double yMax = model.hasVisibleChart()? model.getYMax().getY() : 5;
		
		double xMin = model.hasVisibleChart()? model.getXMin().getX() : - xMax;
		double yMin = model.hasVisibleChart()? model.getYMin().getY() : - yMax;
		
		if (xMax == xMin) {
			xMax = xMax != 0? xMax : 5;
			xMin = xMin != 0? xMax / 2 : (xMax > 0? -xMax/2 : xMax * 1.5);
		}
		
		if (yMax == yMin) {
			yMax = yMax != 0? yMax : 5;
			yMin = yMin != 0? yMax / 2 : (yMax > 0? -yMax/5 : yMax * 1.5);
		}
		
		final double absXmax = Math.abs(xMax);
		final double absYmax = Math.abs(yMax);
		final double absXmin = Math.abs(xMin);
		final double absYmin = Math.abs(yMin);
		
		if (responsive) {
			xAxis.setInterval(xMin, xMax);
			yAxis.setInterval(yMin, yMax);
			
			if (model.getXAxis().isResponsive()) {
				final double xStep = absXmax >= absXmin? absXmax/4: absXmin/4;//maxX <= 10? 1 : (maxX > 10 && maxX < 100 ? 10 : (maxX <= 1000? 100 : 1000));
				xAxis.setStep(xStep);
			}
			
			if (model.getYAxis().isResponsive()) {				
				final double yStep = Math.abs(yMax - yMin) / 5;
				//final double yStep = absYmax >= absYmin? (absYmax/4 == 0? absYmax/4 : absYmax/5) : (absYmin/4 == 0? absYmin/4 : absYmin/5);//maxY <= 10? 1 : (maxY > 10 && maxY < 50 ? 5 : (maxY <= 1000? 50 : 1000));
				yAxis.setStep(yStep);
			}
			
//			double maxX = ((xMin < 0 && xMax >= 0)? absXmin + absXmax : absXmax ) + model.getXAxis().getStep();
//			double maxY = ((yMin < 0 && yMax >= 0)? absYmin + absYmax : absYmax ) + model.getYAxis().getStep();
			double maxX = ((xMin < 0 && xMax >= 0)? absXmin + absXmax : ( (xMin < 0 && xMax < 0)? absXmin : absXmax ) ) + model.getXAxis().getStep();
			double maxY = ((yMin < 0 && yMax >= 0)? absYmin + absYmax : ( (yMin < 0 && yMax < 0)? absYmin : absYmax) ) + model.getYAxis().getStep();
//			double maxX = ((absXmin > absXmax)? absXmin : absXmax ) + model.getXAxis().getStep();
//			double maxY = ((absYmin > absYmax)? absYmin : absYmax ) + model.getYAxis().getStep();
			
			g2.setFont(fontGraduation);
			FontMetrics metrics = g2.getFontMetrics();
			
			BigDecimal big = new BigDecimal(maxY).setScale(2, RoundingMode.HALF_UP);
			int measureX = metrics.stringWidth(big.doubleValue()+""+yAxis.getMeasureUnit());
//			int measureY = metrics.stringWidth(big.doubleValue()+""+yAxis.getMeasureUnit());
			
			paddingX = measureX > padding? measureX + stepsWidth * 2 : padding;
			
			double xRation = (widhtRender - paddingX * 2.5 ) / maxX;
			double yRation = (heightRender - padding * 2) / maxY;
			
			xAxis.setRation(xRation);
			yAxis.setRation(yRation);
		}
		
		
		final double yAxToRation = padding + (yMin < 0 ? yAxis.toRation(Math.abs(yMin)+yAxis.getStep()) : 0);
		final double xAyToRation = paddingX + (xMin < 0 ? xAxis.toRation(Math.abs(xMin)+xAxis.getStep()): 0);
		
		int translateX = (int) xAyToRation;//translation des Y sur X
		int translateY = (int) yAxToRation;//translation des X sur Y
		
		if(yAxis.getMin()<= 0 && yAxis.getMax() >= 0) {			
			g2.setColor(xAxis.getBorderColor());
			if (yAxis.getMin() == -yAxis.getMax()) {
				translateY -= yAxis.toRation(yAxis.getStep()/2);
			}
			g2.drawLine(0, heightRender-translateY, widhtRender, heightRender-translateY);//axe des X
			int fXx [] = {widhtRender - stepsWidth, widhtRender, widhtRender - stepsWidth};
			int fXy [] = {heightRender - translateY - stepsWidth/2, heightRender - translateY, heightRender - translateY + stepsWidth/2};
			g2.fillPolygon(fXx, fXy, 3);
		} else {
			if(yAxis.getMin() > 0) {				
				translateY += paddingX - (int) yAxis.getPixelPlacement(yAxis.getFirst());
			} else {
				translateY = heightRender;//(int) -yAxis.getPixelPlacement(yAxis.getLast());
			}
		}
		
		if(xAxis.getMin() <= 0 && xAxis.getMax() >= 0) {
			g2.setColor(yAxis.getBorderColor());
			if (xAxis.getMin() == -xAxis.getMax()) {
				translateX -= xAxis.toRation(xAxis.getStep()/2);
			} 
//			else if(xAxis.getMax() <= xAxis.getRation()){
//				translateX -= xAxis.toRation(xAxis.getStep()/1.5);
//			}
			
			g2.drawLine( translateX, 0, translateX, heightRender);//axe des Y
			int fYx [] = {translateX - stepsWidth/2, translateX, translateX + stepsWidth/2};
			int fYy [] = {stepsWidth, 0, stepsWidth};
			g2.fillPolygon(fYx, fYy, 3);
		} else if (xAxis.getMin() < 0 && xAxis.getMax() < 0) {
			translateX = widhtRender - paddingX - padding;
		} else {
			if(xAxis.getMin() > 0) {				
				translateX =  (int) -xAxis.getPixelPlacement(xAxis.getFirst());
			} else {
				translateX =  padding;//(int) +xAxis.getPixelPlacement(xAxis.getLast());
			}
		}
		
		//x
		
		AxisGraduation gr = xAxis.getFirst();
		int x = 0, y = 0;
		
		g2.setFont(fontGraduation);
		FontMetrics metrics = g2.getFontMetrics();
		
		y = (int) (heightRender - paddingY/2);
		while (xAxis.checkAfter(gr) && x < widhtRender) {
			
			x = (int) (xAxis.getPixelPlacement(gr) + translateX);
			
			g2.setColor(gr.getBorderColor());
			g2.drawLine(x, heightRender-(padding - 1), x, heightRender-(paddingY - stepsWidth));
			g2.drawString(gr.getLabel(), x - metrics.stringWidth(gr.getLabel())/2, y);
			
			if(visibleGrid) {
				g2.setColor(gridColor);
				g2.drawLine(x, stepsWidth, x, (heightRender - paddingY));
			}
			
			gr = model.getXAxis().getAfter(gr);
		}
		//==> end X
		
		
		//y
		x = 0;
		gr = yAxis.getFirst();
		
		while (yAxis.checkAfter(gr) && y >= -padding) {
			Point p = normalize(new Point2d(x, yAxis.toRation(gr.getValue())));
			y = (int)(p.getY() - translateY);
			
			g2.setColor(gr.getBorderColor());
			g2.drawLine(paddingX - stepsWidth, y, paddingX-1,  y);
			String label = gr.getLabel()+(yAxis.getMeasureUnit());
			g2.drawString(label, paddingX-stepsWidth-1-metrics.stringWidth(label), y + metrics.getHeight()/4 );
			
			if(visibleGrid) {				
				g2.setColor(gridColor);
				g2.drawLine(paddingX, y, (int) (widhtRender - xAxis.getStep()/10),  y);
			}
			
			gr = yAxis.getAfter(gr);
		}
		//==> Y
		
		if(!model.hasVisibleChart())
			return;
		
		//chars
		for (int i = 0, count = model.getSize(); i< count; i++) {
			
			if(!model.getChartAt(i).isVisible() || model.getChartAt(i).countPoints()  == 0)
				continue;
			
			Point []  points = model.getChartAt(i).getPoints();
			int length = points.length+2;
			int [] xs = new int[length];
			int [] ys = new int[length];
			
			
			for (int  j = 0; j < points.length; j++) {
				
				Point auther = new Point2d(xAxis.toRation(points[j].getX()), yAxis.toRation(points[j].getY()));
				Point normal = normalize(auther);
				
				x = (int) ((normal.getX()) + translateX);
				y = (int) ((normal.getY()) - translateY);
				
				String xy = points[j].getLabel() == null? "" : points[j].getLabel();
				g2.drawString(xy, x, y);
				
				xs[j] = x;
				ys[j] = y;
				
				g2.setColor(gr.getBorderColor());
				g2.fillOval(x - (normal.getSize()/2), y - (normal.getSize()/2), normal.getSize(), normal.getSize());
			}
			
			g2.setStroke(new BasicStroke(model.getChartAt(i).getBorderWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.setColor(model.getChartAt(i).getBorderColor());
			g2.drawPolyline(xs, ys, length-2);
			
			if (model.getChartAt(i).isFill()) {
				xs[length-2] = xs[length-3];
				xs[length-1] = xs[0];
				
				Point auther = new Point2d(xAxis.toRation(model.getYMin().getX()), yAxis.toRation(model.getYMin().getY()));
				Point normal = normalize(auther);
				y = (int) ((normal.getY()) - translateY);
				
				ys[length-1] = (yAxis.getMin() > 0)? y : heightRender-translateY;
				ys[length-2] = ys[length-1];
				
				g2.setColor(model.getChartAt(i).getBackgroundColor());
				g2.fillPolygon(xs, ys, length);
			}
			
		}
		
	}
	
	/**
	 * normalisation du point
	 * @param point
	 * @return
	 */
	protected Point normalize (Point point) {
		Point p = new Point2d(point);
		p.translateXY(point.getX(), (heightRender - point.getY()));
		return p;
	}

}
