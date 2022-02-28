/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Point;

/**
 * @author Esaie MUHASA
 */
public class LineSegment {
	
	private Point start;
	private Point end;

	/**
	 * @param start
	 * @param end
	 */
	public LineSegment(Point start, Point end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * @return the start
	 */
	public Point getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Point start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Point getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(Point end) {
		this.end = end;
	}

}
