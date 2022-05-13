/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.DBEntity;
import net.uorbutembo.beans.DefaultRecipePart;
import net.uorbutembo.beans.RecipePart;

/**
 * @author Esaie MUHASA
 *
 */
public interface RecipePartDao <T extends RecipePart<S>, H extends DefaultRecipePart<S>, S extends DBEntity> extends BaseStatistic<H>, DAOInterface<H>{
	/**
	 * Renvoie la collection des montants mis au compte en parametre
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpend (AnnualSpend spend) throws DAOException;
	
	/**
	 * Renvoie les operations qui pointe vers la sources en parametre
	 * @param source
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySource (S source) throws DAOException;
	
	/**
	 * renvoie une partie des operations concernant un compte
	 * @param spend
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpend (AnnualSpend spend, int limit, int offset) throws DAOException;
	
	/**
	 * Renvoie la collection des operations faites sur un compte en une intervale de temps
	 * @param spend
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpendAtDate (AnnualSpend spend, Date min, Date max) throws DAOException;
	
	/**
	 * renvoie la collection des operations faites sur un compte avant la date en 2em parametre
	 * @param spend
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default List<T> findBySpendAtDate (AnnualSpend spend, Date date) throws DAOException {
		return findBySpendAtDate(spend, date, date);
	}
	
	/**
	 * renvoie la collection des operations sur un compte avant une date
	 * @param spend
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpendBeforDate (AnnualSpend spend, Date date) throws DAOException;
	
	/**
	 * Verifie s'il y a deja aumoin une miette mis au compte en parametres
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	boolean checkBySpend (AnnualSpend spend) throws DAOException;
	
	/**
	 * Comptage des operation concernant un compte
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	int countBySpend (AnnualSpend spend) throws DAOException;
}
