/**
 * 
 */
package net.uorbutembo.views.forms;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

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
	
	/**
	 * @return
	 */
	public static final Dimension createDimensionSmCare () {
		return new Dimension(30, 30);
	}
}
