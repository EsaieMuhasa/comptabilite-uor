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
public abstract class DAOAdapter <H extends DBEntity> implements DAOListener<H>, 
	AcademicYearDaoListener, DAOProgressListener<H> {

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
	public void onFind(List<H> e, int requestId) {}

	@Override
	public void onError(DAOException e, int requestId) {}

	@Override
	public void onCurrentYear(AcademicYear year) {}

	@Override
	public void onCreate(H[] e, int requestId) {}

	@Override
	public void onUpdate(H[] e, int requestId) {}

	@Override
	public void onDelete(H[] e, int requestId) {}

	@Override
	public void onCheck(boolean check, int requestId) {}

	@Override
	public void onStart(int requestId) {}

	@Override
	public void onProgress(int current, int max, String message, int requestId) {}

	@Override
	public void onFinish(int requestId) {}

	@Override
	public void onFinish(H data, int requestId) {}

	@Override
	public void onFinish(List<H> data, int requestId) {}

}
