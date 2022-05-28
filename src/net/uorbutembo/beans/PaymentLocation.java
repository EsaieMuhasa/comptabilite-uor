/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class PaymentLocation extends DBEntity {
	private static final long serialVersionUID = -6675925694218725224L;
	
	private String name;

	/**
	 * 
	 */
	public PaymentLocation() {
		super();
	}

	/**
	 * @param id
	 */
	public PaymentLocation(long id) {
		super(id);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
