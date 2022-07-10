/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.tools.FormUtil.DEFAULT_FROMATER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 * Formulaire de manutation des annees academique
 */
public class FormAcademicYear extends DefaultFormPanel {
	private static final long serialVersionUID = -4419809391236771300L;
	
	private AcademicYearDao academicYearDao;
	
	private FormGroup<String> startDate = FormGroup.createTextField("Date d'ouverture  (jj-mm-aaaa)");
	private FormGroup<String> closeDate = FormGroup.createTextField("Date de fermeture  (jj-mm-aaaa)");
	private FormGroup<String> label = FormGroup.createTextField("Label de l'année");
	private Button btnCancel = new Button(new ImageIcon(R.getIcon("close")), "Annuler");
	
	private final Panel panelImport = new Panel(new BorderLayout());
	private JCheckBox boxImport = new JCheckBox("Importer les configurations", true);
	private final DefaultComboBoxModel<AcademicYear> modelAcademicYar = new DefaultComboBoxModel<>();
	private final ComboBox<AcademicYear> comboAcademicYear = new ComboBox<>("Année académique", modelAcademicYar);
	private final FormGroup<AcademicYear> groupAcademicYear = FormGroup.createComboBox(comboAcademicYear);
	{
		Box box = Box.createHorizontalBox();
		box.setOpaque(true);
		box.add(boxImport);
		box.setBackground(FormUtil.BORDER_COLOR);
		box.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		boxImport.setForeground(Color.WHITE);
		boxImport.setFont(boxImport.getFont().deriveFont(Font.BOLD));
		boxImport.addChangeListener(event -> {
			groupAcademicYear.setVisible(boxImport.isSelected());
		});
		
		panelImport.add(box, BorderLayout.NORTH);
		panelImport.add(groupAcademicYear, BorderLayout.CENTER);
		panelImport.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
	}
	private AcademicYear academicYear;// != null lors de la modification
	
	private DAOAdapter<AcademicYear> academicAdapter = new DAOAdapter<AcademicYear>() {

		@Override
		public synchronized void onCreate(AcademicYear e, int requestId) {
			modelAcademicYar.addElement(e);
		}

		@Override
		public synchronized void onUpdate(AcademicYear e, int requestId) {
			for (int i = 0; i < modelAcademicYar.getSize(); i++) {
				if(modelAcademicYar.getElementAt(i).getId() == e.getId()) {
					modelAcademicYar.removeElementAt(i);
					modelAcademicYar.insertElementAt(e, i);
					break;
				}
			}
		}

		@Override
		public synchronized void onDelete(AcademicYear e, int requestId) {
			for (int i = 0; i < modelAcademicYar.getSize(); i++) {
				if(modelAcademicYar.getElementAt(i).getId() == e.getId()) {
					modelAcademicYar.removeElementAt(i);
					break;
				}
			}
		}
		
	};

	/**
	 * @param mainWindow
	 */
	public FormAcademicYear(MainWindow mainWindow) {
		super(mainWindow);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicYearDao.addListener(academicAdapter);
		setTitle(TITLE_1);
		init();
	}
	
	/**
	 * initalisation d'interface graphique
	 */
	private void init() {		
		JPanel center = new JPanel(new BorderLayout());
		Box form = Box.createHorizontalBox();
		center.setOpaque(false);
		form.setOpaque(false);
		
		center.add(label, BorderLayout.NORTH);
		form.add(startDate);
		form.add(closeDate);
		
		center.add(form, BorderLayout.CENTER);
		center.add(panelImport, BorderLayout.SOUTH);
		
		getBody().add(center, BorderLayout.CENTER);
		getFooter().add(btnCancel);
		btnCancel.setVisible(false);
		
		btnCancel.addActionListener(event -> {
			setAcademicYear(null);
		});
		
		if (academicYearDao.countAll() != 0) {			
			List<AcademicYear> list = academicYearDao.findAll();
			for (AcademicYear year : list)
				modelAcademicYar.addElement(year);
		} else
			panelImport.setVisible(false);
	}
	
	/**
	 * @param academicYear the academicYear to set
	 */
	public void setAcademicYear(AcademicYear academicYear) {
		this.academicYear = academicYear;
		boolean isnull = academicYear == null;
		setTitle(isnull? TITLE_1 : TITLE_2);
		btnCancel.setVisible(!isnull);
		panelImport.setVisible(isnull && modelAcademicYar.getSize() != 0);
		
		if (!isnull) {
			label.getField().setValue(academicYear.getLabel());
			startDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(academicYear.getStartDate()));
			if(academicYear.getCloseDate() != null)
				closeDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(academicYear.getCloseDate()));
		} else {
			label.getField().setValue("");
			startDate.getField().setValue(FormUtil.DEFAULT_FROMATER.format(new Date()));
			closeDate.getField().setValue("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String start  = this.startDate.getValue();
		String close = this.closeDate.getValue();
		String label = this.label.getValue();
		
		long id = academicYear == null? 0 : academicYear.getId();
		AcademicYear year = new AcademicYear();
		year.setLabel(label);
		String message = "";
		
		if (label.trim().length() == 0)
			message += "Entrez le labele de l'annee academique\n";
		else if (academicYearDao.checkByLabel(label, id))
			message += "le label "+label+", est déjà utiliser\n";
		
		try {
			year.setStartDate(DEFAULT_FROMATER.parse(start));
		} catch (ParseException e) {
			message += "Entrez la date d'ouverture de l'année academique au format valide\n";
		}
		
		if(close != null && !close.trim().isEmpty()) {				
			try {
				year.setStartDate(DEFAULT_FROMATER.parse(close));
			} catch (ParseException e) {
				message += "Entrez la date de fermeture de l'année academique au format valide\n";
			}
		}
		
		if (message.length() != 0){
			JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(year.getStartDate() != null && year.getLabel() != null && !year.getLabel().isEmpty()) {
			Date now = new Date();
			try {
				if(academicYear == null){
					year.setRecordDate(now);
					this.academicYearDao.create(year);
				} else {
					year.setLastUpdate(now);
					year.setRecordDate(academicYear.getRecordDate());
					year.setId(academicYear.getId());
					academicYearDao.update(year, academicYear.getId());
				}
				setAcademicYear(null);
				showMessageDialog("Information", "Année académique enregistrer avec success", JOptionPane.INFORMATION_MESSAGE);
			} catch (DAOException e) {
				showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
