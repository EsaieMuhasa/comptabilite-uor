/**
 * 
 */
package net.uorbutembo.swing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
	
	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'à' hh:mm:ss");
	
	protected List<T> data = new ArrayList<>();
	protected int limit;
	protected int offset;

	/**
	 * 
	 */
	public TableModel(DAOInterface<T> daoInterface) {
		super();
		limit = 50;
		if(daoInterface != null)
			daoInterface.addListener(this);
	}
	
	/**
	 * Demande au model de recharger les donnees depuis la sources
	 */
	public synchronized void reload() {}
	
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit (int limit) {
		if(limit == this.limit)
			return;
		
		this.limit = limit;
		reload();
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		if(offset == this.offset)
			return;
		
		this.offset = offset;
		reload();
	}
	
	public void setInterval (int limit, int offset) {
		if(this.limit == limit && this.offset == offset )
			return;
		
		this.limit = limit;
		this.offset = offset;
		
		reload();
	}
	
	/**
	 * renvoie les comptes tottal des donnees
	 * @return
	 */
	public int getCount () {
		return getRowCount();
	}
	
	/**
	 * Charger la liste suivante des donnees
	 */
	public void next () {
		setOffset(offset + limit);
	}
	
	/**
	 * Charger la liste precedante des donnees
	 */
	public void previous () {
		setOffset(offset - limit);
	}
	
	/**
	 * est-il possible de charger la  liste des donnees suivant???
	 * @return
	 */
	public boolean hasNext() {
		return (getCount() > (offset + limit ));
	}
	
	/**
	 * Est-il possible de lire les donnees precedant
	 * @return
	 */
	public boolean hasPrevious() {
		return (0 <= (offset - limit ));
	}

	@Override
	public boolean isCellEditable (int rowIndex, int columnIndex) {
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
	 * Ajout d'une suite d'elemenets dans le tableau
	 * @param rows
	 */
	public void addRows (T [] rows) {
		for (T t : rows) {
			data.add(t);
		}
		fireTableRowsInserted( rows.length - data.size() -1, data.size()-1);	
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
	 * supression d'un ligne du table
	 * @param index
	 */
	public void removeRow (int index) {
		this.data.remove(index);
		if(getRowCount() == 0)
			fireTableDataChanged();
		else 
			fireTableRowsDeleted(index, index);
	}
	
	/**
	 * Mis en jour d'une ligne du model
	 * @param t
	 * @param index
	 */
	public void updateRow (T t, int index) {
		data.set(index, t);
		fireTableRowsUpdated(index, index);
	}
	
	/**
	 * Mise en jour d'une ligne
	 * @param t
	 */
	public void updateRow (T t ) {
		for (int i = 0, count = getRowCount(); i < count; i++) {
			if(data.get(i).getId() == t.getId()) {
				updateRow(t, i);
				return;
			}
		}
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
		if(data.size() == 1) {
			reload();
			return;
		}
		
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
	public void onFind (T e, int requestId) {}
	
	@Override
	public void onFind (List<T> e, int requestId) {}
	
	@Override
	public void onError (DAOException e, int requestId) {}

	@Override
	public void onCreate (T[] e, int requestId) {
		addRows(e);
	}

	@Override
	public void onUpdate (T[] e, int requestId) {
		for (T t : e) {
			onUpdate(t, requestId);
		}
	}

	@Override
	public void onDelete (T[] e, int requestId) {
		for (T t : e) {
			onDelete(t, requestId);
		}
	}

	@Override
	public void onCheck(boolean check, int requestId) {}
}
