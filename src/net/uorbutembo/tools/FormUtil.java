/**
 * 
 */
package net.uorbutembo.tools;


import java.awt.Color;
import java.awt.Cursor;
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
	public static final String UNIT_MONEY_SYMBOL = "$";
	
	//bordure vide par defaut
	public static final EmptyBorder DEFAULT_EMPTY_BORDER = new EmptyBorder(DEFAULT_H_GAP, DEFAULT_H_GAP, DEFAULT_H_GAP, DEFAULT_H_GAP);
	
	
	public static SimpleDateFormat DEFAULT_FROMATER = new SimpleDateFormat("dd-MM-yyyy");
	
	//les curseur les plus utiliser
	public static final Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	public static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);
	
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
	
	public static final JScrollPane createScrollPane (JComponent view) {
		final JScrollPane scroll = new JScrollPane(view);
		//scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);	
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
	
	//couleurs
	public static final Color [] COLORS = new Color[] {
			new Color(0xFFCE30), new Color(0xE83845), new Color(0xE9669F),
			new Color(0x746AB0), new Color(0x2050FA), new Color(0x9A9A00), 
			new Color(0xEF5020), new Color(0xF0CF6F), new Color(0xAFAFAF),
			new Color(0x20FF76), new Color(0x077B8A), new Color(0x5C3C92),
			new Color(0xE2D810), new Color(0xD9138A), new Color(0x12A4D9),
			new Color(0x22780F), new Color(0x381A3C), new Color(0x004C56),
			new Color(0x9B571D), new Color(0xB03468), new Color(0x4E5352),
			
			new Color(0x0081F8), new Color(0x00AFAF), new Color(0xF0F3FF)
	};
	
	public static final Color [] COLORS_ALPHA = new Color[] {
			new Color(0x55FFCE30, true), new Color(0x55E83845, true), new Color(0x55E9889F, true),
			new Color(0x55746AB0, true), new Color(0x55288BA8, true), new Color(0x55FF88FF, true), 
			new Color(0x555F7FF0, true), new Color(0x5590C86F, true), new Color(0x550756FF, true),
			new Color(0x55A2D5C6, true), new Color(0x55077B8A, true), new Color(0x555C3C92, true),
			new Color(0x55E2D810, true), new Color(0x55D9138A, true), new Color(0x5512A4D9, true),
			new Color(0x5522780F, true), new Color(0x55381A3C, true), new Color(0x55004C56, true),
			new Color(0x559B571D, true), new Color(0x55B03468, true), new Color(0x554E5352, true)
	};
	
}
