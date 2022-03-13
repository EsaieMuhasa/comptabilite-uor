/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.GeneralBudgetModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDashboard extends DefaultScenePanel {
	private static final long serialVersionUID = 4525497607858984186L;
	
	private Box panelCards = Box.createHorizontalBox();
	private Panel panelCurrent = new Panel(new BorderLayout());
	
	private InscriptionDao inscriptionDao;
	private AcademicFeeDao academicFeeDao;
	private FeePromotionDao feePromotionDao;
	private PaymentFeeDao paymentFeeDao;
	private FacultyDao facultyDao;
	private AnnualSpendDao annualSpendDao;
	private AllocationCostDao allocationCostDao;
	
	private AcademicYear currentYear;
	
	//models cards
	private final DefaultCardModel<Integer> modelCardStudents = new DefaultCardModel<>(COLORS[6], Color.WHITE);
	private final DefaultCardModel<Double> modelCardPaymentFee = new DefaultCardModel<>(COLORS[11], Color.WHITE);
	// == model cards
	
	//model pie
	private final DefaultPieModel modelPieStudents = new DefaultPieModel(0, "Etudiant par faculté");
	private final GeneralBudgetModel modelPieBudget;
	private final DefaultPieModel modelPiePaymentFee = new DefaultPieModel(0, "Payement par faculté");
	
	private PiePanel piePanel = new PiePanel();
	private Panel panelBottom = new Panel();
	
	public PanelDashboard(MainWindow mainWindow) {
		super("Tableau de board", new ImageIcon(R.getIcon("dashboard")), mainWindow, false);
		modelPieBudget = new GeneralBudgetModel(mainWindow.factory);
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		feePromotionDao = mainWindow.factory.findDao(FeePromotionDao.class);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		allocationCostDao = mainWindow.factory.findDao(AllocationCostDao.class);
		
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		
		initCards();
		init();
		
		Panel center = new Panel(new BorderLayout());
		center.setBorder(new EmptyBorder(10, 0, 0, 0));
		center.add(piePanel, BorderLayout.CENTER);
		piePanel.setBorderColor(FormUtil.BORDER_COLOR);
		
		panelCurrent.add(panelCards, BorderLayout.NORTH);
		panelCurrent.add(center, BorderLayout.CENTER);
		panelCurrent.add(panelBottom, BorderLayout.SOUTH);
		panelCurrent.setBorder(BODY_BORDER);
		
		ButtonGroup group = new ButtonGroup();
		
		JRadioButton  radio = new JRadioButton("Etudiants");
		JRadioButton  payment = new JRadioButton("Payement");
		JRadioButton  budget = new JRadioButton("Budget général");
		ActionListener radionListener = (event -> {
			if(event.getSource() == radio) {
				piePanel.setModel(modelPieStudents);
			} else if(event.getSource() == payment) {
				piePanel.setModel(modelPiePaymentFee);
			} else if (event.getSource() == budget) {
				piePanel.setModel(modelPieBudget);
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

		this
		.addItemMenu(new NavbarButtonModel("general", "Générale"), panelCurrent)
		.addItemMenu(new NavbarButtonModel("payments", "Evolution des payement"), new Panel());
	}
	
	private void init() {
		
		//students		
		modelCardStudents.setTitle("Etudiants inscrits");
		modelCardStudents.setInfo("Nombre des étudiants inscrits");
		modelCardStudents.setIcon(R.getIcon("toge"));
		modelCardStudents.setValue(inscriptionDao.countByAcademicYear(currentYear));
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
		List<Faculty> faculties = facultyDao.findByAcademicYear(currentYear);
		modelPieStudents.setMax(inscriptionDao.countByAcademicYear(currentYear));
		for (int i=0, max=faculties.size(); i<max; i++) {
			Faculty faculty = faculties.get(i);
			Color color = COLORS[i%(COLORS.length-1)];
			int count = inscriptionDao.countByFaculty(faculty, currentYear);
			DefaultPiePart part = new DefaultPiePart(color, color, count, faculty.getName());
			part.setData(faculty);
			modelPieStudents.addPart(part);
		}
		//==students

		//budget generale
		this.modelPieBudget.setCurrentYear(currentYear);
	}
	
	/**
	 * Initialisation de cards
	 * <ul>
	 * <li>personnalisation de models des cards</li>
	 * <li>initalisation des vues des cards</li>
	 * <li></li>
	 * </ul>
	 */
	private void initCards() {

		modelCardPaymentFee.setValue(0.0);
		modelCardPaymentFee.setTitle("Payer par les etudiants");
		modelCardPaymentFee.setInfo("Montant déjà payer par tout les étudiants");
		modelCardPaymentFee.setIcon(R.getIcon("caisse"));
		modelCardPaymentFee.setSuffix("$");
		paymentFeeDao.addListener(new DAOAdapter <PaymentFee>() {
			@Override
			public void onCreate(PaymentFee e, int requestId) {
				modelCardPaymentFee.setValue(modelCardPaymentFee.getValue()+e.getAmount());
			}
		});
		

		panelCards.add(new Card(modelCardStudents));
		panelCards.add(Box.createHorizontalStrut(10));
		panelCards.add(new Card(modelCardPaymentFee));
		panelCards.add(Box.createHorizontalStrut(10));
		panelCards.add(new Card(modelPieBudget.getCardModel()));
	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
}
