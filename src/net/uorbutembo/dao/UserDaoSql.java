/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.SQLException;

import net.uorbutembo.beans.User;

/**
 * @author Esaie MUHASA
 *
 */
abstract class UserDaoSql <T extends User> extends UtilSql<T> implements UserDao<T> {

	public UserDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	
	@Override
	public void create(T t) throws DAOException {
		try (Connection connection = this.factory.getConnection()){
			this.create(connection, t);
			this.emitOnCreate(t);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void updatePicture(String picture, long id) throws DAOException {
		T t = this.findById(id);
		try {
			this.updateInTable(new String[] {"picture"}, new Object[] {picture}, id);
			t.setPicture(picture);
			this.emitOnUpdate(t);
		}catch (SQLException e) {
			throw new DAOException("Une erreur est survenue lors de la mise en jour de la photo de profil. \n"+e.getMessage(), e);
		}
	}

}
