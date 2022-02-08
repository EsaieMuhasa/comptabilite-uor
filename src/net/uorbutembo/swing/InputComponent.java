/**
 * 
 */
package net.uorbutembo.swing;

import javax.swing.JComponent;

/**
 * @author Esaie MUHASA
 *
 */
public interface InputComponent <T> {
	
	/**
	 * mutation du la bel d'un input
	 * @param label
	 */
	public void setLabel (String label);
	
	/**
	 * Renvoie le label du component
	 * @return
	 */
	public String getLabel ();
	
	/**
	 * Recuperation de la valeur du label d'un input
	 * @return
	 */
	public T getValue ();
	
	/**
	 * modification de la valeur de l'input
	 * @param value
	 */
	public void setValue (T value);
	
	/**
	 * Renvoie le composent graphique qui represente l'input
	 * @return
	 */
	public JComponent getComponent () ;
}
