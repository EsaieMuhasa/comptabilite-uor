/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.ArrayList;

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
		if(this.departmentDao.checkByFaculty(faculty.getId())) {			
			this.data = this.departmentDao.findByFaculty(this.faculty.getId());
		} else {
			this.data = new ArrayList<>();
		}
	}
	
	@Override
	public void onCreate(Department e, int requestId) {
		if(e.getFaculty().getId() == this.faculty.getId()) {
			this.data.add(e);
			this.fireTableRowsInserted(this.data.size()-1, this.data.size()-1);
		}
	}

	@Override
	public int getRowCount() {
		return this.data.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return (rowIndex+1);
			case 1: return (this.data.get(rowIndex).getAcronym());
			case 2: return (this.data.get(rowIndex).getName());
		}
		return "";
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return ("N°");
			case 1: return ("Abbreviation");
			case 2: return ("Appelation complette");
		}
		return "Option";
	}

}
