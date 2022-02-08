/**
 * 
 */
package net.uorbutembo.dao;

import java.util.List;

import net.uorbutembo.beans.DBEntity;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class DAOAdapter <H extends DBEntity> implements DAOListener<H> {

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
	public void onFindAll(List<H> e, int requestId) {}

	@Override
	public void onError(DAOException e, int requestId) {}

}
