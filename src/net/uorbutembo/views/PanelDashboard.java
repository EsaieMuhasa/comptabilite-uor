/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.GeneralBudgetPieModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDashboard extends DefaultScenePanel implements YearChooserListener{
	private static final long serialVersionUID = 4525497607858984186L;
	
	private Box panelCards = Box.createHorizontalBox();
	private Panel panelCurrent = new Panel(new BorderLayout());
	
	private InscriptionDao inscriptionDao;
	private FacultyDao facultyDao;
	
	private AcademicYear currentYear;
	private JLabel labelTitle = FormUtil.createTitle("");
	
	//models cards
	private final DefaultCardModel<Integer> modelCardStudents = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
	// == model cards
	
	//model pie
	private final DefaultPieModel modelPieStudents = new DefaultPieModel(0, "Etudiant par faculté");
	private final GeneralBudgetPieModel globalModel;
	
	private PiePanel piePanel = new PiePanel();
	private Panel panelBottom = new Panel();
	
	public PanelDashboard(MainWindow mainWindow) {
		super("Tableau de board", new ImageIcon(R.getIcon("dashboard")), mainWindow, false);
		globalModel = new GeneralBudgetPieModel(mainWindow.factory);
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		
		mainWindow.getSidebar().addYearChooserListener(this);
		modelPieStudents.setSuffix("");
		init();
		
		Panel center = new Panel(new BorderLayout());
		Panel top = new Panel(new BorderLayout());
		center.setBorder(new EmptyBorder(10, 0, 0, 0));
		center.add(piePanel, BorderLayout.CENTER);
		piePanel.setBorderColor(FormUtil.BORDER_COLOR);
		
		labelTitle.setHorizontalAlignment(JLabel.RIGHT);
		top.add(labelTitle, BorderLayout.NORTH);
		top.add(panelCards, BorderLayout.CENTER);
		
		panelCurrent.add(top, BorderLayout.NORTH);
		panelCurrent.add(center, BorderLayout.CENTER);
		panelCurrent.add(panelBottom, BorderLayout.SOUTH);
		panelCurrent.setBorder(BODY_BORDER);
		
		ButtonGroup group = new ButtonGroup();
		
		JRadioButton  radio = new JRadioButton("Etudiants");
		JRadioButton  payment = new JRadioButton("Solde en caisse");
		JRadioButton  budget = new JRadioButton("Budget globale");
		ActionListener radionListener = (event -> {
			if(event.getSource() == radio) {
				piePanel.setModel(modelPieStudents);
			} else if(event.getSource() == payment) {
				piePanel.setModel(globalModel.getPieModelCaisse());
			} else if (event.getSource() == budget) {
				piePanel.setModel(globalModel);
			} else {
				piePanel.setModel(null);
			}
		});
		group.add(radio);
		group.add(payment);
		group.add(budget);
		panelBottom.add(radio);
		panelBottom.add(payment);
		panelBottom.add(budget);
		
		radio.addActionListener(radionListener);
		payment.addActionListener(radionListener);
		budget.addActionListener(radionListener);
		
		radio.setForeground(Color.WHITE);
		payment.setForeground(Color.WHITE);
		budget.setForeground(Color.WHITE);
		
		radio.doClick();

		this.getBody().add(panelCurrent, BorderLayout.CENTER);
	}
	
	/**
	 * @return the globalModel
	 */
	public GeneralBudgetPieModel getGlobalModel() {
		return globalModel;
	}

	@Override
	public boolean hasHeader() {
		return false;
	}
	
	@Override
	public void onChange(AcademicYear year) {		
		currentYear = year;
		if(year == null)
			return;
		
		if(year != null) 
			labelTitle.setText(year.getLabel());
		load();
	}
	
	/**
	 * Chargement des donnees depuis la BD
	 */
	private void load () {
		modelCardStudents.setValue(inscriptionDao.countByAcademicYear(currentYear));
		List<Faculty> faculties = facultyDao.checkByAcademicYear(currentYear)? facultyDao.findByAcademicYear(currentYear) : new ArrayList<>();
		modelPieStudents.setRealMaxPriority(true);
		modelPieStudents.removeAll();
		for (int i=0, max=faculties.size(); i<max; i++) {
			Faculty faculty = faculties.get(i);
			Color color = COLORS[i%(COLORS.length-1)];
			int count = inscriptionDao.countByFaculty(faculty, currentYear);
			DefaultPiePart part = new DefaultPiePart(color, color, count, faculty.getName());
			part.setData(faculty);
			modelPieStudents.addPart(part);
		}
		this.globalModel.setCurrentYear(currentYear);
	}
	
	/**
	 * initialisation de l'interface graphique
	 */
	private void init() {
		//students		
		modelCardStudents.setTitle("Etudiants inscrits");
		modelCardStudents.setInfo("Effectif total des étudiants inscrits");
		modelCardStudents.setIcon(R.getIcon("toge"));
		modelCardStudents.setValue(0);
		inscriptionDao.addListener(new DAOAdapter<Inscription>() {
			@Override
			public void onCreate(Inscription e, int requestId) {
				modelCardStudents.setValue(modelCardStudents.getValue()+1);
				
				PiePart []  parts =  modelPieStudents.getParts();
				for (PiePart part : parts) {
					Faculty fac = (Faculty) part.getData();
					if(fac.getId() == e.getPromotion().getDepartment().getFaculty().getId()) {
						part.setValue(part.getValue()+1);
						break;
					}
				}
			}
		});
		//==students
		
		panelCards.add(new Card(modelCardStudents));
		panelCards.add(Box.createHorizontalStrut(10));
		panelCards.add(new Card(globalModel.getCardModelPayment()));
		panelCards.add(Box.createHorizontalStrut(10));
		panelCards.add( new Card(globalModel.getCardModel()));
	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
}
