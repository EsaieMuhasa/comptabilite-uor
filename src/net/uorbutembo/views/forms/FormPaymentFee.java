/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
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
	
	private final DefaultComboBoxModel<PaymentLocation> locationModel = new DefaultComboBoxModel<>();
	private final ComboBox<PaymentLocation> comboLocation = new ComboBox<>("Lieux de payment", locationModel);
	
	private final FormGroup<String> slipNumber = FormGroup.createTextField("Numéro");
	private final FormGroup<String> slipDate = FormGroup.createTextField("Date de payement (jj-mm-aaaa)");
	private final FormGroup<String> receipNumber = FormGroup.createTextField("Numéro du réçu en caisse");
	private final FormGroup<String> receivedDate = FormGroup.createTextField("Date du reçu en caisse (jj-mm-aaaa)");
	private final FormGroup<String> wording = FormGroup.createTextField("libelé");
	private final FormGroup<String> amount = FormGroup.createTextField("Montant payer (en USD)");
	private final FormGroup<PaymentLocation> location = FormGroup.createComboBox(comboLocation);
	
	private PaymentFee fee = new  PaymentFee();//objet encours de traitement
	
	private Button btnCancel = new Button("Annuler");


	/**
	 * @param mainWindow
	 * @param paymentFeeDao
	 */
	public FormPaymentFee(MainWindow mainWindow, PaymentFeeDao paymentFeeDao) {
		super(mainWindow);
		this.paymentFeeDao = paymentFeeDao;
		this.setTitle(TITLE_1);
		
		Panel content = new Panel (new BorderLayout());
		
		final Panel 
			panelReference = new Panel (new BorderLayout()),
			panelCaisse = new Panel(new BorderLayout());
		
		final Box 
			boxReference = Box.createVerticalBox(),
			boxCaisse = Box.createVerticalBox();
		
		//titles
		final Panel 
			pTitleReference = new Panel(new BorderLayout()),
			pTitleCaisse = new Panel(new BorderLayout());
		
		final JLabel 
			labelReference = new  JLabel("Référence du lieux de payement"),
			labelCaisse = new  JLabel("Référence en caisse");
		
		final LineBorder border = new LineBorder(FormUtil.BORDER_COLOR);
		
		boxReference.add(location);
		boxReference.add(slipDate);
		boxReference.add(slipNumber);
		boxReference.add(amount);
		
		pTitleReference.setBackground(FormUtil.BORDER_COLOR);
		pTitleReference.add(labelReference, BorderLayout.NORTH);
		
		panelReference.add(pTitleReference, BorderLayout.NORTH);
		panelReference.add(boxReference, BorderLayout.CENTER);
		panelReference.setBorder(border);
		
		boxCaisse.add(receivedDate);
		boxCaisse.add(receipNumber);
		boxCaisse.add(wording);
		
		pTitleCaisse.setBackground(FormUtil.BORDER_COLOR);
		pTitleCaisse.add(labelCaisse, BorderLayout.NORTH);
		
		panelCaisse.add(pTitleCaisse, BorderLayout.NORTH);
		panelCaisse.add(boxCaisse, BorderLayout.CENTER);
		panelCaisse.setBorder(border);
		
		content.add(panelReference, BorderLayout.NORTH);
		content.add(panelCaisse, BorderLayout.SOUTH);
		
		this.setBorder(new LineBorder(Color.BLACK));
		this.getBody().add(FormUtil.createVerticalScrollPane(content), BorderLayout.CENTER);
		this.setBackground(Color.BLACK);
		this.setOpaque(true);
		
		this.getFooter().add(btnCancel);
		btnCancel.addActionListener(event -> {
			razFields(true);
			this.setVisible(false);
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
		this.receivedDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(fee.getReceivedDate()));
		this.receipNumber.getField().setValue(fee.getReceiptNumber());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String 
			amount = this.amount.getValue(),
			receipNumber = this.receipNumber.getValue(),
			wording = this.wording.getValue(),
			receivedDate = this.receivedDate.getValue(),
			slipDate = this.slipDate.getValue();
		
		fee.setInscription(inscription);
		fee.setWording(wording);
		fee.setReceiptNumber(receipNumber);
		fee.setSlipNumber(slipNumber.getValue());
		try {
			fee.setAmount(Float.parseFloat(amount));
			fee.setReceivedDate(FormUtil.DEFAULT_FROMATER.parse(receivedDate));
			fee.setSlipDate(FormUtil.DEFAULT_FROMATER.parse(slipDate));
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
			this.setVisible(false);
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
