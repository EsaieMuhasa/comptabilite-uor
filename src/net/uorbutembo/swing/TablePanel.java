/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class TablePanel extends Panel {
	private static final long serialVersionUID = 7441995737736176243L;
	
	private Table table;
	private JLabel title;
	
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
	
	private void init(boolean scrollable) {
		Panel header = new Panel(new BorderLayout());
		Panel body = new Panel(new BorderLayout());
		header.setOpaque(true);
		header.setBackground(FormUtil.BKG_START);
		header.add(this.title, BorderLayout.CENTER);
		
		if (scrollable) {			
			JScrollPane scroll = new JScrollPane(this.table);
			scroll.setVerticalScrollBar(new ScrollBar());
			scroll.setOpaque(false);
			scroll.getViewport().setOpaque(false);
			scroll.getViewport().setBorder(null);
			scroll.setViewportBorder(null);
			body.add(scroll, BorderLayout.CENTER);
		} else {
			body.add(table, BorderLayout.NORTH);
		}
		
		this.add(header, BorderLayout.NORTH);
		this.add(body, BorderLayout.CENTER);
		this.setBorder(new LineBorder(FormUtil.BORDER_COLOR, 1));
	}

}
