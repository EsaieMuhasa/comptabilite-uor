/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class Table extends JTable {
	private static final long serialVersionUID = 1086643646477646234L;
	
	private EmptyBorder padding = FormUtil.DEFAULT_EMPTY_BORDER;

	/**
	 * 
	 */
	public Table() {
		this.init();
	}
	
	/**
	 * @param rowData
	 * @param columnNames
	 */
	public Table(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		this.init();
	}

	/**
	 * @param dm
	 */
	public Table(TableModel dm) {
		super(dm);
		this.init();
	}
	
	/**
	 * 
	 */
	private void init() {
		setShowHorizontalLines(true);
		setShowVerticalLines(true);
        setGridColor(FormUtil.BORDER_COLOR);
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setBackground(FormUtil.BKG_END_2);
        setForeground(Color.WHITE);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        setSelectionBackground(FormUtil.BKG_END);
        setBackground(FormUtil.BKG_DARK);
        
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 5825532896554469057L;

			@Override
        	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(padding);
                com.setForeground(Table.this.getForeground());
                if (isSelected) {
                    com.setBackground(Table.this.getSelectionBackground());
                } else {
                    com.setBackground(Table.this.getBackground());
                }
                return com;
        	}
        });
	}
	
	/**
	 * Modification des marges horizotaux pour chaque case du table
	 * @param padding
	 */
	public void setPadding (int padding) {
		this.padding = new EmptyBorder(padding, padding, padding, padding);
		this.repaint();
	}

}
