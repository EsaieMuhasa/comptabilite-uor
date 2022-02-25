/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public interface PieModel {
	
	void setTitle (String title);
	String getTitle ();	
	
	//adding parts
	void addPart (PiePart part);
	void addPart (int index, PiePart part);
	void addParts (PiePart ...parts);
	// --
	
	/**
	 * Renvoie la valeur max. l'equivalent de 360°
	 * @return
	 */
	double getMax ();
	void setMax (double max);
	
	/**
	 * @param index
	 * @return
	 */
	default PiePart getPartAt (int index) {
		return this.getParts().get(index);
	}
	
	/**
	 * recuperation du part dont le nom est en parametre
	 * @param name
	 * @return
	 */
	default PiePart getPartByName (String name) {
		for (PiePart part : getParts()) {
			if(part.getName().equals(name)) 
				return part;
		}
		return null;
	}
	
	/**
	 * @param index
	 */
	void removePartAt (int index);
	void removeAll ();
	List<PiePart> getParts ();
	
	void addListener (PieModelListener listener);
	void removeListener (PieModelListener listener);
}
