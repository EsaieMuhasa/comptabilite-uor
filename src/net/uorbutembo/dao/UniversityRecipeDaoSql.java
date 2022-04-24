/**
 * 
 */
package net.uorbutembo.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.UniversityRecipe;

/**
 * @author Esaie MUHASA
 *
 */
class UniversityRecipeDaoSql extends UtilSql<UniversityRecipe> implements UniversityRecipeDao {

	public UniversityRecipeDaoSql(DefaultSqlDAOFactory factory) {
		super(factory);
	}

	@Override
	public void create(UniversityRecipe r) throws DAOException {
		try {
			long id = this.insertInTable(
					new String [] {"title", "description", "recordDate"},
					new Object[] {r.getTitle(), r.getDescription(), r.getRecordDate().getTime()});
			r.setId(id);
			this.emitOnCreate(r);
		} catch (SQLException e) {
			throw new DAOException("Une ererur est survenue lors de l'enregistrement\n"+e.getMessage(), e);
		}
	}

	@Override
	public void update(UniversityRecipe r, long id) throws DAOException {
		try {
			this.updateInTable(
					new String [] {"title", "description", "lastUpdate"},
					new Object[] {r.getTitle(), r.getDescription(), r.getLastUpdate().getTime()}, id);
			r.setId(id);
			this.emitOnUpdate(r);
		} catch (SQLException e) {
			throw new DAOException("Une erreur est survenue dans le processuce de modification\n"+e.getMessage(), e);
		}
	}
	
	@Override
	public List<UniversityRecipe> findByAcademicYear(long academicYearId) throws DAOException {
		List<UniversityRecipe> data = new ArrayList<>();
		String sql = String.format("SELECT * FROM %s WHERE id IN(SELECT universityRecipe FROM %s WHERE academicYear = %d)", getTableName(), AnnualRecipe.class.getSimpleName(), academicYearId);
		System.out.println(sql);
		try(Connection connection = factory.getConnection();
				Statement statement = connection.createStatement();
				ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) 
				data.add(mapping(result));
			
			if(data.isEmpty()) 
				throw new DAOException("Aucune configuration des recete pour l'annee academique ayant pour index => "+academicYearId);
		} catch (SQLException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return data;
	}

	@Override
	protected UniversityRecipe mapping(ResultSet result) throws SQLException, DAOException {
		UniversityRecipe recipe = new UniversityRecipe(result.getLong("id"));
		recipe.setDescription(result.getString("description"));
		recipe.setTitle(result.getString("title"));
		recipe.setRecordDate(new Date(result.getLong("recordDate")));
		if(result.getLong("lastUpdate") != 0) {
			recipe.setLastUpdate(new Date(result.getLong("lastUpdate")));
		}
		return recipe;
	}

	@Override
	protected String getTableName() {
		return UniversityRecipe.class.getSimpleName();
	}

}
