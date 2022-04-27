package net.uorbutembo.beans;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class AnnualRecipe extends DBEntity {
	private static final long serialVersionUID = 523281614049699114L;
	
	/**
	 * le montant total deja collecter
	 * cotee BDD ce champ est calculer (confert la vue materiel V_AnnualRecipe)
	 */
	private double collected;
	
	/**
	 * montant prevue (mais pas encore disponible)
	 */
	private double forecasting;
	
	/**
	 * l'annee concerner par la recette
	 */
	private AcademicYear academicYear;
	
	/**
	 * description de la recette
	 */
	private UniversityRecipe universityRecipe;

	/**
	 * 
	 */
	public AnnualRecipe() {
		super();
		collected = 0;
	}

	/**
	 * @param id
	 */
	public AnnualRecipe(long id) {
		super(id);
		collected = 0;
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
	public void setAcademicYear(final AcademicYear academicYear) {
		this.academicYear = academicYear;
	}
	
	public void setAcademicYear (final long id) {
		if(academicYear == null)
			academicYear = new AcademicYear();
		
		academicYear.setId(id);
	}

	/**
	 * @return the universityRecipe
	 */
	public UniversityRecipe getUniversityRecipe() {
		return universityRecipe;
	}

	/**
	 * @param universityRecipe the universityRecipe to set
	 */
	public void setUniversityRecipe(final UniversityRecipe universityRecipe) {
		this.universityRecipe = universityRecipe;
	}
	
	public void setUniversityRecipe (final long id) {
		if(universityRecipe == null)
			universityRecipe = new UniversityRecipe();
		universityRecipe.setId(id);
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
	public void setCollected (double collected) {
		this.collected = collected;
	}

	/**
	 * @return the forecasting
	 */
	public double getForecasting () {
		return forecasting;
	}

	/**
	 * @param forecasting the forecasting to set
	 */
	public void setForecasting (double forecasting) {
		this.forecasting = forecasting;
	}

	@Override
	public String toString() {
		return universityRecipe.getTitle();
	}

}
