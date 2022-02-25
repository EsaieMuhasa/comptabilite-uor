/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.Student;

/**
 * @author Esaie MUHASA
 *
 */
class StudentDaoSql extends UserDaoSql<Student> implements StudentDao {

	public StudentDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	
	@Override
	protected void create(Connection connection, Student t) throws DAOException {
		try {
			long id = this.insertInTransactionnelTable(
					connection, 
					new String[] {
							"name", "postName", "lastName",
							"birthDate", "birthPlace", 
							"email", "matricul", "picture",
							"telephone", "recordDate"
					},
					new Object[] {
							t.getName(), t.getPostName(), t.getFirstName(),
							t.getBirthDate().getTime(), t.getBirthPlace(),
							t.getEmail(), t.getMatricul(),
							t.getPicture(), t.getTelephone(),
							t.getRecordDate().getTime()
					});
			t.setId(id);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(Student e, long id) throws DAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Student> search(String[] values) throws DAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Student mapping(ResultSet result) throws SQLException, DAOException {
		Student s = new Student(result.getLong("id"));
		s.setName(result.getString("name"));
		s.setPostName(result.getString("postName"));
		s.setFirstName(result.getString("lastName"));
		s.setEmail(result.getString("email"));
		s.setBirthPlace(result.getString("birthPlace"));
		s.setMatricul(result.getString("matricule"));
		s.setPicture(result.getString("picture"));
		s.setBirthDate(new Date(result.getLong("birthDate")));
		s.setRecordDate(new Date(result.getLong("recordDate")));
		s.setTelephone(result.getString("telephone"));
		if(result.getLong("lastUpdate") != 0)
			s.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return s;
	}

	@Override
	protected String getTableName() {
		return Student.class.getSimpleName();
	}

}
