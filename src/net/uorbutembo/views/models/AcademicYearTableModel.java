/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class AcademicYearTableModel extends TableModel<AcademicYear> {
	private static final long serialVersionUID = -7387061286873853838L;
	
	private AcademicYearDao academicYearDao;

	public AcademicYearTableModel(AcademicYearDao daoInterface) {
		super(daoInterface);
		academicYearDao = daoInterface;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		
		if (academicYearDao.countAll() != 0)
			data = academicYearDao.findAll();
		
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "Libelé l'année";
			case 1: return "Date d'ouverture";
			case 2: return "Date de fermeture";
			case 3: return "Date d'enregistrement";
		}
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt (int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return data.get(rowIndex).getLabel();
			case 1: return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getStartDate());
			case 2: return data.get(rowIndex).getCloseDate() != null? DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getCloseDate()) :  "-";
			case 3: return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getRecordDate());
		}
		return null;
	}

}
