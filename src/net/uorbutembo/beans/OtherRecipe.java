/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public class OtherRecipe extends DBEntity {
	private static final long serialVersionUID = 1990982448127829332L;
	
	private AcademicYear collectionYear;
	private double amount;
	private String label;
	private AnnualRecipe account;
	private Date collectionDate;

	/**
	 * 
	 */
	public OtherRecipe() {
		super();
	}

	/**
	 * @param id
	 */
	public OtherRecipe(long id) {
		super(id);
	}

	/**
	 * @return the collectionYear
	 */
	public AcademicYear getCollectionYear() {
		return collectionYear;
	}

	/**
	 * @param collectionYear the collectionYear to set
	 */
	public void setCollectionYear(AcademicYear collectionYear) {
		this.collectionYear = collectionYear;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount (double amount) {
		this.amount = amount;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel (String label) {
		this.label = label;
	}

	/**
	 * @return the account
	 */
	public AnnualRecipe getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount (AnnualRecipe account) {
		this.account = account;
	}

	/**
	 * @return the collectionDate
	 */
	public Date getCollectionDate() {
		return collectionDate;
	}

	/**
	 * @param collectionDate the collectionDate to set
	 */
	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

}
