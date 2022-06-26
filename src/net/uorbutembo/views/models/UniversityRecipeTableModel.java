/**
 * 
 */
package net.uorbutembo.views.models;

import java.util.List;

import net.uorbutembo.beans.UniversityRecipe;
import net.uorbutembo.dao.UniversityRecipeDao;
import net.uorbutembo.swing.TableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class UniversityRecipeTableModel extends TableModel <UniversityRecipe>{
	private static final long serialVersionUID = -3715461019742870626L;

	private UniversityRecipeDao universityRecipeDao;
	
	public UniversityRecipeTableModel(UniversityRecipeDao daoInterface) {
		super(daoInterface);
		this.universityRecipeDao = daoInterface;
	}

	@Override
	public void reload() {
		data.clear();
		List<UniversityRecipe> spends = (universityRecipeDao.countAll() != 0) ? universityRecipeDao.findAll() : null;
		
		if (spends != null)
			data = spends;
		
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
			case 0: return "Label";
			case 1: return "Date d'enregistrement";
			case 2: return "Dernière modification";
		}
		return super.getColumnName(column);
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return data.get(rowIndex).getTitle();
			case 1: return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getRecordDate());
			case 2: {
				if (data.get(rowIndex).getLastUpdate() == null)
					return " - ";
				return DEFAULT_DATE_TIME_FORMAT.format(data.get(rowIndex).getLastUpdate());
			}
		}
		return null;
	}

}
