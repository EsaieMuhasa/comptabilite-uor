/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.List;

import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class UniversitySpendTableModel extends TableModel <UniversitySpend>{
	private static final long serialVersionUID = -3715461019742870626L;

	private UniversitySpendDao universitySpendDao;
	
	public UniversitySpendTableModel (UniversitySpendDao daoInterface) {
		super(daoInterface);
		this.universitySpendDao = daoInterface;
	}

	@Override
	public synchronized void reload() {
		data.clear();
		List<UniversitySpend> spends = (universitySpendDao.countAll() != 0) ? universitySpendDao.findAll() : null;
		
		if (spends != null)
			data = spends;
		
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "label";
			case 1: return "Date d'enregistrement";
			case 2: return "Dermière modification";
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
			case 0: return getData().get(rowIndex).getTitle();
			case 1: return DEFAULT_DATE_TIME_FORMAT.format(getData().get(rowIndex).getRecordDate());
			case 2: {
				if (getData().get(rowIndex).getLastUpdate() == null)
					return " - ";
				return DEFAULT_DATE_TIME_FORMAT.format(getData().get(rowIndex).getLastUpdate());
			}
		}
		
		return "";
	}

}
