/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.DAOException;
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
	
	private static final String TITLE_1 ="Enregistrement des frais universitaire",
			TITLE_2 ="Modificationd des frais universitaires";
	
	private PaymentFeeDao paymentFeeDao;
	private Inscription inscription;
	
	private final FormGroup<String> slipNumber = FormGroup.createTextField("Numéro du bordereau");
	private final FormGroup<String> slipDate = FormGroup.createTextField("Date de livraison du bordereau");
	private final FormGroup<String> receipNumber = FormGroup.createTextField("Numéro du reçu");
	private final FormGroup<String> receipDate = FormGroup.createTextField("Date de reception du reçu");
	private final FormGroup<String> wording = FormGroup.createTextField("libelé");
	private final FormGroup<String> amount = FormGroup.createTextField("Montant payer (en USD)");
	
	private final JDialog parent;
	private PaymentFee fee = new  PaymentFee();//objet encours de traitement

	/**
	 * @param paymentFeeDao
	 */
	public FormPaymentFee(JDialog parent, PaymentFeeDao paymentFeeDao) {
		super();
		this.parent = parent;
		this.paymentFeeDao = paymentFeeDao;
		this.setTitle(TITLE_1);
		
		Panel content = new Panel (new BorderLayout());
		
		Panel panTop = new Panel (new GridLayout(2, 2));
		Panel panBottom = new Panel (new BorderLayout());
		panTop.add(slipDate);
		panTop.add(slipNumber);
		panTop.add(receipDate);
		panTop.add(receipNumber);
		
		panBottom.add(wording, BorderLayout.NORTH);
		panBottom.add(amount, BorderLayout.SOUTH);
		
		content.add(panTop, BorderLayout.NORTH);
		content.add(panBottom, BorderLayout.SOUTH);
		
		this.getBody().add(content, BorderLayout.CENTER);
		
		parent.addWindowListener (new WindowAdapter() {
			//si l'utilisateur ferme la fenetre alors qu'il devait enregistrer les modifications,
			//alors on raz l'id de l'ojet qui facilite le la mediation
			@Override
			public void windowClosing(WindowEvent e) {
				if(fee.getId()>0)
					fee.setId(0);
			}
		});
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription (Inscription inscription) {
		this.inscription = inscription;
		this.setTitle(TITLE_1);
		this.razFields(false);
	}

	/**
	 * mutateur de l'obejt enours de modification
	 * @param fee the fee to set
	 */
	public void setFee (PaymentFee fee) {
		this.fee = fee;
		this.setTitle(TITLE_2);
		this.wording.getField().setValue(fee.getWording());
		this.amount.getField().setValue(fee.getAmount()+"");
		this.slipDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(fee.getSlipDate()));
		this.slipNumber.getField().setValue(fee.getSlipNumber());
		this.receipDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(fee.getReceivedDate()));
		this.receipNumber.getField().setValue(fee.getReceiptNumber());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		fee.setInscription(inscription);
		fee.setWording(wording.getValue());
		fee.setReceiptNumber(receipNumber.getValue());
		fee.setSlipNumber(slipNumber.getValue());
		try {
			fee.setAmount(Float.parseFloat(amount.getValue()));
			fee.setReceivedDate(FormUtil.DEFAULT_FROMATER.parse(receipDate.getValue()));
			fee.setSlipDate(FormUtil.DEFAULT_FROMATER.parse(slipDate.getValue()));
		} catch (Exception e) {
			this.showMessageDialog("Error", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			if(fee.getId()>0) {
				fee.setLastUpdate(new Date());
				paymentFeeDao.update(fee, fee.getId());
			}else{
				fee.setRecordDate(new Date());
				paymentFeeDao.create(fee);
			}
			
			this.razFields(true);
			this.parent.setVisible(false);
		} catch (DAOException e) {
			this.showMessageDialog("Echec d'enregistrement", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * vidage de champs de saisie
	 * @param razFee, faut-il re-instancier l'objet charger du stockge temporaire
	 */
	private void razFields (boolean razFee) {
		this.amount.getField().setValue("");
		this.slipNumber.getField().setValue("");
		this.receipNumber.getField().setValue("");
		
		if(razFee) 
			fee = new  PaymentFee();
	}

}
