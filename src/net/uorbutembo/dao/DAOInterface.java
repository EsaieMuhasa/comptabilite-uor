/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.DBEntity;


/**
 * @author Esaie MUHASA
 */
public interface DAOInterface <T extends DBEntity>{
	
	/**
	 * identifiant par de faut de l'evenement lors de l'execution d'un tache dans un thread
	 *  difference de cel qui ait invoquee la methode
	 */
	public static final int DEFAULT_REQUEST_ID = 0x000;
	public static final int DEFAULT_REQUEST_RELOAD_ID = 0xFFF;
	public static final int DEFAULT_LIMIT = 50;
	
	/**
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	public boolean check (String columnName, Object value) throws DAOException;
	
	/**
	 * 
	 * @param columnName
	 * @param value
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public boolean check (String columnName, Object value, long id) throws DAOException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public default boolean checkById (long id) throws DAOException {
		return this.check("id", id);
	}
	
	/**
	 * creation d'une occurence.
	 * la communication avec le DAO se fait dans le meme thread
	 * @param e
	 */
	public void create (T e) throws DAOException;
	
	/**
	 * cration dans un nouvea thread
	 * @param e
	 * @param requestId l'identifiant a retransmetre de l'evenement de syncronisation apres execution de la requette
	 */
	public default void doCreate (T e, int requestId) {
		Thread t = new Thread( () ->  {
			try {
				create(e);
				for (DAOListener<T> lis : getListeners()) {
					lis.onCreate(e, requestId);
				}
			} catch (DAOException ex) {
				for (DAOListener<T> lis : getListeners()) {
					lis.onError(ex, requestId);
				}
			}
		}, this.getClass().getSimpleName()+"_"+System.currentTimeMillis());
		t.start();
	}
	
	/**
	 * surcharge cration d'une occurence dans un nouveau thread
	 * @param e
	 */
	public default void doCreate(T e) {
		this.doCreate(e, DEFAULT_REQUEST_ID);
	}
	
	
	/**
	 * mise en jour d'une occurence.
	 * l'execution de la mis en jour c fait ds le meme thread
	 * @param e
	 * @param id
	 * @throws DAOException
	 */
	public void update (T e, long id) throws DAOException;
	
	/**
	 * mis en jour
	 * la tache c fait dans un nouveau thread
	 * @param e
	 * @param id
	 * @param requestId
	 */
	public default void doUpdate(T e, long id, int requestId) {
		Thread t = new Thread( () ->  {
			try {
				this.update(e, id);
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onUpdate(e, requestId);
				}
			} catch (DAOException ex) {
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onError(ex, requestId);
				}
			}
		}, this.getClass().getSimpleName()+"_"+System.currentTimeMillis());
		t.start();
	}
	
	/**
	 * @param e
	 * @param id
	 */
	public default void doUpdate(T e, long id) {
		this.doUpdate(e, id, DEFAULT_REQUEST_ID);
	}
	
	/**
	 * @param id
	 * @throws DAOException
	 */
	public void delete (long id)  throws DAOException;
	
	/**
	 * @param id
	 * @param requestId
	 */
	public default void doDelete(long id, int requestId) {
		Thread t = new Thread( () ->  {
			try {
				T data = this.findById(id);
				this.delete(id);
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onDelete(data, requestId);
				}
			} catch (DAOException e) {
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onError(e, requestId);
				}
			}
		}, this.getClass().getSimpleName()+"_delete_"+System.currentTimeMillis());
		t.start();
	}
	
	/***
	 * revoie une occurence pour la colone de filtrage
	 * N.B: l'occurence doit etre unique, si une exception sera lever
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	public T find (String columnName, Object value)  throws DAOException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public default T findById (long id)  throws DAOException {
		return this.find("id", id);
	}
	
	/**
	 * @return
	 * @throws DAOException
	 */
	public List<T>  findAll ()  throws DAOException;
	
	/**
	 * compte tout les occures
	 * @return
	 * @throws DAOException
	 */
	public int countAll () throws DAOException;
	
	/**
	 * @param requestId
	 */
	public default void  doFindAll (int requestId) {
		Thread t = new Thread( () ->  {
			try {
				List<T> data = this.findAll();
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onFindAll(data, requestId);
				}
			} catch (DAOException e) {
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onError(e, requestId);
				}
			}
			
		}, this.getClass().getSimpleName()+"_findAll_"+System.currentTimeMillis());
		t.start();
	}
	
	/**
	 * 
	 */
	public default void  doFindAll () {
		this.doFindAll(DEFAULT_REQUEST_ID);
	}
	
	/**
	 * @param limit
	 * @param requestId
	 */
	public default void  doFindAll (int limit, int requestId) {
		Thread t = new Thread( () ->  {
			try {
				List<T> data = this.findAll(limit);
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onFindAll(data, requestId);
				}
			} catch (DAOException e) {
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onError(e, requestId);
				}
			}
			
		}, this.getClass().getSimpleName()+"_findAll_"+System.currentTimeMillis());
		t.start();
	}
	
	/**
	 * 
	 * @param limit
	 * @param offset
	 * @param requestId
	 */
	public default void  doFindAll (int limit, int offset, int requestId) {
		Thread t = new Thread( () ->  {
			try {
				List<T> data = this.findAll(limit, offset);
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onFindAll(data, requestId);
				}
			} catch (DAOException e) {
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onError(e, requestId);
				}
			}
		}, this.getClass().getSimpleName()+"_findAll_"+System.currentTimeMillis());
		t.start();
	}
	
	/**
	 * Recuperation d'une intervale des donnees
	 * @param limit
	 * @return
	 */
	public default List<T> findAll(int limit) throws DAOException {
		return this.findAll(limit, 0);
	}
	
	/**
	 * Verifie s'il a des occurence serialiser en date en parametre
	 * @param recordDate
	 * @return
	 * @throws DAOException
	 */
	public default boolean checkByRecordDate (Date recordDate) throws DAOException {
		return this.checkByRecordDate(recordDate, recordDate);
	}
	
	/**
	 * verifie s'il y a des occureces serialiser en intervale de date en parametre
	 * @param dateMin
	 * @param dateMax
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByRecordDate (Date dateMin, Date dateMax) throws DAOException;
	
	/**
	 * verifie s'il y a des occurences serialiser pour les intervale de selection en parametre
	 * @param dateMin
	 * @param dateMax
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByRecordDate (Date dateMin, Date dateMax, int limit, int offset) throws DAOException;
	
	/**
	 * compte les occures en date en parametre
	 * @param recordDate
	 * @return
	 * @throws DAOException
	 */
	public default int countByRecordDate (Date recordDate) throws DAOException{
		return this.countByRecordDate(recordDate, recordDate);
	}
	
	/**
	 * Compte les occurences serialiser dans l'intervale de date en parmetre
	 * @param dateMin
	 * @param dateMax
	 * @return
	 * @throws DAOException
	 */
	public int countByRecordDate (Date dateMin, Date dateMax) throws DAOException;
	
	/**
	 * Recuperation des occurence enregistrer en date en paramere
	 * @param recordDate
	 * @return
	 * @throws DAOException
	 */
	public default List<T> findByRecordDate (Date recordDate) throws DAOException {
		return this.findByRecordDate(recordDate, recordDate);
	}
	
	/**
	 * recuperation des elements enregistrer en une intervale de date donnee
	 * @param dateMin
	 * @param dateMax
	 * @return
	 * @throws DAOException
	 */
	public List<T> findByRecordDate (Date dateMin, Date dateMax) throws DAOException;
	
	/**
	 * renvoie les occurences enregistrer en une intervale de date en parametre, en limitant la selection
	 * @param dateMin
	 * @param dateMax
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	public default List<T> findByRecordDate (Date dateMin, Date dateMax, int limit) throws DAOException{
		return this.findByRecordDate(dateMin, dateMax, limit, 0);
	}
	/**
	 * renveoie les occurences serialiser en une intervale de date en parametre.
	 * @param dateMin
	 * @param dateMax
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	public List<T> findByRecordDate (Date dateMin, Date dateMax, int limit, int offset) throws DAOException;
	
	/**
	 * surcharge recuperation d'une intervale des donnees
	 * @param limit
	 * @param offset
	 * @return
	 */
	public List<T> findAll(int limit, int offset) throws DAOException ;
	
	/**
	 * Ajout d'un ecouteur au DAO
	 * @param listener
	 */
	default void addListener (DAOListener<T> listener) {
		this.getListeners().add(listener);
	}
	
	/**
	 * retire l'ecouteur dont la refernce est en parametre
	 * @param listener
	 */
	default void removeListener (DAOListener<T> listener) {
		this.getListeners().remove(listener);
	}
	
	/**
	 * retire tout les ecouteurs
	 */
	default void clearListener () {
		this.getListeners().clear();
	}
	
	/**
	 * @return
	 */
	public List<DAOListener<T>> getListeners ();
	
	/**
	 * demande de synchronisation des ecouteurs
	 */
	public default void reload () {
		this.reload(DEFAULT_REQUEST_RELOAD_ID);
	}
	
	/**
	 * @param requestId
	 */
	public default void reload(int requestId) {
		Thread t = new Thread(() ->  {
			try {
				List<T> all = this.findAll(DEFAULT_LIMIT);
				for (DAOListener<T> ls : this.getListeners()) {
					ls.onFindAll(all, requestId);
				}				
			} catch (DAOException e) {
				for (DAOListener<T> lis : this.getListeners()) {
					lis.onError(e, requestId);
				}
			}
		});
		
		t.start();
	}
}
