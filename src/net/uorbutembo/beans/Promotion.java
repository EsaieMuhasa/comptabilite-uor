/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * une promotion
 */
public class Promotion extends DBEntity {
	private static final long serialVersionUID = -3691926998902857304L;
	
	/**
	 * Le depatement de la dite dpromotion
	 */
	private Department department;
	
	/**
	 * La classe d'etude de la dite promotion
	 */
	private StudyClass studyClass;
	
	/**
	 * L'annee d'etude de la dite promotion
	 */
	private AcademicYear academicYear;

	/**
	 * 
	 */
	public Promotion() {
		super();
	}

	/**
	 * @param id
	 */
	public Promotion(Long id) {
		super(id);
	}

	/**
	 * @return the department
	 */
	public Department getDepartment() {
		return department;
	}

	/**
	 * @param department the department to set
	 */
	public void setDepartment(Department department) {
		this.department = department;
	}

	/**
	 * @return the studyClass
	 */
	public StudyClass getStudyClass() {
		return studyClass;
	}

	/**
	 * @param studyClass the studyClass to set
	 */
	public void setStudyClass(StudyClass studyClass) {
		this.studyClass = studyClass;
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

}
