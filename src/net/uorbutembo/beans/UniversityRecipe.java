/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class UniversityRecipe extends DBEntity {
	private static final long serialVersionUID = 8120290436743476600L;
	
	private String title;
	private String description;

	/**
	 * 
	 */
	public UniversityRecipe() {
		super();
	}

	/**
	 * @param id
	 */
	public UniversityRecipe(long id) {
		super(id);
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

}
