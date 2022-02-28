package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class FormInscription extends DefaultFormPanel{
	
	private static final long serialVersionUID = -7867072774750271198L;
	
	private JLabel picture = new JLabel();
	private JButton pickPicture = new JButton("Photo paceport");
	
	private GridLayout fieldsLayoutLg = new GridLayout(6, 2, DEFAULT_H_GAP, DEFAULT_V_GAP), 
			fieldsLayoutSm = new GridLayout(12, 1, DEFAULT_H_GAP, DEFAULT_V_GAP) ;
	
	private Panel fields;
	private Panel responsiveFileds;
	private Panel panelPicture;
	
	// models combo
	private DefaultComboBoxModel<Faculty> modelComboFaculty = new DefaultComboBoxModel<>();
	private DefaultComboBoxModel<Department> modelComboDepartment = new DefaultComboBoxModel<>();
	private DefaultComboBoxModel<StudyClass> modelComboStudyClass= new DefaultComboBoxModel<>();
	private Map<Department, List<StudyClass>> studyClassByDepartment = new HashMap<>(); //permet de classifier les classe d'etude pour chaque departement
	//-- models combo
	
	// fields
	private ComboBox<Faculty> comboFaculty = new ComboBox<>("Faculté", modelComboFaculty);
	private ComboBox<Department> comboDepartment = new ComboBox<>("Département", modelComboDepartment);
	private ComboBox<StudyClass> comboStudyClass = new ComboBox<>("Class d'étude", modelComboStudyClass);
	
	private FormGroup<Faculty> groupFaculty = FormGroup.createComboBox(comboFaculty);
	private FormGroup<Department> groupDepartment = FormGroup.createComboBox(comboDepartment);
	private FormGroup<StudyClass> groupStudyClass = FormGroup.createComboBox(comboStudyClass);
	
	private FormGroup<String> name = FormGroup.createTextField("Nom");
	private FormGroup<String> postName = FormGroup.createTextField("Post-nom");
	private FormGroup<String> lastName = FormGroup.createTextField("Prénom");
	private FormGroup<String> telephone = FormGroup.createTextField("Téléphone");
	private FormGroup<String> email = FormGroup.createTextField("E-mail");
	private FormGroup<String> school = FormGroup.createTextField("Ecole d'origine");
	private FormGroup<String> birthPlace = FormGroup.createTextField("Leux de naissance");
	private FormGroup<String> birthDate = FormGroup.createTextField("Date de naissance");
	private FormGroup<String> matricul = FormGroup.createTextField("Matricule");
	//-- fields
	
	private AcademicYear currentYear;
	
	// dao
	private InscriptionDao inscriptionDao;
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	private StudyClassDao studyClassDao;
	private PromotionDao promotionDao;
	private StudentDao studentDao;
	//-- dao
	
	/**
	 * Constructeur par defaut
	 */
	public FormInscription(InscriptionDao inscriptionDao) {
		super();
		this.currentYear = inscriptionDao.getFactory().findDao(AcademicYearDao.class).findCurrent();
		this.inscriptionDao = inscriptionDao;
		this.facultyDao = inscriptionDao.getFactory().findDao(FacultyDao.class);
		this.departmentDao = inscriptionDao.getFactory().findDao(DepartmentDao.class);
		this.studyClassDao = inscriptionDao.getFactory().findDao(StudyClassDao.class);
		this.promotionDao = inscriptionDao.getFactory().findDao(PromotionDao.class);
		this.studentDao = inscriptionDao.getFactory().findDao(StudentDao.class);
		
		
		this.init();
		this.setTitle("Formulaire d'inscription");
		
		Panel container = new Panel(new BorderLayout());

		container.add(this.fields, BorderLayout.CENTER);
		container.add(this.panelPicture, BorderLayout.EAST);
		
		
		this.getBody().add(container, BorderLayout.CENTER);
		this.loadData();
	}
	
	/**
	 * Initialisation d'interface graphique
	 */
	protected void init() {
		responsiveFileds = new Panel(fieldsLayoutLg);
		fields = new Panel(new BorderLayout());
		panelPicture = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		
		responsiveFileds.add(groupDepartment);
		responsiveFileds.add(groupStudyClass);
		responsiveFileds.add(this.name);
		responsiveFileds.add(this.postName);
		responsiveFileds.add(this.lastName);
		responsiveFileds.add(this.telephone);
		responsiveFileds.add(this.email);
		responsiveFileds.add(this.school);
		responsiveFileds.add(this.birthDate);
		responsiveFileds.add(this.birthPlace);
		responsiveFileds.add(this.matricul);
		
		fields.add(groupFaculty, BorderLayout.NORTH);
		fields.add(responsiveFileds, BorderLayout.CENTER);
		
		this.panelPicture.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
		this.panelPicture.add(this.pickPicture, BorderLayout.NORTH);
		this.panelPicture.add(this.picture, BorderLayout.CENTER);
	}
	
	/**
	 * Chargement des donnees depuis la BDD
	 */
	private void loadData() {
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
	
	@Override
	public void doLayout() {
		super.doLayout();
		
		if(this.getWidth() <= 650) {
			if(this.responsiveFileds.getLayout() != this.fieldsLayoutSm) {
				this.responsiveFileds.setLayout(fieldsLayoutSm);
			}
		} else {
			if(this.responsiveFileds.getLayout() != this.fieldsLayoutLg) {
				this.responsiveFileds.setLayout(this.fieldsLayoutLg);
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
		Promotion promotion = this.promotionDao.find(currentYear, 
				this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()), 
				this.modelComboStudyClass.getElementAt(this.comboStudyClass.getSelectedIndex()));
		
		String name = this.name.getField().getValue(),
				postName = this.postName.getField().getValue(),
				firstName = this.lastName.getField().getValue(),
				telephone = this.telephone.getField().getValue(),
				email = this.email.getField().getValue(),
				school = this.school.getField().getValue(),
				bPlace = this.birthPlace.getField().getValue(),
				bDate = this.birthDate.getField().getValue(),
				matricul = this.matricul.getField().getValue();
		Date birthDate = null;
		try {
			birthDate = FormUtil.DEFAULT_FROMATER.parse(bDate);
		} catch (ParseException e) {
			this.showMessageDialog("Erreur", "Entrez la date au format dd/MM/yyyy ou jj/MM/aaaa", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Date now = new Date();
		
		Student student = new Student();
		student.setName(name);
		student.setFirstName(firstName);
		student.setPostName(postName);
		student.setBirthDate(birthDate);
		student.setBirthPlace(bPlace);
		student.setTelephone(telephone);
		student.setEmail(email);
		student.setMatricul(matricul);
		student.setOriginalSchool(school);
		student.setRecordDate(now);
		
		Inscription inscription = new Inscription();
		inscription.setStudent(student);
		inscription.setPromotion(promotion);
		inscription.setRecordDate(now);
		
		try {
			this.inscriptionDao.create(inscription);
			this.showMessageDialog("Information", "Success d'enregistrement de l'inscription de\n l'etudiant "+student.toString()+", \ndans la promtion "+promotion.toString(), JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
