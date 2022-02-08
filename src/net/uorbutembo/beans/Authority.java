/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 * classe de base pour tout les autorites utilisateur du systeme
 */
public abstract class Authority extends User {
	private static final long serialVersionUID = -1049788427210521265L;
	
	/**
	 * la date de debut de service
	 */
	protected Date startDate;
	
	/**
	 * la date de cloture de service au poste
	 */
	protected Date closeDate;

	/**
	 * 
	 */
	public Authority() {
		super();
	}

	/**
	 * @param id
	 */
	public Authority(Long id) {
		super(id);
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the closeDate
	 */
	public Date getCloseDate() {
		return closeDate;
	}

	/**
	 * @param closeDate the closeDate to set
	 */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

}
