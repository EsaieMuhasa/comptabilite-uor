/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.StudyClass;

/**
 * @author Esaie MUHASA
 *
 */
class StudyClassDaoSql extends OrientationDaoSql<StudyClass> implements StudyClassDao{

	public StudyClassDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	protected synchronized StudyClass maping(ResultSet result) throws SQLException, DAOException {
		StudyClass s = new StudyClass(result.getLong("id"));
		s.setAcronym(result.getString("acronym"));
		s.setName(result.getString("name"));
		s.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			s.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return s;
	}

	@Override
	protected String getTableName() {
		return StudyClass.class.getSimpleName();
	}

}
