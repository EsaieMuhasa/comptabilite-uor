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
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class AnnualSpendTableModel extends TableModel <AnnualSpend> {
	private static final long serialVersionUID = -3715461019742870626L;
	
	private AnnualSpendDao annualSpendDao;
	private UniversitySpendDao universitySpendDao;
	private AcademicYear currentYear;
	
	private DAOAdapter<UniversitySpend> univAdapter = new DAOAdapter<UniversitySpend>() {
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
	};
	
	public AnnualSpendTableModel(AnnualSpendDao daoInterface) {
		super(daoInterface);
		annualSpendDao = daoInterface;
		universitySpendDao = daoInterface.getFactory().findDao(UniversitySpendDao.class);
		
		universitySpendDao.addListener(univAdapter);
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
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "Libelé du dépense";
			case 1: return "Date enregistrement";
			case 2: return "Frais académique";
			case 3: return "Autres recettes";
			case 4: return "Déjà dépenser";
		}
		return super.getColumnName(column);
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return getData().get(rowIndex).getUniversitySpend().getTitle();
			case 1 : return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getRecordDate());
			case 2: return data.get(rowIndex).getCollectedCost()+" "+FormUtil.UNIT_MONEY;
			case 3: return data.get(rowIndex).getCollectedRecipe()+" "+FormUtil.UNIT_MONEY;
			case 4: return data.get(rowIndex).getUsed()+" "+FormUtil.UNIT_MONEY;
		}
		return null;
	}

}
