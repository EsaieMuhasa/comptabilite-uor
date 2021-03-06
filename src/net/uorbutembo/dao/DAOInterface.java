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
	 * Doit renvoyer une reference vers le factory des DAO
	 * @return
	 */
	DAOFactory getFactory ();
	
	/**
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	boolean check (String columnName, Object value) throws DAOException;
	
	/**
	 * 
	 * @param columnName
	 * @param value
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	boolean check (String columnName, Object value, long id) throws DAOException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkById (long id) throws DAOException {
		return check("id", id);
	}
	
	/**
	 * creation d'une occurence.
	 * la communication avec le DAO se fait dans le meme thread
	 * @param e
	 */
	void create (T e) throws DAOException;
	
	/**
	 * creation d'un tableau d'occurence
	 * @param t
	 * @throws DAOException
	 */
	void create (T [] t) throws DAOException;
	
	/**
	 * cration dans un nouvea thread
	 * @param e
	 * @param requestId l'identifiant a retransmetre de l'evenement de syncronisation apres execution de la requette
	 */
	void doCreate (T e, int requestId);
	
	/**
	 * surcharge cration d'une occurence dans un nouveau thread
	 * @param e
	 */
	default void doCreate(T e) {
		doCreate(e, DEFAULT_REQUEST_ID);
	}
	
	
	/**
	 * mise en jour d'une occurence.
	 * l'execution de la mis en jour c fait ds le meme thread
	 * @param e
	 * @param id
	 * @throws DAOException
	 */
	void update (T e, long id) throws DAOException;
	
	/**
	 * mis en jours d'un collection d'occurences
	 * @param e
	 * @param id
	 * @throws DAOException
	 */
	default void update (T[] e, long [] id) throws DAOException{
		throw new DAOException("Mis en jours multiple non pris en charge");
	}
	
	/**
	 * mis en jour
	 * la tache c fait dans un nouveau thread
	 * @param e
	 * @param id
	 * @param requestId
	 */
	void doUpdate(T e, long id, int requestId);
	
	/**
	 * @param e
	 * @param id
	 */
	default void doUpdate(T e, long id) {
		doUpdate(e, id, DEFAULT_REQUEST_ID);
	}
	
	/**
	 * @param id
	 * @throws DAOException
	 */
	void delete (long id)  throws DAOException;
	
	/**
	 * Supression d'une collection d'occurences
	 * @param ids
	 * @throws DAOException
	 */
	void delete (long [] ids)  throws DAOException;
	
	/**
	 * @param id
	 * @param requestId
	 */
	void doDelete(long id, int requestId) ;
	
	/***
	 * revoie une occurence pour la colone de filtrage
	 * N.B: l'occurence doit etre unique, si une exception sera lever
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	T find (String columnName, Object value)  throws DAOException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default T findById (long id)  throws DAOException {
		return find("id", id);
	}
	
	/**
	 * @return
	 * @throws DAOException
	 */
	List<T>  findAll ()  throws DAOException;
	
	/**
	 * selection d'une collection d'occurence dont leurs ID sont connue
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	List<T>  findAll (long ...ids)  throws DAOException;
	
	/**
	 * compte tout les occures
	 * @return
	 * @throws DAOException
	 */
	int countAll () throws DAOException;
	
	/**
	 * @param requestId
	 */
	void  doFindAll (int requestId);
	
	/**
	 * 
	 */
	default void  doFindAll () {
		doFindAll(DEFAULT_REQUEST_ID);
	}
	
	/**
	 * @param limit
	 * @param requestId
	 */
	void doFindAll (int limit, int requestId);
	
	/**
	 * 
	 * @param limit
	 * @param offset
	 * @param requestId
	 */
	void doFindAll (int limit, int offset, int requestId);
	
	/**
	 * Recuperation d'une intervale des donnees
	 * @param limit
	 * @return
	 */
	default List<T> findAll(int limit) throws DAOException {
		return findAll(limit, 0);
	}
	
	/**
	 * Verifie s'il a des occurence serialiser en date en parametre
	 * @param recordDate
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByRecordDate (Date recordDate) throws DAOException {
		return checkByRecordDate(recordDate, recordDate);
	}
	
	/**
	 * verifie s'il y a des occureces serialiser en intervale de date en parametre
	 * @param dateMin
	 * @param dateMax
	 * @return
	 * @throws DAOException
	 */
	boolean checkByRecordDate (Date dateMin, Date dateMax) throws DAOException;
	
	/**
	 * verifie s'il y a des occurences serialiser pour les intervale de selection en parametre
	 * @param dateMin
	 * @param dateMax
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	boolean checkByRecordDate (Date dateMin, Date dateMax, int limit, int offset) throws DAOException;
	
	/**
	 * compte les occures en date en parametre
	 * @param recordDate
	 * @return
	 * @throws DAOException
	 */
	default int countByRecordDate (Date recordDate) throws DAOException{
		return countByRecordDate(recordDate, recordDate);
	}
	
	/**
	 * Compte les occurences serialiser dans l'intervale de date en parmetre
	 * @param dateMin
	 * @param dateMax
	 * @return
	 * @throws DAOException
	 */
	int countByRecordDate (Date dateMin, Date dateMax) throws DAOException;
	
	/**
	 * Recuperation des occurence enregistrer en date en paramere
	 * @param recordDate
	 * @return
	 * @throws DAOException
	 */
	default List<T> findByRecordDate (Date recordDate) throws DAOException {
		return findByRecordDate(recordDate, recordDate);
	}
	
	/**
	 * recuperation des elements enregistrer en une intervale de date donnee
	 * @param dateMin
	 * @param dateMax
	 * @return
	 * @throws DAOException
	 */
	List<T> findByRecordDate (Date dateMin, Date dateMax) throws DAOException;
	
	/**
	 * renvoie les occurences enregistrer en une intervale de date en parametre, en limitant la selection
	 * @param dateMin
	 * @param dateMax
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	default List<T> findByRecordDate (Date dateMin, Date dateMax, int limit) throws DAOException{
		return findByRecordDate(dateMin, dateMax, limit, 0);
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
	List<T> findByRecordDate (Date dateMin, Date dateMax, int limit, int offset) throws DAOException;
	
	/**
	 * surcharge recuperation d'une intervale des donnees
	 * @param limit
	 * @param offset
	 * @return
	 */
	List<T> findAll(int limit, int offset) throws DAOException ;
	
	/**
	 * Ajout d'un ecouteur au DAO
	 * @param listener
	 */
	void addListener (DAOListener<T> listener) ;
	
	/**
	 * retire l'ecouteur dont la refernce est en parametre
	 * @param listener
	 */
	void removeListener (DAOListener<T> listener);
	
	/**
	 * retire tout les ecouteurs
	 */
	void clearListener ();
	
	/**
	 * abonnement d'un ecouteur de progression des taches lorde
	 * @param listener
	 */
	void addProgressListener (DAOProgressListener<T> listener);
	
	/**
	 * desabonnement d'un ecouteur de progression d'une tache lorde
	 * @param listener
	 */
	void removeProgressListener (DAOProgressListener<T> listener);
	
	/**
	 * desabonneement de tout les ecouteur de progression des taches lourdes
	 */
	void clearProgressListener ();
	
	/**
	 * demande de synchronisation des ecouteurs
	 */
	default void reload () {
		reload(DEFAULT_REQUEST_RELOAD_ID);
	}
	
	/**
	 * Esque le DAO est encour de rechargement de donnee??
	 * @return
	 */
	default boolean isReload() {
		return false;
	}
	
	/**
	 * @param requestId
	 */
	default void reload(int requestId) {
		throw new DAOException("Cette methode n'est pas prise en charge par ce DAO");
	}
}
