/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class Point2d extends AbstractChartData implements Point {
	
	protected final List<PointListener> listeners = new ArrayList<>();
	
	private double x;
	private double y;
	private int size;
	private String label;
	
	/**
	 * @param x
	 * @param y
	 */
	public Point2d(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Point2d (Point point) {
		super(point.getBackgroundColor(), point.getForegroundColor(), point.getBorderColor());
		setBorderWidth(point.getBorderWidth());
		x = point.getX();
		y = point.getY();
		size = point.getSize();
		data = point.getData();
		label = point.getLabel();
	}

	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param borderColor
	 */
	public Point2d(Color backgroundColor, Color foregroundColor, Color borderColor) {
		super(backgroundColor, foregroundColor, borderColor);
	}

	/**
	 * @param backgroundColor
	 */
	public Point2d(Color backgroundColor) {
		super(backgroundColor);
	}

	/**
	 * @param x
	 * @param y
	 * @param size
	 */
	public Point2d(double x, double y, int size) {
		super();
		this.x = x;
		this.y = y;
		this.size = size;
	}

	/**
	 * @param x the x to set
	 */
	public synchronized void setX(double x) {
		if(this.x == x)
			return;
		
		this.x = x;
		emitOnChange();
	}

	/**
	 * @param y the y to set
	 */
	public synchronized void setY(double y) {
		if(this.x == y)
			return;
		
		this.y = y;
		emitOnChange();
	}

	@Override
	public void translate(double x, double y, double z) {
		if(this.x == x && this.y == y)
			return;
		
		this.x = x;
		this.y = y;
		emitOnChange();
	}

	/**
	 * @param size the size to set
	 */
	public synchronized void setSize(int size) {
		if(this.size == size)
			return;
		
		this.size = size;
		emitOnChange();
	}

	@Override
	public double getX () {
		return x;
	}

	@Override
	public double getY () {
		return y;
	}

	@Override
	public double getZ() {
		return 0;
	}

	@Override
	public double getRoundX(int decimal) {
		BigDecimal big = new BigDecimal(x).setScale(decimal, RoundingMode.FLOOR);
		return big.doubleValue();
	}

	@Override
	public double getRoundY(int decimal) {
		BigDecimal big = new BigDecimal(y).setScale(decimal, RoundingMode.FLOOR);
		return big.doubleValue();
	}

	@Override
	public double getRoundZ(int decimal) {
		return 0;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void addListener(PointListener listener) {
		if(!listeners.contains(listener) && listener != null)
			listeners.add(listener);
	}

	@Override
	public boolean removeListener(PointListener listener) {
		return listeners.remove(listener);
	}
	
	protected synchronized void emitOnChange () {
		for (PointListener ls : listeners)
			ls.onChange(this);
	}

	@Override
	public String toString() {
		return "Point2d [x=" + x + ", y=" + y + "]";
	}

	@Override
	public boolean equals (Object obj) {
		if(obj instanceof Point) {
			Point point = (Point) obj;
			return point.getX() == x && point.getY() == y;
		}
		
		return super.equals(obj);
	}

}
