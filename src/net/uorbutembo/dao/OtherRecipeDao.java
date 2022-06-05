/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.PaymentLocation;

/**
 * @author Esaie MUHASA
 *
 */
public interface OtherRecipeDao extends DAOInterface<OtherRecipe>, BaseStatistic<OtherRecipe>{
	
	/**
	 * verification du numero du recu en caisse
	 * @param receivedNumber
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByReceivedNumber (int receivedNumber) throws DAOException {
		return check("receivedNumber", receivedNumber);
	}
	
	/**
	 * Renvoie l'occurence qui fais reference au numero du recu en parametre
	 * @param receivedNumber
	 * @return
	 * @throws DAOException
	 */
	OtherRecipe findByReceivedNumber (int receivedNumber) throws DAOException;
	
	/**
	 * Verifie l'existance les recettes concernat un compte
	 * @param accountId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAccount (long accountId) throws DAOException;
	default boolean checkByAccount (AnnualRecipe account) throws DAOException {
		return checkByAccount(account.getId());
	}
	
	/**
	 * Comptage des operations convernat un compte
	 * @param accountId
	 * @return
	 * @throws DAOException
	 */
	int countByAccount (long accountId) throws DAOException;
	default int countByAccount (AnnualRecipe account) throws DAOException {
		return countByAccount(account.getId());
	}
	
	/**
	 * Comptage des operations effectuer en une intervale de date, sur un compte
	 * @param accountId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByAccount (long accountId, Date min, Date max) throws DAOException;
	default int countByAccount (long accountId, Date date) throws DAOException {
		return countByAccount(accountId, date, date);
	}
	
	/**
	 * Recuperation de la collection des operations d'entrer dans une compte
	 * @param account
	 * @return
	 * @throws DAOException
	 */
	List<OtherRecipe> findByAccount (AnnualRecipe account) throws DAOException;
	default List<OtherRecipe> findByAccount (long accountId) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualRecipeDao.class).findById(accountId));
	}
	
	/**
	 * renvoie  la collection des operations faites sur un compte en un lieux de payment
	 * @param account
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	List<OtherRecipe> findByAccount (AnnualRecipe account, PaymentLocation location) throws DAOException;
	default List<OtherRecipe> findByAccount (long account, long location) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualRecipeDao.class).findById(account), getFactory().findDao(PaymentLocationDao.class).findById(location));
	}
	
	/**
	 * verification des operations dans un compte pour le lieux de payment en dexieme parametre
	 * @param account
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAccount (long account, long location) throws DAOException;
	default boolean checkByAccount (AnnualRecipe account, PaymentLocation location) throws DAOException {
		return checkByAccount(account.getId(), location.getId());
	}
	
	/**
	 * Verification des operations faite sur un compte en un lieux en une intervale de temps
	 * @param account
	 * @param location
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAccount (long account, long location, Date min, Date max) throws DAOException;
	default boolean checkByAccount (long account, long location, Date date) throws DAOException {
		return checkByAccount(account, location, date, date);
	}
	default boolean checkByAccount (AnnualRecipe account, PaymentLocation location, Date date) throws DAOException {
		return checkByAccount(account.getId(), location.getId(), date);
	}
	default boolean checkByAccount (AnnualRecipe account, PaymentLocation location, Date min, Date max) throws DAOException {
		return checkByAccount(account.getId(), location.getId(), min, max);
	}
	
	/**
	 * Comptage des operations faire sur un compte en un lieux en une intervale de temps
	 * @param account
	 * @param location
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByAccount (long account, long location, Date min, Date max) throws DAOException;
	default int countByAccount (long account, long location, Date date) throws DAOException {
		return countByAccount(account, location, date, date);
	}
	default int countByAccount (AnnualRecipe account, PaymentLocation location, Date date) throws DAOException {
		return countByAccount(account.getId(), location.getId(), date);
	}
	default int countByAccount (AnnualRecipe account, PaymentLocation location, Date min, Date max) throws DAOException {
		return countByAccount(account.getId(), location.getId(), min, max);
	}
	
	/**
	 * Renvoie le sold de tout ce compte en un lieux de payement
	 * (montant deja recu pour chaque compte en un leiux de payment)
	 * @param accounts
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	double getSoldByAccounts (long [] accounts, long location) throws DAOException;
	
	/**
	 * selection des operations faite sur un compte en un leux en une intervale de date
	 * @param account
	 * @param location
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List<OtherRecipe> findByAccount (AnnualRecipe account, PaymentLocation location, Date min, Date max) throws DAOException;
	default List<OtherRecipe> findByAccount (AnnualRecipe account, PaymentLocation location, Date date) throws DAOException{
		return findByAccount(account, location, date, date);
	}
	default List<OtherRecipe> findByAccount (long account, long location, Date min, Date max) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualRecipeDao.class).findById(account), getFactory().findDao(PaymentLocationDao.class).findById(location), min, max);
	}
	default List<OtherRecipe> findByAccount (long account, long location, Date date) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualRecipeDao.class).findById(account), getFactory().findDao(PaymentLocationDao.class).findById(location), date, date);
	}
	
	/**
	 * Renvoie les operations effectuer en une intervale de temps, sur une compte
	 * @param account
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List<OtherRecipe> findByAccount (AnnualRecipe account, Date min, Date max) throws DAOException;
	default List<OtherRecipe> findByAccount (AnnualRecipe account, Date date) throws DAOException{
		return findByAccount(account, date, date);
	}
	default List<OtherRecipe> findByAccount (long accountId, Date min, Date max) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualRecipeDao.class).findById(accountId), min, max);
	}
	default List<OtherRecipe> findByAccount (long accountId, Date date) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualRecipeDao.class).findById(accountId), date, date);
	}
	
	/**
	 * verification de perception d'une recette en un endroit 
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLocation(long location) throws DAOException;
	default boolean checkByLocation(PaymentLocation location) throws DAOException {
		return checkByLocation(location.getId());
	}
	
	/**
	 * verification de la perception d'une recette en un leux
	 * @param location
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLocation(long location, Date min, Date max) throws DAOException;
	default boolean checkByLocation(long location, Date date) throws DAOException {
		return checkByLocation(location, date, date);
	}
	default boolean checkByLocation(PaymentLocation location, Date min, Date max) throws DAOException {
		return checkByLocation(location.getId(), min, max);
	}
	
	/**
	 * verification de perception de l'argent en un leux
	 * @param location
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	boolean checkByLocation(long location, long year) throws DAOException;
	default boolean checkByLocation(PaymentLocation location, AcademicYear year) throws DAOException {
		return checkByLocation(location.getId(), year.getId());
	}
}
