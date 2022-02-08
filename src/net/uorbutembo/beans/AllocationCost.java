/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * montant du frais academique prevue pour satisfaire une depence
 */
public class AllocationCost extends DBEntity {
	private static final long serialVersionUID = -3662208739900368782L;
	
	/**
	 * le montant prevue
	 */
	private float amount;
	
	/**
	 * le frais concerner
	 */
	private AcademicFee academicFee;
	
	/**
	 * Le depence annuel concerner
	 */
	private AnnualSpend annualSpend;

	/**
	 * 
	 */
	public AllocationCost() {
		super();
	}

	/**
	 * @param id
	 */
	public AllocationCost(long id) {
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
	 * @return the annualSpend
	 */
	public AnnualSpend getAnnualSpend() {
		return annualSpend;
	}

	/**
	 * @param annualSpend the annualSpend to set
	 */
	public void setAnnualSpend(AnnualSpend annualSpend) {
		this.annualSpend = annualSpend;
	}

}
