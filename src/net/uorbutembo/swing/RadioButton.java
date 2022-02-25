/**
 * 
 */
package net.uorbutembo.swing;

import javax.swing.Icon;
import javax.swing.JRadioButton;

/**
 * @author Esaie MUHASA
 *
 */
public class RadioButton<T> extends JRadioButton {
	private static final long serialVersionUID = -509037669061554953L;
	
	private T data;

	/**
	 * @param data
	 */
	public RadioButton(T data) {
		super(data.toString());
		this.data = data;
	}

	/**
	 * @param icon
	 * @param selected
	 */
	public RadioButton(Icon icon, boolean selected) {
		super(icon, selected);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param icon
	 */
	public RadioButton(Icon icon, T data) {
		super(icon);
		this.data = data;
	}

	/**
	 * @param text
	 * @param selected
	 */
	public RadioButton(String text, boolean selected) {
		super(text, selected);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param text
	 * @param icon
	 * @param selected
	 */
	public RadioButton(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param text
	 * @param icon
	 * @param data
	 */
	public RadioButton(String text, Icon icon, T data) {
		super(text, icon);
		this.data = data;
	}

	/**
	 * @param text
	 * @param data
	 */
	public RadioButton(String text, T data) {
		super(text);
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}

}
