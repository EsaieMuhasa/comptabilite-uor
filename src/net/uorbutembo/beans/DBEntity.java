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
	protected long id;
	
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
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			try {
				DBEntity d = (DBEntity) obj;
				return (d.getClass().getName().equals(obj.getClass().getName())) && d.id == id;
			} catch (Exception e) {}
		}
		return super.equals(obj);
	}

}
