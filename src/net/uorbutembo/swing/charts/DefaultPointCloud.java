/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultPointCloud extends AbstractChartData implements PointCloud {

	protected boolean fill = false;
	protected final List<Point> points = new ArrayList<>();
	protected final List<PointCloudListener> listeners = new ArrayList<>();
	
	protected final PointListener pointListener = (point) -> {
		for (PointCloudListener ls : listeners) 
			ls.onPointChange(this, indexOf(point));
	};
	
	private int xMax = -1;
	private int yMax = -1;
	private int xMin = -1;
	private int yMin = -1;
	
	/**
	 * 
	 */
	public DefaultPointCloud() {
		super();
	}

	/**
	 * @param backgroundColor
	 */
	public DefaultPointCloud(Color backgroundColor) {
		super(backgroundColor);
	}

	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param borderColor
	 */
	public DefaultPointCloud(Color backgroundColor, Color foregroundColor, Color borderColor) {
		super(backgroundColor, foregroundColor, borderColor);
	}
	
	/**
	 * Transforme le nuage des point en un signale carret
	 */
	public void toSquareSignal () {
		
		if(countPoints() <= 1 )
			return;

		for (int i = 0, count = (points.size() * 2) - 3; i < count; i += 2) {
			
			if (i > points.size()-3)
				break;
			
			Point p = getPointAt(i);
			Point2d copie = new Point2d(getPointAt(i+1).getX(), p.getY());
			
			if (getPointAt(i+1).getY() == p.getY() && copie.getX() == getPointAt(i+2).getX()){
				i -= 1;
				continue;
			}
			
			copie.setData(getPointAt(i+1).getData());
			points.add(i+1, copie);
		}
		
		Point last = getPointAt(points.size()-1);
		Point beforLast = getPointAt(points.size()-2);
		
		if (last.getY() != beforLast.getY()) {
			double y = last.getY(),
					x = last.getX() + Math.abs(last.getX() - beforLast.getX());
			Point2d copie = new Point2d(x, y);
			points.add(copie);
		}
		
		checkMinMax();
		emitOnChange();
		
	}

	@Override
	public boolean isFill() {
		return fill;
	}

	@Override
	public synchronized void setFill (boolean fill) {
		if(this.fill == fill)
			return;
		
		this.fill = fill;
		emitOnChange();
	}

	@Override
	public void addPoint(Point point) {
		if(points.contains(point))
			return;
			
		points.add(point);
		point.addListener(pointListener);
		emitInsertPoint(points.size()-1);
	}

	@Override
	public void addPoint(Point point, int index) {
		if(points.contains(point))
			return;
			
		points.add(index, point);
		point.addListener(pointListener);
		emitInsertPoint(index);
	}

	@Override
	public int indexOf(Point point) {
		if(point == null)
			return -1;
		return points.indexOf(point);
	}

	@Override
	public void removePointAt(int index) {
		Point point = points.get(index);
		
		point.removeListener(pointListener);
		points.remove(index);		
		
		if (index == xMax){
			xMax = -1;
			checkXMax();
		} else {
			xMax += index > xMax? 0 : -1;
		}
			
		if (index == yMax){
			yMax = -1;
			checkYMax();
		} else {
			yMax += index > yMax? 0 : -1;
		}
		
		if (index == xMin){
			xMin = -1;
			checkXMin();
		} else {
			xMin += index < xMin? -1 : 0;
		}
		
		if (index == yMin){
			yMin = -1;
			checkYMin();
		} else {
			yMin += index < yMin? -1 : 0;
		}
		
		emitRemovePoint(index, point);
	}

	@Override
	public void removePoint(Point point) {
		removePointAt(indexOf(point));
	}
	
	@Override
	public void removePoints() {
		for (Point p : points)
			p.removeListener(pointListener);
		
		points.clear();
		checkMinMax();
		emitOnChange();
	}

	@Override
	public int countPoints() {
		return points.size();
	}

	@Override
	public Point[] getPoints() {
		Point [] p = new Point[points.size()];
		for (int i = 0; i < p.length; i++)
			p[i] = points.get(i);
		return p;
	}

	@Override
	public Point getXMax() {
		if(xMax == -1) 
			return null;
		return getPointAt(xMax);
	}

	@Override
	public Point getYMax() {
		if(yMax == -1) 
			return null;
		
		return getPointAt(yMax);
	}

	@Override
	public Point getXMin() {
		if(xMin == -1) 
			return null;
		return getPointAt(xMin);
	}

	@Override
	public Point getYMin() {
		if(yMin == -1) 
			return null;
		return getPointAt(yMin);
	}

	@Override
	public Point getPointAt(int index) {
		return points.get(index);
	}

	@Override
	public void addListener(PointCloudListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public void removeListener(PointCloudListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	protected synchronized void emitOnChange () {
		for (PointCloudListener ls : listeners)
			ls.onChange(this);
	}
	
	protected synchronized void emitRemovePoint (int index, Point point) {
		for (PointCloudListener ls : listeners)
			ls.onRemovePoint(this, index, point);
	}
	
	/**
	 * @param index
	 */
	protected synchronized void emitInsertPoint (int index) {
		
		if (xMax == -1) {
			setXMax(0);
			setXMin(0);
			
			setYMax(0);
			setYMin(0);
		} else {			
			Point p = points.get(index);
			
			int xMax = p.getX() > getXMax().getX()? index : this.xMax;
			int xMin = p.getX() < getXMin().getX()? index : this.xMin;
			
			int yMax = p.getY() > getYMax().getY()? index : this.yMax;
			int yMin = p.getY() < getYMin().getY()? index : this.yMin;
			
			setXMax(xMax);
			setXMin(xMin);
			
			setYMax(yMax);
			setYMin(yMin);
		}

		for (PointCloudListener ls : listeners)
			ls.onInsertPoint(this, index);
	}

	/**
	 * @param xMax the xMax to set
	 */
	protected void setXMax(int xMax) {
		if(this.xMax == xMax)
			return;
		
		this.xMax = xMax;
	}
	
	protected void checkMinMax () {
		checkXMax();
		checkXMin();
		checkYMax();
		checkYMin();
	}
	
	protected void checkXMax () {
		Point point = points.size() != 0? points.get(0) : null;
		for (Point p : points) {				
			if (p.getX() > point.getX())
				point = p;			
		}
		xMax = indexOf(point);
	}

	/**
	 * @param yMax the yMax to set
	 */
	protected void setYMax(int yMax) {
		if(this.yMax == yMax)
			return;
		
		this.yMax = yMax;
	}
	
	protected void checkYMax () {
		Point point = points.size() != 0? points.get(0) : null;
		for (Point p : points) {				
			if (p.getY() > point.getY())
				point = p;			
		}
		yMax = indexOf(point);
	}

	/**
	 * @param xMin the xMin to set
	 */
	protected void setXMin(int xMin) {
		if(this.xMin == xMin)
			return;
		
		this.xMin = xMin;
	}
	
	protected void checkXMin () {
		Point point = points.size() != 0? points.get(0) : null;
		for (Point p : points) {				
			if (p.getX() < point.getX())
				point = p;			
		}
		xMin = indexOf(point);
	}

	/**
	 * @param yMin the yMin to set
	 */
	protected void setYMin(int yMin) {
		this.yMin = yMin;
	}
	
	protected void checkYMin () {
		Point point = points.size() != 0? points.get(0) : null;
		for (Point p : points) {				
			if (p.getY() < point.getY())
				point = p;			
		}
		yMin = indexOf(point);
	}

}
