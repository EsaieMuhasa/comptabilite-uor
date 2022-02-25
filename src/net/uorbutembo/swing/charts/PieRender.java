package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JComponent;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class PieRender extends JComponent implements PieModelListener{
	private static final long serialVersionUID = -1944742088465191107L;
	
	private PieModel model;
	
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
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		//on prende la plus petie valeur entre la hauteur et la largeur de la vue
		int ration = (this.getWidth() < this.getHeight())?  this.getWidth() : this.getHeight();
		
		ration -= 30;
		
		int start = 0;
		int translateX = (this.getWidth()/2) - (ration/2),
				translateY = (this.getHeight()/2) - (ration/2);
		
		g2.translate(translateX, translateY);
		g2.setColor(Color.BLACK);
		g2.drawOval(0, 0, ration, ration);
		for (PiePart part : model.getParts()) {
			double toPercent = (100 / max) * part.getValue();//valeur du part en pourcent
			BigDecimal big = new BigDecimal((360.0 / 100.0) * toPercent).setScale(0, RoundingMode.HALF_UP);
			int toDegre = big.intValue();
//			System.out.println("\t* "+part.getValue()+" -> "+toDegre+" -> "+toPercent+" %");
			g2.setColor(part.getBackgroundColor());
			g2.fillArc(0, 0, ration, ration, start, toDegre);
			start += toDegre;
		}
		int x = ration/3;
		g2.setColor(getBackground());
		g2.fillOval(x, x, ration/3, ration/3);
		
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
