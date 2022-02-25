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
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
abstract class UtilSql <T extends DBEntity> implements DAOInterface<T> {
	
	protected DefaultSqlDAOFactory factory;
	
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
	@Override
	public DefaultSqlDAOFactory getFactory () {
		return factory;
	}
	
	/**
	 * Enregistrement d'une occurence dans une transaction deja demarer d'avence
	 * @param connection
	 * @param t
	 * @throws DAOException
	 */
	protected void create (Connection connection, T t) throws DAOException{
		throw new DAOException("Les enregistrement transactionnel ne sont pas pris en charche par le DAO de la table "+this.getTableName());
	}
	
	/**
	 * Enregistrement d'une collection d'occurence dans une transaction deja demarer d'avence
	 * @param connection
	 * @param t
	 * @throws DAOException
	 */
	protected void create (Connection connection, T [] t) throws DAOException{
		throw new DAOException("Les enregistrement transactionnel ne sont pas pris en charche par le DAO de la table "+this.getTableName());
	}
	
	
	@Override
	public synchronized boolean check(String columnName, Object value) throws DAOException {
		final String SQL_QUERY = String.format("SELECT %s FROM %s WHERE %s=? LIMIT 1", columnName, this.getViewName(), columnName);
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, value);
				ResultSet result = statement.executeQuery();
			) {
			
			return result.next();
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized boolean check(String columnName, Object value, long id) throws DAOException {
		final String SQL_QUERY = String.format("SELECT %s FROM %s WHERE %s=? AND id != ? LIMIT 1", columnName, this.getViewName(), columnName);
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, value, id);
				ResultSet result = statement.executeQuery();
			) {
			
			return result.next();
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	protected synchronized boolean check(String[] keys, Object [] value) throws DAOException {
		String SQL_QUERY = String.format("SELECT * FROM %s WHERE ", this.getViewName());

		for (int i=0, max = keys.length; i<max; i++) {
			SQL_QUERY += keys[i]+" = ? "+((i+1)==max? " LIMIT 1 " : " AND ");
		}
		
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, value);
				ResultSet result = statement.executeQuery();
			) {
			
			return result.next();
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized void delete(long id) throws DAOException {
		final String SQL_QUERY = String.format("SELECT FROM %s WHERE id = ?", this.getTableName());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, id);
			) {
			int status = statement.executeUpdate();
			
			if(status == 0) {
				throw new DAOException("Aucune occurence suprimer");
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized T find(String columnName, Object value) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE %s=? LIMIT 1", this.getViewName(), columnName);
		T t = null;
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, value);
				ResultSet result = statement.executeQuery();
			) {
			
			if(result.next()) {
				t = this.fullMapping(result);
			} else {
				throw new DAOException("Aucune donnée ne correspond aux critere de la requêtte de selection");
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return t;
	}
	
	protected synchronized T find(String[] keys, Object [] value) throws DAOException {
		String SQL_QUERY = String.format("SELECT * FROM %s WHERE ", this.getViewName());

		for (int i=0, max = keys.length; i<max; i++) {
			SQL_QUERY += keys[i]+" = ? "+((i+1)==max? " LIMIT 1 " : " AND ");
		}
		
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, value);
				ResultSet result = statement.executeQuery();
			) {
			
			if( result.next() ) {
				return this.fullMapping(result);
			} else  {
				throw new DAOException("Aucune donnée ne correspond aux criteres de selection");
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized List<T> findAll() throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s", this.getViewName());
		System.out.println(SQL_QUERY);
		List<T> t = new ArrayList<>();
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				t.add(this.mapping(result));
				while (result.next()) {
					t.add(this.mapping(result));
				}
			} else {
				throw new DAOException("Aucune donnée dans la table "+this.getViewName());
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return t;
	}
	
	/**
	 * Renvoie
	 * @param keys
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	protected synchronized List<T> findAll(String[] keys, Object [] value) throws DAOException {
		return this.findAll(keys, value, null);
	}
	
	/**
	 * 
	 * @param keys
	 * @param value
	 * @param order
	 * @return
	 * @throws DAOException
	 */
	protected synchronized List<T> findAll(String[] keys, Object [] value, String order) throws DAOException {
		String SQL_QUERY = String.format("SELECT * FROM %s WHERE ", this.getViewName());

		for (int i=0, max = keys.length; i<max; i++) {
			SQL_QUERY += keys[i]+" = ? "+((i+1)==max? "" : " AND ");
		}
		
		if(order != null && !order.trim().isEmpty()) {			
			SQL_QUERY += " ORDER BY "+order;
		}
		
		System.out.println(SQL_QUERY);
		List<T> t = new ArrayList<>();
		try (
				Connection connection =  this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, value);
				ResultSet result = statement.executeQuery();
			) {
			
			if(result.next()) {
				t.add(this.mapping(result));
				while (result.next()) {
					t.add(this.mapping(result));
				}
			} else {
				throw new DAOException("Aucune donnée dans la table "+this.getViewName());
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return t;
	}
	

	@Override
	public synchronized int countAll () throws DAOException {
		final String SQL_QUERY = String.format("SELECT COUNT(*) AS nombre FROM %s", this.getViewName());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				return result.getInt("nombre");
			} 
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		
		return 0;
	}

	@Override
	public synchronized boolean checkByRecordDate(Date dateMin, Date dateMax) throws DAOException {
		final String SQL_QUERY = String.format("SELECT recordDate FROM %s WHERE recordDate IN (%d, %d) LIMIT 1", this.getViewName(),  dateMin.getTime(), dateMax.getTime());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			return result.next();
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized boolean checkByRecordDate(Date dateMin, Date dateMax, int limit, int offset) throws DAOException {
		final String SQL_QUERY = String.format("SELECT recordDate FROM %s WHERE recordDate IN (%d, %d) LIMIT %d OFFSET %d", this.getViewName(),  dateMin.getTime(), dateMax.getTime(), limit, offset);
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			return result.next();
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public synchronized int countByRecordDate(Date dateMin, Date dateMax) throws DAOException {
		final String SQL_QUERY = String.format("SELECT COUNT(*) AS nombre FROM %s WHERE recordDate IN (%d, %d)", this.getViewName(),  dateMin.getTime(), dateMax.getTime());
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				return result.getInt("nombre");
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public synchronized List<T> findByRecordDate(Date dateMin, Date dateMax) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE recordDate IN (%d, %d)", this.getViewName(),  dateMin.getTime(), dateMax.getTime());
		List<T> t = new ArrayList<>();
		System.out.println(SQL_QUERY);
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				t.add(this.mapping(result));
				while(result.next()) {
					t.add(this.mapping(result));
				}
			} else {
				throw new DAOException("Aucune donnees serialiser pour l'intervale de selection en parametre");
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return t;
	}

	@Override
	public synchronized List<T> findByRecordDate(Date dateMin, Date dateMax, int limit, int offset) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s WHERE recordDate IN (%d, %d) LIMIT %d OFFSET %d", this.getViewName(),  dateMin.getTime(), dateMax.getTime(), limit, offset);
		System.out.println(SQL_QUERY);
		List<T> t = new ArrayList<>();
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				t.add(this.mapping(result));
				while(result.next()) {
					t.add(this.mapping(result));
				}
			} else {
				throw new DAOException("Aucune donnees serialiser pour l'intervale de selection en parametre");
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return t;
	}

	@Override
	public synchronized List<T> findAll(int limit, int offset) throws DAOException {
		final String SQL_QUERY = String.format("SELECT * FROM %s LIMIT %d OFFSET %d", this.getViewName(),  limit, offset);
		System.out.println(SQL_QUERY);
		List<T> t = new ArrayList<>();
		try (
				Connection connection =  this.factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(SQL_QUERY);
			) {
			
			if(result.next()) {
				t.add(this.mapping(result));
				while(result.next()) {
					t.add(this.mapping(result));
				}
			} else {
				throw new DAOException("Aucune donnees serialiser pour l'intervale de selection en parametre");
			}
			
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return t;
	}

	@Override
	public synchronized List<DAOListener<T>> getListeners() {
		return this.listeners;
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
				this.emitOnError(new DAOException(e.getMessage(), e));
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
				this.emitOnError(new DAOException(e.getMessage(), e));
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
				this.emitOnError(new DAOException(e.getMessage(), e));
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
				this.emitOnError(new DAOException(e.getMessage(), e));
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
	protected synchronized static PreparedStatement prepare (String SQL_QUERY, Connection connection, boolean autoGeneratedKeys, Object...objects) throws SQLException{
		PreparedStatement statement = connection.prepareStatement(SQL_QUERY, autoGeneratedKeys? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
		for (int i = 0; i < objects.length; i++) {
			statement.setObject(i+1, objects[i]);
		}
		return statement;
	}
	
	/**
	 * Insersion des donnee dans une table de la base de donnee
	 * @param tableName le nom de la table
	 * @param columnsNames un tableau des noms de colones
	 * @param columnsValues
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized int insertInTable(String [] columnsNames, Object [] columnsValues) throws SQLException, DAOException{
		try ( Connection connection = this.factory.getConnection() ) {
			return this.insertInTransactionnelTable(connection, columnsNames, columnsValues);
		} 
	}
	
	/**
	 * insertion des donnee dans une table de maniere transactionnee
	 * @param connection
	 * @param columnsNames
	 * @param columnsValues
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized int insertInTransactionnelTable(Connection connection, String [] columnsNames, Object [] columnsValues) throws SQLException, DAOException{
		String 
			SQL_QUERY = "INSERT INTO "+this.getTableName()+" (",
			SQL_SUITE=" VALUES (";
		for (int i=0; i<columnsNames.length; i++) {
			SQL_QUERY += columnsNames[i]+(i!=(columnsNames.length-1)? ", ":")");
			SQL_SUITE += "?"+(i!=(columnsNames.length-1)? ", ":")");
		}
		
		SQL_QUERY += SQL_SUITE;
		System.out.println(SQL_QUERY);
		int id=0;
		ResultSet result = null;
		try (
				PreparedStatement statement = prepare(SQL_QUERY, connection, true, columnsValues);
			) {
			int status = statement.executeUpdate();
			if(status == 0) {
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
			close(result);
		}
		return id;
	}
	
	/**
	 * Mise ajour d'une occurence de la table de la base de donnee
	 * @param columnsNames les noms de la colonnes a metre ajours
	 * @param columnsValues les valeurs de colones de l'occurence a metre a jours
	 * @param idEntity l'identifiant de l'occurence a metre a jours
	 * @param dateModif si on doit actualiser la date de modification
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected synchronized void updateInTable(String [] columnsNames, Object [] columnsValues, long idEntity) throws SQLException, DAOException{
		String SQL_QUERY = "UPDATE "+this.getTableName()+" SET ";
		for (int i=0; i<columnsNames.length; i++) {
			SQL_QUERY += columnsNames[i]+(i!=(columnsNames.length-1)? ("= ?, ") : ("=? "));
		}
		
		SQL_QUERY += " WHERE id="+idEntity;
		System.out.println(SQL_QUERY);
		
		try (
				Connection connection = this.factory.getConnection();
				PreparedStatement statement = prepare(SQL_QUERY, connection, false, columnsValues);
			) {
			int statut=statement.executeUpdate();
			if(statut == 0 ) {
				throw new DAOException("Aucune mise ajours n'a été effectuer. Veiller re-essayer svp!");
			}
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * effectue le mapping sans tenir compte de dependances
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	protected abstract T mapping (ResultSet result) throws SQLException, DAOException;
	
	/**
	 * Effectue le mapping en tena compte des dependance
	 * @param result
	 * @return
	 * @throws SQLException
	 * @throws DAOException
	 */
	protected T fullMapping (ResultSet result)  throws SQLException, DAOException {
		return this.mapping(result);
	}
	
	/**
	 * Revoie le nom de la table
	 * @return
	 */
	protected abstract String getTableName ();
	
	/**
	 * Return le nom de la vue
	 * @return
	 */
	protected String getViewName () {
		return this.hasView()? "V_"+this.getTableName() : this.getTableName();
	}
	
	/**
	 * Esqu'il existe de vue materiel pour cette table???
	 * @return
	 */
	protected boolean hasView () {
		return false;
	}
	
	/**
	 * Treansmission d'un event los de la creation d'un occurence
	 * @param event
	 */
	protected synchronized void emitOnCreate (T e) {
		this.emitOnCreate(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void emitOnCreate (T e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onCreate(e, DAOInterface.DEFAULT_REQUEST_ID);
		}
	}
	
	protected synchronized void emitOnUpdate (T e) {
		this.emitOnUpdate(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void emitOnUpdate (T e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onUpdate(e, requestId);
		}
	}
	
	
	protected synchronized void emitOnDelete (T e) {
		this.emitOnDelete(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void emitOnDelete (T e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onDelete(e, requestId);
		}
	}
	
	protected synchronized void emitOnError (DAOException e) {
		this.emitOnError(e, DAOInterface.DEFAULT_REQUEST_ID);
	}
	
	protected synchronized void emitOnError (DAOException e, int requestId) {
		for (DAOListener<T> ls : listeners) {
			ls.onError(e, requestId);
		}
	}

}
