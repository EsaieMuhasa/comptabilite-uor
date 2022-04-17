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
public class UniversitySpendTableModel extends TableModel <UniversitySpend> {
	private static final long serialVersionUID = -3715461019742870626L;

	private UniversitySpendDao universitySpendDao;
	
	public UniversitySpendTableModel(UniversitySpendDao daoInterface) {
		super(daoInterface);
		this.universitySpendDao = daoInterface;
	}

	@Override
	public void reload() {
		data.clear();
		List<UniversitySpend> spends = (universitySpendDao.countAll() != 0) ? universitySpendDao.findAll() : null;
		
		if (spends != null)
			data = spends;
		
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getData().get(rowIndex).getTitle();
	}

}
