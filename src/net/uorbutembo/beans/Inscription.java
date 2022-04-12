/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 * l'inscription (ou la re-inscription) d'un etudiant
 */
public class Inscription extends DBEntity {
	private static final long serialVersionUID = 6061302060878755187L;
	
	/**
	 * L'etudiant qui s'inscrit
	 */
	private Student student;
	
	/**
	 * la promotion soliciter
	 */
	private Promotion promotion;
	
	/**
	 * L'appariteur centrale qui aurait valider l'inscription
	 */
	private CentralApparitor centralApparitor;
	
	/**
	 * l'adresse de residence de l'inscrit
	 */
	private String adress;

	/**
	 * 
	 */
	public Inscription() {
		super();
	}

	/**
	 * @param id
	 */
	public Inscription(Long id) {
		super(id);
	}

	/**
	 * @return the student
	 */
	public Student getStudent() {
		return student;
	}

	/**
	 * @param student the student to set
	 */
	public void setStudent(Student student) {
		this.student = student;
	}

	/**
	 * @return the promotion
	 */
	public Promotion getPromotion() {
		return promotion;
	}

	/**
	 * @param promotion the promotion to set
	 */
	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	/**
	 * @return the centralApparitor
	 */
	public CentralApparitor getCentralApparitor() {
		return centralApparitor;
	}

	/**
	 * @param centralApparitor the centralApparitor to set
	 */
	public void setCentralApparitor(CentralApparitor centralApparitor) {
		this.centralApparitor = centralApparitor;
	}

	/**
	 * @return the adress
	 */
	public String getAdress() {
		return adress;
	}

	/**
	 * @param adress the adress to set
	 */
	public void setAdress(String adress) {
		this.adress = adress;
	}

}
