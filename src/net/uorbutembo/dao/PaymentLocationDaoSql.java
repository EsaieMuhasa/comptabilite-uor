/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.PaymentLocation;

/**
 * @author Esaie MUHASA
 *
 */
class PaymentLocationDaoSql extends UtilSql<PaymentLocation> implements PaymentLocationDao {

	public PaymentLocationDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public void create(PaymentLocation l) throws DAOException {
		try {
			long id = insertInTable(
					new String[] {"name", "recordDate"},
					new Object[] {l.getName(), l.getRecordDate().getTime()});
			l.setId(id);
			emitOnCreate(l);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	public void update (PaymentLocation l, long id) throws DAOException {
		try {
			updateInTable(
					new String[] {"name", "lastUpdate"},
					new Object[] {l.getName(), l.getLastUpdate().getTime()}, id);
			emitOnUpdate(l);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	@Override
	protected PaymentLocation mapping (ResultSet result) throws SQLException, DAOException {
		PaymentLocation location = new  PaymentLocation(result.getLong("id"));
		location.setName(result.getString("name"));
		location.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") > 0)
			location.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return location;
	}

	@Override
	protected String getTableName() {
		return PaymentLocation.class.getSimpleName();
	}

}
