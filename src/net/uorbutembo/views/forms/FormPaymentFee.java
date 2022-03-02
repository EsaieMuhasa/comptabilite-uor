/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormPaymentFee extends DefaultFormPanel {
	private static final long serialVersionUID = -5541179441212225569L;
	
	private PaymentFeeDao paymentFeeDao;
	private Inscription inscription;
	
	private final FormGroup<String> slipNumber = FormGroup.createTextField("Numéro du bordereau");
	private final FormGroup<String> slipDate = FormGroup.createTextField("Date de livraison du bordereau");
	private final FormGroup<String> receipNumber = FormGroup.createTextField("Numéro du reçu");
	private final FormGroup<String> receipDate = FormGroup.createTextField("Date de reception du reçu");
	private final FormGroup<String> wording = FormGroup.createTextField("libelé");
	private final FormGroup<String> amount = FormGroup.createTextField("Montant payer");

	/**
	 * @param paymentFeeDao
	 */
	public FormPaymentFee(PaymentFeeDao paymentFeeDao) {
		super();
		this.paymentFeeDao = paymentFeeDao;
		this.setTitle("Frais universitaire");
		
		Panel content = new Panel(new BorderLayout());
		
		Panel panTop = new Panel(new GridLayout(2, 2));
		Panel panBottom = new Panel(new BorderLayout());
		panTop.add(slipDate);
		panTop.add(slipNumber);
		panTop.add(receipDate);
		panTop.add(receipNumber);
		
		panBottom.add(wording, BorderLayout.NORTH);
		panBottom.add(amount, BorderLayout.SOUTH);
		
		content.add(panTop, BorderLayout.NORTH);
		content.add(panBottom, BorderLayout.SOUTH);
		
		this.getBody().add(content, BorderLayout.CENTER);
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(Inscription inscription) {
		this.inscription = inscription;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
