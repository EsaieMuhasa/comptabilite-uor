/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.tools.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.tools.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.ImagePicker;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 * classe de base pour le formutaire d'inscription et de re-inscription
 */
public abstract class AbstractInscriptionForm extends DefaultFormPanel implements AcademicYearDaoListener {
	private static final long serialVersionUID = -3161263910061751128L;
	
	protected final GridLayout fieldsLayout = new GridLayout(6, 2, DEFAULT_H_GAP, DEFAULT_V_GAP);
	
	// models combo
	protected final DefaultComboBoxModel<Faculty> modelComboFaculty = new DefaultComboBoxModel<>();
	protected final DefaultComboBoxModel<Department> modelComboDepartment = new DefaultComboBoxModel<>();
	protected final DefaultComboBoxModel<StudyClass> modelComboStudyClass = new DefaultComboBoxModel<>();
	protected final Map<Department, List<StudyClass>> studyClassByDepartment = new HashMap<>();
	//-- models combo
	
	//fields
	protected final ComboBox<Faculty> comboFaculty = new ComboBox<>("Faculté", modelComboFaculty);
	protected final ComboBox<Department> comboDepartment = new ComboBox<>("Département", modelComboDepartment);
	protected final ComboBox<StudyClass> comboStudyClass = new ComboBox<>("Class d'étude", modelComboStudyClass);
	
	protected final FormGroup<Faculty> groupFaculty = FormGroup.createComboBox(comboFaculty);
	protected final FormGroup<Department> groupDepartment = FormGroup.createComboBox(comboDepartment);
	protected final FormGroup<StudyClass> groupStudyClass = FormGroup.createComboBox(comboStudyClass);
	protected final FormGroup<String> matricul = FormGroup.createTextField("Matricule");
	protected final FormGroup<String> adresse = FormGroup.createTextField("Adresse de residence");
	protected final ImagePicker imagePicker = new ImagePicker("photo paceport");
	//fields
	
	protected final InscriptionDao inscriptionDao;
	protected final StudentDao studentDao;
	protected final FacultyDao facultyDao;
	protected final DepartmentDao departmentDao;
	protected final StudyClassDao studyClassDao;
	protected final PromotionDao promotionDao;
	
	protected final Panel container = new Panel(new BorderLayout());
	protected final Panel responsiveFileds = new Panel(fieldsLayout);
	protected final Panel fields = new Panel(new BorderLayout());
	protected final Panel panelPicture = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
	
	private boolean firstResize = true;
	
	protected AcademicYear currentYear;

	/**
	 * constructeur d'initialisation
	 * @param mainWindow
	 */
	public AbstractInscriptionForm(MainWindow mainWindow) {
		super(mainWindow);
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		studentDao = mainWindow.factory.findDao(StudentDao.class);
		facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		mainWindow.factory.findDao(AcademicYearDao.class).addYearListener(this);
		
		container.add(fields, BorderLayout.CENTER);
		container.add(panelPicture, BorderLayout.EAST);
		getBody().add(container, BorderLayout.CENTER);
	}
	
	/**
	 * Initialisationd l'interface graphique
	 */
	protected void init() {		
		responsiveFileds.add(groupDepartment);
		responsiveFileds.add(groupStudyClass);
		
		fields.add(groupFaculty, BorderLayout.NORTH);
		fields.add(responsiveFileds, BorderLayout.CENTER);
		
		imagePicker.setMaintFrame(mainWindow);
		panelPicture.setBorder(new EmptyBorder(0, 0, 0, 5));
		final Box box = Box.createVerticalBox();
		box.add(Box.createVerticalStrut(5));
		box.add(imagePicker);
		box.add(Box.createVerticalGlue());
		panelPicture.add(box, BorderLayout.CENTER);
		
		this.comboFaculty.addItemListener(event -> {
			this.modelComboDepartment.removeAllElements();
			Faculty fac = this.modelComboFaculty.getElementAt(this.comboFaculty.getSelectedIndex());
			for (Department dp : fac.getDepartments()) {
				this.modelComboDepartment.addElement(dp);
			}
		});
		
		this.comboDepartment.addItemListener(event -> {
			this.modelComboStudyClass.removeAllElements();
			List<StudyClass> studys = this.studyClassByDepartment.get(this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()));
			if(studys == null) 
				return;
			for (StudyClass cl : studys) {
				this.modelComboStudyClass.addElement(cl);
			}
		});
		
		promotionDao.addListener(new DAOAdapter<Promotion>() {
			@Override
			public synchronized void onCreate(Promotion e, int requestId) {
				Faculty faculty = null;
				for (int i = 0, count = modelComboFaculty.getSize(); i<count; i++) {
					Faculty fac = modelComboFaculty.getElementAt(i);
					if(fac.getId() == e.getDepartment().getFaculty().getId()) {
						faculty = fac;
						break;
					}
				}
				
				Department department = null;
				if(faculty == null) {//nouvelle configuration
					faculty = facultyDao.findById(e.getDepartment().getFaculty().getId());
					faculty.setDepartments(new ArrayList<>());
					modelComboFaculty.addElement(faculty);
				} else {					
					for (int i = 0; i < faculty.getDepartments().size(); i++) {
						if(faculty.getDepartments().get(i).getId() == e.getDepartment().getId()) {
							department = faculty.getDepartments().get(i);
							break;
						}
					}
				}
				
				if (department == null) {//aucune promotion du departement n'etait encore associer a une classe d'etude pour l'annee currante
					faculty.getDepartments().add(e.getDepartment());
					department = e.getDepartment();
					
					//mis en jour du model de departements dans le cas où le departement d'y était pas.
					if(faculty.getId() == modelComboFaculty.getElementAt(comboFaculty.getSelectedIndex()).getId()) {
						modelComboDepartment.addElement(e.getDepartment());
					}
					
					studyClassByDepartment.put(department, new ArrayList<>());
				}
				
				Set<Department> departments = studyClassByDepartment.keySet();
				for (Department dep : departments) {
					if(dep.getId() == department.getId()) {
						studyClassByDepartment.get(dep).add(e.getStudyClass());
						break;
					}
				}
				
				//mis en jour du model des classes d'etudes dans le cas où le departement actuel est belle et biens celle selectionner
				if(department.getId() == modelComboDepartment.getElementAt(comboDepartment.getSelectedIndex()).getId()) {
					modelComboStudyClass.addElement(e.getStudyClass());
				}
			}
			
			@Override
			public synchronized void onCreate (Promotion[] e, int requestId) {
				for (Promotion promotion : e) {
					onCreate(promotion, requestId);
				}
			}
			
			@Override
			public synchronized void onDelete(Promotion e, int requestId) {
				loadData();
			}
			
			@Override
			public synchronized void onDelete(Promotion[] e, int requestId) {
				loadData();
			}
		});
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		if(!firstResize)
			this.onResize(this.getWidth());
		
		firstResize = false;
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
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
		btnSave.setEnabled(currentYear != null);
		loadData();
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		setCurrentYear(year);
	}

	/**
	 * Chargement des donnees depuis la BDD
	 */
	protected synchronized void loadData() {
		modelComboDepartment.removeAllElements();
		modelComboFaculty.removeAllElements();
		modelComboStudyClass.removeAllElements();
		studyClassByDepartment.clear();
			
		if(currentYear == null){
			setEnabled(false);
			return;
		}
		
		List<Faculty> facs = this.facultyDao.checkByAcademicYear(currentYear) ? this.facultyDao.findByAcademicYear(currentYear) : null;

		if(facs == null){
			setEnabled(false);
			return;
		}
		
		for (Faculty fac : facs) {
			if(this.departmentDao.checkByFaculty(fac, currentYear)) {
				fac.setDepartments(this.departmentDao.findByFaculty(fac, currentYear));
				
				for (Department dp : fac.getDepartments()) {
					this.studyClassByDepartment.put(dp, this.studyClassDao.findByAcademicYear(currentYear, dp));
				}
				this.modelComboFaculty.addElement(fac);
			}
		}
		
		Faculty selectedFac = this.modelComboFaculty.getElementAt(this.comboFaculty.getSelectedIndex());
		for (Department dp : selectedFac.getDepartments()) {
			this.modelComboDepartment.addElement(dp);
		}
		
		List<StudyClass> sClass = this.studyClassByDepartment.get(this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()));
		for (StudyClass cl : sClass) {
			this.modelComboStudyClass.addElement(cl);
		}

		setEnabled(true);
	}
	
	protected void onResize(int width) {}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		comboFaculty.setEnabled(enabled);
		comboDepartment.setEnabled(enabled);
		comboStudyClass.setEnabled(enabled);
		matricul.setEnabled(enabled);
		adresse.setEnabled(enabled);
		btnSave.setEnabled(enabled);
		imagePicker.setEnabled(enabled);
		
	}
	
}
