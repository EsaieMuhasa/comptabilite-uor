/**
 * 
 */
package net.uorbutembo.dao;

import net.uorbutembo.beans.AcademicYear;

/**
 * @author Esaie MUHASA
 *
 */
public interface AcademicYearDaoListener {
	
	/**
	 * Lors du chargemnt de l'annee academique actuel
	 * @param year
	 */
	void onCurrentYear (AcademicYear year);
}
