/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * depense universitaire (projet de l'universite)
 * Construction, achat voiture, ...
 */
public class UniversitySpend extends DBEntity {
	private static final long serialVersionUID = -676589369500451883L;

	/**
	 * Description du depense 
	 */
	private String description;
	
	/**
	 * Titre du dempense
	 */
	private String title;
	
	/**
	 * 
	 */
	public UniversitySpend() {
		super();
	}

	/**
	 * @param id
	 */
	public UniversitySpend(Long id) {
		super(id);
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

}
