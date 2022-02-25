/**
 * 
 */
package net.uorbutembo.beans;

import java.util.List;

/**
 * @author Esaie MUHASA
 * une faculte particulier
 */
public class Faculty extends Orientation {
	private static final long serialVersionUID = 2595051583019910828L;
	private List<Department> departments;

	/**
	 * 
	 */
	public Faculty() {
		super();
	}

	/**
	 * @param id
	 */
	public Faculty(Long id) {
		super(id);
	}

	/**
	 * @return the departments
	 */
	public List<Department> getDepartments() {
		return departments;
	}

	/**
	 * @param departments the departments to set
	 */
	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

}
