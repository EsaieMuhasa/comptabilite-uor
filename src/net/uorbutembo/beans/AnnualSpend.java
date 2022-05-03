/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * une depense annuel
 */
public class AnnualSpend extends DBEntity {
	private static final long serialVersionUID = 7021187757541634583L;
	
	/**
	 * Reference de l'annee academique
	 */
	private AcademicYear academicYear;
	
	/**
	 * reference de  la depense
	 */
	private UniversitySpend universitySpend;
	
	private double collectedRecipe;//montant total deja collecter pour les autres recettes
	
	private double collectedCost;//montant deja collecter pour les frais payer par les etudiants
	
	private double used;//montant deja utiliser

	/**
	 * 
	 */
	public AnnualSpend() {
		super();
	}

	/**
	 * @param id
	 */
	public AnnualSpend(Long id) {
		super(id);
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
	 * @return the universitySpend
	 */
	public UniversitySpend getUniversitySpend() {
		return universitySpend;
	}

	/**
	 * @param universitySpend the universitySpend to set
	 */
	public void setUniversitySpend(UniversitySpend universitySpend) {
		this.universitySpend = universitySpend;
	}
	
	@Override
	public String toString() {
		return universitySpend.getTitle();
	}

	/**
	 * @return the collectedRecipe
	 */
	public double getCollectedRecipe() {
		return collectedRecipe;
	}

	/**
	 * @param collectedRecipe the collectedRecipe to set
	 */
	public void setCollectedRecipe(double collectedRecipe) {
		this.collectedRecipe = collectedRecipe;
	}

	/**
	 * @return the collectedCost
	 */
	public double getCollectedCost() {
		return collectedCost;
	}

	/**
	 * @param collectedCost the collectedCost to set
	 */
	public void setCollectedCost(double collectedCost) {
		this.collectedCost = collectedCost;
	}

	/**
	 * @return the used
	 */
	public double getUsed() {
		return used;
	}

	/**
	 * @param used the used to set
	 */
	public void setUsed(double used) {
		this.used = used;
	}

}
