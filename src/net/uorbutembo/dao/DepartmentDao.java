/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;

/**
 * @author Esaie MUHASA
 *
 */
public interface DepartmentDao extends OrientationDao<Department> {
	
	/**
	 * verifie si la faculte a aumon un departement
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByFaculty (long facultyId)  throws DAOException{
		return this.check("faculty", facultyId);
	}
	
	/**
	 * Renvoie le nombre de departements dans une faculte
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	public int countByFaculty (long facultyId) throws DAOException;
	
	/**
	 * renvoie le departements d'une faculte
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	public List<Department> findByFaculty (long facultyId) throws DAOException;
	
	/**
	 * Renvoie les departements de la faculte en parametre
	 * @param faculty
	 * @return
	 * @throws DAOException
	 */
	public List<Department> findByFaculty (Faculty faculty) throws DAOException;
	
	/**
	 * Renvoie le departements qui on fonctionner dans une faculte pour l'annee en dexieme parametre
	 * @param faculty
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public List<Department> findByFaculty (Faculty faculty, AcademicYear year) throws DAOException;
	
	/**
	 * Compte de nombre departement fonctionnel dans une faculte pour une annee academique precis
	 * @param faculty
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public int countByFaculty (Faculty faculty, AcademicYear year) throws DAOException;
	
	/**
	 * verifie s'il y aumoin un departement fonctionnel pour la faculté, pour une annee academique 
	 * @param faculty
	 * @param year
	 * @return
	 * @throws DAOException
	 */
	public boolean checkByFaculty (Faculty faculty, AcademicYear year) throws DAOException;
}
