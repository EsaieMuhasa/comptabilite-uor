/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretListener;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OtherRecipePartDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PaymentFeePartDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TextField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormOutlay extends DefaultFormPanel {
	private static final long serialVersionUID = -3723343048984497445L;
	
	private final DefaultComboBoxModel<AcademicYear> comboYearModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<AcademicYear> comboFilterYearModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<PaymentLocation> comboLocationModel = new DefaultComboBoxModel<>();
	
	private final DefaultComboBoxModel<AnnualSpend> comboAccountModel = new DefaultComboBoxModel<>();
	private final Map<AcademicYear, List<AnnualSpend>> classementSpends = new HashMap<>();
	
	private final DefaultPieModel pieModel = new DefaultPieModel();
	private final PiePanel piePanel = new PiePanel(pieModel, FormUtil.BKG_END_2);
	
	private final ComboBox<AcademicYear> comboYear = new ComboBox<>("Année académique", comboYearModel);
	private final ComboBox<AnnualSpend> comboAccount = new ComboBox<>("Compte à crediter", comboAccountModel);
	private final ComboBox<AcademicYear> comboAccountYearFilter = new ComboBox<>("Année académique du compte", comboFilterYearModel);
	private final ComboBox<PaymentLocation> comboLocation = new  ComboBox<>("Leux de livraison", comboLocationModel);
	private final TextField<String> amountField = new TextField<>("Montant en "+FormUtil.UNIT_MONEY);
	
	private final FormGroup<String> groupAmount = FormGroup.createTextField(amountField);
	private final FormGroup<String> groupWording = FormGroup.createTextField("Libele de livraison");
	private final FormGroup<String> groupDate = FormGroup.createTextField("Date de livraison  (jj-mm-aaaa)");
	private final FormGroup<AcademicYear> groupYear = FormGroup.createComboBox(comboYear);
	private final FormGroup<PaymentLocation> groupLocation = FormGroup.createComboBox(comboLocation);
	private final FormGroup<AnnualSpend> groupAccount = FormGroup.createComboBox(comboAccount);
	private final FormGroup<AcademicYear> groupAccountYearFilter = FormGroup.createComboBox(comboAccountYearFilter);
	
	private final Button btnCancel = new Button(new ImageIcon(R.getIcon("close")), "Annuler la modification");
	
	private final AnnualSpendDao annualSpendDao;
	private final AcademicYearDao academicYearDao;
	private final OutlayDao outlayDao;
	private final PaymentFeeDao paymentFeeDao;
	private final OtherRecipeDao otherRecipeDao;
	private final PaymentLocationDao paymentLocationDao;
	private final PaymentFeePartDao paymentFeePartDao;
	private final OtherRecipePartDao otherRecipePartDao;
	
	private Outlay outlay;
	
	{
		pieModel.setRealMaxPriority(true);
		pieModel.setSuffix("$");
		pieModel.setTitle("Disponible par leux de perception");
	}
	
	private final CaretListener amountListener = event -> {
		validateAmount();
	};
	
	private final DAOAdapter<OtherRecipe> otherAdapter = new DAOAdapter<OtherRecipe>() {

		@Override
		public synchronized void onCreate(OtherRecipe e, int requestId) {
			reloadPieChart();
		}

		@Override
		public synchronized void onUpdate(OtherRecipe e, int requestId) {
			reloadPieChart();
		}

		@Override
		public synchronized void onDelete(OtherRecipe e, int requestId) {
			reloadPieChart();
		}
		
	};
	
	private final DAOAdapter<PaymentFee> paymentAdapter = new DAOAdapter<PaymentFee>() {

		@Override
		public synchronized void onCreate(PaymentFee e, int requestId) {
			reloadPieChart();
		}

		@Override
		public synchronized void onUpdate(PaymentFee e, int requestId) {
			reloadPieChart();
		}

		@Override
		public synchronized void onDelete(PaymentFee e, int requestId) {
			reloadPieChart();
		}
		
	};
	
	private final DAOAdapter<Outlay> outlayAdapter = new DAOAdapter<Outlay>() {

		@Override
		public synchronized void onCreate(Outlay e, int requestId) {
			PiePart part = pieModel.findByData(comboLocationModel.getElementAt(comboLocation.getSelectedIndex()));
			if (part != null) 
				part.setValue(part.getValue() - e.getAmount());
		}

		@Override
		public synchronized void onUpdate(Outlay e, int requestId) {
			reloadPieChart();
		}

		@Override
		public synchronized void onDelete(Outlay e, int requestId) {
			reloadPieChart();
		}
		
	};

	/**
	 * @param mainWindow
	 */
	public FormOutlay(MainWindow mainWindow) {
		super(mainWindow);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		outlayDao = mainWindow.factory.findDao(OutlayDao.class);
		paymentLocationDao = mainWindow.factory.findDao(PaymentLocationDao.class);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		otherRecipeDao = mainWindow.factory.findDao(OtherRecipeDao.class);
		paymentFeePartDao = mainWindow.factory.findDao(PaymentFeePartDao.class);
		otherRecipePartDao = mainWindow.factory.findDao(OtherRecipePartDao.class);
		
		outlayDao.addListener(outlayAdapter);
		paymentFeeDao.addListener(paymentAdapter);
		otherRecipeDao.addListener(otherAdapter);
		
		init();
		load();
		
		getFooter().add(btnCancel);
		btnCancel.setVisible(false);
		btnCancel.addActionListener(event -> {
			setOutlay(null);
		});
	}
	
	/**
	 * @param outlay the outlay to set
	 */
	public void setOutlay(Outlay outlay) {
		this.outlay = outlay;
		
		btnCancel.setVisible(outlay != null);
		
		if(outlay != null) {
			setTitle(TITLE_2);	
			
			groupAmount.getField().setValue(outlay.getAmount()+"");
			groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(outlay.getDeliveryDate()));
			groupWording.getField().setValue(outlay.getWording());
			for (int i = 0, count = comboFilterYearModel.getSize(); i < count; i++) {
				if(comboFilterYearModel.getElementAt(i).getId() == outlay.getAcademicYear().getId()) {
					comboAccountYearFilter.setSelectedIndex(i);
					break;
				}
			}
			
			for (int i = 0, count = comboAccountModel.getSize(); i < count; i++) {
				if(comboAccountModel.getElementAt(i).getId() == outlay.getAccount().getId()) {
					comboAccount.setSelectedIndex(i);
					break;
				}
			}
			
			for (int i = 0, count = comboLocationModel.getSize(); i < count; i++) {
				PaymentLocation l = comboLocationModel.getElementAt(i);
				
				if (outlay.getLocation().getId() == l.getId()) {
					comboLocation.setSelectedIndex(i);
					break;
				}
			}
		} else {
			groupAmount.getField().setValue("");
			groupWording.getField().setValue("");
			groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(new Date()));
		}
	}

	/**
	 * Chargement des donnees
	 */
	private void load() {
		List<AcademicYear> years = academicYearDao.countAll() == 0 ? new ArrayList<>() : academicYearDao.findAll();
		for (AcademicYear year : years) {
			if (annualSpendDao.checkByAcademicYear(year.getId())) 
				classementSpends.put(year, annualSpendDao.findByAcademicYear(year));
			else
				classementSpends.put(year, new ArrayList<>());
			
			comboFilterYearModel.addElement(year);
			comboYearModel.addElement(year);
		}
		
		List<PaymentLocation> locations = paymentLocationDao.countAll() != 0? paymentLocationDao.findAll() : null;
		if (locations != null) {
			for (PaymentLocation l : locations)
				comboLocationModel.addElement(l);
		}
		
		reloadPieChart();
		groupDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(new Date()));
	}
	
	private void validateAmount () {
		String amount = groupAmount.getField().getValue();
		if (!amount.trim().isEmpty() && amount.matches(RGX_NUMBER)) {
			double value = Double.parseDouble(amount);
			PiePart part = pieModel.findByData(comboLocationModel.getElementAt(comboLocation.getSelectedIndex()));
			btnSave.setEnabled(part != null && value <= part.getValue());
		} else {
			btnSave.setEnabled(false);
		}
	}
	
	/**
	 * initialisation de l'inteface graphique
	 */
	private void init() {
		final Panel pie = new Panel(new BorderLayout());
		final Panel container = new Panel(new BorderLayout());
		final Panel top = new Panel (new GridLayout(1, 2));
		final Box box = Box.createVerticalBox();
		
		//year combobox and combobox of payment location
		top.add(groupYear);
		top.add(groupLocation);
		//==
		
		//account
		final Panel boxAccount = new Panel(new GridLayout(1, 2));
		boxAccount.add(groupAccountYearFilter);
		boxAccount.add(groupAccount);
		//==
		
		//amount and date
		final Panel boxAmount = new Panel(new GridLayout(1, 1));
		boxAmount.add(groupAmount);
		boxAmount.add(groupDate);
		//==
		
		box.setOpaque(false);
		box.add(top);
		box.add(boxAccount);
		box.add(groupWording);
		box.add(boxAmount);
		
		container.add(box, BorderLayout.CENTER);
		container.setBackground(FormUtil.BKG_DARK);
		container.setOpaque(false);
		
		pie.setBorder(new EmptyBorder(10, 0, 0, 0));
		pie.add(piePanel, BorderLayout.CENTER);

		getBody().add(container, BorderLayout.NORTH);
		add(pie, BorderLayout.CENTER);
		setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		amountField.addCaretListener(amountListener);
		
		comboLocation.addItemListener(event -> {
			if (event.getStateChange() != ItemEvent.SELECTED) 
				return;
			
			if(comboLocation.getSelectedIndex() < pieModel.getCountPart())
				pieModel.setSelectedIndex(comboLocation.getSelectedIndex());
		});
		
		comboYear.addItemListener(event -> {
			if (event.getStateChange() != ItemEvent.SELECTED) 
				return;
			
			comboAccountYearFilter.setSelectedIndex(comboYear.getSelectedIndex());
			//AcademicYear year = comboFilterYearModel.getElementAt(comboAccountYearFilter.getSelectedIndex());
		});
		
		//events
		comboAccountYearFilter.addItemListener(event -> {
			List<AnnualSpend> spends = classementSpends.get(comboFilterYearModel.getElementAt(comboAccountYearFilter.getSelectedIndex()));
			comboAccount.setEnabled(spends!= null && !spends.isEmpty());
			comboAccountModel.removeAllElements();
			if(spends == null)
				return;
			
			for (AnnualSpend spend : spends) {
				comboAccountModel.addElement(spend);
			}
			
			btnSave.setEnabled(comboAccountModel.getSize() != 0);
		});
		//==
		
		comboAccount.addItemListener(event -> {
			if (event.getStateChange() != ItemEvent.SELECTED) 
				return;
			
			wait(true);
			Thread t = new Thread( () -> {
				reloadPieChart();
				wait(false);
			});
			t.start();
		});
	}
	
	private void wait (boolean status) {
		setCursor(status? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
		comboYear.setEnabled(!status);
		comboAccountYearFilter.setEnabled(!status);
		comboAccount.setEnabled(!status);
		btnSave.setEnabled(!status);
		comboAccount.setEnabled(!status && comboAccountModel.getSize() != 0);
		btnSave.setEnabled(!status && comboAccountModel.getSize() != 0);
	}
	
	private void reloadPieChart ()  {
		pieModel.removeAll();
		
		AnnualSpend account = comboAccountModel.getElementAt(comboAccount.getSelectedIndex());
		
		if(account != null) {
			int count = comboLocationModel.getSize();
			PiePart [] parts = new PiePart[count];
			for (int i = 0, colorIndex = 0; i < count; i++) {
				PaymentLocation l = comboLocationModel.getElementAt(i);
				
				if (!paymentFeePartDao.checkBySpend(account, l) && !otherRecipePartDao.checkBySpend(account, l))
					continue;
				
				double soldFee = paymentFeePartDao.getSoldBySpend(account, l);
				double soldOther = otherRecipePartDao.getSoldBySpend(account, l);
				double soldOut = outlayDao.getSoldByAccount(account.getId(), l.getId());
				
				double sold = soldFee + soldOther - soldOut;
				DefaultPiePart part = new DefaultPiePart(FormUtil.COLORS[(colorIndex) % (FormUtil.COLORS.length-1)], sold, l.toString());
				part.setData(l);
				parts[colorIndex++] = part;
			}
			pieModel.addParts(parts);
			pieModel.setTitle(account.toString()+", soldes diponibles");
			if (pieModel.getCountPart() > comboLocation.getSelectedIndex())
				pieModel.setSelectedIndex(comboLocation.getSelectedIndex());
			
		}
		validateAmount();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		final String amount = groupAmount.getField().getValue();
		final String label = groupWording.getField().getValue();
		final String date = groupDate.getField().getValue();
		final Date now = new Date();
		
		Outlay out  = new Outlay();
		String message = "";
		
		if (date.trim().length() == 0)
			message += "Entrez la date de livraison\n";
		else if (!date.matches(RGX_SIMBLE_DATE))
			message += "Entrez la date au format valide\n";
		else {			
			try {
				final Date deliveryDate = FormUtil.DEFAULT_FROMATER.parse(date);
				out.setDeliveryDate(deliveryDate);
			} catch (ParseException e) {
				message += "Une erreur est survenue lors du formatage de la date\n\t-> "+e.getMessage()+"\n";
			}
		}
		
		if (amount.trim().length() == 0)
			message += "Entrez le montant sortie en caisse\n";
		else if (!amount.matches(RGX_NUMBER))
			message += "Le montant doit être une valeur numérique valide";
		
		if (message.length() != 0) {
			showMessageDialog("Erreur", message, JOptionPane.ERROR_MESSAGE);
			return;			
		}
		
		AnnualSpend account = comboAccountModel.getElementAt(comboAccount.getSelectedIndex());
		AcademicYear academicYear = comboYearModel.getElementAt(comboYear.getSelectedIndex());
		AcademicYear deliveryYear = academicYearDao.findCurrent();
		
		out.setWording(label);
		out.setAccount(account);
		out.setAmount(Float.parseFloat(amount));
		out.setAcademicYear(academicYear);
		out.setLocation(comboLocationModel.getElementAt(comboLocation.getSelectedIndex()));
		
		try {
			if (outlay == null) {
				out.setRecordDate(now);
				out.setDeliveryYear(deliveryYear);
				outlayDao.create(out);
			} else {
				out.setLastUpdate(now);
				out.setRecordDate(outlay.getRecordDate());
				outlayDao.update(out, outlay.getId());
			}
			setOutlay(null);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
