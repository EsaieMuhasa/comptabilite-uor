/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.event.ActionEvent;

import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.views.MainWindow;

/**
 * @author Esaie MUHASA
 *
 */
public class FormReRegister extends AbstractInscriptionForm {
	private static final long serialVersionUID = -3235229997664179423L;
	
	public FormReRegister(MainWindow mainWindow, InscriptionDao inscriptionDao, StudentDao studentDao) {
		super(mainWindow, inscriptionDao, studentDao);
		this.setTitle("Formulaire de re-inscription");
		init();
	}

	@Override
	protected void init() {
		super.init();
		responsiveFileds.add(this.matricul);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void onResize(int width) {
		super.onResize(width);
//		if(this.getWidth() <= 650) {
//			if(fieldsLayout.getRows()==2) {
//				fieldsLayout.setRows(3);
//				fieldsLayout.setColumns(1);
//			}
//		} else {
//			if(fieldsLayout.getRows() != 2) {
//				fieldsLayout.setRows(2);
//				fieldsLayout.setColumns(2);
//			}
//		}
	}

}
