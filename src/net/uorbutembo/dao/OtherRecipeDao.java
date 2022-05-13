/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.OtherRecipe;

/**
 * @author Esaie MUHASA
 *
 */
public interface OtherRecipeDao extends DAOInterface<OtherRecipe>, BaseStatistic<OtherRecipe>{
	
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
}
