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
			long id = this.insertInTable(
					connection, 
					new String[] {
							"name", "postName", "lastName",
							"birthDate", "birthPlace", "kind", 
							"email", "matricul", "picture", 
							"originalSchool", "telephone", "recordDate"
					},
					new Object[] {
							t.getName(), t.getPostName(), t.getFirstName(),
							t.getBirthDate().getTime(), t.getBirthPlace(), t.getKind().getShortName(),
							t.getEmail(), t.getMatricul(), t.getPicture(),
							t.getOriginalSchool(),t.getTelephone(),
							t.getRecordDate().getTime()
					});
			t.setId(id);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update(Student t, long id) throws DAOException {
		try {
			updateInTable (
					new String[] {
							"name", "postName", "lastName",
							"birthDate", "birthPlace", "kind", 
							"email", "matricul", "picture", 
							"originalSchool", "telephone", "lastUpdate"
					},
					new Object[] {
							t.getName(), t.getPostName(), t.getFirstName(),
							t.getBirthDate().getTime(), t.getBirthPlace(), t.getKind().getShortName(),
							t.getEmail(), t.getMatricul(), t.getPicture(),
							t.getOriginalSchool(),t.getTelephone(),
							t.getLastUpdate().getTime()
					}, id);
			t.setId(id);
			emitOnUpdate(t);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public Student findByMatricul(String matricul) throws DAOException {
		// TODO Auto-generated method stub
		return null;
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
		s.setMatricul(result.getString("matricul"));
		s.setPicture(result.getString("picture"));
		s.setOriginalSchool(result.getString("originalSchool"));
		s.setKind(result.getString("kind"));
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
