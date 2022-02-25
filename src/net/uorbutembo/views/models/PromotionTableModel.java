/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.ArrayList;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PromotionTableModel extends TableModel<Promotion> {
	private static final long serialVersionUID = 4028556346147170789L;

	private PromotionDao promotionDao;
	private AcademicYear academicYear;

	/**
	 * constructeur d'initialisation
	 * @param departmentDao
	 * @param faculty
	 */
	public PromotionTableModel(PromotionDao promotionDao, AcademicYear academicYear) {
		super(promotionDao);
		this.academicYear = academicYear;
		this.promotionDao = promotionDao;
		if(this.promotionDao.checkByAcademicYear(academicYear.getId())) {			
			this.data = this.promotionDao.findByAcademicYear(academicYear.getId());
		} else {
			this.data = new ArrayList<>();
		}
	}
	
	@Override
	public void onCreate(Promotion e, int requestId) {
		if(e.getAcademicYear().getId() == this.academicYear.getId()) {
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
			case 1: {
				return (this.data.get(rowIndex).getStudyClass().getAcronym())+" "+(this.data.get(rowIndex).getDepartment().getAcronym());
			}
			case 2: {
				return (this.data.get(rowIndex).getStudyClass().getName())+" "+(this.data.get(rowIndex).getDepartment().getName());
			}
		}
		return "";
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return ("N°");
			case 1: return ("Abbreviation");
			case 2: return ("Appelation complete");
		}
		return "Option";
	}

}
