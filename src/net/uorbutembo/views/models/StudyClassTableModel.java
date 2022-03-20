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
		if(studyClassDao.countAll() != 0)
			this.data = this.studyClassDao.findAll();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return (this.data.get(rowIndex).getAcronym());
			case 1: return (this.data.get(rowIndex).getName());
		}
		return "";
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return ("Abbreviation");
			case 1: return ("Appelation complette");
		}
		return "Option";
	}

}
