/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class DAOAdapter <H extends DBEntity> implements DAOListener<H>, AcademicYearDaoListener{

	public DAOAdapter() {
		super();
	}

	@Override
	public void onCreate(H e, int requestId) {}

	@Override
	public void onUpdate(H e, int requestId) {}

	@Override
	public void onDelete(H e, int requestId) {}

	@Override
	public void onFind(H e, int requestId) {}

	@Override
	public synchronized void onFind(List<H> e, int requestId) {}

	@Override
	public synchronized void onError(DAOException e, int requestId) {}

	@Override
	public void onCurrentYear(AcademicYear year) {}

	@Override
	public void onCreate(H[] e, int requestId) {}

	@Override
	public void onUpdate(H[] e, int requestId) {}

	@Override
	public void onDelete(H[] e, int requestId) {}

	@Override
	public synchronized void onCheck(boolean check, int requestId) {}

}
