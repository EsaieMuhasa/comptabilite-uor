/**
 * 
 */
package net.uorbutembo.dao;

/**
 * @author Esaie MUHASA
 *
 */
public class DAOException extends Exception {
	private static final long serialVersionUID = -5053637567665840772L;

	/**
	 * @param message
	 */
	public DAOException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DAOException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DAOException(String message, Throwable cause) {
		super(message, cause);
	}

}
