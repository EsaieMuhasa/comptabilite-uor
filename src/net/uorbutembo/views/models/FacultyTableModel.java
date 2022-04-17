/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class FacultyTableModel extends TableModel<Faculty> {
	private static final long serialVersionUID = 4028556346147170789L;

	private FacultyDao facultyDao;

	/**
	 * 
	 */
	public FacultyTableModel(FacultyDao facultyDao) {
		super(facultyDao);
		this.facultyDao = facultyDao;
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		if(facultyDao.countAll() != 0)
			data = facultyDao.findAll();
		
		fireTableDataChanged();
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
		return null;
	}
	
}
