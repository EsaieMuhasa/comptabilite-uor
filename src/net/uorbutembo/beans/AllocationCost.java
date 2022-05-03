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
	
	private float percent;//compiens de pourcent veau amount dans academicFee
	
	private double collecetd;//montant dela collecter au compte
	
	private double totalExpected;//montant total attendue

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

	/**
	 * @return the percent
	 */
	public float getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(float percent) {
		this.percent = percent;
	}

	/**
	 * @return the collecetd
	 */
	public double getCollecetd() {
		return collecetd;
	}

	/**
	 * @param collecetd the collecetd to set
	 */
	public void setCollecetd(double collecetd) {
		this.collecetd = collecetd;
	}

	/**
	 * @return the totalExpected
	 */
	public double getTotalExpected() {
		return totalExpected;
	}

	/**
	 * @param totalExpected the totalExpected to set
	 */
	public void setTotalExpected(double totalExpected) {
		this.totalExpected = totalExpected;
	}

}
