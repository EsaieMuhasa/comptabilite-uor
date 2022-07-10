/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;

/**
 * @author Esaie MUHASA
 *
 */
class PaymentFeePartDaoSql extends AbstractRecipePartDao<PaymentFee> implements PaymentFeePartDao {

	public PaymentFeePartDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}
	
	@Override
	protected String getViewName() {
		return "V_PaymentFeePart";
	}
	
	@Override
	protected String getDateColumnName() {
		return "slipDate";
	}
	
	@Override
	protected PaymentFee mapSource(ResultSet result) throws SQLException {
		PaymentFee fee = new PaymentFee(result.getLong("id"));
		fee.setRecordDate(new Date(result.getLong("recordDate")));
		fee.setInscription(new Inscription(result.getLong("inscription")));
		fee.setAmount(result.getFloat("amount"));
		fee.setReceivedDate(new Date(result.getLong("receivedDate")));
		fee.setReceiptNumber(result.getString("receiptNumber"));
		fee.setSlipDate(new Date(result.getLong("slipDate")));
		fee.setSlipNumber(result.getString("slipNumber"));
		fee.setWording(result.getString("wording"));
		return fee;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <L extends DAOInterface<PaymentFee>> Class<L> getSourceDAOInterface() {
		return (Class<L>) PaymentFeeDao.class;
	}

}
