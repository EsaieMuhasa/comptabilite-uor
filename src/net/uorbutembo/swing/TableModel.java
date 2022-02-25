/**
 * 
 */
package net.uorbutembo.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.uorbutembo.beans.DBEntity;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOInterface;
import net.uorbutembo.dao.DAOListener;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class TableModel <T extends DBEntity> extends AbstractTableModel implements DAOListener<T> {
	private static final long serialVersionUID = 6162491854899469995L;
	
	protected List<T> data = new ArrayList<>();

	/**
	 * 
	 */
	public TableModel(DAOInterface<T> daoInterface) {
		super();
		daoInterface.addListener(this);
	}

	@Override
	public void onCreate(T e, int requestId) {
		data.add(e);
		fireTableRowsInserted(data.size()-1, data.size()-1);
	}
	
	@Override
	public void onUpdate(T e, int requestId) {
		for (int i=0; i < data.size(); i++) {
			T t = data.get(i);
			if(t.getId() == e.getId()) {
				data.set(i, e);
				fireTableRowsUpdated(i, i);
				break;
			}
		}
	}
	
	@Override
	public void onDelete(T e, int requestId) {
		for (int i=0; i < data.size(); i++) {
			T t = data.get(i);
			if(t.getId() == e.getId()) {
				data.remove(i);
				fireTableRowsDeleted(i, i);
				break;
			}
		}
	}
	
	@Override
	public void onFind(T e, int requestId) {}
	
	@Override
	public void onFindAll(List<T> e, int requestId) {}
	
	@Override
	public void onError(DAOException e, int requestId) {}
}
