/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TreeCellRender;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 * Panel du menu de navigation d'une scene du dashboard
 */
public class SidebarStudents extends Panel{
	private static final long serialVersionUID = -26597150295854443L;
	
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private final TreePath rootPath = new TreePath(root);
	private final DefaultTreeModel treeModel = new DefaultTreeModel(root);
	private final JTree tree = new JTree(treeModel);
	private final DefaultTreeSelectionModel treeSelection = new DefaultTreeSelectionModel();
	private final JCheckBox checkFilter = new JCheckBox("Filtrage", false);
	
	private final JButton btnInscription = new JButton("Inscription");
	private final JButton btnReinscription = new JButton("Ré-inscription");
	
	private final List<NavigationListener> listeners = new ArrayList<>();
	private final List<FacultyFilter> lastPathFilter = new ArrayList<>();//la derniere collection des chemains filtrer
	private FacultyFilter [] faculties;
			
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	private StudyClassDao studyClassDao;
	private AcademicYearDao academicYearDao;
	private PromotionDao promotionDao;
	
	private AcademicYear currentYear;
	
	
	//selection d'une annee academique
	private DefaultComboBoxModel<AcademicYear> comboModel = new DefaultComboBoxModel<>();
	private JComboBox<AcademicYear> comboBox = new JComboBox<>(comboModel);
	//==
	
	private final TreeSelectionListener treeListener = (event) -> {
		TreePath [] paths = tree.getSelectionPaths();
		if(paths == null)
			return;
		
		lastPathFilter.clear();
		
		for (TreePath p : paths) {					
			Object [] pathDatas = p.getPath();//decomposition du path
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
		
		if(checkFilter.isSelected() && !lastPathFilter.isEmpty())
			prepareFilter();
	};
	
	private final ItemListener comboListener = (event) -> {
		if (event.getStateChange() == ItemEvent.DESELECTED)
			return;
		
		comboBox.setEnabled(false);
		EventQueue.invokeLater(() -> {			
			Thread t = new Thread(() -> {
				boolean  current = academicYearDao.isCurrent(comboModel.getElementAt(comboBox.getSelectedIndex()));
				btnInscription.setEnabled(current);
				btnReinscription.setEnabled(current);
				setCurrentYear(comboModel.getElementAt(comboBox.getSelectedIndex()));
				comboBox.setEnabled(true);
			});
			t.start();
		});
	};
	
	public SidebarStudents(DAOFactory factory) {
		super(new BorderLayout());	
		facultyDao = factory.findDao(FacultyDao.class);
		departmentDao = factory.findDao(DepartmentDao.class);
		studyClassDao = factory.findDao(StudyClassDao.class);
		academicYearDao = factory.findDao(AcademicYearDao.class);
		promotionDao = factory.findDao(PromotionDao.class);
		
		tree.setSelectionModel(treeSelection);
		init();
		DAOAdapter<AcademicYear> listener = new DAOAdapter<AcademicYear>() {
			@Override
			public void onCurrentYear(AcademicYear year) {
				if(comboModel.getSize() == 0) {
					comboBox.removeItemListener(comboListener);
					if(academicYearDao.countAll() != 0) {
						List<AcademicYear> years = academicYearDao.findAll();
						for (AcademicYear y : years) {
							comboModel.addElement(y);
						}
						comboBox.setEnabled(true);
						checkFilter.setEnabled(true);
					}
					comboBox.addItemListener(comboListener);
					btnInscription.setEnabled(true);
					btnReinscription.setEnabled(true);
				}
				setCurrentYear(year);
				checkFilter.setSelected(false);
			}
			
			@Override
			public synchronized void onError(DAOException e, int requestId) {
				e.printStackTrace();
			}
		};
		academicYearDao.addYearListener(listener);
		academicYearDao.addListener(listener);
		promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public synchronized void onCreate(Promotion e, int requestId) {
				if(currentYear!=null && e.getAcademicYear().getId() == currentYear.getId()) 
					insertPromotion(e);
			}
			
			@Override
			public synchronized void onCreate(Promotion[] e, int requestId) {reload();}
			
			@Override
			public void onDelete(Promotion e, int requestId) {removePromotion(e);}
			
			@Override
			public void onUpdate(Promotion e, int requestId) {reload();}
			
			@Override
			public void onUpdate(Promotion[] e, int requestId) {reload();}
		});
	}

	/**
	 * @return the currentYear
	 */
	public AcademicYear getCurrentYear() {
		return currentYear;
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public synchronized void setCurrentYear(AcademicYear currentYear) {
		if(this.currentYear == currentYear)
			return;
		
		this.currentYear = currentYear;
		if(comboBox.getSelectedIndex() != -1 && comboModel.getElementAt(comboBox.getSelectedIndex()).getId() != currentYear.getId()) {
			for (int i = 0, count = comboModel.getSize(); i < count; i++) {
				if(comboModel.getElementAt(i).getId() == currentYear.getId()) {
					comboBox.setSelectedIndex(i);
					break;
				}
			}
		}
		
		wait(true);
		this.reload();
		wait(false);
		
		checkFilter.setEnabled(root.getChildCount() != 0);
	}
	
	/**
	 * Insersion d'une promotion dans l'arbre de filtrage
	 * @param promotion
	 */
	private void insertPromotion(Promotion promotion) {
		reload();//pas efficace, s'il y a d'autres promotion où il y a des inscription
	}
	
	/**
	 * Supression d'une promotion dans l'arbre de filtrage
	 * @param promotion
	 */
	private void removePromotion(Promotion promotion) {
		reload();
	}

	/**
	 * @return the faculties
	 */
	public FacultyFilter [] getFaculties() {
		return faculties;
	}

	/**
	 * initialisation de l'interface graphique
	 */
	private void init() {
		final JScrollPane scroll = new JScrollPane(tree);
		final Panel top = new Panel(new BorderLayout());
		final Panel bottom = new Panel(new GridLayout(1,2, 5, 5));
		final Panel center = new Panel(new BorderLayout());
		
		scroll.setBorder(null);
		scroll.setBackground(FormUtil.BKG_END);
		scroll.getViewport().setOpaque(false);
		scroll.getViewport().setBorder(null);
		scroll.setViewportBorder(null);
		scroll.setOpaque(false);

		tree.setScrollsOnExpand(true);
		tree.setExpandsSelectedPaths(true);
		tree.setRowHeight(25);
		tree.setCellRenderer(new TreeCellRender());
		tree.setRootVisible(false);
		tree.setBackground(FormUtil.BKG_END);
		tree.setEnabled(false);
		
		checkFilter.setForeground(Color.WHITE);
		checkFilter.addActionListener(event -> {
			tree.setEnabled(checkFilter.isSelected());
			if (!lastPathFilter.isEmpty())
				emitOnFilter(false);
		});
		
		top.add(checkFilter, BorderLayout.EAST);
		top.add(comboBox, BorderLayout.CENTER);
		top.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		center.add(scroll, BorderLayout.CENTER);
		center.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		bottom.add(btnInscription);
		bottom.add(btnReinscription);
		bottom.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		comboBox.setEnabled(false);
		comboBox.addItemListener(comboListener);
		
		btnInscription.setEnabled(false);
		btnInscription.addActionListener(event -> {
			for (NavigationListener ls : listeners)
				ls.onInscription(event);
		});
		btnReinscription.setEnabled(false);
		btnReinscription.addActionListener(event -> {
			for (NavigationListener ls : listeners)
				ls.onReinscription(event);
		});
		
		this.add(top, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);
		this.add(bottom, BorderLayout.SOUTH);
		
		//evenements sur l'arbre (filtrage)
		tree.addTreeSelectionListener(treeListener);
		
	}
	
	/**
	 * preparation du filtre
	 */
	private void prepareFilter () {
		tree.removeTreeSelectionListener(treeListener);
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
		
		tree.addTreeSelectionListener(treeListener);
		Thread t = new Thread(() -> {
			emitOnFilter(false);
		});
		t.start();
	}
	
	/**
	 * Chargement des contenues de l'arbre
	 */
	public synchronized void reload () {
		
		if(lastPathFilter != null && !lastPathFilter.isEmpty())
			lastPathFilter.clear();
			
		if(!root.isLeaf())
			root.removeAllChildren();

		root.setUserObject(currentYear);
		List<Faculty> faculties = facultyDao.checkByAcademicYear(currentYear)? facultyDao.findByAcademicYear(currentYear) : new ArrayList<>();
		this.faculties = new FacultyFilter[faculties.size()];
		
		for (int index = 0, countFac = faculties.size(); index < countFac; index++) {
			
			Faculty faculty = faculties.get(index);
			List<Department> departments = departmentDao.findByFaculty(faculty, currentYear);
			List<DepartmentFilter> deps = new ArrayList<>();
			DefaultMutableTreeNode facNode = new DefaultMutableTreeNode(faculty);
			
			treeModel.insertNodeInto(facNode, root, root.getChildCount());
			treeModel.nodeChanged(root);
			
			for (Department department : departments) {
				department.setFaculty(faculty);
				DefaultMutableTreeNode depNode = new DefaultMutableTreeNode(department);
				List<StudyClass> studys = studyClassDao.findByAcademicYear(currentYear, department);
				for (StudyClass st : studys) {
					DefaultMutableTreeNode stNode = new DefaultMutableTreeNode(st);
					depNode.add(stNode);
				}
				facNode.add(depNode);
				deps.add(new DepartmentFilter(department, studys, depNode));
			}
			
			this.faculties[index] = new FacultyFilter(faculty, deps, facNode);
		}

		treeModel.reload();
		tree.expandPath(rootPath);
		
		checkFilter.setSelected(false);
		emitOnFilter(true);
	}
	
	/**
	 * Verification si la faculte est deja selectionner
	 * @param filters
	 * @param faculty
	 * @return
	 */
	private boolean check (List<FacultyFilter> filters, Faculty faculty) {
		for (FacultyFilter filter : filters) {
			if(filter.getFaculty() == faculty || filter.getFaculty().getId() == faculty.getId())
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
	 * pour notifier certains composant du menu 
	 * qu'il y a un traitement lourd encours d'execution
	 * @param wait
	 */
	public void wait (boolean wait) {
		checkFilter.setEnabled(!wait);
		
		if(wait) {
			tree.setEnabled(false);
			tree.setCursor(FormUtil.WAIT_CURSOR);
			setCursor(FormUtil.WAIT_CURSOR);			
		} else {
			tree.setEnabled(checkFilter.isSelected());
			tree.setCursor(Cursor.getDefaultCursor());
			setCursor(Cursor.getDefaultCursor());
		}
	}
	
	/**
	 * Emission de l'evenement de filtrage
	 * @param first s'il s'ajit du changement de l'annee academique
	 */
	protected synchronized void emitOnFilter (boolean first) {
		wait(true);
		if(checkFilter.isSelected() && !lastPathFilter.isEmpty()) {			
			FacultyFilter [] filters = new FacultyFilter[this.lastPathFilter.size()];
			for (int i=0; i < lastPathFilter.size(); i++) {
				filters[i] = lastPathFilter.get(i);
			}
			
			if (first) {
				for (NavigationListener ls : listeners)
					ls.onFilter(currentYear, filters);
			} else
				for (NavigationListener ls : listeners)
					ls.onFilter(filters);	
		} else {
			if (first) {
				for (NavigationListener ls : listeners)
					ls.onFilter(currentYear, faculties);
			} else
				for (NavigationListener ls : listeners)
					ls.onFilter(faculties);		
		}
		
		wait(false);
		checkFilter.setEnabled(root.getChildCount() != 0);
		
	}
	
	/**
	 * @author Esaie MUHASA
	 * Listener specifique au panel de navigation
	 */
	public static interface NavigationListener {
		
		/**
		 * Lors du filtrage sans changemnt de l'annee academique
		 * @param filters
		 */
		void onFilter(FacultyFilter [] filters);
		
		/**
		 * Lors du filtrage apres changement de l'annee academique
		 * @param year
		 * @param filters
		 */
		void onFilter(AcademicYear year, FacultyFilter [] filters);
		
		/**
		 * Lors de la demande d'inscription d'un nouveau etudiant
		 * @param event
		 */
		void onInscription (ActionEvent event);
		
		/**
		 * Lors de la demande renouvellement de l'inscription
		 * @param event
		 */
		void onReinscription (ActionEvent event);
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 * Empacketage du path de filtrage pour une faculté
	 */
	public static class FacultyFilter {
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

	
}
