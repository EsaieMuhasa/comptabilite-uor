package net.uorbutembo.swing.charts;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class PieRender extends JComponent implements PieModelListener{
	private static final long serialVersionUID = -1944742088465191107L;
	
	private static final BasicStroke LINE_STROKE = new BasicStroke(1.2f);
	
	private PieModel model;
	private int radius;//le rayons d'un cercle
	private int minRaduis;//le raduis du milieux
	private final List<LineSegment> segments = new ArrayList<>();
	private final List<PiePartInfo> parts = new ArrayList<>();
	
	private boolean hovable = false;//esque ce diagramme est hovable?
	private Point center;
	private final MouseListener ls = new MouseListener();
	
	public PieRender() {
		this.addMouseListener(ls);
		this.addMouseMotionListener(ls);
	}

	/**
	 * @param model
	 */
	public PieRender(PieModel model) {
		this.model = model;
		
		if(model != null){
			model.addListener(this);
		}
		
		this.addMouseListener(ls);
		this.addMouseMotionListener(ls);
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		this.prepareRender();
	}
	
	/**
	 * @return the hovable
	 */
	public boolean isHovable() {
		return hovable;
	}

	/**
	 * @param hovable the hovable to set
	 */
	public void setHovable (boolean hovable) {
		this.hovable = hovable;
	}

	@Override
	protected synchronized void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		g2.translate(center.x, center.y);//translation des axes au centre de la vue

		int reelRadius = radius;
		for (PiePartInfo info : parts) {
			PiePart part  = info.getPart();
			reelRadius = radius + (model.getSelectedIndex() == model.indexOf(info.getPart())? 5 : 0);
			
			g2.setColor(part.getBackgroundColor());
			g2.fillArc(-reelRadius, -reelRadius, reelRadius*2, reelRadius*2, info.getStart(), info.getDegre());				
			
		}
		
		g2.setColor(getBackground());
		g2.setStroke(LINE_STROKE);
		for (LineSegment segment : segments) {
			g2.drawLine((int)segment.getStart().getX(), (int)segment.getStart().getY(), (int)segment.getEnd().getX(), (int)segment.getEnd().getY());
		}
		
		g2.setColor(getBackground());
		int xy = -radius/3;
		g2.fillOval(xy, xy, minRaduis, minRaduis);
		
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
		if(this.model != model) {
			if(this.model != null )
				this.model.removeListener(this);
			this.model = model;
			this.model.addListener(this);
		}
	}
	
	/**
	 * utilitaire de preparation du rendu du graphique
	 */
	private synchronized void prepareRender () {
		segments.clear();
		parts.clear();
		
		int translateX = (this.getWidth()/2),
				translateY = (this.getHeight()/2);
		
		this.center = new Point(translateX, translateY);
		
		//on prend la plus petie valeur entre la hauteur et la largeur de la vue
		this.radius = ((this.getWidth() < this.getHeight())?  this.getWidth() : this.getHeight())/2 - 10;
		this.minRaduis = (radius * 2) /3;
		int start = 0;

		Point O = new Point(radius+6, 0);//6 pour les segments ne soient pas invisible pour les bordure du pie selectionner
		Point center = new Point(0, 0);
		if(model == null) {
			return;
		}
		for (PiePart part : model.getParts()) {
			BigDecimal big = new BigDecimal((360.0 / 100.0) * model.getPercentOf(part)).setScale(0, RoundingMode.HALF_UP);
			int toDegre = big.intValue();

			double toRad = -(big.doubleValue()+start) * (Math.PI / 180);//en rad
			int rotOx = new BigDecimal((O.getX() * Math.cos(toRad)) - (O.getY() * Math.sin(toRad))).setScale(0, RoundingMode.HALF_UP).intValue();
			int rotOy = new BigDecimal((O.getX() * Math.sin(toRad)) + (O.getY() * Math.cos(toRad))).setScale(0, RoundingMode.HALF_UP).intValue();
			
			PiePartInfo info =new PiePartInfo(part, start, toDegre, toDegre, toRad) ;
			
			Point P1 = new Point(rotOx, rotOy);
			LineSegment segment = new LineSegment(P1, center);
			
			if(!segments.isEmpty()) {//recuperation du dernier point
				int index = segments.size()-1;
				Point P2 = segments.get(index).getStart();
				parts.get(index).initPoints(P2, P1);
			}
			
			segments.add(segment);
			parts.add(info);
			start += toDegre;
		}
	}

	@Override
	public void refresh(PieModel model) {
		this.prepareRender();
		this.repaint();
	}
	
	@Override
	public void onSelectedIndex(PieModel model, int oldIndex, int newIndex) {
		repaint();
	}

	@Override
	public void repaintPart(PieModel model, int partIndex) {
		this.prepareRender();
		this.repaint();
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	private class MouseListener extends MouseAdapter{

		@Override
		public void mouseExited(MouseEvent e) {
			if(!hovable)
				return;
			model.setSelectedIndex(-1);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if(!hovable)
				return;
			
			Point point = e.getPoint();
			double X = point.getX()-center.getX(),
					Y = point.getY()-center.getY();
			
			double distance = Math.pow(X, 2) + Math.pow(Y, 2);
			distance = Math.sqrt(distance);

			boolean circleHover = distance <= radius && distance >= minRaduis/2; //on entre dans la surface du cerlce
			
			if(circleHover) {
				//recherche des cordonnee de la sourie pour le repere translater
				double 
					translatedX = point.getX() > center.getX() ? Math.abs(X) : -Math.abs(X), 
					translatedY = point.getY() > center.getY() ? Math.abs(Y) : -Math.abs(Y);
				
				Point translatedPoint  = new Point((int)translatedX, (int)translatedY);
				int index = -1;
				for (PiePartInfo info : parts) {
					if(info.match(translatedPoint)) {
						index = model.indexOf(info.getPart());
						break;
					}
				}
				
				model.setSelectedIndex(index);
			} else {
				mouseExited(e);
			}
		}
		
	}
	
	protected class PiePartInfo {
		
		private int start;//l'angle de depart en degree
		private int end;//l'angle d'arriver en degree
		
		private int degre;
		private double rad;
		
		private Point a;
		private Point b;
		
		private PiePart part;
		
		/**
		 * @param start
		 * @param end
		 * @param percent
		 * @param degre
		 * @param rad
		 * @param a
		 * @param b
		 */
		public PiePartInfo(PiePart part, int start, int end, int degre, double rad) {
			super();
			this.part = part;
			this.start = start;
			this.end = end;
			this.degre = degre;
			this.rad = rad;
		}
		
		public void initPoints (Point a, Point b) {
			this.a = a;
			this.b = b;
		}
		
		public boolean match (Point M) {			
			if(a == null || b == null)
				return false;
			
			double a = Math.sqrt(Math.pow((radius - M.getX()), 2) + Math.pow(M.getY(), 2));
			double b = radius;
			double c = Math.sqrt(Math.pow(M.getX(), 2) + Math.pow(M.getY(), 2));
			
			double cosA = ((a * a) - ( (b * b) + (c * c))) / (-2 * a *c);//consinus de l'engle (theoreme Alkashi triangle quelconque)
			double alpha = Math.acos(cosA);//
			try {				
				BigDecimal big = new BigDecimal(alpha * (180 / Math.PI)).setScale(0, RoundingMode.HALF_UP);
				int alphaToDeg = big.intValue();
				//System.out.println("cos(A) = "+cosA+" => A = "+alpha+" rad = "+alphaToDeg+"°");
				
				int start = getStart(), end = getEnd()+getStart();
				if (start <= alphaToDeg && end >= alphaToDeg) {
					return true;
				}
			} catch (NumberFormatException e) {
				//System.out.println(">>> cos(A) = "+cosA+" => A = "+alpha+" rad");
			}
			
			return false;
		}
		
		/**
		 * @return the start en degre
		 */
		public int getStart() {
			return start;
		}
		
		public double getStartToRadian () {
			double angle = start;
			return angle * Math.PI / 180.0;
		}
		/**
		 * @return the end
		 */
		public int getEnd() {
			return end;
		}
		
		public double getEndToRadian () {
			double angle = end;
			return angle * Math.PI / 180.0;
		}

		/**
		 * @return the degre
		 */
		public int getDegre() {
			return degre;
		}
		/**
		 * @return the rad
		 */
		public double getRad() {
			return rad;
		}
		/**
		 * @return the a
		 */
		public Point getA() {
			return a;
		}
		
		/**
		 * @return the b
		 */
		public Point getB() {
			return b;
		}

		/**
		 * @return the part
		 */
		public PiePart getPart() {
			return part;
		}
		
	}

}
