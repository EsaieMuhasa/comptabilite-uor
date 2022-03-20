package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.DBEntity;

/**
 * 
 * @author Esaie MUHASA
 *
 * @param <T>
 */
public interface DAOListener <T extends DBEntity> {
	
	/**
	 * creation d'une nouvelle occurence
	 * @param e
	 * @param requestId
	 */
	void onCreate(T e, int requestId);
	
	/**
	 * Lors de la creation d'un collection d'occurence
	 * @param e
	 * @param requestId
	 */
	void onCreate (T[] e, int requestId);
	
	/**
	 * Mis en jour d'une nouvelle occurence
	 * @param e
	 * @param requestId
	 */
	void onUpdate(T e, int requestId);
	
	/**
	 * Lors de la mise en jour d'une collection d'occurence
	 * @param e
	 * @param requestId
	 */
	void onUpdate(T[] e, int requestId);
	
	/**
	 * Supression d'une occurence dans la bdd
	 * @param e
	 * @param requestId
	 */
	void onDelete(T e, int requestId);
	
	/**
	 * Lors d'une supression multiple
	 * @param e
	 * @param requestId
	 */
	void onDelete(T [] e, int requestId);
	
	/**
	 * relors du resultat de recherche d'une occurence unique
	 * @param e
	 * @param requestId
	 */
	void onFind (T e, int requestId);
	
	void onCheck (boolean check, int requestId);
	
	/**
	 * Lors du resultat de recherche d'une collection d'occurence
	 * @param e
	 * @param requestId
	 */
	void onFind (List<T> e, int requestId);
	
	/**
	 * Lorsque l'erreur surviens dans un processuce de communication avec le SGBD
	 * @param e
	 * @param requestId
	 */
	void onError (DAOException e, int requestId);

}
