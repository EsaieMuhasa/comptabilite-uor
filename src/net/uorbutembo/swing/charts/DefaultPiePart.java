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
public class DefaultPiePart implements PiePart {
	
	protected final List<PiePartListener> listeners = new ArrayList<>();
	
	protected Color backgroundColor;
	protected Color foregroundColor;
	protected Color borderColor;
	protected String label;
	protected String name;
	protected double value;
	protected boolean visible;

	/**
	 * 
	 */
	public DefaultPiePart() {
		this.backgroundColor = Color.BLACK;
		this.foregroundColor = Color.WHITE;
		this.borderColor = Color.DARK_GRAY;
		this.label = "";
		this.visible = true;
	}

	/**
	 * @param backgroundColor
	 * @param value
	 * @param label
	 */
	public DefaultPiePart(Color backgroundColor, double value, String label) {
		this();
		this.backgroundColor = backgroundColor;
		this.value = value;
		this.label = label;
	}

	/**
	 * @param backgroundColor
	 * @param borderColor
	 * @param value
	 * @param label
	 */
	public DefaultPiePart(Color backgroundColor, Color borderColor, double value, String label) {
		this(backgroundColor, value, label);
		this.borderColor = borderColor;
	}

	/**
	 * @param backgroundColor
	 * @param name
	 */
	public DefaultPiePart(Color backgroundColor, String name) {
		this();
		this.backgroundColor = backgroundColor;
		this.name = name;
	}
	

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public Color getForegrounColor() {
		return foregroundColor;
	}

	@Override
	public Color getBorderColor() {
		return borderColor;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setBackgroundColor(Color backgroundColor) {
		if(this.backgroundColor.equals(backgroundColor))
			return;
		
		this.backgroundColor = backgroundColor;
		this.emitOnChange();
	}

	@Override
	public void setForegroundColor(Color foregroundColor) {
		if(this.foregroundColor.equals(foregroundColor)) 
			return;
		
		this.foregroundColor = foregroundColor;
		this.emitOnChange();
	}

	@Override
	public void setBorderColor(Color borderColor) {
		if(this.borderColor.equals(borderColor))
			return;
		
		this.borderColor = borderColor;
		this.emitOnChange();
	}

	@Override
	public void setValue(double value) {
		if(this.value == value)
			return;
		
		this.value = value;
		this.emitOnChange();
	}

	@Override
	public void setLabel(String label) {
		if(this.label.equals(label))
			return;
		
		this.label = label;
		this.emitOnChange();		
	}

	@Override
	public void setVisible(boolean visible) {
		if(this.visible == visible)
			return;
		
		this.visible = visible;
		this.emitOnChange();
	}

	@Override
	public void addListener(PiePartListener listener) {	
		if(!this.listeners.contains(listener))
			this.listeners.add(listener);
	}

	@Override
	public void removeListener(PiePartListener listener) {
		this.listeners.remove(listener);
	}
	
	protected void emitOnChange () {
		for (PiePartListener listener : listeners) {
			listener.onChange(this);
		}
	}

}
