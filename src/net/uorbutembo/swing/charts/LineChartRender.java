/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 * @author Esaie MUHASA
 *
 */
public class LineChartRender extends JComponent{
	private static final long serialVersionUID = -7566892662084935440L;
	
	private LineChartModel model;
	private final LineChartModelListener chartListener = new LineChartModelListener() {
		
		@Override
		public void onRemovePoint(LineChartModel model, int cloudIndex, int pointIndex, Point point) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onRemoveCloud(LineChartModel model, int cloudIndex) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPointChange(LineChartModel model, int cloudIndex, int pointIndex) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onInsertPoint(LineChartModel model, int cloudIndex, int pointIndex) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onInsertCloud(LineChartModel model, int cloudIndex) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCloudChange(LineChartModel model, int index) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onChange(LineChartModel model) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private int widhtRender;
	private int heightRender;
	
	private Color gridColor;
	private boolean responsive = true;
	private Font fontGraduation = new Font("Arial", Font.PLAIN, 10);
	
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
		
		double xMax = model.getXMax().getX() ;
		double yMax = model.getYMax().getY() ;
		
		double xMin = model.getXMin().getX();
		double yMin = model.getYMin().getY();
		
		double absXmax = Math.abs(xMax);
		double absYmax = Math.abs(yMax);
		double absXmin = Math.abs(xMin);
		double absYmin = Math.abs(yMin);
		
		
		if (responsive) {
			xAxis.setInterval(xMin, xMax);
			yAxis.setInterval(yMin, yMax);
			
			double maxX = (xMin < 0 || xMax < 0) ? absXmin + absXmax : absXmax;
			double maxY = (yMin < 0 || yMax < 0)? absYmin + absYmax : absYmax;
			
			double xRation = (widhtRender - padding * 2) / maxX;
			double yRation = (heightRender - padding * 2) /maxY;
			
			double xStep = 10;//maxX <= 10? 1 : (maxX > 10 && maxX < 100 ? 10 : (maxX <= 1000? 100 : 1000));
			double yStep = 1;//maxY <= 10? 1 : (maxY > 10 && maxY < 50 ? 5 : (maxY <= 1000? 50 : 1000));
			
			xAxis.setRation(xRation);
			xAxis.setStep(xStep);
			
			yAxis.setRation(yRation);
			yAxis.setStep(yStep);
		}
		
		final double yAxToRation = padding + (yMin < 0 ? yAxis.toRation(Math.abs(yMin)+yAxis.getStep()) : 0);
		final double xAyToRation = padding + (xMin < 0 ? xAxis.toRation(Math.abs(xMin)+xAxis.getStep()): 0);
		
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
				translateY += padding - (int) yAxis.getPixelPlacement(yAxis.getFirst());
			} else {
				translateY = heightRender;//(int) -yAxis.getPixelPlacement(yAxis.getLast());
			}
		}
		
		if(xAxis.getMin() <= 0 && xAxis.getMax() >= 0) {
			g2.setColor(yAxis.getBorderColor());
			g2.drawLine( translateX, 0, translateX, heightRender);//axe des Y
			int fYx [] = {translateX - stepsWidth/2, translateX, translateX + stepsWidth/2};
			int fYy [] = {stepsWidth, 0, stepsWidth};
			g2.fillPolygon(fYx, fYy, 3);
		} else {
			if(xAxis.getMin() > 0) {				
				translateX +=  (int) -xAxis.getPixelPlacement(xAxis.getFirst());
			} else {
				translateX =  padding;//(int) +xAxis.getPixelPlacement(xAxis.getLast());
			}
		}
		
		//x
		
		AxisGraduation gr = xAxis.getFirst();
		int x = 0, y = 0;
		
		g2.setFont(fontGraduation);
		FontMetrics metrics = g2.getFontMetrics();
		
		while (xAxis.checkAfter(gr) && x < widhtRender) {
			
			x = (int) (xAxis.getPixelPlacement(gr) + translateX);
			
			g2.setColor(gr.getBorderColor());
			g2.drawLine(x, heightRender-(padding - 1), x, heightRender-(padding - stepsWidth));
			g2.drawString(gr.getLabel(), x - metrics.stringWidth(gr.getLabel())/2, (int) (heightRender - padding/2));
			
			g2.setColor(gridColor);
			g2.drawLine(x, (int)(yAxis.getStep()/10), x, (heightRender - padding));
			
			gr = model.getXAxis().getAfter(gr);
		}
		//==> end X
		
		
		//y
		
		x = 0;
		y = 0;
		gr = yAxis.getFirst();
		
		while (yAxis.checkAfter(gr) && y >= -padding) {
			Point p = normalize(new Point2d(x, yAxis.toRation(gr.getValue())));
			y = (int)(p.getY() - translateY);
			
			g2.setColor(gr.getBorderColor());
			g2.drawLine(padding - stepsWidth, y, padding-1,  y);
			g2.drawString(gr.getLabel(), padding-stepsWidth-1-metrics.stringWidth(gr.getLabel()), y + metrics.getHeight()/4 );
			
			g2.setColor(gridColor);
			g2.drawLine(padding, y, (int) (widhtRender - xAxis.getStep()/10),  y);
			
			gr = yAxis.getAfter(gr);
		}
		//==> Y
		
		//chars
		for (int i = 0, count = model.countChars(); i< count; i++) {
			Point []  points = model.getChartAt(i).getPoints();
			int length = points.length+2;
			int [] xs = new int[length];
			int [] ys = new int[length];
			
			
			for (int  j = 0; j < points.length; j++) {
				
				Point auther = new Point2d(xAxis.toRation(points[j].getX()), yAxis.toRation(points[j].getY()));
				Point normal = normalize(auther);
				
				x = (int) ((normal.getX()) + translateX);
				y = (int) ((normal.getY()) - translateY);
				
				//String xy = "["+(points[j].getRoundX(3))+" , "+(points[j].getRoundY(3))+"]";
				//g2.drawString(xy, x, y);
				
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
