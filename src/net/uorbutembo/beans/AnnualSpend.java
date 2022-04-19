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

}
