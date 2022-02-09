/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * un departement precis
 */
public class Department extends Orientation {
	private static final long serialVersionUID = -8082981429253230554L;
	
	/**
	 * La faculte d'affectation du departement
	 */
	private Faculty faculty;

	/**
	 * 
	 */
	public Department() {
		super();
	}

	/**
	 * @param id
	 */
	public Department(Long id) {
		super(id);
	}

	/**
	 * Constructeur d'initialisation de la faculte d'affectation d'un departement
	 * @param faculty
	 */
	public Department(Faculty faculty) {
		super();
		this.faculty = faculty;
	}
	
	/**
	 * constructeur d'initialisation de l'identifiant du departement
	 * et de la faculte d'affectation
	 * @param id
	 * @param faculty
	 */
	public Department(Long id, Faculty faculty) {
		this(id);
		this.faculty = faculty;
	}

	/**
	 * @return the faculty
	 */
	public Faculty getFaculty() {
		return faculty;
	}

	/**
	 * @param faculty the faculty to set
	 */
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}
	
	public void setFaculty (long id) {
		this.faculty = new  Faculty(id);
	}

}
