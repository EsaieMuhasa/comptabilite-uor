/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.DefaultRecipePart;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.RecipePart;

/**
 * @author Esaie MUHASA
 *
 */
public interface PaymentFeePartDao extends RecipePartDao<RecipePart<PaymentFee>, DefaultRecipePart<PaymentFee>, PaymentFee> {

}
