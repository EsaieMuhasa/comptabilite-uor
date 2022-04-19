/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 * Utilisation de l'argent
 * represente une sortie en caisse
 */
public class Outlay extends DBEntity {
	private static final long serialVersionUID = 1895217861825360802L;
	
	private Date deliveryDate;
	private double amount;
	private String wording;//une petit label (description, justification de la sortie)
	private String reference;//une photo en quise de reference
	private AnnualSpend account;//rubrique toucher par la dite sortie
	private AcademicYear academicYear;//annee qui doit suporter le depence 
	private AcademicYear deliveryYear;//l'annee academique pour le quel le montant a ete payer (pour une bonne localisation dans le temps)

	
	public Outlay() {
		super();
	}

	/**
	 * @param id
	 */
	public Outlay(long id) {
		super(id);
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
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
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the account
	 */
	public AnnualSpend getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount (AnnualSpend account) {
		this.account = account;
	}
	
	/**
	 * @param accountId the accountId to set in current account
	 */
	public void setAccount(long accountId) {
		if(this.account == null)
			this.account = new AnnualSpend();
		
		this.account.setId(accountId);
	}

	/**
	 * @return the wording
	 */
	public String getWording() {
		return wording;
	}

	/**
	 * @param wording the wording to set
	 */
	public void setWording(String wording) {
		this.wording = wording;
	}

	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * @return the academicYear
	 */
	public AcademicYear getAcademicYear() {
		return academicYear;
	}

	/**
	 * @param academicYear the academicYear to set
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
	}

	/**
	 * @return the deliveryYear
	 */
	public AcademicYear getDeliveryYear() {
		return deliveryYear;
	}

	/**
	 * @param deliveryYear the deliveryYear to set
	 */
	public void setDeliveryYear(AcademicYear deliveryYear) {
		this.deliveryYear = deliveryYear;
	}

}
