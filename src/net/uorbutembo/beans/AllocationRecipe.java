/**
 * 
 */
package net.uorbutembo.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class AllocationRecipe extends DBEntity {
	private static final long serialVersionUID = 7469715824639163128L;
	
	private float percent;//pourventage 
	private AnnualRecipe recipe;//source de finalcement
	private AnnualSpend spend;//depense annuel associer au finalcement
	private double collected;//montant deja collecter

	/**
	 * 
	 */
	public AllocationRecipe() {
		super();
	}

	/**
	 * @param id
	 */
	public AllocationRecipe(long id) {
		super(id);
	}

	/**
	 * @return the percent
	 */
	public float getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(float percent) {
		this.percent = percent;
	}

	/**
	 * @return the recipe
	 */
	public AnnualRecipe getRecipe() {
		return recipe;
	}

	/**
	 * @param recipe the recipe to set
	 */
	public void setRecipe(AnnualRecipe recipe) {
		this.recipe = recipe;
	}

	/**
	 * @return the spend
	 */
	public AnnualSpend getSpend() {
		return spend;
	}

	/**
	 * @param spend the spend to set
	 */
	public void setSpend(AnnualSpend spend) {
		this.spend = spend;
	}

	/**
	 * @return the collected
	 */
	public double getCollected() {
		return collected;
	}

	/**
	 * @param collected the collected to set
	 */
	public void setCollected(double collected) {
		this.collected = collected;
	}

}
