/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * frais univeritaire
 * ----------------------
 * les frais univeritaire sont fixer pour chaque annee.
 * un groupe de promotion peuvement payer les meme fais univeritaire.
 * Ainsi le fais universitaires devient configurable. 
 * Cette classe represene le motant de frais universitaire.
 * Il est en suite re utiliser pour plusier promotion
 */
public class AcademicFee extends DBEntity {
	private static final long serialVersionUID = -8443135589726366475L;
	
	/**
	 * montant parametrer
	 */
	private float amount;
	
	private String description;
	
	/**
	 * L'annee academique concerner par les frais academique
	 */
	private AcademicYear academicYear;
	
	/**
	 * montant total deja collecter (Confert la vue V_AcademicFee)
	 */
	private double collected;
	
	/**
	 * Montant total attendue (Convfert la vue V_AcademicFee)
	 */
	private double totalExpected;

	/**
	 * 
	 */
	public AcademicFee() {
		super();
	}

	/**
	 * @param id
	 */
	public AcademicFee(long id) {
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	
	public void setAcademicYear (long academicYear) {
		this.academicYear = new AcademicYear(academicYear);
	}
	
	@Override
	public String toString() {
		return this.amount+" USD";
	}

	/**
	 * @return the collected
	 */
	public double getCollected() {
		return collected;
	}

	/**
	 * @param collected the collected to set
	 */
	public void setCollected(double collected) {
		this.collected = collected;
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
