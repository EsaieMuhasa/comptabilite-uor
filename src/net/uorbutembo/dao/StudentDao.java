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
	List<Student> search (String [] values) throws DAOException;
	
	/**
	 * Recuperation de l'etudiant dont le matricule est en parametre
	 * @param matricul
	 * @return
	 * @throws DAOException
	 */
	Student findByMatricul (String matricul) throws DAOException;
	
	/**
	 * Verification de l'existance du numero matricule dans le BDD
	 * @param matricul
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByMatricul (String matricul) throws DAOException {
		return check("matricul", matricul);
	}
	
	/**
	 * Verification de l'existance du numero matricule dans la BDD, en ignorant l'occurence dont l'id est en 2em param
	 * @param matricul
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	default boolean checkByMatricul (String matricul, long id) throws DAOException {
		return check("matricul", matricul, id);
	}

}
