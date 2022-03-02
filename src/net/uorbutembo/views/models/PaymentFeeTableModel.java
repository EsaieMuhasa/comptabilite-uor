/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class PaymentFeeTableModel extends TableModel<PaymentFee> {
	private static final long serialVersionUID = 7032190897666231971L;

	private PaymentFeeDao paymentFeeDao;
	private Inscription inscription;
	
	public PaymentFeeTableModel(PaymentFeeDao paymentFeeDao) {
		super(paymentFeeDao);
		this.paymentFeeDao = paymentFeeDao;
	}

	/**
	 * @return the inscription
	 */
	public Inscription getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(Inscription inscription) {
		this.inscription = inscription;
		if (paymentFeeDao.checkByInscription(inscription)) 
			this.data = this.paymentFeeDao.findByInscription(inscription);
		else
			this.data.clear();
		
		this.fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getSlipDate());
			case 1:
				return data.get(rowIndex).getSlipNumber();
			case 2:
				return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getReceivedDate());
			case 3:
				return data.get(rowIndex).getReceiptNumber();
			case 4:
				return data.get(rowIndex).getWording();
			case 5:
				return data.get(rowIndex).getAmount();
			case 6:
				return "";
			case 7:
				return "";
			default:
				break;
		}
		return null;
	}

}
