/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.SearchField;
import net.uorbutembo.swing.TreeCellRender;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class SidebarStudents extends Panel implements TreeSelectionListener{
	private static final long serialVersionUID = 7076526607851240031L;
	
	/**
	 * interface permetant le paneau parent d'ecouter les changements 
	 * des etats de l'arbre du sidebar
	 * @author Esaie MUHASA
	 *
	 */
	public interface SidebarListener {
		
		/**
		 * Lors de la selection d'une anne academique
		 * @param year
		 */
		void onSelectAcademicYear (AcademicYear year);
		
		/**
		 * Losque l'utilisateur selectionne une faculte dans le menu
		 * @param faculty
		 * @param year
		 */
		void onSelectFaulty (Faculty faculty, AcademicYear year);
		
		/**
		 * Lorsque l'utilisateur selectionne un departement dans le menu
		 * @param department
		 * @param year
		 */
		void onSelectDepartment (Department department, AcademicYear year);
		
		/**
		 * Lors l'utilisateur selectionne la promotion dans le menu
		 * @param promotion
		 */
		void onSelectPromotion (Promotion promotion);
		
		/**
		 * Losque l'utilisateur selectionne un inscrit dans la liste des inscrit du menu
		 * @param inscription
		 */
		void onSelectInscription (Inscription inscription);
	}
	
	private DefaultComboBoxModel<AcademicYear> comboYearModel = new DefaultComboBoxModel<>();
	private ComboBox<AcademicYear> comboAcademic = new ComboBox<>("Année Academique", comboYearModel);
	private SearchField fieldSearch = new SearchField("Recherche");
	
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private final TreeModel treeModel = new DefaultTreeModel(root);
	private final JTree tree = new JTree(treeModel);
	
	private Map<AcademicYear, List<Faculty>> facultyByAcademicYear = new HashMap<>();//collection regroupant les faculté par annee academique
	private Map<Faculty, List<Department>> departmentByAcademicYear = new HashMap<>();//collection des departements pour chaque faculte
	private Map<Department, List<StudyClass>> studyClassByAcademicYear = new HashMap<>();//classe d'etude pour chaque department
	
	private AcademicYearDao academicYearDao;
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	private StudyClassDao studyClassDao;
	private PromotionDao promotionDao;
	
	private final List<SidebarListener> listeners = new ArrayList<>();

	/**
	 * @param mainWindow
	 * @param listener
	 */
	public SidebarStudents(MainWindow mainWindow, SidebarListener ...listener) {
		super(new BorderLayout());
		
		for (SidebarListener ls : listener) {			
			this.listeners.add(ls);
		}
		
		//les dao
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		//-- dao
		
		this.setBorder(new EmptyBorder(5, 10, 5, 5));
		
		final Box top = Box.createVerticalBox();
		final JScrollPane scrollPane = new JScrollPane(tree);
		
		scrollPane.setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBorder(null);
		
		top.add(fieldSearch);
		top.add(Box.createVerticalStrut(5));
		
		
		this.add(top, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(comboAcademic, BorderLayout.SOUTH);
		
		List<AcademicYear> years = this.academicYearDao.findAll();
		for (AcademicYear year : years) {
			comboYearModel.addElement(year);
			if(this.facultyDao.checkByAcademicYear(year)) {
				
				List<Faculty> faculties = this.facultyDao.findByAcademicYear(year);
				this.facultyByAcademicYear.put(year, faculties);
				
				for (Faculty faculty : faculties) {
					if(this.departmentDao.checkByFaculty(faculty, year)) {
						
						List<Department> deps =this.departmentDao.findByFaculty(faculty, year);
						departmentByAcademicYear.put(faculty, deps);
						
						for (Department dep : deps) {
							if(this.studyClassDao.checkByAcademicYear(year, dep)) {
								this.studyClassByAcademicYear.put(dep, this.studyClassDao.findByAcademicYear(year, dep));
							}
						}
					}
				}
			}
		}
		
		comboAcademic.addItemListener(event -> {
			root.removeAllChildren();
			AcademicYear year  =comboYearModel.getElementAt(comboAcademic.getSelectedIndex());
			this.updateTree(year);
		});
		
		AcademicYear year = comboYearModel.getElementAt(comboAcademic.getSelectedIndex());
		this.updateTree(year);
		
		
		tree.setRowHeight(25);
		tree.setCellRenderer(new TreeCellRender());
		tree.setBackground(FormUtil.BKG_END);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		tree.addTreeSelectionListener(this);
	}
	
	private void updateTree (AcademicYear year) {
		
		List<Faculty> facs = facultyByAcademicYear.get(year);
		this.root.setUserObject(year);
		this.root.removeAllChildren();
		
		if(facs != null) {
			for (Faculty faculty : facs) {
				
				DefaultMutableTreeNode nodeFaculty = new DefaultMutableTreeNode(faculty);
				List<Department> deps = this.departmentByAcademicYear.get(faculty);
				
				if(deps != null) {
					for (Department dep : deps) {
						
						DefaultMutableTreeNode nodeDepartment = new DefaultMutableTreeNode(dep);
						dep.setFaculty(faculty);
						List<StudyClass> studys = this.studyClassByAcademicYear.get(dep);
						
						if(studys != null) {
							for (StudyClass st : studys) {
								nodeDepartment.add(new DefaultMutableTreeNode(st));
							}
						}
						
						nodeFaculty.add(nodeDepartment);
					}
				}
				
				root.add(nodeFaculty);
			}
		}
		
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getPath();
		
		AcademicYear year = null;
		Faculty faculty = null;
		Department department = null;
		StudyClass study = null;
		
		for (Object o : path.getPath()) {//recuperation de reference des objet dont on a besoin
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			
			if (node.getUserObject().getClass() == StudyClass.class) {
				study = (StudyClass) node.getUserObject();
			} else if(node.getUserObject().getClass() == Department.class) {
				department = (Department) node.getUserObject();
			} else if (node.getUserObject().getClass() == Faculty.class) {
				faculty = (Faculty) node.getUserObject(); 
			} else if (node.getUserObject().getClass() == AcademicYear.class) {
				year = (AcademicYear) node.getUserObject();
			}
		}
		
		int count = path.getPath().length;
		
		switch (count) {
			case 1:
				this.emitSelectYear(year);;
				break;
			case 2:
				this.emitSelectFaculty(faculty, year);
				break;
			case 3:
				this.emitSelectDepartment(department, year);
				break;
			case 4:
				this.emitSelectPromotion(promotionDao.find(year, department, study));
				break;
				
			default:
				System.out.println("->Error: "+path);
				break;
		}
	}
	
	public void addListener (SidebarListener listener) {
		if(!this.listeners.contains(listener))
			this.listeners.add(listener);
	}	
	
	//emit events
	private void emitSelectYear (AcademicYear year) {
		for (SidebarListener l : listeners) {
			l.onSelectAcademicYear(year);
		}
	}
	
	private void emitSelectFaculty (Faculty faculty, AcademicYear year) {
		for (SidebarListener l : listeners) {
			l.onSelectFaulty(faculty, year);
		}
	}
	
	private void emitSelectDepartment (Department department, AcademicYear year) {
		for (SidebarListener l : listeners) {
			l.onSelectDepartment(department, year);
		}
	}
	
	private void emitSelectPromotion (Promotion promotion) {
		for (SidebarListener l : listeners) {
			l.onSelectPromotion(promotion);
		}
	}
	// -- emit events
}
