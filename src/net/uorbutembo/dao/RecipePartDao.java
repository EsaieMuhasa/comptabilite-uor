/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.DBEntity;
import net.uorbutembo.beans.DefaultRecipePart;
import net.uorbutembo.beans.PaymentLocation;
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
	 * Verification si la source a une refference de repartition
	 * @param source
	 * @return
	 * @throws DAOException
	 */
	boolean checkBySource (S source) throws DAOException;
	
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
	 * renvoie la collection des operations faite dans un leux sur un compte dans leux de payement 
	 * en une intervale de date
	 * @param spend
	 * @param location
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpendAtDate (AnnualSpend spend, PaymentLocation location, Date min, Date max) throws DAOException;
	
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
	 * renvoie la collection des operation sur un compte en un leux de payement en une date
	 * @param spend
	 * @param location
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default List<T> findBySpendAtDate (AnnualSpend spend, PaymentLocation location, Date date) throws DAOException {
		return findBySpendAtDate(spend, location, date, date);
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
	 * renvoie la collection des operations faite sur un compte dans un leux de payement 
	 * avant la date en troixieme parametre
	 * @param spend
	 * @param location
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpendBeforDate (AnnualSpend spend, PaymentLocation location, Date date) throws DAOException;
	
	/**
	 * Verifie s'il y a deja aumoin une miette mis au compte en parametres
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	boolean checkBySpend (AnnualSpend spend) throws DAOException;
	
	/**
	 * verification de l'existance des donnees
	 * @param spend
	 * @param offset nombre d'occurence a sauter
	 * @return
	 * @throws DAOException
	 */
	boolean checkBySpend (AnnualSpend spend, int offset) throws DAOException;
	
	/**
	 * verification des operations sur un compte dans un lieux de payment
	 * @param spend
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	boolean checkBySpend (AnnualSpend spend, PaymentLocation location) throws DAOException;
	
	/**
	 * compteage des operation dans un lieux de payement
	 * @param spend
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	int countBySpend (AnnualSpend spend, PaymentLocation location) throws DAOException;
	
	/**
	 * Renvoie la collection des operations qui ont ete faite sur compte dans un lieux de payement
	 * @param spend
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpend (AnnualSpend spend, PaymentLocation location) throws DAOException;
	
	/**
	 * renvoie la somme des recettes pour un compte en un lieux de payement
	 * @param spend
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	double getSoldBySpend (AnnualSpend spend, PaymentLocation location) throws DAOException;
	
	/**
	 * renvoie le montant total qui aurait entrer dans un compte en une date
	 * @param spend
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	double getSoldBySpendAtDate (AnnualSpend spend, Date date) throws DAOException;
	
	/**
	 * Renvoie le solde d'un compte avant la date en deuxieme parametre (inclue la date en deuxieme parametre)
	 * @param spend
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	double getSoldBySpendBeforDate (AnnualSpend spend, Date date) throws DAOException;
	
	/**
	 * Renvoie la collection des operations qui ont ete faite sur un compte dans un lieux.
	 * (7x6 avec intervale de selection)
	 * @param spend
	 * @param location
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<T> findBySpend (AnnualSpend spend, PaymentLocation location, int limit, int offset) throws DAOException;
	
	/**
	 * Comptage des operation concernant un compte
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	int countBySpend (AnnualSpend spend) throws DAOException;
}
