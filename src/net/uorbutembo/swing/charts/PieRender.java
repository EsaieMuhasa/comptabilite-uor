package net.uorbutembo.swing.charts;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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
	
	private PieModel model;
	private int radius;//le rayons d'un cercle
	private List<LineSegment> segments = new ArrayList<>();
	
	public PieRender() {}

	/**
	 * @param model
	 */
	public PieRender(PieModel model) {
		this.model = model;
		
		if(model != null){
			model.addListener(this);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//en premier vue, la somme de part ne doit pas depaser le max
		double somme = 0;
		double max = model.getMax();
		for (PiePart part : model.getParts()) {
			somme += part.getValue();
		}
		
		
		if(somme > model.getMax()) {
			max = somme;
		}
		segments.clear();
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		//on prende la plus petie valeur entre la hauteur et la largeur de la vue
		this.radius = ((this.getWidth() < this.getHeight())?  this.getWidth() : this.getHeight())/2 - 30;
		
		
		int start = 0;
		int translateX = (this.getWidth()/2),
				translateY = (this.getHeight()/2);
//		center = new Point(translateX, translateY);
		
		g2.translate(translateX, translateY);
		
		Point O = new Point(radius, 0);
		
		for (PiePart part : model.getParts()) {
			double toPercent = (100 / max) * part.getValue();//valeur du part en pourcent
			BigDecimal big = new BigDecimal((360.0 / 100.0) * toPercent).setScale(0, RoundingMode.HALF_UP);
			int toDegre = big.intValue();
//			System.out.println("\t* "+part.getValue()+" -> "+toDegre+" -> "+toPercent+" %");
			g2.setColor(part.getBackgroundColor());
			g2.fillArc(-radius, -radius, radius*2, radius*2, start, toDegre);

			double toRad = -(big.doubleValue()+start) * (Math.PI / 180);//en rad
			int rotOx = new BigDecimal((O.getX() * Math.cos(toRad)) - (O.getY() * Math.sin(toRad))).setScale(0, RoundingMode.HALF_UP).intValue();
			int rotOy = new BigDecimal((O.getX() * Math.sin(toRad)) + (O.getY() * Math.cos(toRad))).setScale(0, RoundingMode.HALF_UP).intValue();
			
			LineSegment segment = new LineSegment(new Point(rotOx, rotOy), new Point(0, 0));
			segments.add(segment);
			start += toDegre;
		}
		
		g2.setColor(this.getBackground());
		g2.setStroke(new BasicStroke(2f));
		for (LineSegment segment : segments) {
			g2.drawLine((int)segment.getStart().getX(), (int)segment.getStart().getY(), (int)segment.getEnd().getX(), (int)segment.getEnd().getY());
		}
		
		g2.setColor(getBackground());
		int xy = -radius/3, minRaduis = (radius * 2) /3;
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
		if(this.model != null && this.model != model) {
			this.model = model;
			this.model.addListener(this);
		}
	}

	@Override
	public void refresh(PieModel model) {
		this.repaint();
	}

	@Override
	public void repaintPart(PieModel model, int partIndex) {
		this.repaint();
	}

}
