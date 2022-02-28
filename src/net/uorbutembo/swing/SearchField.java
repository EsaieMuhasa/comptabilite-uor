/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Graphics2D;

import javax.swing.border.EmptyBorder;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class SearchField extends TextField<String> {
	private static final long serialVersionUID = 1034837196197272487L;

	public SearchField(String hint) {
		super(hint);
		this.setBorder(new EmptyBorder(12, 25, 5, 25));
	}
	
	@Override
	protected void createBorderStyle(Graphics2D g2) {
//		super.createBorderStyle(g2);
	}
	
	@Override
	protected void createLineStyle(Graphics2D g2) {
		if (mouseOver || this.hasFocus()) {
			g2.setColor(lineColor);
		} else {
			g2.setColor(FormUtil.BORDER_COLOR);
		}
		g2.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, this.getHeight(), this.getHeight());
	}

}
