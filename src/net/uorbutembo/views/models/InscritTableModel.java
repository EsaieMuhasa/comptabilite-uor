/**
 * 
 */
package net.uorbutembo.views.models;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class InscritTableModel extends TableModel<Inscription> {
	private static final long serialVersionUID = 1999957028728594769L;

	/**
	 * constructeur d'initialisation
	 * @param inscriptionDao
	 * @param year
	 */
	public InscritTableModel(InscriptionDao inscriptionDao, AcademicYear year) {
		super(inscriptionDao);
		this.data = inscriptionDao.findByAcademicYear(year);
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

}
