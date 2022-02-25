/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class Table extends JTable {
	private static final long serialVersionUID = 1086643646477646234L;

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
		this.setShowHorizontalLines(true);
        this.setGridColor(FormUtil.BORDER_COLOR);
        this.setRowHeight(40);
        this.getTableHeader().setReorderingAllowed(false);
//        this.setOpaque(false);
        this.getTableHeader().setBackground(FormUtil.BKG_END_2);
        
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 5825532896554469057L;

			@Override
        	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        		Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder);
                com.setForeground(Color.WHITE);
                if (isSelected) {
                    com.setBackground(FormUtil.BKG_END);
                } else {
                    com.setBackground(FormUtil.BKG_DARK);
                }
                return com;
        	}
        });
	}

}
