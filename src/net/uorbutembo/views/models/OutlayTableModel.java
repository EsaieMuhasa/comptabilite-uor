/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.List;

import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 */
public class OutlayTableModel extends TableModel<Outlay> {
	private static final long serialVersionUID = -90393497836126101L;
	
	private AnnualSpend account;
	private OutlayDao outlayDao;

	public OutlayTableModel(OutlayDao daoInterface) {
		super(daoInterface);
		limit = 20;
		outlayDao = daoInterface;
	}

	/**
	 * @return the account
	 */
	public AnnualSpend getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(AnnualSpend account) {
		if(this.account != null && account != null && account.getId() == this.account.getId())
			return;
		offset = 0;
		this.account = account;
		reload();
	}
	
	@Override
	public synchronized void reload () {
		data.clear();
		if(account != null && outlayDao.checkByAccount(account)) {
			List<Outlay> outs = outlayDao.findByAccount(account);
			for (Outlay out : outs)
				data.add(out);
		}
		fireTableDataChanged();
	}
	
	@Override
	public void onUpdate(Outlay e, int requestId) {
		reload();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0 : return "Date";
			case 1 : return "libelé";
			case 2 : return "Montant";
		}
		return super.getColumnName(column);
	}


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getDeliveryDate());
			case 1: return data.get(rowIndex).getWording();
			case 2: return data.get(rowIndex).getAmount()+" "+FormUtil.UNIT_MONEY;
		}
		return null;
	}
	
	@Override
	public void onCreate(Outlay e, int requestId) {
		if(account != null && e.getAccount().getId() == account.getId())
			super.onCreate(e, requestId);
	}

}
