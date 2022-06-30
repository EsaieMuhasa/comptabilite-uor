/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.AcademicFee;
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
	private AcademicFee academicFee;

	/**
	 * constructeur d'initialisation
	 * @param departmentDao
	 * @param faculty
	 */
	public PromotionTableModel(PromotionDao promotionDao) {
		super(promotionDao);
		this.promotionDao = promotionDao;
	}
	
	@Override
	public synchronized void reload() {	
		data.clear();
		
		if(academicFee == null) {
			if(academicYear != null && promotionDao.checkByAcademicYear(academicYear.getId()))			
				data = promotionDao.findByAcademicYear(academicYear);
		} else {
			if(promotionDao.checkByAcademicFee(academicFee))
				data = promotionDao.findByAcademicFee(academicFee);
		}
		
		fireTableStructureChanged();
	}
	
	/**
	 * relecture de pormotion
	 * le parametre != null alors, on charge uniquement les promotions
	 * qui doivent paayer les frais universitaire en parametre
	 * @param fee
	 */
	public synchronized void reload (AcademicFee fee) {
		academicFee = fee;
		reload();
	}
	
	/**
	 * @param academicYear the academicYear to set
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		if (academicYear == this.academicYear)
			return;
		this.academicYear = academicYear;
		reload();
	}

	@Override
	public void onCreate (Promotion e, int requestId) {
		if(academicFee == null && academicYear != null && e.getAcademicYear().getId() == this.academicYear.getId()) 
			super.onCreate(e, requestId);
	}
	
	@Override
	public void onCreate (Promotion[] e, int requestId) {
		if(academicFee == null && academicYear != null && e[0].getAcademicYear().getId() == this.academicYear.getId()) 
			super.onCreate(e, requestId);
	}
	
	
	@Override
	public void onUpdate(Promotion e, int requestId) {
		reload();
	}
	
	@Override
	public void onUpdate(Promotion[] e, int requestId) {
		reload();
	}
	
	@Override
	public int getColumnCount() {
		return academicFee != null? 2 : 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return (data.get(rowIndex).getStudyClass().getAcronym())+" "+(data.get(rowIndex).getDepartment().getAcronym());
			case 1:
				return (data.get(rowIndex).getStudyClass().getName())+" "+(data.get(rowIndex).getDepartment().getName());
			case 2:
				return (data.get(rowIndex).getAcademicFee() != null? data.get(rowIndex).getAcademicFee() : " - ");
		}
		return "";
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "Abbreviation";
			case 1: return "Appélation complete";
			case 2: return "Frais académique";
		}
		return null;
	}

}
