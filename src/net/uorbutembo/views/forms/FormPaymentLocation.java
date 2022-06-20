/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormPaymentLocation extends DefaultFormPanel {
	private static final long serialVersionUID = -4354613955916407820L;

	private final TextField<String> name = new TextField<>("Appélation du lieux");
	private final FormGroup<String> nameGroup = FormGroup.createTextField(name);
	private final PaymentLocationDao paymentLocationDao;
	
	private PaymentLocation location;//occurence encours de modification
	
	/**
	 * @param mainWindow
	 */
	public FormPaymentLocation(MainWindow mainWindow) {
		super(mainWindow);
		
		paymentLocationDao = mainWindow.factory.findDao(PaymentLocationDao.class);
		setTitle(TITLE_1);
		Box box = Box.createVerticalBox();
		box.add(nameGroup);
		getBody().add(box, BorderLayout.CENTER);
	}

	/**
	 * @param location the location to set
	 */
	public void setPaymentLocation(PaymentLocation location) {
		this.location = location;
		
		if(location == null){
			setTitle(TITLE_1);
		} else 
			setTitle(TITLE_2);
		name.setValue(location!=null? location.getName() : "");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		PaymentLocation l = new PaymentLocation();
		l.setName(name.getValue().trim());
		
		if(paymentLocationDao.checkByName(l.getName())) {
			showMessageDialog("Avertissement", name.getValue()+"\nest déjà enregistrer en tant que leux de payement", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Date now = new Date();
		
		try {
			if (location == null) {
				l.setRecordDate(now);
				paymentLocationDao.create(l);
			} else {
				l.setRecordDate(location.getRecordDate());
				l.setLastUpdate(now);
				l.setId(location.getId());
				paymentLocationDao.update(l, location.getId());
			}
			
			setPaymentLocation(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
}
