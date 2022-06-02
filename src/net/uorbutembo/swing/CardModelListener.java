/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;

/**
 * @author Esaie MUHASA
 *
 */
public interface CardModelListener {
	
	/**
	 * lorsqu'il y a des changement mageurs
	 * @param model
	 */
	void onChange (CardModel<?> model);
	
	/**
	 * lors du cgangement de l'etat de la valeur
	 * @param model
	 * @param oldValue
	 */
	void onValueChange (CardModel<?> model, Object oldValue);
	
	/**
	 * Lors du changement du titre du card
	 * @param model
	 * @param index (1 pour le titre pricipale et 2 pour le titre secogaire)
	 * @param oldTitle
	 */
	void onTitleChange (CardModel<?> model, int index, String oldTitle);

	/**
	 * lors du changement de la couleur du card
	 * @param model
	 * @param index (1 pour foreground et 2 pour background)
	 * @param oldColor
	 */
	void onColorChange (CardModel<?> model, int index, Color oldColor);
	
	/**
	 * Lorsque l'URI de l'icone change
	 * @param model
	 * @param oldIcon
	 */
	void onIconChange (CardModel<?> model, String oldIcon);
}
