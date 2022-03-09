/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultCardModel <T> implements CardModel <T> {
	
	/**
	 * @author Esaie MUHASA
	 * Type par defaut des cardes
	 */
	enum CardType {
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
	
	private Card view;
	private Color backgroundColor;
	private Color foregroundColor;
	
	public DefaultCardModel() {
		super();
	}

	/**
	 * constructeur d'initialisation de la vue
	 * @param view
	 */
	public DefaultCardModel(Card view) {
		super();
		this.view = view;
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
				this.backgroundColor = Color.BLACK;
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
	 * @param view
	 * @param backgroundColor
	 * @param foregroundColor
	 */
	public DefaultCardModel(Card view, Color backgroundColor, Color foregroundColor) {
		super();
		this.view = view;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
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
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @param foregroundColor the foregroundColor to set
	 */
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	/**
	 * demade a la vue de ce redessiner
	 */
	protected void repaintView() {
		if(view != null)
			view.repaint();
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(T value) {
		if (value == this.value)
			return;
		this.value = value;
		
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
		this.repaintView();
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
		this.repaintView();
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
		this.repaintView();
	}

	/**
	 * @param view the view to set
	 */
	public void setView(Card view) {
		this.view = view;
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
	public void setSuffix(String suffix) {
		this.suffix = suffix;
		this.repaintView();
	}

}
