/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.StudyClass;
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
public abstract class AbstractInscriptionForm extends DefaultFormPanel {
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

	/**
	 * constructeur d'initialisation
	 * @param mainWindow
	 * @param inscriptionDao
	 * @param studentDao
	 */
	public AbstractInscriptionForm(MainWindow mainWindow, InscriptionDao inscriptionDao, StudentDao studentDao) {
		super(mainWindow);
		this.inscriptionDao = inscriptionDao;
		this.studentDao = studentDao;
		this.facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		this.departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		this.studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		this.promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		
		
		container.add(fields, BorderLayout.CENTER);
		container.add(panelPicture, BorderLayout.EAST);
		this.getBody().add(container, BorderLayout.CENTER);
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
		
	}
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		if(!firstResize)
			this.onResize(this.getWidth());
		
		firstResize = false;
	}
	
	/**
	 * Chargement des donnees depuis la BDD
	 */
	protected void loadData(AcademicYear currentYear) {
		List<Faculty> facs = this.facultyDao.findByAcademicYear(currentYear);

		for (Faculty fac : facs) {
			if(this.departmentDao.checkByFaculty(fac, currentYear)) {
				fac.setDepartments(this.departmentDao.findByFaculty(fac, currentYear));
				
				for (Department dp : fac.getDepartments()) {
					this.studyClassByDepartment.put(dp, this.studyClassDao.findByAcademicYear(currentYear, dp));
				}
			}
			this.modelComboFaculty.addElement(fac);
		}
		
		Faculty selectedFac = this.modelComboFaculty.getElementAt(this.comboFaculty.getSelectedIndex());
		for (Department dp : selectedFac.getDepartments()) {
			this.modelComboDepartment.addElement(dp);
		}
		
		List<StudyClass> sClass = this.studyClassByDepartment.get(this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()));
		for (StudyClass cl : sClass) {
			this.modelComboStudyClass.addElement(cl);
		}
		
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
	}
	
	protected void onResize(int width) {}
	
}
