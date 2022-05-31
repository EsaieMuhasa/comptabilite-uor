/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class TablePanel extends Panel {
	private static final long serialVersionUID = 7441995737736176243L;
	
	private Table table;
	private JLabel title;
	private final Panel header = new Panel(new BorderLayout());
	
	public TablePanel(Table table, String title, boolean scrollable) {
		super(new BorderLayout());
		this.table = table;
		this.title = FormUtil.createSubTitle(title);
		this.init(scrollable);
	}

	/**
	 * 
	 */
	public TablePanel(Table table, String title) {
		super(new BorderLayout());
		this.table = table;
		this.title = FormUtil.createTitle(title);
		this.init(true);
	}
	
	/**
	 * Modificationd du titre du panel
	 * @param title
	 */
	public void setTitle (String title) {
		this.title.setText(title);
	}
	
	/**
	 * @return the header
	 */
	public Panel getHeader() {
		return header;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        g2.setColor(FormUtil.BORDER_COLOR);
        g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
	}

	private void init(boolean scrollable) {
		Panel body = new Panel(new BorderLayout());
		header.setOpaque(true);
		header.setBackground(FormUtil.BORDER_COLOR);
		header.add(this.title, BorderLayout.CENTER);
		
		if (scrollable) {			
			JScrollPane scroll = FormUtil.createVerticalScrollPane(table);
			body.add(scroll, BorderLayout.CENTER);
		} else {
			JTableHeader tHeader = table.getTableHeader();
			tHeader.setBackground(FormUtil.BORDER_COLOR);
			body.add(tHeader, BorderLayout.NORTH);
			body.add(table, BorderLayout.CENTER);
		}
		
		this.add(header, BorderLayout.NORTH);
		this.add(body, BorderLayout.CENTER);
		setBorder(new EmptyBorder(1, 1, 0, 0));
	}

}
