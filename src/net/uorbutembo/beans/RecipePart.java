/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 *
 */
public interface RecipePart <T extends DBEntity>{
	
	/**
	 * Renvoie le libele d'entrer
	 * @return
	 */
	String getLabel ();
	
	/**
	 * Renvoie le leux où l'argent a ete deposer
	 * @return
	 */
	PaymentLocation getPaymentLocation ();
	
	/**
	 * renvoie le label de provenance
	 * @return
	 */
	String getTitle ();
	
	/**
	 * Renvoie la source de l'enter
	 * @return
	 */
	T getSource ();
	
	/**
	 * renvoie le montant
	 * @return
	 */
	double getAmount();
	
	/**
	 * Renvoie le compte qui a ete debuter
	 * @return
	 */
	AnnualSpend getAccount();
}
