/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 * une somme payer par l'etudiant. (Frais universitaire)
 * ou encore un transh des frais universitaire d'un inscrit
 */
public class PaymentFee extends DBEntity {
	private static final long serialVersionUID = -2712698091924677921L;
	
	/**
	 * l'inscription concerner
	 */
	private Inscription inscription;
	
	/**
	 * lieu de payment des frais
	 */
	private PaymentLocation location;
	
	/**
	 * Le montant payer
	 */
	private float amount;
	
	/**
	 * la date de reception du bordereau par le caissier
	 */
	private Date receivedDate;
	
	/**
	 * Numero du recu
	 */
	private String receiptNumber;
	
	/**
	 * Date de payement a la banque (date du bordereau)
	 */
	private Date slipDate;

	/**
	 * Numero du bordereau
	 */
	private String slipNumber;
	
	/**
	 * libeleee du montant payer
	 */
	private String wording;
	
	/**
	 * 
	 */
	public PaymentFee() {
		super();
	}

	/**
	 * @param id
	 */
	public PaymentFee(long id) {
		super(id);
	}

	/**
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * @return the receivedDate
	 */
	public Date getReceivedDate() {
		return receivedDate;
	}

	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	/**
	 * @return the receiptNumber
	 */
	public String getReceiptNumber() {
		return receiptNumber;
	}

	/**
	 * @param receiptNumber the receiptNumber to set
	 */
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	/**
	 * @return the slipDate
	 */
	public Date getSlipDate() {
		return slipDate;
	}

	/**
	 * @param slipDate the slipDate to set
	 */
	public void setSlipDate(Date slipDate) {
		this.slipDate = slipDate;
	}

	/**
	 * @return the slipNumber
	 */
	public String getSlipNumber() {
		return slipNumber;
	}

	/**
	 * @param slipNumber the slipNumber to set
	 */
	public void setSlipNumber(String slipNumber) {
		this.slipNumber = slipNumber;
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
	 * @return the inscription
	 */
	public Inscription getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(Inscription inscription) {
		this.inscription = inscription;
	}

	/**
	 * @return the location
	 */
	public PaymentLocation getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(PaymentLocation location) {
		this.location = location;
	}
	
	/**
	 * surcharge de setLocation(PaymentLocationlocation)
	 * @param location
	 */
	public void setLocation (long location) {
		setLocation(new PaymentLocation(location));
	}

}
