/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormPaymentFee extends DefaultFormPanel {
	private static final long serialVersionUID = -5541179441212225569L;
	
	private static final String TITLE_1 ="Formulaire",
			TITLE_2 ="Formulaire";
	
	private final PaymentFeeDao paymentFeeDao;
	private final PaymentLocationDao paymentLocationDao;
	private Inscription inscription;
	
	private final DefaultComboBoxModel<PaymentLocation> locationModel = new DefaultComboBoxModel<>();
	private final ComboBox<PaymentLocation> comboLocation = new ComboBox<>("Lieux de payement", locationModel);
	
	private final FormGroup<String> slipNumber = FormGroup.createTextField("Numéro (bodereau ou reçu)");
	private final FormGroup<String> slipDate = FormGroup.createTextField("Date de payement (jj-mm-aaaa)");
	private final FormGroup<String> receipNumber = FormGroup.createTextField("Numéro du reçu en caisse");
	private final FormGroup<String> receivedDate = FormGroup.createTextField("Date du reçu en caisse (jj-mm-aaaa)");
	private final FormGroup<String> wording = FormGroup.createTextField("libellé");
	private final FormGroup<String> amount = FormGroup.createTextField("Montant payer (en USD)");
	private final FormGroup<PaymentLocation> location = FormGroup.createComboBox(comboLocation);
	
	private PaymentFee fee;//objet encours de modification
	
	private Button btnCancel = new Button("Fermer");

	private final DAOAdapter<PaymentLocation> locationAdapter = new DAOAdapter<PaymentLocation>() {
		@Override
		public synchronized void onCreate(PaymentLocation e, int requestId) {
			locationModel.addElement(e);
		};
		
		@Override
		public synchronized void onUpdate(PaymentLocation e, int requestId) {
			for(int i = 0, count = locationModel.getSize(); i < count; i++)
				if (locationModel.getElementAt(i).getId() == e.getId()) {
					locationModel.removeElementAt(i);
					locationModel.addElement(e);
					return;
				}
		};
		
		@Override
		public synchronized void onDelete(PaymentLocation e, int requestId) {
			for(int i = 0, count = locationModel.getSize(); i < count; i++)
				if (locationModel.getElementAt(i).getId() == e.getId()) {
					locationModel.removeElementAt(i);
					return;
				}
		};
	};

	/**
	 * @param mainWindow
	 */
	public FormPaymentFee(MainWindow mainWindow) {
		super(mainWindow);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		paymentLocationDao = mainWindow.factory.findDao(PaymentLocationDao.class);
		setTitle(TITLE_1);
		
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
		
		pTitleReference.setOpaque(true);
		pTitleReference.setBackground(FormUtil.BORDER_COLOR);
		pTitleReference.add(labelReference, BorderLayout.NORTH);
		pTitleReference.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		labelReference.setForeground(Color.WHITE);
		
		panelReference.add(pTitleReference, BorderLayout.NORTH);
		panelReference.add(boxReference, BorderLayout.CENTER);
		panelReference.setBorder(border);
		
		boxCaisse.add(receivedDate);
		boxCaisse.add(receipNumber);
		boxCaisse.add(wording);
		
		pTitleCaisse.setOpaque(true);
		pTitleCaisse.setBackground(FormUtil.BORDER_COLOR);
		pTitleCaisse.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		pTitleCaisse.add(labelCaisse, BorderLayout.NORTH);
		labelCaisse.setForeground(Color.WHITE);
		
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
			razFields();
		});
		
		loadLocations();
		paymentLocationDao.addListener(locationAdapter);
		
	}
	
	/**
	 * premier chargement des donnees du combobox location
	 */
	private void loadLocations () {
		List<PaymentLocation> locations = paymentLocationDao.countAll() != 0? paymentLocationDao.findAll() : new ArrayList<>();
		for (PaymentLocation location : locations)
			locationModel.addElement(location);
		
		final Date now = new Date();
		receivedDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(now));
		slipDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(now));
	}

	/**
	 * @return the btnCancel
	 */
	public Button getBtnCancel() {
		return btnCancel;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription (Inscription inscription) {
		this.inscription = inscription;
		setTitle(TITLE_1);
		razFields();
	}

	/**
	 * mutateur de l'obejt enours de modification
	 * @param fee the fee to set
	 */
	public void setFee (PaymentFee fee) {
		this.fee = fee;
		if (fee != null) {			
			setTitle(TITLE_2);
			wording.getField().setValue(fee.getWording());
			amount.getField().setValue(fee.getAmount()+"");
			slipDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(fee.getSlipDate()));
			slipNumber.getField().setValue(fee.getSlipNumber());
			receivedDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(fee.getReceivedDate()));
			receipNumber.getField().setValue(fee.getReceiptNumber());
			
			for(int i = 0, count = locationModel.getSize(); i < count; i++)
				if (locationModel.getElementAt(i).getId() == fee.getLocation().getId()) {
					comboLocation.setSelectedIndex(i);
					break;
				}
		} else {
			razFields();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String 
			amount = this.amount.getField().getValue(),
			receipNumber = this.receipNumber.getField().getValue(),
			wording = this.wording.getField().getValue(),
			receivedDate = this.receivedDate.getField().getValue(),
			slipDate = this.slipDate.getField().getValue(),
			slipNumber = this.slipNumber.getField().getValue();
		
		String message = "";
		long id = fee == null? 0: fee.getId();
		
		if (amount.trim().length() == 0)
			message += "Entrez le montant payer!\n";
		else if (!amount.matches(RGX_NUMBER))
			message += "Le montant doit être une valeur numérique\n";
		
		if (receivedDate.trim().length() != 0 && !receivedDate.matches(RGX_SIMBLE_DATE))
			message += "Entrez la date du reçu au format valide\n";
		
		if (slipDate.trim().length() != 0 && !slipDate.matches(RGX_SIMBLE_DATE))
			message += "Entrez la date du lieux de payement au format valide\n";
		
		if (receipNumber.trim().length() != 0 && !receipNumber.matches(RGX_POSITIV_INT))
			message += "Le numéro du réçu doit être un entier positif";
		else if (paymentFeeDao.checkByReceiptNumber(receipNumber, id))
			message += "Ce numéro est déjà attribuer à un autre reçu en caisse\n";
		
		if (message.length() != 0) {
			showMessageDialog("Error", message, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		PaymentFee fee = new PaymentFee();
		
		try {
			fee.setAmount(Float.parseFloat(amount));
			fee.setReceivedDate(FormUtil.DEFAULT_FROMATER.parse(receivedDate));
			fee.setSlipDate(FormUtil.DEFAULT_FROMATER.parse(slipDate));
			
			if (fee.getAmount() <= 0)
				message += "Le montant doit être une valeur suppérieur à Zéro";
		} catch (Exception e) {
			message += "Une erreur est survenue lors du parsage des données saisie\n"+e.getMessage();
			return;
		}
		
		if (message.length() != 0) {
			showMessageDialog("Error", message, JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		receipNumber = receipNumber.length() == 0 ? null : receipNumber;
		slipNumber = slipNumber.length() == 0 ? null : slipNumber;
		
		fee.setInscription(inscription);
		fee.setLocation(locationModel.getElementAt(comboLocation.getSelectedIndex()));
		fee.setWording(wording);
		fee.setReceiptNumber(receipNumber);
		fee.setSlipNumber(slipNumber);
		Date now  = new Date();
		
		try {
			if(this.fee != null) {
				fee.setLastUpdate(now);
				fee.setRecordDate(this.fee.getRecordDate());
				paymentFeeDao.update(fee, this.fee.getId());
			}else{
				fee.setRecordDate(now);
				paymentFeeDao.create(fee);
			}
			showMessageDialog("Success", "Opération faite avec succès", JOptionPane.INFORMATION_MESSAGE);
			setFee(null);
		} catch (DAOException e) {
			showMessageDialog("Echec d'enregistrement", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	protected void doRaz() {
		super.doRaz();
		amount.getField().setValue("");
		slipNumber.getField().setValue("");
		receipNumber.getField().setValue("");
		wording.getField().setValue("");
	}
	
}
