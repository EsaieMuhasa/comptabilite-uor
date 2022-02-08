/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * les frais qui seron payer par une promotion
 */
public class FeePromotion extends DBEntity {
	private static final long serialVersionUID = -2301732439477642353L;
	
	/**
	 * reference vers les frais academique
	 */
	private AcademicFee academicFee;
	
	/**
	 * Reference vers la promotion
	 */
	private Promotion promotion;

	/**
	 * 
	 */
	public FeePromotion() {
		super();
	}

	/**
	 * @param id
	 */
	public FeePromotion(Long id) {
		super(id);
	}

	/**
	 * @return the academicFee
	 */
	public AcademicFee getAcademicFee() {
		return academicFee;
	}

	/**
	 * @param academicFee the academicFee to set
	 */
	public void setAcademicFee(AcademicFee academicFee) {
		this.academicFee = academicFee;
	}

	/**
	 * @return the promotion
	 */
	public Promotion getPromotion() {
		return promotion;
	}

	/**
	 * @param promotion the promotion to set
	 */
	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

}
