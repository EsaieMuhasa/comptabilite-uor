/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;

/**
 * @author Esaie MUHASA
 *
 */
public class PaymentFeeTableModel extends TableModel<PaymentFee> {
	private static final long serialVersionUID = 7032190897666231971L;

	private Inscription inscription;
	
	public PaymentFeeTableModel(PaymentFeeDao paymentFeeDao) {
		super(paymentFeeDao);
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
	public void setInscription(InscriptionDataRow inscription) {
		this.inscription = inscription.getInscription();
		this.data.clear();
		for (PaymentFee paymentFee : inscription.getPayments()) {
			data.add(paymentFee);
		}
		this.fireTableDataChanged();
	}
	
	@Override
	public void onCreate(PaymentFee e, int requestId) {
		if(e.getInscription().getId() == inscription.getId())
			super.onCreate(e, requestId);
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		double debit = this.calculDebit(rowIndex),
				credit = data.get(rowIndex).getAmount();
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
				return debit+" USD";
			case 6:
				return credit+" USD";
			case 7:
				return (debit-credit)+" USD";
			default:
				break;
		}
		return null;
	}
	
	/**
	 * Pour determiner le debit
	 * @param rowIndex
	 * @return
	 */
	private double calculDebit (int rowIndex) {
		double amount = this.inscription.getPromotion().getAcademicFee().getAmount();
		if(rowIndex == 0) 
			return amount;
		
		for (int i = 0; i < rowIndex; i++) {
			amount -= this.data.get(i).getAmount();
		}
		
		return amount;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0:
				return "Date du bordereau";
			case 1:
				return "N° du borderau";
			case 2:
				return "Date reçu";
			case 3:
				return "N° reçu";
			case 4:
				return "Libele";
			case 5:
				return "Débit";
			case 6:
				return "Crédit";
			case 7:
				return "Solde";
			default:
				break;
		}
		return super.getColumnName(column);
	}

}
