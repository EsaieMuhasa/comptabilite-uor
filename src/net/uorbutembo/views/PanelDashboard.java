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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.FeePromotionDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.dashboard.PanelNavigation;
import net.uorbutembo.views.dashboard.PanelNavigation.DepartmentFilter;
import net.uorbutembo.views.dashboard.PanelNavigation.FacultyFilter;
import net.uorbutembo.views.dashboard.PanelNavigation.NavigationListener;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.GeneralBudgetModel;
import net.uorbutembo.views.models.PromotionPaymentTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDashboard extends DefaultScenePanel implements AcademicYearDaoListener{
	private static final long serialVersionUID = 4525497607858984186L;
	
	private Box panelCards = Box.createHorizontalBox();
	private Panel panelCurrent = new Panel(new BorderLayout());
	private PanelEvolution panelEvolution = new PanelEvolution();
	
	private InscriptionDao inscriptionDao;
	private AcademicFeeDao academicFeeDao;
	private FeePromotionDao feePromotionDao;
	private PaymentFeeDao paymentFeeDao;
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	private PromotionDao promotionDao;
	private StudyClassDao studyClassDao;
	private AnnualSpendDao annualSpendDao;
	private AllocationCostDao allocationCostDao;
	
	private AcademicYear currentYear;
	
	//models cards
	private final DefaultCardModel<Integer> modelCardStudents = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
	private final DefaultCardModel<Double> modelCardPaymentFee = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
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
		departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		allocationCostDao = mainWindow.factory.findDao(AllocationCostDao.class);
		
		mainWindow.factory.findDao(AcademicYearDao.class).addYearListener(this);
		mainWindow.factory.findDao(AcademicYearDao.class).addListener(new DAOAdapter<AcademicYear>() {
			@Override
			public void onError(DAOException e, int requestId) {
				e.printStackTrace();
			}
		});

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
		.addItemMenu(new NavbarButtonModel("payments", "Evolution de payement"), panelEvolution);
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		currentYear = year;
		load();
	}
	
	/**
	 * Chargement des donnees depuis la BD
	 */
	private void load () {
		modelCardStudents.setValue(inscriptionDao.countByAcademicYear(currentYear));
		List<Faculty> faculties = facultyDao.findByAcademicYear(currentYear);
		modelPieStudents.setMax(modelCardStudents.getValue());
		modelPieStudents.removeAll();
		for (int i=0, max=faculties.size(); i<max; i++) {
			Faculty faculty = faculties.get(i);
			Color color = COLORS[i%(COLORS.length-1)];
			int count = inscriptionDao.countByFaculty(faculty, currentYear);
			DefaultPiePart part = new DefaultPiePart(color, color, count, faculty.getName());
			part.setData(faculty);
			modelPieStudents.addPart(part);
		}
		this.modelPieBudget.setCurrentYear(currentYear);
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
		
		modelCardPaymentFee.setValue(0d);
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
		Card card = new Card(modelPieBudget.getCardModel());
		((DefaultCardModel<Double>) modelPieBudget.getCardModel()).setView(card);
		panelCards.add(card);
	}
	
	@Override
	public String getNikeName() {
		return "dashboard";
	}
	
	/**
	 * @author Esaie MUHASA
	 * le panel de visualisation de l'evolution de payement de frais
	 */
	protected class PanelEvolution extends Panel {
		private static final long serialVersionUID = 2483552038817065024L;
		
		private final Panel center  = new Panel(new BorderLayout());
		private final Panel right = new Panel(new BorderLayout());
		private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, right);
		private PanelNavigation navigation = new PanelNavigation(mainWindow.factory);
		
		public PanelEvolution() {
			super(new BorderLayout());
			init();
		}

		private void init() {
			split.setOneTouchExpandable(true);
			right.add(navigation);
			JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
			final Panel panelData = new Panel(new BorderLayout());
			panelData.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			tabbed.addTab("Liste", new ImageIcon(R.getIcon("list")), FormUtil.createVerticalScrollPane(panelData), "Liste des etats des compte des etudiants");
			tabbed.addTab("Graphique", new ImageIcon(R.getIcon("chart")), new Panel(), "Evolution des payements dans le temps");
			
			center.add(tabbed, BorderLayout.CENTER);
			this.add(split, BorderLayout.CENTER);
			
			split.setDividerLocation(550);
			navigation.addListener(new NavigationListener() {
				
				@Override
				public void onFilterStatusChange(boolean filter) {
					System.out.println("Filter: "+filter);
				}
				
				@Override
				public void onFilter(List<FacultyFilter> filters) {
					panelData.removeAll();
					Box box = Box.createVerticalBox();
					for (FacultyFilter f : filters) {
						Panel panelFaculty = new Panel(new BorderLayout());
						Box boxFaculty = Box.createVerticalBox();
						panelFaculty.add(FormUtil.createTitle(f.getFaculty().toString()), BorderLayout.NORTH);
						for (DepartmentFilter d : f.getDepartments()) {
							for (StudyClass s : d.getClasses()) {
								Promotion promotion = promotionDao.find(currentYear, d.getDepartment(), s);
								if(inscriptionDao.checkByPromotion(promotion)) {
									PromotionPaymentTableModel tableModel = new PromotionPaymentTableModel(mainWindow.factory);
									tableModel.setPromotion(promotion);
									Table table = new Table(tableModel);
									boxFaculty.add(new TablePanel(table, s.getAcronym()+" "+d.getDepartment().getName(), false));
									boxFaculty.add(Box.createVerticalStrut(10));
								}
							}
						}
						panelFaculty.add(boxFaculty, BorderLayout.CENTER);
						box.add(panelFaculty);
					}
					box.add(Box.createVerticalGlue());
					panelData.add(box, BorderLayout.CENTER);
					panelData.repaint();
				}
			});
		}
		
		@Override
		public void doLayout() {
			super.doLayout();
		}
	}
	

}
