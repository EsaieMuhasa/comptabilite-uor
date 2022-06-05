/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentLocation;

/**
 * @author Esaie MUHASA
 *
 */
public interface OutlayDao extends DAOInterface<Outlay>, BaseStatistic<Outlay> {
	
	
	/**
	 * Verification de l'existance des operations pour l'annee academique en parametre 
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAcademicYear (long yearId) throws DAOException;
	
	/**
	 * Verification de l'existance des operations de sorties pour une annee academique 
	 * pour l'intervale de date en parmatre
	 * @param yearId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAcademicYear (long yearId, Date min, Date max)  throws DAOException;
	
	/**
	 * Verification des operations pour une annee academique, en une date precise
	 * @param yearId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYear (long yearId, Date date) throws DAOException {
		return checkByAcademicYear(yearId, date, date) ;
	}
	
	/**
	 * comptage des operations faite pendant une annee academique
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long yearId) throws DAOException;
	
	/**
	 * Comptage des operations faite pour une date d'une annee academique
	 * @param yearId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default int countByAcademicYear (long yearId, Date date) throws DAOException {
		return countByAcademicYear(yearId, date, date);
	}
	
	/**
	 * Comptage des operations faite pendant une annee academique pour une intervale de date en parametre
	 * @param yearId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long yearId, Date min, Date max)  throws DAOException;
	
	/**
	 * recuperationd es operations faite pendant une annee acadmique
	 * @param yearId
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAcademicYear (long yearId) throws DAOException;
	default List<Outlay> findByAcademicYear (AcademicYear year) throws DAOException{
		return findByAcademicYear(year.getId());
	}
	
	/**
	 * Recuperation d'une partie des operations faite pendant une annee academique
	 * @param yearId
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAcademicYear (long yearId, int limit, int offset) throws DAOException;
	default List<Outlay> findByAcademicYear (AcademicYear year, int limit, int offset) throws DAOException{
		return findByAcademicYear(year.getId(), limit, offset);
	}
	
	/**
	 * renvoie la collection des operations en une date d'une annee academique
	 * @param yearId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default List<Outlay> findByAcademicYear (long yearId, Date date) throws DAOException{
		return findByAcademicYear(yearId, date, date);
	}
	default List<Outlay> findByAcademicYear (AcademicYear year, Date date) throws DAOException{
		return findByAcademicYear(year.getId(), date, date);
	}
	
	/**
	 * renvoie la collection des operations en une intervale de date pour une annee academique
	 * @param yearId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAcademicYear (long yearId, Date min, Date max) throws DAOException;
	default List<Outlay> findByAcademicYear (AcademicYear year, Date min, Date max) throws DAOException{
		return findByAcademicYear(year.getId(), min, max);
	}
	
	/**
	 * Renvoie une collection des operation pour une intervale de date d'une anneee academique
	 * on recupere une partie des operations correspondant aux critere de selection dans la BDD
	 * @param yearId
	 * @param min
	 * @param max
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAcademicYear (long yearId, Date min, Date max, int limit, int offset) throws DAOException;
	default List<Outlay> findByAcademicYear (AcademicYear year, Date min, Date max, int limit, int offset) throws DAOException {
		return findByAcademicYear(year.getId(), min, max, limit, offset);
	}
	
	//==verification des operations sur un compte
	boolean checkByAccount (long accountId) throws DAOException;
	default boolean checkByAccount (AnnualSpend account) throws DAOException{
		return checkByAccount(account.getId());
	}
	
	/**
	 * verification des operations sur compte dans un lieux
	 * @param account
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAccount (long account, long location) throws DAOException;
	default boolean checkByAccount (AnnualSpend account, PaymentLocation location) throws DAOException {
		return checkByAccount(account.getId(), location.getId());
	}
	
	/**
	 * comptage des operations faite sur un compte dans un lieux
	 * @param account
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	int countByAccount (long account, long location) throws DAOException;
	default int countByAccount (AnnualSpend account, PaymentLocation location) throws DAOException {
		return countByAccount(account.getId(), location.getId());
	}

	default boolean checkByAccount (AnnualSpend account, Date date) throws DAOException {
		return checkByAccount(account.getId(), date, date);
	}
	default boolean checkByAccount (long accountId, Date date) throws DAOException {
		return checkByAccount(accountId, date, date);
	}
	
	boolean checkByAccount (long accountId, Date min, Date max) throws DAOException;
	default boolean checkByAccount (AnnualSpend account, Date min, Date max) throws DAOException {
		return checkByAccount(account.getId(), min, max);
	}
	//==\\
	
	/**
	 * Selection des operations qui touche un compte
	 * @param account
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAccount (AnnualSpend account) throws DAOException;
	default List<Outlay> findByAccount (long accountId) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualSpendDao.class).findById(accountId));
	}
	
	/**
	 * renvoie la collection des operations faite sur un compte en un lieux
	 * @param account
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAccount (AnnualSpend account, PaymentLocation location) throws DAOException;
	default List<Outlay> findByAccount (long account, long location) throws DAOException {
		return findByAccount(
				getFactory().findDao(AnnualSpendDao.class).findById(account),
				getFactory().findDao(PaymentLocationDao.class).findById(location));
	}
	
	/**
	 * renvoie la collection des operations faites sur un compte
	 * @param account
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAccount (AnnualSpend account, int limit, int offset) throws DAOException;
	default List<Outlay> findByAccount (long accountId, int limit, int offset) throws DAOException{
		return findByAccount(getFactory().findDao(AnnualSpendDao.class).findById(accountId), limit, offset);
	}

	/**
	 * renvoie la collection d'operation faite sur un compte en une date
	 * @param account
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default List<Outlay> findByAccount (AnnualSpend account, Date date) throws DAOException {
		return findByAccount(account, date, date);
	}
	
	/**
	 * renvoie la collection d'operation faite sur un compte en une date
	 * @param accountId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default List<Outlay> findByAccount (long accountId, Date date) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualSpendDao.class).findById(accountId), date, date);
	}
	
	/**
	 * Renvoie le operation faite en un compte pour une intervale de temps
	 * @param account
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List<Outlay> findByAccount (AnnualSpend account, Date min, Date max) throws DAOException;
	default List<Outlay> findByAccount (long accountId, Date min, Date max) throws DAOException {
		return findByAccount(getFactory().findDao(AnnualSpendDao.class).findById(accountId), min, max);
	}
	
	/**
	 * renvie le solde des sorties pour le compte en premier paramtre dans le lieux en deuxieme paramtre
	 * @param account
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	double getSoldByAccount (long account, long location) throws DAOException;
	default double getSoldByAccount (AnnualSpend account, PaymentLocation location) throws DAOException {
		return getSoldByAccount(account.getId(), location.getId());
	}
	
	/**
	 * renvoie le solde des sorties pour un compte
	 * @param account
	 * @return
	 * @throws DAOException
	 */
	double getSoldByAccount (long account) throws DAOException;
	default double getSoldByAccount (AnnualSpend account) throws DAOException {
		return getSoldByAccount(account.getId());
	}
	
	/**
	 * Renvoie le sold du montant deja retirer pour tout ces comptes en un lieux de payement
	 * @param accounts
	 * @param location
	 * @return
	 * @throws DAOException
	 */
	double getSoldByAccounts (long [] accounts, long location) throws DAOException;
	
}
