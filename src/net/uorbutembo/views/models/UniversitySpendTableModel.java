/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.UniversitySpend;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class UniversitySpendTableModel extends TableModel <UniversitySpend> {
	private static final long serialVersionUID = -3715461019742870626L;
	
	private AnnualSpendDao annualSpendDao;
	private UniversitySpendDao universitySpendDao;
	private AcademicYear currentYear;
	
	public UniversitySpendTableModel(UniversitySpendDao daoInterface) {
		super(daoInterface);
		this.universitySpendDao = daoInterface;
		this.annualSpendDao = daoInterface.getFactory().findDao(AnnualSpendDao.class);
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		this.reload();
	}
	
	/**
	 * Recuperation du depence annuel pour l'index en paramtre
	 * @param index
	 * @return
	 */
	public AnnualSpend getAt (int index ) {
		if(currentYear != null) {
			return annualSpendDao.find(currentYear, data.get(index));
		}
		return null;
	}
	
	

	/**
	 * rechargement des donnes
	 */
	public void reload() {
		this.clear();
		List<UniversitySpend> spends = null;
		if(currentYear == null) {
			if(universitySpendDao.countAll() != 0)
				spends = universitySpendDao.findAll();
		} else {
			if (universitySpendDao.checkByAcademicYear(currentYear))
				spends = universitySpendDao.findByAcademicYear(currentYear);
		}
		
		if (spends != null) {
			UniversitySpend [] data = new UniversitySpend[spends.size()];
			for (int index = 0; index< spends.size(); index ++) {
				data[index] = spends.get(index);
			}
			addRows(data);
		}
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getData().get(rowIndex).getTitle();
	}

}
