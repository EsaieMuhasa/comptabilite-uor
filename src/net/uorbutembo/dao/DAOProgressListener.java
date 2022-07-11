/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
public interface DAOProgressListener <T extends DBEntity> {
	
	/**
	 * debut de progression du traitement
	 * @param event
	 */
	void onStart(int requestId);
	
	/**
	 * Lors de l'evolution des traitements
	 * @param current
	 * @param max
	 * @param message
	 * @param requestId
	 */
	void onProgress(int current, int max, String message, int requestId);
	
	/**
	 * lors qu'une erreur surviens dans le processuce de traitement
	 * @param e
	 * @param requestId
	 */
	void onError (DAOException e, int requestId);
	
	/**
	 * fin du traitement, aucune donnee a transmetre
	 * @param requestId
	 */
	void onFinish(int requestId);
	
	/**
	 * fin du traitement, avec une occurence a transmetre
	 * @param data
	 * @param requestId
	 */
	void onFinish(T data, int requestId);
	
	/**
	 * avec une collection d'occurence a transmetre
	 * @param requestId
	 * @param data
	 */
	void onFinish(List<T> data, int requestId);

}
