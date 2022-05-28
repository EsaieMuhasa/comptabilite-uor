/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PaymentLocationTableModel extends TableModel<PaymentLocation> implements AcademicYearDaoListener{
	private static final long serialVersionUID = -2604306950655339577L;
	
	private final PaymentLocationDao paymentLocationDao;
	private boolean firstLoad = true;

	/**
	 * @param daoInterface
	 */
	public PaymentLocationTableModel(PaymentLocationDao daoInterface) {
		super(daoInterface);
		paymentLocationDao = daoInterface;
		daoInterface.getFactory().findDao(AcademicYearDao.class).addYearListener(this);
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		if(firstLoad) {
			reload();
			firstLoad = false;
		}
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		if(paymentLocationDao.countAll() != 0){
			data = paymentLocationDao.findAll();
		}
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "Label du lieux";
			case 1: return "Date d'enregistrement";
			case 2: return "Dernière modification";
		}
		return super.getColumnName(column);
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return data.get(rowIndex).getName();
			case 1: return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getRecordDate());
			case 2: {
				if (data.get(rowIndex).getLastUpdate() == null)
					return " - ";
				return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getLastUpdate());
			}
		}
		return null;
	}

}
