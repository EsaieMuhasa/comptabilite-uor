package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JScrollBar;

public class ScrollBar extends JScrollBar {
	private static final long serialVersionUID = -1964884899589131543L;

	/**
	 * constructeur par defaut
	 */
	public ScrollBar() {
        setUI(new ScrollBarUI());
        setPreferredSize(new Dimension(5, 5));
        setForeground(new Color(94, 139, 231));
        setUnitIncrement(20);
        setOpaque(false);
    }
}
