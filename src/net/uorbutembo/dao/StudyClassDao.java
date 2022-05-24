/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 *
 */
public interface StudyClassDao extends OrientationDao<StudyClass> {
	
	/**
	 * Verifie s'il y a des orientations qui ont ete configurer pour fonctionner pour l'annee en parametre
	 * @param year
	 * @param department
	 * @return
	 * @throws DAOException
	 */
	boolean checkByAcademicYear (AcademicYear year, Department department) throws DAOException;
	
	/**
	 * Comptage des orientations qui ont fonctionner pour l'annee en parametre
	 * @param year
	 * @param department
	 * @return
	 * @throws DAOException
	 */
	int countByAcademicYear (AcademicYear year, Department department) throws DAOException;
	
	/**
	 * renvoie la collection des orientations qui ont fonctionner pour l'annee en paramere
	 * @param year
	 * @param department
	 * @return
	 * @throws DAOException
	 */
	List<StudyClass> findByAcademicYear (AcademicYear year, Department department) throws DAOException;
}
