/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * classe de base des oritentations, dans une universite (Faculte, Departement, ...)
 */
public abstract class Orientation extends DBEntity {

	private static final long serialVersionUID = 642776003222541159L;
	
	/**
	 * L'acronyme ou l'abbreviation de l'orientation
	 */
	protected String acronym;
	
	/**
	 * Le nom complet de l'orientation
	 */
	protected String name;

	/**
	 * 
	 */
	public Orientation() {
		super();
	}

	/**
	 * @param id
	 */
	public Orientation(Long id) {
		super(id);
	}

	/**
	 * @return the acronym
	 */
	public String getAcronym() {
		return acronym;
	}

	/**
	 * @param acronym the acronym to set
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.getName()!=null? this.getName() : "";
	}

}
