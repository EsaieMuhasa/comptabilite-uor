/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Point;

/**
 * @author Esaie MUHASA
 *
 */
public class Triangle {
	
	private Point a;
	private Point b;
	private Point c;

	/**
	 * @param a
	 * @param b
	 * @param c
	 */
	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	/**
	 * @return the a
	 */
	public Point getA() {
		return a;
	}

	/**
	 * @param a the a to set
	 */
	public void setA(Point a) {
		this.a = a;
	}

	/**
	 * @return the b
	 */
	public Point getB() {
		return b;
	}

	/**
	 * @param b the b to set
	 */
	public void setB(Point b) {
		this.b = b;
	}

	/**
	 * @return the c
	 */
	public Point getC() {
		return c;
	}

	/**
	 * @param c the c to set
	 */
	public void setC(Point c) {
		this.c = c;
	}
	
	@Override
	public String toString() {
		String txt = String.format("A (%d, %d), B (%d, %d) et C (%d, %d)\n", 
				(int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY(), (int)c.getX(), (int)c.getY());
		return txt;
	}

}
