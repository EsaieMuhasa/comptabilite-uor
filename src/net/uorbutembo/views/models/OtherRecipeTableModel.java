/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class OtherRecipeTableModel extends TableModel<OtherRecipe> {
	private static final long serialVersionUID = 605942222382586566L;
	
	private final OtherRecipeDao otherRecipeDao;
	private final AnnualRecipeDao annualRecipeDao;
	private AcademicYear currentYear;

	/**
	 * @param daoInterface
	 */
	public OtherRecipeTableModel(OtherRecipeDao daoInterface) {
		super(daoInterface);
		otherRecipeDao = daoInterface;
		annualRecipeDao = daoInterface.getFactory().findDao(AnnualRecipeDao.class);
		limit = 50;
		offset = 0;
	}
	
	@Override
	public synchronized void reload() {
		data.clear();
		
		if (currentYear != null) {
			if(otherRecipeDao.checkByAcademicYear(currentYear, offset)) {
				List<OtherRecipe> recipes = otherRecipeDao.findByAcademicYear(currentYear, limit, offset);
				for (OtherRecipe r : recipes){
					r.setAccount(annualRecipeDao.findById(r.getAccount().getId()));
					data.add(r);
				}
			}			
		}
		
		fireTableDataChanged();
	}
	
	@Override
	public int getCount() {
		return otherRecipeDao.countByAcademicYear(currentYear);
	}

	/**
	 * @return the currentYear
	 */
	public AcademicYear getCurrentYear() {
		return currentYear;
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear (AcademicYear currentYear) {
		if(this.currentYear  == currentYear || (this.currentYear != null  && currentYear != null && currentYear.getId() == this.currentYear.getId()) )
			return;
		
		this.currentYear = currentYear;
		reload();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0 : return "Date";
			case 1 : return "Compte";
			case 2 : return "libelé";
			case 3 : return "Lieux de perception";
			case 4 : return "Montant";
		}
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0 : return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getRecordDate());
			case 1 : return data.get(rowIndex).getAccount().getUniversityRecipe().getTitle();
			case 2 : return data.get(rowIndex).getLabel();
			case 3 : return data.get(rowIndex).getLocation().toString();
			case 4 : return data.get(rowIndex).getAmount()+" USD";
		}
		return null;
	}
	
	@Override
	public void onCreate(OtherRecipe e, int requestId) {
		addRow(e, 0);
	}

}
