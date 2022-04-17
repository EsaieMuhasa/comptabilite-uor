/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class AnnualSpendTableModel extends TableModel <AnnualSpend> {
	private static final long serialVersionUID = -3715461019742870626L;
	
	private AnnualSpendDao annualSpendDao;
	private UniversitySpendDao universitySpendDao;
	private AcademicYear currentYear;
	
	public AnnualSpendTableModel(AnnualSpendDao daoInterface) {
		super(daoInterface);
		annualSpendDao = daoInterface;
		universitySpendDao = daoInterface.getFactory().findDao(UniversitySpendDao.class);
		
		universitySpendDao.addListener(new DAOAdapter<UniversitySpend>() {
			@Override
			public synchronized void onUpdate(UniversitySpend e, int requestId) {
				for (int i = 0, count = getRowCount(); i < count; i++) {
					if(getRow(i).getUniversitySpend().getId() == e.getId()) {
						getRow(i).setUniversitySpend(e);
						updateRow(getRow(i), i);
						return;
					}
				}
			}
		});
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public synchronized void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		reload();
	}
	
	@Override
	public void onCreate(AnnualSpend e, int requestId) {
		if(currentYear == null || currentYear.getId()  != e.getAcademicYear().getId())
			return;
		super.onCreate(e, requestId);
	}
	
	@Override
	public void onCreate(AnnualSpend[] e, int requestId) {
		if(currentYear == null || currentYear.getId()  != e[0].getAcademicYear().getId())
			return;
		super.onCreate(e, requestId);
	}

	@Override
	public void reload() {
		data.clear();
		if(currentYear == null) {
			fireTableDataChanged();
			return;
		}
		
		List<AnnualSpend> spends = annualSpendDao.checkByAcademicYear(currentYear.getId())?  annualSpendDao.findByAcademicYear(currentYear): null;
		
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
		return getData().get(rowIndex).getUniversitySpend().getTitle();
	}

}
