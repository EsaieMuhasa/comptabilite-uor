package net.uorbutembo.beans;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class AnnualRecipe extends DBEntity {
	private static final long serialVersionUID = 523281614049699114L;
	
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
	}

	/**
	 * @param id
	 */
	public AnnualRecipe(long id) {
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
	public void setAcademicYear(final AcademicYear academicYear) {
		this.academicYear = academicYear;
	}
	
	public void setAcadmicYear (final long id) {
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
	
	@Override
	public String toString() {
		return universityRecipe.getTitle();
	}

}
