/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class DepartmentTableModel extends TableModel<Department> {
	private static final long serialVersionUID = 4028556346147170789L;

	private DepartmentDao departmentDao;
	private Faculty faculty;

	/**
	 * constructeur d'initialisation
	 * @param departmentDao
	 * @param faculty
	 */
	public DepartmentTableModel(DepartmentDao departmentDao, Faculty faculty) {
		super(departmentDao);
		this.faculty = faculty;
		this.departmentDao = departmentDao;
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		if(faculty != null && departmentDao.checkByFaculty(faculty.getId()))	
			data = departmentDao.findByFaculty(faculty.getId());
		
		fireTableDataChanged();
	}
	
	@Override
	public void onCreate(Department e, int requestId) {
		if(e.getFaculty().getId() == this.faculty.getId()) {
			super.onCreate(e, requestId);
		}
	}
	
	@Override
	public void onUpdate(Department e, int requestId) {
		boolean exist = false;
		for (int i=0; i < data.size(); i++) {
			Department t = data.get(i);
			if (t.getId() == e.getId()) {
				exist = true;
				if (t.getFaculty().getId() != faculty.getId()) {
					removeRow(i);
				} else {
					updateRow(t, i);
				}
				break;
			}
		}
		
		if(!exist && e.getFaculty().getId() == faculty.getId()) {
			addRow(e);
		}
	}

	@Override
	public int getRowCount() {
		return this.data.size();
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
