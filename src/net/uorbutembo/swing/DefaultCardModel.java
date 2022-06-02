/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultCardModel <T> implements CardModel <T> {
	
	/**
	 * @author Esaie MUHASA
	 * Type par defaut des cardes
	 */
	public enum CardType {
		PRIMARY,
		SECONDARY,
		DANGER,
		DEFAULT,
		SUCCESS,
		WARNING,
		INFO,
		DARK,
		LIGHT
	}
	
	private T value;
	private String title;
	private String icon;
	private String info;
	private String suffix="";
	
	private final List<CardModelListener> listeners = new ArrayList<>();
	
	private Color backgroundColor;
	private Color foregroundColor;
	
	public DefaultCardModel() {
		super();
	}
	
	/**
	 * constructeur d'initialisation rapide d'un card
	 * @param type
	 */
	public DefaultCardModel(CardType type) {
		
		this.foregroundColor = Color.WHITE;
		switch (type) {
			case PRIMARY:
				this.backgroundColor = Color.BLUE;
				break;
			case SECONDARY:
				this.backgroundColor = Color.DARK_GRAY;
				break;
			case DANGER:
				this.backgroundColor = Color.RED;
				break;
			case DARK:
				this.backgroundColor = new Color(0x353535);
				break;
			case INFO:
				this.backgroundColor = new Color(0x5050C0);
				break;
			case SUCCESS:
				this.backgroundColor = Color.GREEN;
				break;
			case LIGHT:
				this.backgroundColor = Color.LIGHT_GRAY;
				this.foregroundColor = Color.BLACK;
				break;
			default:
				break;
		}
	}
	
	/**
	 * Constructeur d'initialisation rapide d'un card
	 * @param type
	 * @param icon
	 * @param suffix
	 */
	public DefaultCardModel(CardType type, String icon, String suffix)  {
		this(type);
		this.icon = icon;
		this.suffix = suffix;
	}

	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 */
	public DefaultCardModel(Color backgroundColor, Color foregroundColor) {
		super();
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
	}
	
	public DefaultCardModel(Color backgroundColor, Color foregroundColor, String icon, String suffix) {
		super();
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.icon = icon;
		this.suffix = suffix;
	}

	@Override
	public Color getForegroundColor() {
		return foregroundColor;
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		if (this.backgroundColor == backgroundColor)
			return;
		
		Color oldColor = this.backgroundColor;
		this.backgroundColor = backgroundColor;
		for (CardModelListener ls : listeners) 
			ls.onColorChange(this, 2, oldColor);
	}

	/**
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(Color foregroundColor) {
		if (this.foregroundColor == foregroundColor)
			return;
		
		Color old = this.foregroundColor;
		this.foregroundColor = foregroundColor;
		for (CardModelListener ls : listeners) 
			ls.onColorChange(this, 1, old);
	}
	
	/**
	 * Lors du changement de la value
	 * @param old
	 */
	protected void emitOnValueChange (T old) {
		for (CardModelListener ls : listeners) 
			ls.onValueChange(this, old);
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(T value) {
		if (value == this.value)
			return;
		T old = this.value;
		this.value = value;
		emitOnValueChange(old);
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		if(this.title == title)
			return;
		
		String old = this.title;
		this.title = title;
		for (CardModelListener ls : listeners) 
			ls.onTitleChange(this, 1, old);
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		if(this.icon == icon)
			return;
		
		String old = this.icon;
		this.icon = icon;
		for (CardModelListener ls : listeners) 
			ls.onIconChange(this, old);
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		if(this.info == info)
			return;
		
		String old = this.info;
		this.info = info;
		for (CardModelListener ls : listeners) 
			ls.onTitleChange(this, 2, old);
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getInfo() {
		return info;
	}

	@Override
	public String getIcon() {
		return icon;
	}

	/**
	 * @return the suffix
	 */
	@Override
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix (String suffix) {
		this.suffix = suffix;
		for (CardModelListener ls : listeners) 
			ls.onChange(this);
	}

	@Override
	public void removeListener(CardModelListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void addListener(CardModelListener listener) {
		if (listener != null && !listeners.contains(listener))
			listeners.add(listener);
	}

}
