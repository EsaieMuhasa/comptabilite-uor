/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.views.components.SidebarStudents.SidebarListener;

/**
 * @author Esaie MUHASA
 *
 */
public class InscritTableModel extends TableModel<Inscription> implements SidebarListener{
	private static final long serialVersionUID = 1999957028728594769L;
	
	private InscriptionDao inscriptionDao;

	/**
	 * constructeur d'initialisation
	 * @param inscriptionDao
	 */
	public InscritTableModel(InscriptionDao inscriptionDao) {
		super(inscriptionDao);
		this.inscriptionDao = inscriptionDao;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0:
				return this.data.get(rowIndex).getStudent().getMatricul();
			case 1:
				return this.data.get(rowIndex).getStudent().getFullName();
			case 2:
				return this.data.get(rowIndex).getStudent().getTelephone();
			case 3:
				return this.data.get(rowIndex).getPromotion();
		}
		return null;
	}

	@Override
	public void onSelectAcademicYear(AcademicYear year) {
		this.data.clear();
		if(this.inscriptionDao.checkByAcademicYear(year)) {
			this.data = this.inscriptionDao.findByAcademicYear(year);
		}
		fireTableDataChanged();
	}

	@Override
	public void onSelectFaulty(Faculty faculty, AcademicYear year) {
		this.data.clear();
		if(this.inscriptionDao.checkByFaculty(faculty, year)) {
			this.data = this.inscriptionDao.findByFaculty(faculty, year);
		}
		fireTableDataChanged();
	}

	@Override
	public void onSelectDepartment(Department department, AcademicYear year) {
		this.data.clear();
		if(this.inscriptionDao.checkByDepartment(department, year)) {
			this.data = this.inscriptionDao.findByDepartment(department, year);
		}
		fireTableDataChanged();
	}

	@Override
	public void onSelectPromotion(Promotion promotion) {
		this.data.clear();
		if(this.inscriptionDao.checkByPromotion(promotion.getId())) {
			this.data = this.inscriptionDao.findByPromotion(promotion);
		}
		fireTableDataChanged();
	}

	@Override
	public void onSelectInscription(Inscription inscription) {
		// TODO Auto-generated method stub
		
	}

}
