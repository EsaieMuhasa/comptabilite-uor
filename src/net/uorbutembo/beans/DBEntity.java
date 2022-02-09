/**
 * 
 */
package net.uorbutembo.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Esaie MUHASA
 * classe de base de tout les entites persistable dans la base de donnees
 */
public abstract class DBEntity implements Serializable {

	private static final long serialVersionUID = -115739324234608094L;//generer par Eclipse

	/**
	 * Identifiant de l'occurence dans la base de donnee
	 */
	protected Long id;
	
	/**
	 * Date de creation d'une occurence
	 */
	protected Date recordDate;
	
	/**
	 * Derniere modification aporter sur une occurence
	 */
	protected Date lastUpdate;

	/**
	 * constructeur par defaut
	 */
	public DBEntity() {
		super();
	}
	
	/**
	 * constructeur d'initialisation de l'identifiant d'une occurence
	 * @param id
	 */
	public DBEntity (long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the recordDate
	 */
	public Date getRecordDate() {
		return recordDate;
	}

	/**
	 * @param recordDate the recordDate to set
	 */
	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}
