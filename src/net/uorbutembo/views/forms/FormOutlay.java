/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormOutlay extends DefaultFormPanel {
	private static final long serialVersionUID = -3723343048984497445L;
	
	private final GridLayout layout = new GridLayout(1, 1);
	
	private final DefaultComboBoxModel<AcademicYear> comboYearModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<AcademicYear> comboFilterYearModel = new DefaultComboBoxModel<>();
	
	private final DefaultComboBoxModel<AnnualSpend> comboAccountModel = new DefaultComboBoxModel<>();
	private final Map<AcademicYear, List<AnnualSpend>> classementSpends = new HashMap<>();
	
	private final ComboBox<AcademicYear> comboYear = new ComboBox<>("Année académique", comboYearModel);
	private final ComboBox<AnnualSpend> comboAccount = new ComboBox<>("Compte à crediter", comboAccountModel);
	private final ComboBox<AcademicYear> comboAccountYearFilter = new ComboBox<>("Année académique du compte", comboFilterYearModel);
	
	private final FormGroup<String> groupAmount = FormGroup.createTextField("Montant en "+FormUtil.UNIT_MONEY);
	private final FormGroup<String> groupWording = FormGroup.createTextField("Libele de livraison");
	private final FormGroup<String> groupDate = FormGroup.createTextField("Date de livraison");
	private final FormGroup<AcademicYear> groupYear = FormGroup.createComboBox(comboYear);
	
	private final AnnualSpendDao annualSpendDao;
	private final AcademicYearDao academicYearDao;
	private final OutlayDao outlayDao;
	
	private Outlay outlay;
	
	private final PieModelAccount pieModel = new PieModelAccount();
	private final PiePanel piePanel = new PiePanel(pieModel, FormUtil.BORDER_COLOR);

	/**
	 * @param mainWindow
	 */
	public FormOutlay(MainWindow mainWindow) {
		super(mainWindow);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		outlayDao = mainWindow.factory.findDao(OutlayDao.class);
		init();
		
		load();
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
	}
	
	/**
	 * initialisation de l'inteface graphique
	 */
	private void init() {
		final Panel container = new Panel(layout);
		final Box box = Box.createVerticalBox();
		
		//account
		final Panel boxAccount = new Panel(new GridLayout(1, 1));
		boxAccount.add(comboAccountYearFilter);
		boxAccount.add(comboAccount);
		boxAccount.setBorder(new EmptyBorder(0, 5, 0, 5));
		//==
		
		box.setOpaque(false);
		box.add(groupYear);
		box.add(boxAccount);
		box.add(groupAmount);
		box.add(groupWording);
		box.add(groupDate);
		
		container.add(box);
		container.add(piePanel);
		container.setBackground(FormUtil.BKG_END);
		container.setOpaque(false);

		getBody().add(container, BorderLayout.NORTH);
		
		comboYear.addItemListener(event -> {
			comboAccountYearFilter.setSelectedIndex(comboYear.getSelectedIndex());
			AcademicYear year = comboFilterYearModel.getElementAt(comboAccountYearFilter.getSelectedIndex());
			Thread t = new Thread(() -> {
				setCursor(FormUtil.WAIT_CURSOR);
				comboYear.setEnabled(false);
				comboAccountYearFilter.setEnabled(false);
				comboAccount.setEnabled(false);
				btnSave.setEnabled(false);
				pieModel.reload(year);
				comboYear.setEnabled(true);
				comboAccountYearFilter.setEnabled(true);
				comboAccount.setEnabled(comboAccountModel.getSize() != 0);
				setCursor(Cursor.getDefaultCursor());
				btnSave.setEnabled(comboAccountModel.getSize() != 0);
			});
			t.start();
		});
		
		//events
		comboAccountYearFilter.addItemListener(event -> {
			List<AnnualSpend> spends = classementSpends.get(comboYearModel.getElementAt(comboAccountYearFilter.getSelectedIndex()));
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
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		final float amount = Float.parseFloat(groupAmount.getField().getValue());
		final String label = groupWording.getField().getValue();
		final Date now = new Date();
		
		Outlay out  = new Outlay();
		
		try {
			final Date deliveryDate = FormUtil.DEFAULT_FROMATER.parse(groupDate.getField().getValue());
			if(outlay != null)
				outlay.setDeliveryDate(deliveryDate);
			else
				out.setDeliveryDate(deliveryDate);
		} catch (ParseException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		AnnualSpend account = comboAccountModel.getElementAt(comboAccount.getSelectedIndex());
		AcademicYear academicYear = comboYearModel.getElementAt(comboYear.getSelectedIndex());
		AcademicYear deliveryYear = academicYearDao.findCurrent();
		
		out.setWording(label);
		out.setAccount(account);
		out.setAmount(amount);
		out.setAcademicYear(academicYear);
		
		if (outlay == null) {			
			out.setRecordDate(now);
			out.setDeliveryYear(deliveryYear);
		} else {
			out.setLastUpdate(now);
			out.setRecordDate(outlay.getRecordDate());
		}
		
		try {
			if(outlay == null)
				outlayDao.create(out);
			else
				outlayDao.update(out, outlay.getId());
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private class PieModelAccount extends DefaultPieModel {
		
		private AcademicYear year;
		
		public synchronized void reload (AcademicYear year) {
			if(year == this.year)
				return;
		}
		
	}
	
	

}
