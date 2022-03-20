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
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public int getRowCount() {
		return this.data.size();
	}
	
	/**
	 * Renvoie la ligne a l'index en parametre
	 * @param index
	 * @return
	 */
	public T getRow (int index) {
		return this.data.get(index);
	}
	
	/**
	 * Ajout d'une ligne a la fin du tableau
	 * @param row
	 */
	public void addRow (T row) {
		this.data.add(row);
		fireTableRowsInserted(data.size()-1, data.size()-1);	
	}
	
	/**
	 * Insersion d'une ligne dans la collection des donnees du model d'un table
	 * @param row
	 * @param index
	 */
	public void addRow (T row, int index) {
		this.data.add(index, row);
		fireTableRowsInserted(index, data.size()-1);
	}

	/**
	 * @return the data
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<T> data) {
		this.data = data;
		this.fireTableDataChanged();
	}
	
	/**
	 * Supression de tout les donnees dans le model
	 */
	public void clear () {
		this.data.clear();
		this.fireTableDataChanged();
	}

	@Override
	public void onCreate(T e, int requestId) {
		this.addRow(e);
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
	public void onFind(List<T> e, int requestId) {}
	
	@Override
	public void onError(DAOException e, int requestId) {}

	@Override
	public void onCreate(T[] e, int requestId) {}

	@Override
	public void onUpdate(T[] e, int requestId) {}

	@Override
	public void onDelete(T[] e, int requestId) {}

	@Override
	public void onCheck(boolean check, int requestId) {}
}
