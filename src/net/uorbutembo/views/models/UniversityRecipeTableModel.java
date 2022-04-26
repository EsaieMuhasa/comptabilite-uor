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
public class UniversityRecipeTableModel extends TableModel <UniversityRecipe> {
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
	public int getColumnCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return getData().get(rowIndex).getTitle();
	}

}
