/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
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
import net.uorbutembo.swing.TreeCellRender;
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
		.addItemMenu(new NavbarButtonModel("payments", "Evolution des payement"), new PanelEvolution());
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
	
	/**
	 * @author Esaie MUHASA
	 * le panel de visualisation de l'evolution de payement de frais
	 */
	protected class PanelEvolution extends Panel {
		private static final long serialVersionUID = 2483552038817065024L;
		
		private final Panel center  = new Panel(new BorderLayout());
		private final Panel right = new Panel(new BorderLayout());
		private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, right);
		private PanelNavigation navigation = new PanelNavigation();
		
		public PanelEvolution() {
			super(new BorderLayout());
			init();
		}
		
		private void init() {
			split.setOneTouchExpandable(true);
			right.add(navigation);
			JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
			
			tabbed.addTab("Liste", new ImageIcon(R.getIcon("list")), new Panel(), "Liste des etats des compte des etudiants");
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
					for (FacultyFilter f : filters) {
						System.out.println(f.getFaculty());
						for (DepartmentFilter d : f.getDepartments()) {
							System.out.println("\t"+d.getDepartment());
							for (StudyClass s : d.getClasses()) {
								System.out.println("\t\t"+s);
							}
						}
					}
				}
			});
		}
		
		@Override
		public void doLayout() {
			super.doLayout();
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 * Listener specifique au panel de navigation
	 */
	public static interface NavigationListener {
		/**
		 * Lors du filtrage
		 * @param filters
		 */
		void onFilter(List<FacultyFilter> filters);
		
		/**
		 * Lors de l'activation ou de la desactivation du filtre
		 * @param filter
		 */
		void onFilterStatusChange (boolean filter);
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 * Empacketage du path de filtrage pour une faculté
	 */
	private static class FacultyFilter {
		private final Faculty faculty;
		private final List<DepartmentFilter> departments;
		private final DefaultMutableTreeNode node;
		
		/**
		 * @param faculty
		 * @param departments
		 * @param node
		 */
		public FacultyFilter(Faculty faculty, List<DepartmentFilter> departments, DefaultMutableTreeNode node) {
			super();
			this.faculty = faculty;
			this.departments = departments;
			this.node = node;
		}
		
		/**
		 * @return the faculty
		 */
		public Faculty getFaculty() {
			return faculty;
		}
		/**
		 * @return the departments
		 */
		public List<DepartmentFilter> getDepartments() {
			return departments;
		}
		
		/**
		 * @return the node
		 */
		public DefaultMutableTreeNode getNode() {
			return node;
		}

		/**
		 * Verifie si le departement est dans la pile de filtrage de la faculte
		 * @param department
		 * @return
		 */
		public boolean hasDepartment (Department department) {
			for (DepartmentFilter filter : departments) {
				if(filter.getDepartment().getId() == department.getId())
					return true;
			}
			
			return false;
		}
		
		/**
		 * Enenvoie les informations de filtrage pour le departement en oparemtre
		 * @param department
		 * @return
		 */
		public DepartmentFilter get (Department department) {
			for (DepartmentFilter filter : departments) {
				if(filter.getDepartment().getId() == department.getId())
					return filter;
			}
			return null;
		}
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 * Empacketage du path de filtrage pour un departement
	 */
	public static class DepartmentFilter {
		private final Department department;
		private final List<StudyClass> classes;
		private final DefaultMutableTreeNode node;
		
		/**
		 * @param department
		 * @param classes
		 * @param node
		 */
		public DepartmentFilter(Department department, List<StudyClass> classes, DefaultMutableTreeNode node) {
			super();
			this.department = department;
			this.classes = classes;
			this.node = node;
		}
		/**
		 * @return the department
		 */
		public Department getDepartment() {
			return department;
		}
		/**
		 * @return the classes
		 */
		public List<StudyClass> getClasses() {
			return classes;
		}
		/**
		 * @return the node
		 */
		public DefaultMutableTreeNode getNode() {
			return node;
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 * Panel du menu de navigation d'une scene du dashboard
	 */
	public class PanelNavigation extends Panel {
		private static final long serialVersionUID = -26597150295854443L;
		
		private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		private final TreePath rootPath = new TreePath(root);
		private final JTree tree = new JTree(root);
		private final DefaultTreeSelectionModel treeSelection = new DefaultTreeSelectionModel();
		private final JCheckBox checkFilter = new JCheckBox("Filtrage");
		
		private final List<NavigationListener> listeners = new ArrayList<>();
		private final List<FacultyFilter> lastPathFilter = new ArrayList<>();//la derniere collection des chemains filtrer
		
		public PanelNavigation() {
			super(new BorderLayout());
			tree.setSelectionModel(treeSelection);
			init();
			reload();
		}

		private void init() {
			final JScrollPane scroll = new JScrollPane(tree);
			final Panel bottom = new Panel(new FlowLayout(FlowLayout.LEFT));
			final Panel center = new Panel(new BorderLayout());
			
			scroll.setBorder(null);
			scroll.setBackground(FormUtil.BKG_END);
			scroll.getViewport().setOpaque(false);
			scroll.getViewport().setBorder(null);
			scroll.setViewportBorder(null);
			scroll.setOpaque(false);

			tree.setRowHeight(25);
			tree.setCellRenderer(new TreeCellRender());
			tree.setRootVisible(false);
			tree.setBackground(FormUtil.BKG_END);
			tree.setEnabled(false);
			
			checkFilter.setForeground(Color.WHITE);
			checkFilter.addActionListener(event -> {
				tree.setEnabled(checkFilter.isSelected());
				emitOnFilterStatusChange();
			});
			bottom.add(checkFilter);
			bottom.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			center.add(scroll, BorderLayout.CENTER);
			center.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			this.add(bottom, BorderLayout.NORTH);
			this.add(center, BorderLayout.CENTER);
			
			//evenements sur l'arbre (filtrage)
			tree.addTreeSelectionListener(event -> {
				TreePath [] paths = tree.getSelectionPaths();
				if(paths == null)
					return;
				
				lastPathFilter.clear();
				
				for (TreePath p : paths) {					
					Object [] pathDatas = p.getPath();//decompossition du path
					for (int i = 1; i< pathDatas.length ; i++) {//on laisse la racine
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathDatas[i];
						switch (i) {
							case 1:{//niveau faculte
								Faculty fac = (Faculty) node.getUserObject();
								if(!check(lastPathFilter, fac))
									lastPathFilter.add(new FacultyFilter(fac, new ArrayList<>(), node));
							} break;
							case 2:{//niveau departement d'un faculte
								Department dep = (Department) node.getUserObject();
								FacultyFilter fac = find(lastPathFilter, dep);
								if(!fac.hasDepartment(dep))
									fac.getDepartments().add(new DepartmentFilter(dep, new ArrayList<>(), node));
							} break;
							case 3:{//niveau classe d'etude
								Department dep = (Department) ( (DefaultMutableTreeNode) node.getParent()).getUserObject();
								FacultyFilter fac = find(lastPathFilter, dep);
								StudyClass cl = (StudyClass) node.getUserObject();
								fac.get(dep).getClasses().add(cl);
							} break;
						}
					}
				}
				if(checkFilter.isSelected())
					prepareFilter();
			});
		}
		
		/**
		 * preparation du filtre
		 */
		private void prepareFilter () {
			for (FacultyFilter filter : lastPathFilter) {
				if(filter.getDepartments().isEmpty()) {
					//tree.expandPath(new TreePath(filter.getNode().getPath()));//expension du contenue de la faculte
					for (int i = 0, fCount = filter.getNode().getChildCount(); i < fCount; i++) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) filter.getNode().getChildAt(i);
						filter.getDepartments().add(new DepartmentFilter((Department) node.getUserObject(), new ArrayList<>(), node));
						treeSelection.addSelectionPath(new TreePath(node.getPath()));//ajout du departement au element selectoiner
					}
				}
				
				//pour chaque departement de la faculte
				for(DepartmentFilter depFilter : filter.getDepartments()) {
					if(depFilter.getClasses().isEmpty()) {
						tree.expandPath(new TreePath(depFilter.getNode()));
						for (int i = 0, fCount = depFilter.getNode().getChildCount(); i < fCount; i++) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) depFilter.getNode().getChildAt(i);
							depFilter.getClasses().add((StudyClass) node.getUserObject());
							treeSelection.addSelectionPath(new TreePath(node.getPath()));
						}
					}
				}
			}

			emitOnFilter();
		}
		
		/**
		 * Chargement des contenues de l'arbre
		 */
		public void reload () {
			checkFilter.setSelected(false);
			root.removeAllChildren();
			List<Faculty> faculties = facultyDao.findByAcademicYear(currentYear);
			for (Faculty faculty : faculties) {
				List<Department> departments = departmentDao.findByFaculty(faculty, currentYear);
				DefaultMutableTreeNode facNode = new DefaultMutableTreeNode(faculty);
				for (Department department : departments) {
					department.setFaculty(faculty);
					DefaultMutableTreeNode depNode = new DefaultMutableTreeNode(department);
					
					List<StudyClass> studys = studyClassDao.findByAcademicYear(currentYear, department);
					for (StudyClass st : studys) {
						DefaultMutableTreeNode stNode = new DefaultMutableTreeNode(st);
						depNode.add(stNode);
					}
					facNode.add(depNode);
				}
				root.add(facNode);
			}
			tree.expandPath(rootPath);
			tree.setExpandsSelectedPaths(true);
		}
		
		/**
		 * Verification si la faculte est deja selectionner
		 * @param filters
		 * @param faculty
		 * @return
		 */
		private boolean check (List<FacultyFilter> filters, Faculty faculty) {
			for (FacultyFilter filter : filters) {
				if(filter.getFaculty() == faculty )
					return true;
			}
			return false;
		}
		
		/**
		 * Recuperation du classeur de selection d'un faculte, connaissant le departement
		 * @param filters
		 * @param department
		 * @return
		 */
		private FacultyFilter find (List<FacultyFilter> filters, Department department) {
			for (FacultyFilter filter : filters) {
				if(filter.getFaculty() == department.getFaculty()) {
					return filter;
				}
			}
			return null;
		}
		
		/**
		 * Abonnement d'un listener
		 * @param listener
		 */
		public void addListener (NavigationListener listener) {
			if(!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
		
		/**
		 * desabonnement d'un listener
		 * @param listener
		 */
		public void removeListener (NavigationListener listener) {
			listeners.remove(listener);
		}
		
		/**
		 * Emission du changement de statut du filtre
		 */
		protected void emitOnFilterStatusChange () {
			for (NavigationListener ls : listeners) {
				ls.onFilterStatusChange(checkFilter.isSelected());
			}
		}
		
		/**
		 * Emission de l'evenement de filtrage
		 */
		protected void emitOnFilter () {
			for (NavigationListener ls : listeners) {
				ls.onFilter(lastPathFilter);
			}
		}
		
	}

}
