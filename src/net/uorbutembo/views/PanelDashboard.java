/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
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
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDashboard extends DefaultScenePanel {
	private static final long serialVersionUID = 4525497607858984186L;
	
	private Box panelCards = Box.createHorizontalBox();
	private Panel panelPies = new Panel(new GridLayout(1, 3, 10, 10));
	private Panel panelCurrent = new Panel(new BorderLayout());
	
	private InscriptionDao inscriptionDao;
	private AcademicFeeDao academicFeeDao;
	private FeePromotionDao feePromotionDao;
	private PaymentFeeDao paymentFeeDao;
	private FacultyDao facultyDao;
	
	private AcademicYear currentYear;
	
	//models cards
	private final DefaultCardModel<Integer> modelCardStudents = new DefaultCardModel<>(COLORS[6], Color.WHITE);
	private final DefaultCardModel<Double> modelCardPaymentFee = new DefaultCardModel<>(COLORS[11], Color.WHITE);
	private final DefaultCardModel<Double> modelCardBudget = new DefaultCardModel<>(COLORS[10], Color.WHITE);
	// == model cards
	
	//model pie
	private final DefaultPieModel modelPieStudents = new DefaultPieModel(0, "Etudiant par faculté");
	private final DefaultPieModel modelPieBudget = new DefaultPieModel(0, "Rubirque budgetaire");
	private final DefaultPieModel modelPiePaymentFee = new DefaultPieModel(0, "Payement par faculté");
	
	public PanelDashboard(MainWindow mainWindow) {
		super("Tableau de board", new ImageIcon(R.getIcon("dashboard")), mainWindow, false);
		
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		feePromotionDao = mainWindow.factory.findDao(FeePromotionDao.class);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		
		this.currentYear = mainWindow.factory.findDao(AcademicYearDao.class).findCurrent();
		
		initCards();
		initPies();
		
		
		panelCurrent.add(panelCards, BorderLayout.NORTH);
		panelCurrent.add(panelPies, BorderLayout.CENTER);
		panelCurrent.setBorder(BODY_BORDER);
		
		PiePanel 
			panel1 = new PiePanel(modelPieStudents, COLORS[6]),
			panel2 = new PiePanel(modelPieBudget, COLORS[11]),
			panel3 = new PiePanel(modelPiePaymentFee, COLORS[10]);
		
		panel2.setHorizontalPlacement(false);
		
		panelPies.add(panel1);
		panelPies.add(panel2);
		panelPies.add(panel3);
		
		panelPies.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		this
		.addItemMenu(new NavbarButtonModel("general", "Générale"), panelCurrent)
		.addItemMenu(new NavbarButtonModel("rubrique", "Rubiques budgetaire"), new Panel())
		.addItemMenu(new NavbarButtonModel("payments", "Evolution des payement"), new Panel());
	}
	
	private void initPies() {
		//students
		List<Faculty> faculties = facultyDao.findByAcademicYear(currentYear);
		modelPieStudents.setMax(inscriptionDao.countByAcademicYear(currentYear));
		for (int i=0, max=faculties.size(); i<max; i++) {
			Faculty faculty = faculties.get(i);
			Color color = COLORS[i%(COLORS.length-1)];
			int count = inscriptionDao.countByFaculty(faculty, currentYear);
			DefaultPiePart part = new DefaultPiePart(color, color, count, faculty.getAcronym());
			modelPieStudents.addPart(part);
		}
		//==students

		//budget generale
		
		//==budget generale
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
		
		modelCardStudents.setTitle("Etudiants inscrits");
		modelCardStudents.setInfo("Nombre des étudiants inscrits");
		modelCardStudents.setIcon(R.getIcon("toge"));
		modelCardStudents.setValue(inscriptionDao.countByAcademicYear(currentYear));
		inscriptionDao.addListener(new DAOAdapter<Inscription>() {
			@Override
			public void onCreate(Inscription e, int requestId) {
				modelCardStudents.setValue(modelCardStudents.getValue()+1);
			}
		});

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
		
		modelCardBudget.setValue(0.0);
		modelCardBudget.setTitle("Budget general");
		modelCardBudget.setInfo("Montant que doit payer tout les etudiants");
		modelCardBudget.setIcon(R.getIcon("acounting"));
		modelCardBudget.setSuffix("$");
		
		panelCards.add(new Card(modelCardStudents));
		panelCards.add(Box.createHorizontalStrut(10));
		panelCards.add(new Card(modelCardPaymentFee));
		panelCards.add(Box.createHorizontalStrut(10));
		panelCards.add(new Card(modelCardBudget));
	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
}
