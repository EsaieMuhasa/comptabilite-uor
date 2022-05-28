/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class FormPromotion extends DefaultFormPanel {
	private static final long serialVersionUID = 8664772794012293257L;
	
	private DefaultListModel<Department> departmentsModel = new DefaultListModel<>();
	private DefaultListModel<StudyClass> studyClassModel = new DefaultListModel<>();
	
	private DefaultListModel<Department> selectedDepartmentsModel = new DefaultListModel<>();
	private DefaultListModel<StudyClass> selectedStudyClassModel = new DefaultListModel<>();
	
	private JList<Department> departments = new JList<>(departmentsModel);
	private JList<StudyClass> studyClass = new JList<>(studyClassModel);
	private JList<Department> selectedDepartments = new JList<>(selectedDepartmentsModel);
	private JList<StudyClass> selectedStudyClass = new JList<>(selectedStudyClassModel);
	
	private Button btnAddDepartments = new Button(new ImageIcon(R.getIcon("desc")));
	private Button btnRemoveDepartments = new Button(new ImageIcon(R.getIcon("asc")));
	
	private Button btnAddStudyClass = new Button(new ImageIcon(R.getIcon("desc")));
	private Button btnRemoveStudyClass = new Button(new ImageIcon(R.getIcon("asc")));
	
	private JSplitPane splitGeneral;
	private final PromotionDao promotionDao;
	private final DepartmentDao departmentDao;
	private final StudyClassDao studyClassDao;
	private final AcademicYearDao academicYearDao;
	private AcademicYear currentYear;

	private final DAOAdapter<Department> departmentAdapter = new DAOAdapter<Department>() {
		@Override
		public void onCreate(Department e, int requestId) {
			departmentsModel.addElement(e);
		}
		
		@Override
		public void onUpdate(Department e, int requestId) {
			boolean updated = false;
			for (int i = 0; i < departmentsModel.getSize(); i++) {
				if(e.getId() == departmentsModel.get(i).getId())  {
					departmentsModel.setElementAt(e, i);
					updated = true;
					break;
				}
			}
			
			if(!updated) {
				for (int i = 0, count = selectedDepartmentsModel.getSize(); i < count; i++) {
					if(e.getId() == selectedDepartmentsModel.get(i).getId()){
						selectedDepartmentsModel.remove(i);
						break;
					}
				}	
			}
		}
		
		@Override
		public void onDelete(Department e, int requestId) {
			boolean deleted = false;
			for (int i = 0, count = departmentsModel.getSize(); i < count; i++) {
				if(e.getId() == departmentsModel.get(i).getId()){
					departmentsModel.remove(i);
					deleted = true;
					break;
				}
			}
			
			if(!deleted) {
				for (int i = 0, count = selectedDepartmentsModel.getSize(); i < count; i++) {
					if(e.getId() == selectedDepartmentsModel.get(i).getId()){
						selectedDepartmentsModel.remove(i);
						break;
					}
				}	
			}
		}
		
		@Override
		public void onDelete(Department[] e, int requestId) {
			for (Department department : e) {
				onDelete(department, requestId);
			}
		}
	};
	
	private final DAOAdapter<StudyClass> classAdapter = new DAOAdapter<StudyClass>() {
		@Override
		public void onCreate(StudyClass e, int requestId) {
			studyClassModel.addElement(e);
		}
		
		@Override
		public void onUpdate(StudyClass e, int requestId) {
			boolean updated = false;
			for (int i = 0, count = studyClassModel.getSize(); i < count; i++){
				if (studyClassModel.get(i).getId() == e.getId() ) {
					studyClassModel.set(i, e);
					updated = true;
					break;
				}
			}
			
			if(!updated) {
				for (int i = 0, count = selectedStudyClassModel.getSize(); i < count; i++){
					if (selectedStudyClassModel.get(i).getId() == e.getId() ) {
						selectedStudyClassModel.set(i, e);
						break;
					}
				}
			}
		}
		
		@Override
		public void onDelete(StudyClass e, int requestId) {
			boolean deleted = false;
			for (int i = 0, count = studyClassModel.getSize(); i < count; i++) {
				if (studyClassModel.get(i).getId() == e.getId() ) {
					studyClassModel.remove(i);
					deleted = true;
					break;
				}
			}
			
			if(!deleted) {
				for (int i = 0, count = selectedStudyClassModel.getSize(); i < count; i++){
					if (selectedStudyClassModel.get(i).getId() == e.getId() ) {
						selectedStudyClassModel.remove(i);
						break;
					}
				}
			}
		}
		
		@Override
		public void onDelete(StudyClass[] e, int requestId) {
			for (StudyClass studyClass : e) {
				onDelete(studyClass, requestId);
			}
		}
	};

	public FormPromotion(MainWindow mainWindow) {
		super(mainWindow);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		init();
		
		departmentDao.addListener(departmentAdapter);
		studyClassDao.addListener(classAdapter);
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		btnSave.setEnabled (currentYear != null && academicYearDao.isCurrent(currentYear));
		
		if(!departmentsModel.isEmpty() || currentYear == null)
			return;
		
		this.setTitle("Configuration des promotions pour l'année "+ this.currentYear.getLabel());
		departmentsModel.removeAllElements();
		studyClassModel.removeAllElements();
		
		List<Department> deps = departmentDao.countAll() != 0 ? departmentDao.findAll() : new ArrayList<>();
		List<StudyClass> studys = studyClassDao.countAll() != 0 ? studyClassDao.findAll() : new ArrayList<>();
		for (Department d : deps)
			this.departmentsModel.addElement(d);
		
		for (StudyClass s : studys) 
			this.studyClassModel.addElement(s);
	}

	/**
	 */
	private void init() {
		Panel center = new  Panel(new BorderLayout());
		
		Panel left = new Panel(new GridLayout(1, 1, DEFAULT_V_GAP, DEFAULT_H_GAP));
		Panel right = new Panel(new GridLayout(1, 1, DEFAULT_V_GAP, DEFAULT_H_GAP));
		
		
		//department
		Panel departments = new Panel(new BorderLayout());
		Box boxDepartment = Box.createVerticalBox();
		JLabel labelDepartment = new JLabel("Départements");
		
		labelDepartment.setHorizontalAlignment(JLabel.CENTER);
		
		this.btnRemoveDepartments.setPreferredSize(FormUtil.createDimensionSmCare());
		this.btnAddDepartments.setPreferredSize(FormUtil.createDimensionSmCare());
		boxDepartment.add(Box.createVerticalGlue());
		boxDepartment.add(this.btnRemoveDepartments);
		boxDepartment.add(this.btnAddDepartments);
		boxDepartment.add(Box.createVerticalGlue());
		
		this.btnAddDepartments.addActionListener(event -> {
			int [] index = this.departments.getSelectedIndices();
			for (int i : index) {
				this.selectedDepartmentsModel.addElement(this.departmentsModel.get(i));
			}
			
			while (this.departments.getSelectedIndex() != -1) {
				this.departmentsModel.remove(this.departments.getSelectedIndex());
			}
			
			this.btnAddDepartments.setEnabled(!this.departmentsModel.isEmpty());
			this.btnRemoveDepartments.setEnabled(!this.selectedDepartmentsModel.isEmpty());
		});
		
		this.btnRemoveDepartments.setEnabled(false);
		this.btnRemoveDepartments.addActionListener(event -> {
			int [] index = this.selectedDepartments.getSelectedIndices();
			for (int i : index) {
				this.departmentsModel.addElement(this.selectedDepartmentsModel.get(i));
			}
			
			while (this.selectedDepartments.getSelectedIndex() != -1) {
				this.selectedDepartmentsModel.remove(this.selectedDepartments.getSelectedIndex());
			}
			
			this.btnRemoveDepartments.setEnabled(!this.selectedDepartmentsModel.isEmpty());
			this.btnAddDepartments.setEnabled(!this.departmentsModel.isEmpty());
		});

		
		Panel departmentLists = new Panel(new GridLayout(2, 1, DEFAULT_H_GAP, DEFAULT_V_GAP));
		JScrollPane scrollDepartments = new JScrollPane(this.departments);
		JScrollPane scrollSelectedDepartments = new JScrollPane(this.selectedDepartments);
		departmentLists.add(scrollDepartments);
		departmentLists.add(scrollSelectedDepartments);
		
		departments.add(labelDepartment, BorderLayout.NORTH);
		departments.add(departmentLists, BorderLayout.CENTER);
		//-- department
		
		//class
		Panel studyClass = new Panel(new BorderLayout());
		Box boxStudy = Box.createVerticalBox();
		JLabel labelStudyClass = new JLabel("Classe d'études");
		
		labelStudyClass.setHorizontalAlignment(JLabel.CENTER);
		
		this.btnRemoveStudyClass.setPreferredSize(FormUtil.createDimensionSmCare());
		this.btnAddStudyClass.setPreferredSize(FormUtil.createDimensionSmCare());
		boxStudy.add(Box.createVerticalGlue());
		boxStudy.add(this.btnRemoveStudyClass);
		boxStudy.add(this.btnAddStudyClass);
		boxStudy.add(Box.createVerticalGlue());
		
		this.btnAddStudyClass.addActionListener(event -> {
			int [] index = this.studyClass.getSelectedIndices();
			for (int i : index) {
				this.selectedStudyClassModel.addElement(this.studyClassModel.get(i));
			}
			
			while (this.studyClass.getSelectedIndex() != -1) {
				this.studyClassModel.remove(this.studyClass.getSelectedIndex());
			}
			
			this.btnAddStudyClass.setEnabled(!this.studyClassModel.isEmpty());
			this.btnRemoveStudyClass.setEnabled(!this.selectedStudyClassModel.isEmpty());
		});
		
		this.btnRemoveStudyClass.setEnabled(false);
		this.btnRemoveStudyClass.addActionListener(event -> {
			int [] index = this.selectedStudyClass.getSelectedIndices();
			for (int i : index) {
				this.studyClassModel.addElement(this.selectedStudyClassModel.get(i));
			}
			
			while (this.selectedStudyClass.getSelectedIndex() != -1) {
				this.selectedStudyClassModel.remove(this.selectedStudyClass.getSelectedIndex());
			}
			
			this.btnRemoveStudyClass.setEnabled(!this.selectedStudyClassModel.isEmpty());
			this.btnAddStudyClass.setEnabled(!this.studyClassModel.isEmpty());
		});

		
		Panel studyLists = new Panel(new GridLayout(2, 1, DEFAULT_H_GAP, DEFAULT_V_GAP));
		JScrollPane scrollStudyClass = new JScrollPane(this.studyClass);
		JScrollPane scrollSelectedStudyClass = new JScrollPane(this.selectedStudyClass);
		studyLists.add(scrollStudyClass);
		studyLists.add(scrollSelectedStudyClass);
		
		
		studyClass.add(labelStudyClass, BorderLayout.NORTH);
		studyClass.add(studyLists, BorderLayout.CENTER);
		//--class
		
		left.add(departments, BorderLayout.CENTER);
		right.add(studyClass, BorderLayout.CENTER);
		this.splitGeneral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		
		center.add(boxDepartment, BorderLayout.WEST);//bouton de commandes pour deparmetement
		center.add(boxStudy, BorderLayout.EAST);//bouton de commande pour classe d'etude
		center.add(this.splitGeneral, BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(5, 5, 10, 5));
		this.getBody().add(center, BorderLayout.CENTER);
	}
	
	
	@Override
	public void doLayout() {
		super.doLayout();
		if(this.splitGeneral != null) {			
			splitGeneral.setDividerLocation(this.getWidth()/2);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
		Promotion[] t = new Promotion [this.selectedDepartmentsModel.getSize() * this.selectedStudyClassModel.getSize()];
		Date now = new Date();
		int index = 0;
		for (int i = 0, iMax = this.selectedDepartmentsModel.getSize(); i < iMax ; i++) {
			for (int j = 0, jMax = this.selectedStudyClassModel.getSize(); j < jMax; j++) {
				Promotion p = new Promotion();
				p.setAcademicYear(this.currentYear);
				p.setDepartment(this.selectedDepartmentsModel.get(i));
				p.setStudyClass(this.selectedStudyClassModel.get(j));;
				p.setRecordDate(now);
				
				t[index] = p;
				index++;
			}
		}
		
		try {
			this.promotionDao.create(t);
			this.showMessageDialog("Information", "Success d'enregistrement d"+(t.length==1? "e la":"es")+" promotion"+(t.length!=1? "s":""), JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
}
