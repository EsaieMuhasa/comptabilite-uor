/**
 * 
 */
package net.uorbutembo.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 * une annee academique
 */
public class AcademicYear extends DBEntity {

	private static final long serialVersionUID = 3903173047207517553L;
	
	/**
	 * date d'ouverture de l'annee academique
	 */
	private Date startDate;
	
	/**
	 * date de fermeture de l'annees academique
	 */
	private Date closeDate;
	
	/**
	 * Le labele de l'anne academique
	 */
	private String label;
	
	/**
	 * L'anne predecesseur de l'occurenece encours
	 */
	private AcademicYear previous;

	/**
	 * 
	 */
	public AcademicYear() {
		super();
	}

	/**
	 * @param id
	 */
	public AcademicYear(Long id) {
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

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the previous
	 */
	public AcademicYear getPrevious() {
		return previous;
	}

	/**
	 * @param previous the previous to set
	 */
	public void setPrevious(AcademicYear previous) {
		this.previous = previous;
	}
	
	@Override
	public String toString() {
		return label;
	}

}
