/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class StudyClassTableModel extends TableModel<StudyClass> {
	private static final long serialVersionUID = 4028556346147170789L;

	private StudyClassDao studyClassDao;

	/**
	 * 
	 */
	public StudyClassTableModel(StudyClassDao studyClassDao) {
		super(studyClassDao);
		this.studyClassDao = studyClassDao;
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		if(studyClassDao.countAll() != 0)
			data = this.studyClassDao.findAll();
		
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return data.get(rowIndex).getAcronym();
			case 1: return data.get(rowIndex).getName();
			case 2 : return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getRecordDate());
			case 3 : {
				if(data.get(rowIndex).getLastUpdate() == null)
					return "-";
				return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getLastUpdate());
			}
		}
		return null;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "Abbreviation";
			case 1: return "Appelation complette";
			case 2: return "Date enregistrement";
			case 3: return "Dernière modification";
		}
		return null;
	}

}
