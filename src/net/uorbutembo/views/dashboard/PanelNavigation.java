/**
 * 
 */
package net.uorbutembo.views.dashboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.TreeCellRender;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 * Panel du menu de navigation d'une scene du dashboard
 */
public class PanelNavigation extends Panel  implements AcademicYearDaoListener{
	private static final long serialVersionUID = -26597150295854443L;
	
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private final TreePath rootPath = new TreePath(root);
	private final DefaultTreeModel treeModel = new DefaultTreeModel(root);
	private final JTree tree = new JTree(treeModel);
	private final DefaultTreeSelectionModel treeSelection = new DefaultTreeSelectionModel();
	private final JCheckBox checkFilter = new JCheckBox("Filtrage");
	
	private final List<NavigationListener> listeners = new ArrayList<>();
	private final List<FacultyFilter> lastPathFilter = new ArrayList<>();//la derniere collection des chemains filtrer
	
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	private StudyClassDao studyClassDao;
	
	private AcademicYear currentYear;
	
	public PanelNavigation(DAOFactory factory) {
		super(new BorderLayout());
		
		facultyDao = factory.findDao(FacultyDao.class);
		departmentDao = factory.findDao(DepartmentDao.class);
		studyClassDao = factory.findDao(StudyClassDao.class);
		
		tree.setSelectionModel(treeSelection);
		init();
		
		factory.findDao(AcademicYearDao.class).addYearListener(this);
		factory.findDao(AcademicYearDao.class).addListener(new DAOAdapter<AcademicYear>() {
			@Override
			public void onError(DAOException e, int requestId) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		this.currentYear = year;
		Thread  t = new Thread (() -> {			
			this.reload();
		});
		t.start();
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
			if(checkFilter.isSelected() && !lastPathFilter.isEmpty())
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
		if(!root.isLeaf())
			root.removeAllChildren();

		root.setUserObject(currentYear);
		List<Faculty> faculties = facultyDao.findByAcademicYear(currentYear);
		for (int index = 0, countFac = faculties.size(); index < countFac; index++) {
			
			Faculty faculty = faculties.get(index);
			List<Department> departments = departmentDao.findByFaculty(faculty, currentYear);
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
			}
		}

		treeModel.reload();
		tree.expandPath(rootPath);
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
