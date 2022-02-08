/**
 * 
 */
package net.uorbutembo.dao;

/**
 * @author Esaie MUHASA
 *
 */
public class DAOConfigException extends Exception {
	private static final long serialVersionUID = 2045332905603980035L;

	/**
	 * @param message
	 */
	public DAOConfigException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DAOConfigException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DAOConfigException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
