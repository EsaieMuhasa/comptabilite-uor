/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.OtherRecipe;

/**
 * @author Esaie MUHASA
 *
 */
class OtherRecipePartDaoSql extends AbstractRecipePartDao<OtherRecipe> implements OtherRecipePartDao{

	public OtherRecipePartDaoSql (DefaultSqlDAOFactory factory) {
		super(factory);
	}
	
	@Override
	protected String getViewName() {
		return "V_OtherRecipePart";
	}
	
	@Override
	protected OtherRecipe mapSource(ResultSet result) throws SQLException {
		OtherRecipe recipe = new OtherRecipe(result.getLong("id"));
		recipe.setAmount(result.getDouble("amount"));
		recipe.setCollectionYear(new AcademicYear(result.getLong("collectionYear")));
		recipe.setAccount(new AnnualRecipe(result.getLong("account")));
		recipe.setLabel(result.getString("label"));
		recipe.setCollectionDate(new Date(result.getLong("collectionDate")));
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) 
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		return recipe;
	}

}
