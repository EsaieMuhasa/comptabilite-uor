/**
 * 
 */
package net.uorbutembo.swing;

import javax.swing.Icon;
import javax.swing.JCheckBox;

/**
 * @author Esaie MUHASA
 *
 */
public class CheckBox <T> extends JCheckBox {
	private static final long serialVersionUID = 7652512025451725365L;
	
	private T data;

	/**
	 * @param data
	 */
	public CheckBox(T data) {
		super(data.toString());
		this.data = data;
	}

	/**
	 * @param icon
	 * @param data
	 */
	public CheckBox(Icon icon, T data) {
		super(icon);
		this.data = data;
	}

	/**
	 * @param text
	 * @param data
	 */
	public CheckBox(String text, T data) {
		super(text);
		this.data = data;
	}


	/**
	 * @param icon
	 * @param selected
	 */
	public CheckBox(Icon icon, boolean selected) {
		super(icon, selected);
	}

	/**
	 * @param text
	 * @param selected
	 */
	public CheckBox(String text, boolean selected) {
		super(text, selected);
	}

	/**
	 * @param text
	 * @param icon
	 */
	public CheckBox(String text, Icon icon) {
		super(text, icon);
	}

	/**
	 * @param text
	 * @param icon
	 * @param selected
	 */
	public CheckBox(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
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
