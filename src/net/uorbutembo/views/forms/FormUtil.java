/**
 * 
 */
package net.uorbutembo.views.forms;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.CheckBox;
import net.uorbutembo.swing.RadioButton;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class FormUtil {
	public static final int DEFAULT_H_GAP = 5;
	public static final int DEFAULT_V_GAP = 5;
	
	public static final Color 
			BKG_START = new Color(20, 20, 20),
			BKG_END = new Color(50, 50, 50),
			BKG_END_2 = new Color(100, 100, 100),
			BKG_DARK = new Color(10, 10, 20);
	
	public static final Color 
							BORDER_COLOR = new Color(70, 70, 70),
							ACTIVE_COLOR = new Color(90, 120, 240);
	
	public static final String UNIT_MONEY = "USD";
	
	//bordure vide par defaut
	public static final EmptyBorder DEFAULT_EMPTY_BORDER = new EmptyBorder(DEFAULT_H_GAP, DEFAULT_H_GAP, DEFAULT_H_GAP, DEFAULT_H_GAP);
	
	
	public static SimpleDateFormat DEFAULT_FROMATER = new SimpleDateFormat("dd-MM-yyyy");
	/**
	 * Utilitaire de creation d'un titre
	 * @param title
	 * @return
	 */
	public static final JLabel createTitle (String title) {
		JLabel label = new JLabel(title);
		label.setFont(new Font("Arial", Font.PLAIN, 25));
		label.setForeground(Color.LIGHT_GRAY);
		label.setBorder(DEFAULT_EMPTY_BORDER);
		return label;
	}
	
	public static final JLabel createSubTitle (String title) {
		JLabel label = new JLabel(title);
		label.setFont(new Font("Arial", Font.PLAIN, 20));
		label.setForeground(Color.LIGHT_GRAY);
		label.setBorder(DEFAULT_EMPTY_BORDER);
		return label;
	}
	
	public static final JCheckBox createCheckBox (String label) {
		JCheckBox c = new JCheckBox(label);
		c.setForeground(Color.LIGHT_GRAY);
		c.setFont(new Font("Arial", Font.PLAIN, 14));
		c.setBorder(DEFAULT_EMPTY_BORDER);
		return c;
	}
	
	public static final <T> CheckBox<T> createCheckBox (String label, T data) {
		CheckBox<T> c = new CheckBox<>(label, data);
		c.setForeground(Color.LIGHT_GRAY);
		c.setFont(new Font("Arial", Font.PLAIN, 14));
		c.setBorder(DEFAULT_EMPTY_BORDER);
		return c;
	}
	
	public static final JRadioButton createRadioButon (String label) {
		JRadioButton c = new JRadioButton(label);
		c.setForeground(Color.LIGHT_GRAY);
		c.setFont(new Font("Arial", Font.PLAIN, 14));
		c.setBorder(DEFAULT_EMPTY_BORDER);
		return c;
	}
	
	public static final <T> RadioButton<T> createRadioButon (String label, T data) {
		RadioButton<T> c = new RadioButton<>(label, data);
		c.setForeground(Color.LIGHT_GRAY);
		c.setFont(new Font("Arial", Font.PLAIN, 14));
		c.setBorder(DEFAULT_EMPTY_BORDER);
		return c;
	}
	
	/**
	 * Utilitaire de creation d'un scrollpane, scrollable vericallement
	 * @param view
	 * @return
	 */
	public static final JScrollPane createVerticalScrollPane (JComponent view) {
		final JScrollPane scroll = new JScrollPane(view);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	
		scroll.getViewport().setOpaque(false);
		scroll.setViewportBorder(null);
		scroll.setBorder(null);		
		return scroll;
	}

	
	/**
	 * @return
	 */
	public static final Dimension createDimensionSmCare () {
		return new Dimension(30, 30);
	}
	
	//pouleurs
	public static final Color [] COLORS = new Color[] {
			new Color(0xFFCE30), new Color(0xE83845), new Color(0xE3889B), new Color(0x746AB0), new Color(0x288BA8),
			new Color(0xB22222), new Color(0xFF7F50), new Color(0xF0F80F), new Color(0xD72631), new Color(0xA2D5C6),
			new Color(0x077B8A), new Color(0x5C3C92), new Color(0xE2D810), new Color(0xD9138A), new Color(0x12A4D9)
	};
}
