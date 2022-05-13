/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultAxisGraduation extends AbstractChartData implements AxisGraduation {

	private String label;
	private double value;
	private int size;

	
	/**
	 * @param value
	 */
	public DefaultAxisGraduation(double value) {
		super();
		init(value, 5);
	}
	
	public DefaultAxisGraduation(double value, int size) {
		super();
		init(value, size);
	}
	
	public DefaultAxisGraduation(int value) {
		super();
		init(value+"", value, 5);
	}
	
	public DefaultAxisGraduation(int value, int size) {
		super();
		init(value, size);
	}
	
	/**
	 * @param label
	 * @param value
	 * @param size
	 */
	public DefaultAxisGraduation(String label, double value, int size) {
		super();
		init(label, value, size);
	}

	/**
	 * @param backgroundColor
	 */
	public DefaultAxisGraduation(String label, double value, int size, Color backgroundColor) {
		super(backgroundColor);
		init(label, value, size);
	}
	
	/**
	 * Constructeur de copie de la configuration d'une graduation
	 * @param value
	 * @param graduation
	 */
	public DefaultAxisGraduation(double value, AxisGraduation graduation) {
		super(graduation.getBackgroundColor(), graduation.getForegroundColor(), graduation.getBorderColor());
		init(value, graduation.getSize());
		setBorderWidth(graduation.getBorderWidth());
	}
	
	/**
	 * constructeur de compie de la configuration d'une graduation
	 * @param label
	 * @param value
	 * @param graduation
	 */
	public DefaultAxisGraduation(String label, double value, AxisGraduation graduation) {
		super(graduation.getBackgroundColor(), graduation.getForegroundColor(), graduation.getBorderColor());
		init(label, value, graduation.getSize());
		setBorderWidth(graduation.getBorderWidth());
	}
	
	/**
	 * @param value
	 * @param size
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param borderColor
	 */
	public DefaultAxisGraduation(double value, int size, Color backgroundColor, Color foregroundColor, Color borderColor) {
		super(backgroundColor, foregroundColor, borderColor);
		init(value, size);
	}

	/**
	 * @param label
	 * @param value
	 * @param size
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param borderColor
	 */
	public DefaultAxisGraduation(String label, double value, int size, Color backgroundColor, Color foregroundColor, Color borderColor) {
		super(backgroundColor, foregroundColor, borderColor);
		init(label, value, size);
	}
	
	/**
	 * initialisation de valeur du graduation
	 * @param label
	 * @param value
	 * @param size
	 */
	protected void init (String label, double value, int size) {
		this.label = label;
		this.value = value;
		this.size = size;
	}
	
	protected void init (double value, int size) {
		this.value = value;
		this.size = size;
		
		int round = (int) getRoundValue();
		this.label = ((round == value)? round+"" : getRoundValue(2)+"");
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		if(this.value == value)
			return;
		
		this.value = value;
		emitOnChange();
	}

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel (String label) {
		if(label == this.label)
			return;
		
		this.label = label;
		emitOnChange();
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "DefaultAxisGraduation [label=" + label + ", value=" + value + ", size=" + size + ", data = " + data + "]";
	}
	

}
