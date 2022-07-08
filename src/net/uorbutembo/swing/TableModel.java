/**
 * 
 */
package net.uorbutembo.swing;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.uorbutembo.beans.DBEntity;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOInterface;
import net.uorbutembo.dao.DAOListener;
import net.uorbutembo.tools.Config;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class TableModel <T extends DBEntity> extends AbstractTableModel implements DAOListener<T> {
	private static final long serialVersionUID = 6162491854899469995L;
	
	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'à' hh:mm:ss");
	private final List<ExportationProgressListener> exportationProgressListeners = new ArrayList<>();
	
	protected List<T> data = new ArrayList<>();
	protected int limit;
	protected int offset;
	protected String title;

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
	
	/**
	 * modification de l'intervale de selection
	 * @param limit
	 * @param offset
	 */
	public void setInterval (int limit, int offset) {
		if (this.limit == limit && this.offset == offset )
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
	
	/**
	 * exportation des donnees au format Excell
	 * @param file
	 */
	public synchronized void exportToExcel (File file) {
		
		if(file.isDirectory()){
			fireExportationError(new RuntimeException("Impossible d'effectuer cette operation car le type de fichier n'est pas pris en charge \n"+file.getAbsolutePath()+" est un dossier."));
			return;
		}
		
		List<T> exportables = getExportableData();
		String fileName = file.getAbsolutePath();
		File reel = file;
		if(!fileName.matches("^(.+)(\\.xlsx)$")){
			fileName += ".xlsx";
			reel = new File(fileName);
		}
		
		fireExportationStart();
		try (
				XSSFWorkbook book = new XSSFWorkbook();
				FileOutputStream out = new FileOutputStream(fileName);
			){
			XSSFSheet sheet = book.createSheet(Config.find("appShortName").toLowerCase().replaceAll("(\\.)|([^a-z0-9])", "-")+"-sheet");
			
			//main title
			XSSFRow  headRow = sheet.createRow(0);
			XSSFCellStyle style = book.createCellStyle();
			XSSFFont font = book.createFont();
			font.setBold(true);
			font.setFontHeight(14);
			style.setFont(font);
			
			XSSFCell headCell = headRow.createCell(0);
			headCell.setCellStyle(style);
			headCell.setCellValue(getTitle() == null? "" : getTitle());
			//==
			
			//columns titles
			XSSFRow titles = sheet.createRow(1);
			style = book.createCellStyle();
			font = book.createFont();
			font.setBold(true);
			style.setFont(font);
			
			for (int i = 0, count = getExportableColumnCount(); i < count; i++) {
				XSSFCell title = titles.createCell(i);
				title.setCellValue(getExportableColumnName(i));
				title.setCellStyle(style);
			}
			//==
			
			for (int i = 0, count = exportables.size(); i < count; i++) {
				XSSFRow row = sheet.createRow(i+2);
				for (int j = 0; j < getExportableColumnCount(); j++) {
					XSSFCell cell = row.createCell(j, getColumnType(j));
					Object o = getCellValue(exportables, i, j);
					cell.setCellValue(o == null? "" : o.toString());
				}
				fireExportationProgress(i, count);
			}
			
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, getExportableColumnCount()-1));
			
			for (int i = 0; i < getExportableColumnCount(); i++)
				sheet.autoSizeColumn(i);
			
			
			book.write(out);
			fireExportationFinish(reel);
		} catch (Exception e) {
			fireExportationError(e);
		}
	}
	
	/**
	 * Renvoie la collection des donnees qui doivent etre exporter
	 * @return
	 */
	protected List<T> getExportableData() {
		return data;
	}
	
	/**
	 * Renvoie le title du model
	 * @return
	 */
	public String getTitle () {
		return title;
	}
	
	/**
	 * Mis en jour du titre du model
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * renvoie le nombre des colons exportable
	 * @return
	 */
	protected int getExportableColumnCount () {
		return getColumnCount();
	}
	
	/**
	 * renvoie le nom de la collone lors de l'exportation des donnees
	 * @param column
	 * @return
	 */
	protected String getExportableColumnName (int column) {
		return getColumnName(column);
	}
	
	/**
	 * renvoie la valeur d'une cellule
	 * @param exportables
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	protected Object getCellValue (List<T> exportables, int rowIndex, int columnIndex) {
		return getValueAt(rowIndex, columnIndex);
	}
	
	/**
	 * renvoie le type de la collonne
	 * @param columnIndex
	 * @return
	 */
	protected CellType getColumnType (int columnIndex) {
		return CellType.STRING;
	}
	
	/**
	 * informe tout les ecouteurs de la preogression d'exportation des donnees
	 * @param current
	 * @param max
	 */
	protected void fireExportationProgress (int current, int max) {
		for (ExportationProgressListener ls : exportationProgressListeners)
			ls.onProgress(this, current, max);
	}
	
	/**
	 * Informe les ecouteurs que l'exportation des donnees viens de commancer
	 */
	protected void fireExportationStart () {
		for (ExportationProgressListener ls : exportationProgressListeners)
			ls.onStart(this);
	}
	
	/**
	 * informe le ecouteur qu'il y a eux erreur lors de l'exportation des donnees
	 * @param e
	 */
	protected void fireExportationError (Exception e) {
		for (ExportationProgressListener ls : exportationProgressListeners)
			ls.onError(this, e);
	}
	
	/**
	 * informe les ecouteurs que l'exportation viens de prendre fin
	 * @param file
	 */
	protected void fireExportationFinish (File file) {
		for (ExportationProgressListener ls : exportationProgressListeners)
			ls.onFinish(this, file);
	}
	
	/**
	 * Ajout d'un ecoiuteur de la progression d'exportation des donnees
	 * @param ls
	 */
	public void addExportationProgressListener (ExportationProgressListener ls) {
		if(!exportationProgressListeners.contains(ls))
			exportationProgressListeners.add(ls);
	}
	
	/**
	 * desabonnement d'un ecouteur de progression d'exportation des donnees
	 * @param ls
	 */
	public void removeExportationProgressListener (ExportationProgressListener ls) {
		exportationProgressListeners.remove(ls);
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
	
	/**
	 * Listener d'exportation des donnees
	 * @author Esaie MUHASA
	 */
	public static interface ExportationProgressListener {
		/**
		 * lors du debut d'exportation des donnees
		 * @param model
		 */
		void onStart(TableModel<?> model);
		
		/**
		 * A chaque fois qu'il y a progression d'exportation des donnees
		 * @param model
		 * @param current
		 * @param max
		 */
		void onProgress(TableModel<?> model, int current, int max);
		
		/**
		 * s'il y a erreur lors de l'exportation des donnees
		 * @param model
		 * @param e
		 */
		void onError(TableModel<?> model, Exception e);
		
		/**
		 * fin d'exportation des donnees
		 * 
		 * @param model
		 * @param file
		 */
		void onFinish(TableModel<?> model, File file);
	}
}
