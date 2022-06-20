/**
 * 
 */
package net.uorbutembo.dao;

import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
public interface BaseStatistic <T extends DBEntity> {
	
	//year
	List <T> findByAcademicYear (long academicYearId, int limit, int offset) throws DAOException;
	default List <T> findByAcademicYear (AcademicYear academicYear, int limit, int offset) throws DAOException{
		return findByAcademicYear(academicYear.getId(), limit, offset);
	}
	List <T> findByAcademicYear (long academicYearId) throws DAOException;
	default List <T> findByAcademicYear (AcademicYear academicYear) throws DAOException{
		return findByAcademicYear(academicYear.getId());
	}
	
	/**
	 * selection des operations enregistrer en une date
	 * @param academicYearId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default List <T> findByAcademicYear (long academicYearId, Date date) throws DAOException{
		return findByAcademicYear(academicYearId, date, date);
	}
	
	/***
	 * selection des operation enregistrers en une intervale de date
	 * @param academicYearId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	List <T> findByAcademicYear (long academicYearId, Date min, Date max) throws DAOException;
	
	/**
	 * comptage des operations enregistrer au compte d'une annee acadmeique 
	 * en une intervale des date
	 * @param academicYear
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	default List <T> findByAcademicYear (AcademicYear academicYear, Date min, Date max) throws DAOException{
		return findByAcademicYear(academicYear.getId(), min, max);
	}
	
	boolean checkByAcademicYear (long academicYearId) throws DAOException;
	default boolean checkByAcademicYear (AcademicYear academicYear) throws DAOException{
		return this.checkByAcademicYear(academicYear.getId());
	}
	
	/**
	 * Verification de l'existance des donnees pour une annee academique
	 * @param year
	 * @param offset, nombre d'occurrence a n'est pas prendre en compte lors de la verification
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYear (AcademicYear year, int offset) throws DAOException {
		throw new DAOException("Veuiller re-definir la methode checkByAcademicYear(AcademicYear year, int offset) dans la classe "+getClass().getName());
	}
	
	/**
	 * Comptage des operations au comptpe d'une anne academique
	 * @param academicYearId
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long academicYearId) throws DAOException;
	default int countByAcademicYear (AcademicYear academicYear) throws DAOException{
		return this.countByAcademicYear(academicYear.getId());
	}
	
	/**
	 * comptage des operations enregistrer au compte d'une annee academique
	 * @param academicYearId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (long academicYearId, Date min, Date max) throws DAOException;
	default int countByAcademicYear (long academicYearId, Date date) throws DAOException{
		return this.countByAcademicYear(academicYearId, date, date);
	}
	default int countByAcademicYear (AcademicYear academicYear, Date date) throws DAOException{
		return this.countByAcademicYear(academicYear.getId(), date, date);
	}
	default int countByAcademicYear (AcademicYear academicYear, Date min, Date max) throws DAOException{
		return this.countByAcademicYear(academicYear.getId(), min, max);
	}
	//== year
	
	/**
	 * Verification de tout les operations qui ont ete enregister avant la date en 2em param
	 * pour l'annee acaemique en premier param
	 * @param year
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByAcademicYearBeforDate (AcademicYear year, Date date)  throws DAOException {
		return checkByAcademicYearBeforDate(year.getId(), date);
	}
	
	/**
	 * Verification de l'existance des operaions avant la date en 2eme param
	 * pour l'annee indexer par la valeur en premier param
	 * @param yearId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAcademicYearBeforDate (long yearId, Date date)  throws DAOException;
	
	/**
	 * Renvoie la collection des payements effectuer avant la date en 2eme param
	 * @param yearId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	List<T> findByAcademicYearBeforDate (long yearId, Date date)  throws DAOException;
	
	default List<T> findByAcademicYearBeforDate (AcademicYear year, Date date)  throws DAOException {
		return findByAcademicYearBeforDate(year.getId(), date);
	}
}
