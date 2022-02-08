/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public class Student extends User {
	private static final long serialVersionUID = 7774274276454063899L;

	/**
	 * La date de naissance d'un etudiant
	 */
	private Date birthDate;
	
	/**
	 * Le lieux de naissance d'un etudiant
	 */
	private String birthPlace;
	
	/**
	 * Le matricule
	 */
	private String matricul;
	
	/**
	 * 
	 */
	public Student() {
		super();
	}

	/**
	 * @param id
	 */
	public Student(Long id) {
		super(id);
	}

	/**
	 * @return the birthDate
	 */
	public Date getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate the birthDate to set
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the birthPlace
	 */
	public String getBirthPlace() {
		return birthPlace;
	}

	/**
	 * @param birthPlace the birthPlace to set
	 */
	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	/**
	 * @return the matricul
	 */
	public String getMatricul() {
		return matricul;
	}

	/**
	 * @param matricul the matricul to set
	 */
	public void setMatricul(String matricul) {
		this.matricul = matricul;
	}

}
