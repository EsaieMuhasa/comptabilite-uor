/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.UniversitySpend;

/**
 * @author Esaie MUHASA
 *
 */
class UniversitySpendDaoSql extends UtilSql<UniversitySpend> implements UniversitySpendDao {

	public UniversitySpendDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public void create(UniversitySpend u) throws DAOException {
		try {
			long id = this.insertInTable(
					new String [] {"title", "description", "recordDate"},
					new Object[] {u.getTitle(), u.getDescription(), u.getRecordDate().getTime()});
			u.setId(id);
			this.emitOnCreate(u);
		} catch (SQLException e) {
			throw new DAOException("Une ererur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public void update(UniversitySpend u, long id) throws DAOException {
		try {
			this.updateInTable(
					new String [] {"title", "description", "lastUpdate"},
					new Object[] {u.getTitle(), u.getDescription(), u.getLastUpdate().getTime()}, id);
			u.setId(id);
			this.emitOnUpdate(u);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue dans le processuce de modification\n"+e.getMessage(), e);
		}
	}

	@Override
	protected UniversitySpend mapping(ResultSet result) throws SQLException, DAOException {
		UniversitySpend spend = new UniversitySpend(result.getLong("id"));
		spend.setDescription(result.getString("description"));
		spend.setTitle(result.getString("title"));
		spend.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			spend.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return spend;
	}

	@Override
	protected String getTableName() {
		return UniversitySpend.class.getSimpleName();
	}

}
