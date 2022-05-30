/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AllocationRecipe;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;

/**
 * @author Esaie MUHASA
 *
 */
public interface AllocationRecipeDao extends DAOInterface<AllocationRecipe> {
	
	/**
	 * Verifie l'existance de la clee unique
	 * @param recipeId
	 * @param spendId
	 * @return
	 * @throws DAOException
	 */
	boolean check (long recipeId, long spendId) throws DAOException;
	/**
	 * Verification de l'existance de l'unicite des cles en parametre
	 * @param recipe
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	default boolean check (AnnualRecipe recipe, AnnualSpend spend) throws DAOException{
		return check (recipe.getId(), spend.getId());
	}
	
	/**
	 * renvoie l'occurence qui correspond aux clee uniques en parametre 
	 * @param recipeId
	 * @param spendId
	 * @return
	 * @throws DAOException
	 */
	default AllocationRecipe find (long recipeId, long spendId) throws DAOException {
		return find(getFactory().findDao(AnnualRecipeDao.class).findById(recipeId),
				getFactory().findDao(AnnualSpendDao.class).findById(spendId));
	}
	
	/**
	 * Renvoie l'occurence unique qui fait reference aux elements en parametre
	 * @param recipe
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	AllocationRecipe find (AnnualRecipe recipe, AnnualSpend spend) throws DAOException;
	
	/**
	 * Verifie l'existance d'aumoin une configuration d'une recette annuel
	 * @param recipeId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByRecipe (long recipeId) throws DAOException;
	default boolean checkByRecipe (AnnualRecipe recipe) throws DAOException{
		return checkByRecipe(recipe.getId());
	}
	
	/**
	 * comptage des elements de la configuration d'une recette annuel
	 * @param recipeId
	 * @return
	 * @throws DAOException
	 */
	int countByRecipe (long recipeId) throws DAOException;
	default int countByRecipe (AnnualRecipe recipe) throws DAOException {
		return countByRecipe(recipe.getId());
	}
	
	/**
	 * Renvoie la collection contenant la configuration dela repartition d'une recette annel
	 * @param recipe
	 * @return
	 * @throws DAOException
	 */
	List<AllocationRecipe> findByRecipe (AnnualRecipe recipe) throws DAOException;
	default List<AllocationRecipe> findByRecipe (long recipeId) throws DAOException {
		return findByRecipe(getFactory().findDao(AnnualRecipeDao.class).findById(recipeId));
	}
	
	/**
	 * Verifie l'existance d'une configuration pour une depense annuel
	 * @param spendId
	 * @return
	 * @throws DAOException
	 */
	boolean checkBySpend (long spendId) throws DAOException;
	default boolean checkBySpend (AnnualSpend spend) throws DAOException {
		return checkBySpend(spend.getId());
	}
	
	/**
	 * comptage des occurences qui font reference au depence en parametre
	 * @param spendId
	 * @return
	 * @throws DAOException
	 */
	int countBySpend (long spendId) throws DAOException;
	default int countBySpend (AnnualSpend spend) throws DAOException {
		return countBySpend(spend.getId());
	}
	
	/**
	 * renvoie la collection contenant la configuration concernant une depence annuel
	 * @param spend
	 * @return
	 * @throws DAOException
	 */
	List<AllocationRecipe> findBySpend (AnnualSpend spend) throws DAOException;
	default List<AllocationRecipe> findBySpend (long spendId) throws DAOException {
		return findBySpend(getFactory().findDao(AnnualSpendDao.class).findById(spendId));
	}
}
