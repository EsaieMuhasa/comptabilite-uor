/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.Department;

/**
 * @author Esaie MUHASA
 *
 */
public interface DepartmentDao extends OrientationDao<Department> {
	
	/**
	 * Renvoie un reference vers l'instance du DAO des faculte
	 * @return
	 */
	public FacultyDao getFacultyDao () ;
	
	/**
	 * verifie si la faculte a aumon un departement
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByFaculty (int facultyId)  throws DAOException{
		return this.check("faculty", facultyId);
	}
	
	/**
	 * Renvoie le nombre de departements dans une faculte
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	public int countByFaculty (int facultyId) throws DAOException;
	
	/**
	 * renvoie le departements d'une faculte
	 * @param facultyId
	 * @return
	 * @throws DAOException
	 */
	public List<Department> findByFaculty (int facultyId) throws DAOException;
}
