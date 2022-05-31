/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultRecipePart <T extends DBEntity> extends DBEntity implements RecipePart <T> {
	private static final long serialVersionUID = -8344042348612942391L;
	
	private final T source;
	private final AnnualSpend account;
	private final PaymentLocation paymentLocation;
	private final String label;
	private final String title;
	private final double amount;
	

	/**
	 * @param source
	 * @param account
	 * @param paymentLocation
	 * @param label
	 * @param title
	 * @param amount
	 */
	public DefaultRecipePart(T source, AnnualSpend account, PaymentLocation paymentLocation, String label, String title, double amount) {
		super();
		this.source = source;
		this.account = account;
		this.paymentLocation = paymentLocation;
		this.label = label;
		this.title = title;
		this.amount = amount;
	}

	@Override
	public long getId () {
		throw new RuntimeException("Operation non prise ne charge");
	}
	
	@Override
	public void setId (long id) {
		throw new RuntimeException("Operation non pris en charge");
	}

	@Override
	public Date getRecordDate() {
		return source.getRecordDate();
	}

	@Override
	public void setRecordDate(Date recordDate) {
		throw new RuntimeException("Operation non pris en charge");
	}

	@Override
	public Date getLastUpdate() {
		return source.getLastUpdate();
	}

	@Override
	public void setLastUpdate(Date lastUpdate) {
		throw new RuntimeException("Operation non pris en charge");
	}


	@Override
	public String getLabel() {
		return label;
	}


	@Override
	public String getTitle() {
		return title;
	}


	@Override
	public T getSource() {
		return source;
	}


	@Override
	public double getAmount() {
		return amount;
	}


	@Override
	public AnnualSpend getAccount() {
		return account;
	}
	
	@Override
	public PaymentLocation getPaymentLocation() {
		return paymentLocation;
	}

}
