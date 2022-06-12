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
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.charts.PointCloud.CloudType;

/**
 * @author Esaie MUHASA
 *
 */
public class CloudChartRender extends JComponent {
	private static final long serialVersionUID = -8580414807783085550L;
	
	private static final EmptyBorder DEFAULT_PADDING = new EmptyBorder(10, 10, 10, 10);
	private final ModelListener modelListener = new  ModelListener();
	private final MouseListener mouseListener = new  MouseListener();
	private CloudChartModel model;
	
	private BasicStroke borderStroke = new BasicStroke(1.5f);
	
	//render data
	private final List<PreparedPointCloud> chartMetadatas = new ArrayList<>();
	private int hoverPoint = -1;
	private int hoverChart = -1;
	
	private double xRation;
	private double yRation;
	private double widhtRender;
	private double heightRender;
	private float padding = 15;
	private float paddingLeft = 60f;
	private float paddingBottom = 30f;
	
	private double translateX;
	private double translateY;
	//==
	
	//grid
	private BasicStroke gridStroke = new BasicStroke(0.8f);
	private Color gridColor = new Color(0x559F6666, true);
	private double gridXstep = 80;
	private double gridYstep = 30;
	private boolean gridXvisible = true;
	private boolean gridYvisible = true;
	private Line2D xlineAxis;
	private Line2D ylineAxis;
	private Color lineAxisColor = new Color(0xCC505050, true);
	private int [][] fleshXlineAxis = new int [2][3];
	private int [][] fleshYlineAxis = new int [2][3];
	private Rectangle2D borderRect = new Rectangle2D.Double();//rectagle du bordure
	//
	
	//hover
	private Point2D mouseLocation;
	private double mouseXvalue;
	private double mouseYvalue;
	private final RoundRectangle2D recMouseXvalue = new RoundRectangle2D.Double();
	private final RoundRectangle2D recMouseYvalue = new RoundRectangle2D.Double();
	private Color mouseLineColor = new Color(0xBB550055, true);
	private BasicStroke mouseLineWidth = new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private boolean mouseLineVisible = true;
	//==

	public CloudChartRender() {
		init();
	}
	
	/**
	 * constructeur d'initilisation du model des donnees du graphique
	 * @param model
	 */
	public CloudChartRender(CloudChartModel model) {
		super();
		this.model = model;
		init();
	}

	private void init () {
		setBorder(DEFAULT_PADDING);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		setOpaque(false);
		if (model != null) 
			model.addListener(modelListener);
	}
	
	/**
	 * mis en jour de la refference du model des donnees
	 * @param model
	 */
	public void setModel (CloudChartModel model) {
		if(this.model == model)
			return;
		
		if (this.model != null)
			this.model.removeListener(modelListener);
		
		this.model = model;
		
		if(model != null)
			model.addListener(modelListener);
		
		prepareRender();
		repaint();
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		prepareRender();
	}
	
	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		if (model != null && model.getBorderColor() != null && borderRect != null) {
			g2.setStroke(borderStroke);
			g2.setColor(model.getBorderColor());
			g2.draw(borderRect);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		g2.drawString("W: "+widhtRender+"px", paddingLeft + padding/3, 20f);
		g2.drawString("H: "+heightRender+"px", paddingLeft + padding/3, 35f);
		
		for (int i = 0, count = chartMetadatas.size(); i < count; i++) {
			PreparedPointCloud  cloud = chartMetadatas.get(i);
			PointCloud chart = cloud.getCloud();
			
			if(cloud.getCloud().isFill()){
				g2.setColor(cloud.getCloud().getBackgroundColor());
				g2.fill(cloud.getArea());
			}
			
			g2.setStroke(mouseLineWidth);
			if (chart.getType() == CloudType.STICK_CHART) {
				for (int j = 0; j < cloud.getCloud().countPoints(); j++) {
					g2.setColor(chart.getBackgroundColor());
					g2.fill(cloud.getPoints()[j].getShape());
					
					g2.setColor(cloud.getCloud().getPointAt(j).getBorderColor());
					g2.setColor(chart.getBorderColor());
					g2.draw(cloud.getPoints()[j].getShape());
				}
			} else {
				
				for (int j = 0; j < cloud.getCloud().countPoints(); j++) {
					g2.setColor(cloud.getCloud().getPointAt(j).getBackgroundColor());
					g2.fill(cloud.getPoints()[j].getShape());
					
					g2.setColor(cloud.getCloud().getPointAt(j).getBorderColor());
					g2.draw(cloud.getPoints()[j].getShape());
				}
			}
			
			if (cloud.getCloud().getType() == CloudType.STICK_CHART)
				continue;
			
			g2.setStroke(cloud.getStroke());
			g2.setColor(cloud.getCloud().getBorderColor());
			g2.drawPolyline(cloud.getX(), cloud.getY(), cloud.getCloud().countPoints());
		}
		paintGrid(g2);
		
		if(xlineAxis != null || ylineAxis != null)
			g2.setColor(lineAxisColor);
		
		if(xlineAxis  != null){
			g2.draw(xlineAxis);
			g2.fillPolygon(fleshXlineAxis[0], fleshXlineAxis[1], 3);
		}
		
		if(ylineAxis  != null){
			g2.draw(ylineAxis);
			g2.fillPolygon(fleshYlineAxis[0], fleshYlineAxis[1], 3);
		}
		
		if (mouseLocation != null ) {
			if (mouseLineVisible) {
				g2.setStroke(mouseLineWidth);
				g2.setColor(mouseLineColor);
				int x = (int) mouseLocation.getX(), y = (int) mouseLocation.getY();
				g2.drawLine(0, y, getWidth(), y);
				g2.drawLine(x, 0, x, getHeight());
				showAxisLabel(mouseLocation, g2);
			}
			
			if (hoverChart != -1 && hoverPoint != -1) {
				PreparedPointCloud c = chartMetadatas.get(hoverChart);
				PreparedMaterialPoint p = chartMetadatas.get(hoverChart).getPoints()[hoverPoint];
				g2.setColor(c.getCloud().getBorderColor());
				g2.fill(p.getShape());
				drawPopupLabel(c, p, mouseLocation, g2);
			}
		}
	}
	
	/**
	 * visialisation des label sur les axes
	 * @param point
	 * @param g2
	 */
	private void showAxisLabel (Point2D point, Graphics2D g2) {
		
		FontMetrics metrics = g2.getFontMetrics();
		String xTxt = model.getXAxis().getLabelOf(mouseXvalue);
		String yTxt = model.getYAxis().getLabelOf(mouseYvalue);
		
		float 
			p = 4,//padding
			h = metrics.getHeight() + p * 2;//height
		
		double 
			recXw = metrics.stringWidth(xTxt) + p * 2,
			recYw = metrics.stringWidth(yTxt) + p * 2;
		
		float 
			xRecX = (float) (point.getX() - recXw/2), 
			yRecX = getHeight() - paddingBottom + metrics.getHeight()/2f;
		
		float 
			xRecY = 0, 
			yRecY = (float) (point.getY() - metrics.getHeight());
		
		recMouseXvalue.setRoundRect(xRecX, yRecX, recXw, h, p * 2, p * 2);
		recMouseYvalue.setRoundRect(xRecY, yRecY, recYw, h, p * 2, p * 2);
		
		g2.setColor(mouseLineColor);
		g2.fill(recMouseXvalue);
		g2.fill(recMouseYvalue);
		
		g2.setColor(Color.WHITE);
		g2.draw(recMouseXvalue);
		g2.draw(recMouseYvalue);
		
		g2.drawString(yTxt, xRecY + p, yRecY + p + metrics.getAscent());//Y axis
		g2.drawString(xTxt, xRecX + p, yRecX + p + metrics.getAscent());//X axis
	}
	
	/**
	 * rendu du label de survole sur un point
	 * @param data
	 * @param g2
	 */
    private void drawPopupLabel(PreparedPointCloud chart, PreparedMaterialPoint data, Point2D point, Graphics2D g2) {

		float x = (float) point.getX() + 10f;//x par defaut
		float y = (float) point.getY();//y par defaut
		
    	final float labelPadding = 4f;

        
        //h1: titre de niveau 1
        //h2: titre de niveau 2
        String h2 = chart.getCloud().getTitle();
        String h1 = data.getPoint().getLabelX()+" ("+data.getPoint().getLabelY()+")";
       
        FontMetrics fmH1 = g2.getFontMetrics(getFont().deriveFont(Font.PLAIN, g2.getFont().getSize2D()));
        FontMetrics fmH2 = g2.getFontMetrics(getFont().deriveFont(Font.BOLD, g2.getFont().getSize2D()));
        Rectangle2D r1 = fmH1.getStringBounds(h1, g2);
        Rectangle2D r2 = fmH1.getStringBounds(h2, g2);
        
        double widthH2 = r2.getWidth() + (labelPadding * 2),
        		widthH1 = r1.getWidth() + (labelPadding * 2);
        double width = Math.max(widthH1, widthH2);
        
        double height = r1.getHeight() + r2.getHeight() + labelPadding * 2;
        double recY = (y + height) <= (getHeight() - paddingBottom)?  y : y - height;
        double recX = (x + width) <= (getWidth() - padding)?  x : x - width;
        
        
        Color c = new Color(0x99000000 & getBackground().getRGB(), true);
        g2.setColor(c);
        RoundRectangle2D rec = new RoundRectangle2D.Double(recX, recY, width, height, 5, 5);
        
        g2.fill(rec);
        g2.setColor(Color.WHITE);
        g2.draw(rec);
        g2.setFont(getFont().deriveFont(Font.PLAIN, g2.getFont().getSize2D()));
        g2.drawString(h1, (float)recX + labelPadding, (float) (recY + fmH1.getAscent()));
        g2.setFont(getFont().deriveFont(Font.BOLD, g2.getFont().getSize2D()));
        g2.drawString(h2, (float)recX + labelPadding, (float) (recY + height - r2.getHeight() + fmH2.getAscent()));
    }
	
	private void paintGrid (Graphics2D g) {
		if((!gridXvisible && !gridYvisible) || model.getXMax() == null || model.getYMax() == null)
			return;
		
		g.setStroke(gridStroke);
		g.setColor(gridColor);
		Font font = g.getFont();
		FontMetrics metrics = g.getFontMetrics(font);
		
		if(gridYvisible) {
			if(xlineAxis != null) {//pour le cas où l'axe est visible
				int y = 0;
				double percentAxis = (100f / heightRender) * (xlineAxis.getY1() - padding);
				for (double i = xlineAxis.getY1(); i > 0; i -= gridYstep) {
					y = (int) i;
					double percent = (100f / heightRender) * (i - padding);
					double real = Math.abs(percent - percentAxis) * (model.getYMax().getY() / percentAxis);
					if(real == 0 || y < padding)
						continue;
					
					String txt = model.getYAxis().getLabelOf(real);
					g.setColor(gridColor);
					g.drawLine((int)(paddingLeft - padding/2), y, (int)(getWidth() - (padding/2)), y);
					g.setColor(model.getBorderColor());
					g.drawString(txt, 0f, y + metrics.getHeight() / 4);
				}
				
				y = 0;
				for (double i = xlineAxis.getY1(); y <= getHeight(); i += gridYstep) {
					y =  (int) i;
					double percent = (100f / heightRender) * (i - padding);
					double real = -Math.abs(percent - percentAxis) * (model.getYMax().getY() / percentAxis);
					if(real == 0 || y > (getHeight() - paddingBottom))
						continue;
					
					String txt = model.getYAxis().getLabelOf(real);
					g.setColor(gridColor);
					g.drawLine((int)(paddingLeft - padding/2), y, (int)(getWidth() - (padding/2)), y);
					g.setColor(model.getBorderColor());
					g.drawString(txt, 0f, y + metrics.getHeight() / 4);
				}
				
				if (mouseLocation != null) {//lors du survol de la sourie
					double percent = (100f / heightRender) * (mouseLocation.getY() - padding);
					mouseYvalue = Math.abs(percent - percentAxis) * (model.getYMax().getY() / percentAxis);
					if(mouseLocation.getY() > xlineAxis.getY1()) {
						mouseYvalue *= -1;
					} 
				}
			} else {//dans le cas où l'axe n'est pas visible
				for (double i = heightRender + padding; i >= 0; i -= gridYstep) {
					int y = (int)(i);
					
					if (y < padding)
						continue;
					
					double percent = (100f / heightRender) * (i - padding);
					double real = Math.abs(percent - 100f) * ((model.getYMax().getY() - model.getYMin().getY()) / 100f);
					real += model.getYMin().getY();
					
					String txt = model.getYAxis().getLabelOf(real);
					g.setColor(gridColor);
					g.drawLine((int)(paddingLeft - padding/2), y, (int)(getWidth() - (padding/2)), y);
					g.setColor(model.getBorderColor());
					g.drawString(txt, 0f, y + metrics.getHeight() / 4);
				}
				
				if (mouseLocation != null) {//lors du survol de la sourie
					double percent = (100f / heightRender) * (mouseLocation.getY() - padding);
					mouseYvalue = Math.abs(percent - 100f) * ((model.getYMax().getY() - model.getYMin().getY()) / 100f);
					mouseYvalue += model.getYMin().getY();
				}
			}
		}
		
		if(gridXvisible) {			
			if(ylineAxis != null) {	
				double percentAxis = (100f / widhtRender) * (ylineAxis.getX1() - padding);
				for (double i = ylineAxis.getX1(); i > 0; i -= gridXstep) {
					int x = (int)i;
					double percent = (100f / widhtRender) * (i - padding);
					double real = -Math.abs(percent - percentAxis) * (model.getXMax().getX() / percentAxis);
					
					if(real == 0 || x < paddingLeft)
						continue;
					String txt = model.getXAxis().getLabelOf(real);
					g.setColor(gridColor);
					g.drawLine(x, (int)padding/2, x, (int) (getHeight() - paddingBottom + padding/2));
					g.setColor(model.getBorderColor());
					g.drawString(txt, x - metrics.stringWidth(txt) / 2, getHeight() - padding/2f);
				}
				
				for (double i = ylineAxis.getX1(); i < getWidth(); i += gridXstep) {
					int x = (int)i;
					double percent = (100f / widhtRender) * (i - padding);
					double real = Math.abs(percent - percentAxis) * (model.getXMax().getX() / percentAxis);
					
					if(real == 0 || x > (getWidth() - padding))
						continue;
					
					String txt = model.getXAxis().getLabelOf(real);
					g.setColor(gridColor);
					g.drawLine(x, (int)padding/2, x, (int) (getHeight() - paddingBottom + padding/2));
					g.setColor(model.getBorderColor());
					g.drawString(txt, x - metrics.stringWidth(txt) / 2, getHeight() - padding/2f);
				}
				
				if (mouseLocation != null) {//lors du survol de la sourie
					double percent = (100f / widhtRender) * (mouseLocation.getX() - padding);
					mouseXvalue = Math.abs(percent - percentAxis) * (model.getXMax().getX() / percentAxis);
					if(mouseLocation.getX() < ylineAxis.getX1()) {
						mouseXvalue *= -1;
					} 
				}
			} else {
				for (double i = widhtRender + padding; i >= 0 ; i -= gridXstep) {
					int x = (int)i;
					
					if (x < paddingLeft)
						continue;
					
					double percent = (100f / widhtRender) * (i - padding);
					double real = Math.abs(percent) * ((model.getXMax().getX() - model.getXMin().getX()) / 100f);
					real += model.getXMin().getX();
					
					String txt = model.getXAxis().getLabelOf(real);
					g.setColor(gridColor);
					g.drawLine(x, (int)padding/2, x, (int) (getHeight() - paddingBottom + padding/2));
					g.setColor(model.getBorderColor());
					g.drawString(txt, x - metrics.stringWidth(txt) / 2, getHeight() - padding/2f);
				}
				
				if (mouseLocation != null) {//lors du survol de la sourie
					double percent = (100f / widhtRender) * (mouseLocation.getX() - padding);
					mouseXvalue = Math.abs(percent) * ((model.getXMax().getX() - model.getXMin().getX()) / 100f);
					mouseYvalue += model.getXMin().getX();
				}
			}
		}
	}
	
	/**
	 * utilitaire de preparation du rendu du graphique
	 */
	private synchronized void prepareRender () {
		chartMetadatas.clear();
		
		widhtRender = getWidth() - (padding + paddingLeft);
		heightRender = getHeight() - (padding + paddingBottom);
		
//		gridXstep = widhtRender / 10;
//		gridYstep = heightRender / 10;
		
		double xMax = model.hasVisibleChart()? model.getXMax().getX() : 10;
		double yMax = model.hasVisibleChart()? model.getYMax().getY() : 10;
		
		double xMin = model.hasVisibleChart()? model.getXMin().getX() : - xMax;
		double yMin = model.hasVisibleChart()? model.getYMin().getY() : - yMax;
		
		final double absXmax = Math.abs(xMax);
		final double absYmax = Math.abs(yMax);
		final double absXmin = Math.abs(xMin);
		final double absYmin = Math.abs(yMin);
		
		xRation = widhtRender / Math.max(absXmin, absXmax);
		yRation = heightRender / Math.max(absYmin, absYmax);
		
		double yXline = -1;// le y de l'axe des X
		double xYline = -1;// le x de l'axe des Y
		double maxX = 0;
		double maxY = 0;
		
		//recherche de la valeur a translater sur X
		if (xMax < 0) {//tout les X sont dans R-
			translateX = absXmin;
			maxX =Math.max(xMin + translateX, xMax + translateX);
		} else if (xMin > 0) {//tout les X sont dans R+
			translateX = -absXmin;
			maxX = Math.max(xMin + translateX, xMax + translateX);
		} else {//pour R*
			translateX = absXmin;
			maxX = Math.max(absXmin, absXmax) * 2;
			xYline = (widhtRender / maxX) * translateX + paddingLeft; // xRation...
		}
		xRation = widhtRender / maxX;
		//==
		
		//recherche de la valeur a translater sur Y
		if (yMax < 0) {//tous les Y sont dans R-
			translateY = absYmin ;
			maxY = Math.max(yMin + translateY, yMax + translateY);
		} else if (yMin > 0) {//tous les Y sont dans R+
			translateY = -absYmin;
			maxY = Math.max(yMin + translateY, yMax + translateY);
		} else {//pour R*
			translateY = absYmin;
			maxY = absYmax + absYmin;
			yXline = (-(heightRender / maxY) * translateY) + heightRender + padding;//-yRation ...
		}
		yRation = heightRender / maxY;
		//
		
		if (yXline != -1) {//l'axe des X est visible
			xlineAxis = new Line2D.Double(0, yXline, getWidth(), yXline);
			int h = (int) (padding/4f), w = (int) (padding/2f);
			fleshXlineAxis[0][0] = getWidth() - w;
			fleshXlineAxis[0][1] = getWidth();
			fleshXlineAxis[0][2] = getWidth() - w;
			fleshXlineAxis[1][0] = (int)(xlineAxis.getP1().getY() - h);
			fleshXlineAxis[1][1] = (int)(xlineAxis.getP1().getY());
			fleshXlineAxis[1][2] = (int)(xlineAxis.getP1().getY() + h);
		} else xlineAxis = null;
		
		if (xYline != -1) {//l'axe des Y est visible
			ylineAxis = new Line2D.Double(xYline, 0, xYline, getHeight());
			int w = (int) (padding/4f), h = (int) (padding/2f);
			fleshYlineAxis[0][0] = (int)(ylineAxis.getP1().getX() - w);
			fleshYlineAxis[0][1] = (int)(ylineAxis.getP1().getX());
			fleshYlineAxis[0][2] = (int)(ylineAxis.getP1().getX() + w);
			fleshYlineAxis[1][0] = h;
			fleshYlineAxis[1][1] = 0;
			fleshYlineAxis[1][2] = h;
		} else ylineAxis = null;
		
		for (int i = 0, count = model.getSize(); i < count; i++) {
			if (!model.getChartAt(i).isVisible() || model.getChartAt(i).countPoints() == 0)
				continue;
			
			PreparedPointCloud  cloud = null;
			if (model.getChartAt(i).getType() == CloudType.STICK_CHART)
				cloud = createPreparedStickChart(model.getChartAt(i));
			else 
				cloud = createPreparedLineChart(model.getChartAt(i));
			
			chartMetadatas.add(cloud);
		}
		
		//determination du bordure
		borderRect.setRect(paddingLeft - padding/2, padding/2, widhtRender + padding, heightRender + padding);
	}
	
	/**
	 * normalisation d'un point.
	 * vue que le repere de l'ecran d'un ordinateur ne sont pas identique a ceux utiliser courament en Math
	 * (en math Y+ -> vers le haut, alors que pour l'ecran d'un PC Y+ -> vers le bas)
	 * pour Y on cherche la valeur negative des coordonnee d'un point sur Y, puis on fait un translation de la hauteur disponible.
	 * En plus pour eviter des  surprise (Ex: pour des graphique qui contiens des valeur tres elevee ou dea valeurs tres inferieur a 0)
	 * on prefere translater le graphique, de sorte que la plus petite valeur sur les axes soit = a zero
	 * @param point
	 * @return
	 */
	protected MaterialPoint normalize (MaterialPoint point) {
		MaterialPoint p = new DefaultMaterialPoint(point);
		double x = (xRation * (point.getX() + translateX)) + paddingLeft;
		double y = (-yRation * (point.getY() + translateY)) + heightRender + padding;
		p.translateXY(x, y);
		return p;
	}
	
	/**
	 * @param chart
	 * @return
	 */
	private PreparedPointCloud createPreparedLineChart (PointCloud chart) {
		
		PreparedMaterialPoint [] list = new PreparedMaterialPoint[chart.countPoints()];
		MaterialPoint []  points = chart.getPoints();
		int length = points.length+2;
		int [] xs = new int[length];
		int [] ys = new int[length];
		
		final float space = chart.getBorderWidth() / 2.0f;
		
		for (int  i = 0; i < points.length; i++) {
			MaterialPoint point = normalize(points[i]);
			
			xs[i] = (int) (point.getX() - space);
			ys[i] = (int) (point.getY() - space);
			
			double eX = xs[i] - points[i].getSize() / 2.0f;
			double eY = ys[i] - points[i].getSize() / 2.0f;
			
			Ellipse2D e = new Ellipse2D.Double(eX, eY, points[i].getSize(), points[i].getSize());
			list[i] = new PreparedMaterialPoint(e, point);
		}
		
		Polygon poly  = null;
		
		if (chart.isFill()) {
			xs[length-2] = xs[length-3];
			xs[length-1] = xs[0];
			
			int y = (int) (xlineAxis != null? (xlineAxis.getP1().getY()) : (chart.getYMax().getY() > 0 ? heightRender : 0) + padding );
			ys[length-2] = y;
			ys[length-1] = y;
			
			poly = new Polygon(xs, ys, length);
		} 

		return new PreparedPointCloud(poly, chart, list, xs, ys);
	}
	
	private PreparedPointCloud createPreparedStickChart (PointCloud chart) {
		PreparedMaterialPoint [] list = new PreparedMaterialPoint[chart.countPoints()];
		MaterialPoint []  points = chart.getPoints();
		int length = points.length+2;
		int [] xs = new int[length];
		int [] ys = new int[length];
		
		double size = xRation * chart.getDefaultPointSize();
		double limit = xlineAxis == null? (model.getYMin().getY() > 0? 0 : heightRender) : xlineAxis.getY1();
		
		for (int  i = 0; i < points.length; i++) {
			MaterialPoint point = normalize(points[i]);
			
			xs[i] = (int) point.getX();
			ys[i] = (int) point.getY();
			
			double h = 0;
			if(ys[i] <= limit)
				h = (heightRender - (translateY * yRation)) - ys[i] + padding;
			else 
				h =  ys[i] - (heightRender - (translateY * yRation)) - padding;
			
			Rectangle2D rect = new Rectangle2D.Double(xs[i] - (size/2), ys[i] > limit? limit : ys[i], size, h);
			
			Area area = new Area(rect);			
			list[i] = new PreparedMaterialPoint(area, point);
		}
		
		Polygon poly  = null;
		
		if (chart.isFill()) {
			xs[length-2] = xs[length-3];
			xs[length-1] = xs[0];
			
			int y = (int) (xlineAxis != null? (xlineAxis.getP1().getY()) : (chart.getYMin().getY() < chart.getYMax().getY()? heightRender : 0) + padding );
			ys[length-2] = y;
			ys[length-1] = y;
			
			poly = new Polygon(xs, ys, length);
		} 

		return new PreparedPointCloud(poly, chart, list, xs, ys);
	}

	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	protected static class PreparedMaterialPoint {
		
		private Shape shape;
		private MaterialPoint point;
		
		
		/**
		 * @param shape
		 * @param point
		 */
		public PreparedMaterialPoint(Shape shape, MaterialPoint point) {
			super();
			this.shape = shape;
			this.point = point;
		}


		/**
		 * verification de l'appartenance du point Geometrique au point materiel
		 * @param M
		 * @return
		 */
		public boolean match (Point M) {
			return shape.contains(M);
		}


		/**
		 * @return the shape
		 */
		public Shape getShape() {
			return shape;
		}


		/**
		 * @return the point
		 */
		public MaterialPoint getPoint() {
			return point;
		}
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	protected static class PreparedPointCloud {
		
		private final Shape area;
		private final PointCloud cloud;
		private final PreparedMaterialPoint [] points;
		private final BasicStroke stroke;
		
		private final int [] x;
		private final int [] y;
		
		/**
		 * @param area
		 * @param cloud
		 * @param points
		 * @param x
		 * @param y
		 */
		public PreparedPointCloud(Shape area, PointCloud cloud, PreparedMaterialPoint[] points, int[] x, int[] y) {
			super();
			this.area = area;
			this.cloud = cloud;
			this.points = points;
			this.x = x;
			this.y = y;
			stroke = new BasicStroke(cloud.getBorderWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		}


		/**
		 * Verifie si la ligne apartiens a la ligne de bordure du nuage de point
		 * @param M
		 * @return
		 */
		public boolean match (Point M) {
			if(area == null)
				return false;
			
			return area.contains(M);
		}


		/**
		 * @return the points
		 */
		public PreparedMaterialPoint[] getPoints() {
			return points;
		}


		/**
		 * @return the area
		 */
		public Shape getArea() {
			return area;
		}


		/**
		 * @return the cloud
		 */
		public PointCloud getCloud() {
			return cloud;
		}


		/**
		 * @return the x
		 */
		public int[] getX() {
			return x;
		}


		/**
		 * @return the y
		 */
		public int[] getY() {
			return y;
		}


		/**
		 * @return the stroke
		 */
		public BasicStroke getStroke() {
			return stroke;
		}
		
	}
	
	/**
	 * Ecouteur des changement du model
	 * @author Esaie MUHASA
	 *
	 */
	protected class ModelListener implements CloudChartModelListener {

		@Override
		public void onChange(CloudChartModel model) {
			prepareRender();
			repaint();
		}

		@Override
		public void onChartChange(CloudChartModel model, int index) {
			onChange(model);
		}

		@Override
		public void onPointChange(CloudChartModel model, int chartIndex, int pointIndex) {
			onChange(model);
		}

		@Override
		public void onInsertChart(CloudChartModel model, int chartIndex) {
			onChange(model);
		}

		@Override
		public void onRemoveChart(CloudChartModel model, int chartIndex) {
			onChange(model);
		}

		@Override
		public void onInsertPoint(CloudChartModel model, int chartIndex, int pointIndex) {
			onChange(model);
		}

		@Override
		public void onRemovePoint(CloudChartModel model, int chartIndex, int pointIndex, MaterialPoint materialPoint) {
			onChange(model);
		}
		
		
	}
	
	protected class MouseListener extends MouseAdapter {
		
		@Override
		public void mouseExited(MouseEvent e) {
			mouseLocation = null;
			
			repaint();
		}
		
		@Override
		public void mouseMoved (MouseEvent e) {
			if (borderRect.contains(e.getPoint()) && model.getSize() != 0) {				
				mouseLocation = e.getPoint();
				boolean match = false;
				hoverChart = -1;
				hoverPoint = -1;
				
				for (int i = 0; i < chartMetadatas.size(); i++) {
					PreparedPointCloud c = chartMetadatas.get(i);
					
					for (int j = 0; j < c.getPoints().length; j++) {
						if(c.getPoints()[j].getShape().contains(e.getPoint())) {
							hoverChart = i;
							hoverPoint = j;
							match = true;
							break;
						}
					}
					
					if (match)
						break;
				}
				
				repaint();
			} else 
				mouseExited(e);
		}
	}

}
