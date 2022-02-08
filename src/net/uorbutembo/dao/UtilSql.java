/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
abstract class UtilSql <T extends DBEntity> {
	
	private DefaultSqlDAOFactory factory;
	
	protected List<DAOListener<T>> listeners;

	/**
	 * @param factory
	 */
	public UtilSql(DefaultSqlDAOFactory factory) {
		super();
		this.factory = factory;
		this.listeners = new ArrayList<DAOListener<T>>();
	}

	/**
	 * @return the factory
	 */
	public DefaultSqlDAOFactory getFactory() {
		return factory;
	}

	
	/**
	 * fermeture de la connection
	 * @param connection
	 */
	protected void close (Connection connection) {
		if (connection!=null) {
			try {
				connection.close();
			} catch (SQLException e) {
				this.sendOnErrorEvent(new DAOException(e.getMessage(), e));
			}
		}
	}
	
	
	/**
	 * Liberation de 
	 * @param statement
	 */
	protected void close (Statement statement) {
		if(statement!=null) {
			try {
				statement.close();
			} catch (SQLException e) {
				this.sendOnErrorEvent(new DAOException(e.getMessage(), e));
			}
		}
	}
	
	/**
	 * Fermeture des ressource du resultset
	 * @param result
	 */
	protected void close (ResultSet result) {
		if (result!=null) {
			try {
				result.close();
			} catch (SQLException e) {
				this.sendOnErrorEvent(new DAOException(e.getMessage(), e));
			}
		}
	}
	
	/**
	 * Liberation de resource du PreparedStatement
	 * @param statement
	 */
	protected void close (PreparedStatement statement) {
		if(statement!=null) {
			try {
				statement.close();
			} catch (SQLException e) {
				this.sendOnErrorEvent(new DAOException(e.getMessage(), e));
			}
		}
	}
	
	/**
	 * Fermeture des ressouces
	 * @param statement
	 * @param result
	 */
	protected void close (PreparedStatement statement, ResultSet result) {
		close(statement);
		close(result);
	}
	
	/**
	 * Fermeture des ressouces
	 * @param statement
	 * @param result
	 */
	protected void close (Statement statement, ResultSet result) {
		close(statement);
		close(result);
	}
	
	/**
	 * Liberation des resources
	 * @param connection
	 * @param statement
	 */
	protected void close(Connection connection, Statement statement) {
		close(statement);
		close(connection);
	}
	
	/**
	 * Liberation des ressources apres communication avec la base de donnee
	 * @param connection
	 * @param statement
	 */
	protected void close(Connection connection, PreparedStatement statement) {
		close(statement);
		close(connection);
	}
	
	/**
	 * Liberation des resources
	 * @param connection
	 * @param statement
	 * @param result
	 */
	protected void close(Connection connection, Statement statement, ResultSet result) {
		close(statement, result);
		close(connection);
	}
	
	/**
	 * Liberation des resources
	 * @param connection
	 * @param statement
	 * @param result
	 */
	protected void close(Connection connection, PreparedStatement statement, ResultSet result) {
		close(statement, result);
		close(connection);
	}
	
	/**
	 * Preparation d'une requette
	 * @param SQL_QUERY la requette sql
	 * @param connection une referance vers la connexion
	 * @param autoGeneratedKeys si nous avons besoin de l'id auto-generer
	 * @param objects une colection des donnees a inserer dans la requette preparer
	 * @return
	 * @throws SQLException
	 */
	protected synchronized static PreparedStatement prepare (String SQL_QUERY, Connection connection,
			boolean autoGeneratedKeys, Object...objects) throws SQLException{
		
		PreparedStatement statement = connection.prepareStatement(SQL_QUERY, 
				autoGeneratedKeys? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		for (int i = 0; i < objects.length; i++) {
			statement.setObject(i+1, objects[i]);
		}
		return statement;
	}
	
	
	/**
	 * selection d'une occurence unique dans une table
	 * si plusieur occurence corespond au critere de selection, alors uniquement la premiere occurence est returner
	 * @param columnName
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	protected synchronized T selectSingle (String columnName, Object value) throws DAOException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		T entity = null;
		
		try {
			connection = this.getFactory().getConnection();
			statement = prepare("SELECT * FROM "+this.getTableName()+" WHERE "+columnName+" = ?", connection, false, value);
			result = statement.executeQuery();
			
			if(result.next()) {
				entity = this.maping(result);
			}else {
				throw new DAOException("Aucune occurence dans la table "+this.getTableName()+" ne correspond a "+columnName+" = "+value);
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		} finally {
			this.close(connection, statement, result);
		}
		return entity;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws DAOException
	 */
	protected synchronized List<T> selectAll () throws DAOException {
		List<T> entities = new ArrayList<T>();
		String sql = "SELECT * FROM "+this.getTableName();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result=null;
		try {
			connection=this.factory.getConnection();
			statement = connection.prepareStatement(sql);
			result = statement.executeQuery();
			if(result.next()) {
				entities.add(this.maping(result));
				while(result.next()) {
					entities.add(this.maping(result));
				}
			}else {
				DAOException ex =new DAOException("Aucunne donnée dans la table \""+this.getTableName()+"\"");
				throw ex;
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}finally {
			close(connection, statement, result);
		}
		return entities;
	}
	
	
	/**
	 * 
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	protected synchronized List<T> selectAll (int limit, int offset) throws DAOException {
		List<T> entities = new ArrayList<T>();
		String sql = "SELECT * FROM "+this.getTableName()+" LIMIT ?  OFFSET ?";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result=null;
		try {
			connection=this.factory.getConnection();
			statement = prepare(sql, connection, false, limit, offset);
			result = statement.executeQuery();
			if(result.next()) {
				entities.add(this.maping(result));
				while(result.next()) {
					entities.add(this.maping(result));
				}
			}else {
				DAOException ex =new DAOException("Aucunne donnée dans la table \""+this.getTableName()+"\"");
				throw ex;
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}finally {
			close(connection, statement, result);
		}
		return entities;
	}
	
	/**
	 * 
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	protected synchronized List<T> selectAll (int limit) throws DAOException {
		return this.selectAll(limit, 0);
	}
	
	
	/**
	 * Verification d'une valeur dans une colonne, en faisant abstraction a l'occurence dont l'id est en parametre
	 * @param tableName
	 * @param columnName
	 * @param columnValue
	 * @param idEntity
	 * @return
	 * @throws DAOException
	 */
	protected synchronized boolean columnValueExistInTable(String columnName, Object columnValue, int idEntity) throws DAOException{
		boolean isInTable = false;
		String sql = "SELECT "+columnName+" FROM "+this.getTableName()+" WHERE "+columnName+" = ? AND id != ? LIMIT 1";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result=null;
		try {
			connection=this.factory.getConnection();
			statement = prepare(sql, connection, false, columnValue, idEntity);
			result = statement.executeQuery();
			if(result.next()) {
				isInTable = true;
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}finally {
			close(connection, statement, result);
		}
		return isInTable;
	}
	
	protected synchronized boolean columnValueExistInTable(String columnName, Object columnValue) throws DAOException{
		return this.columnValueExistInTable(columnName, columnValue, 0);
	}
	
	/**
	 * Insersion des donnee dans une table de la base de donnee
	 * @param tableName le nom de la table
	 * @param columnsNames un tableau des noms de colones
	 * @param columnsValues
	 * @throws DAOException
	 */
	protected synchronized int insertInTable(String [] columnsNames, Object [] columnsValues) throws DAOException{
		String SQL_QUERY = "INSERT INTO "+this.getTableName()+" (", SQL_SUITE=" VALUES (";
		for (int i=0; i<columnsNames.length; i++) {
			SQL_QUERY += columnsNames[i]+(i!=(columnsNames.length-1)? ", ":", dateAjout)");
			SQL_SUITE += "?"+(i!=(columnsNames.length-1)? ", ":", NOW())");
		}
		
		SQL_QUERY += SQL_SUITE;
		
		int id=0;
		Connection connection = null;
		PreparedStatement statement=null;
		ResultSet result=null;
		
		try {
			connection = this.factory.getConnection();
			statement = prepare(SQL_QUERY, connection, true, columnsValues);
			int statut=statement.executeUpdate();
			if(statut==0) {
				throw new DAOException("Aucune occurence enregistrer. Veiller ré-éssayer svp!");
			}else {
				result= statement.getGeneratedKeys();
				if (result.next()) {
					id=result.getInt(1);
				}else {
					throw new DAOException("Echec de récuperation de l'ID auto-générer");
				}
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}finally {
			close(connection, statement, result);
		}
		return id;
	}
	
	/**
	 * Mise ajour d'une occurence de la table de la base de donnee
	 * @param columnsNames les noms de la colonnes a metre ajours
	 * @param columnsValues les valeurs de colones de l'occurence a metre a jours
	 * @param idEntity l'identifiant de l'occurence a metre a jours
	 * @param dateModif si on doit actualiser la date de modification
	 * @throws DAOException
	 */
	protected synchronized void updateInTable(String [] columnsNames, Object [] columnsValues, int idEntity, boolean dateModif) throws DAOException{
		String SQL_QUERY = "UPDATE "+this.getTableName()+" SET ";
		for (int i=0; i<columnsNames.length; i++) {
			SQL_QUERY += columnsNames[i]+(i!=(columnsNames.length-1)? ("= ?, ") : ("=? "+(dateModif? ", dateModif=NOW()":"")));
		}
		
		SQL_QUERY += " WHERE id="+idEntity;
		System.out.println(SQL_QUERY);
		Connection connection = null;
		PreparedStatement statement=null;
		
		try {
			connection = this.factory.getConnection();
			statement = prepare(SQL_QUERY, connection, false, columnsValues);
			int statut=statement.executeUpdate();
			if(statut==0) {
				throw new DAOException("Aucune mise ajours n'a été effectuer. Veiller re-essayer svp!");
			}
		} catch (SQLException e) {
			DAOException ex= new DAOException(e.getMessage(), e);
			throw ex;
		}finally {
			close(connection, statement);
		}
	}
	
	/**
	 * Utilitaire de supression definitive d'une occurence dans une table
	 * @param idEntity l'identifiant de l'occurence
	 * @throws DAOException
	 */
	protected synchronized void deleteFromTable(int idEntity) throws DAOException {
		String SQL_QUERY = "DELETE FROM "+this.getTableName()+" WHERE id= "+idEntity;
		Connection connection = null;
		PreparedStatement statement=null;
		
		try {
			connection = this.factory.getConnection();
			statement = connection.prepareStatement(SQL_QUERY);
			int statut=statement.executeUpdate();
			if(statut==0) {
				throw new DAOException("Aucune supression n'a été effectuer. Veiller ré-essayer svp!");
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}finally {
			close(connection, statement);
		}
	}
	
	/**
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	protected abstract T maping (ResultSet result) throws SQLException, DAOException;
	
	/**
	 * Revoie le nom de la table
	 * @return
	 */
	protected abstract String getTableName ();
	
	/**
	 * Treansmission d'un event los de la creation d'un occurence
	 * @param event
	 */
	protected synchronized void sendOnCreateEvent (T e) {
		this.sendOnCreateEvent(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void sendOnCreateEvent (T e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onCreate(e, DAOInterface.DEFAULT_REQUEST_ID);
		}
	}
	
	protected synchronized void sendOnUpdateEvent (T e) {
		this.sendOnUpdateEvent(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void sendOnUpdateEvent (T e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onUpdate(e, requestId);
		}
	}
	
	
	protected synchronized void sendOnDeleteEvent (T e) {
		this.sendOnDeleteEvent(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void sendOnDeleteEvent (T e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onDelete(e, requestId);
		}
	}
	
	protected synchronized void sendOnErrorEvent (DAOException e) {
		this.sendOnErrorEvent(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void sendOnErrorEvent (DAOException e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onError(e, requestId);
		}
	}

}
