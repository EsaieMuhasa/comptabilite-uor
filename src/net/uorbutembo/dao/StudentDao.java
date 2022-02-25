/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.Student;

/**
 * @author Esaie MUHASA
 *
 */
public interface StudentDao extends UserDao<Student> {
	
	/**
	 * Pour effectuer des recherches aproximative dans la table etudiants
	 * @param values
	 * @return
	 * @throws DAOException
	 */
	public List<Student> search (String [] values) throws DAOException;

}
