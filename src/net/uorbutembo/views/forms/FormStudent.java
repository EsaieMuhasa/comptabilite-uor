/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JOptionPane;

import net.uorbutembo.beans.Student;
import net.uorbutembo.beans.User.Kind;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DefaultFormPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class FormStudent extends DefaultFormPanel {
	private static final long serialVersionUID = 4495240619166674028L;
	
	protected final GridLayout fieldsLayout = new GridLayout(5, 2, DEFAULT_H_GAP, DEFAULT_V_GAP);
	protected final Panel responsiveFileds = new Panel(fieldsLayout);

	private final ComboBox<Kind> comboKind = new ComboBox<>("Sexe");
	// fields
	private final FormGroup<String> name = FormGroup.createTextField("Nom");
	private final FormGroup<String> postName = FormGroup.createTextField("Post-nom");
	private final FormGroup<String> lastName = FormGroup.createTextField("Prénom");
	private final FormGroup<String> telephone = FormGroup.createTextField("Téléphone");
	private final FormGroup<String> email = FormGroup.createTextField("E-mail");
	private final FormGroup<String> school = FormGroup.createTextField("Ecole d'origine");
	private final FormGroup<String> birthPlace = FormGroup.createTextField("Leux de naissance");
	private final FormGroup<String> birthDate = FormGroup.createTextField("Date de naissance");
	private final FormGroup<Kind> groupKind = FormGroup.createComboBox(comboKind);
	private final FormGroup<String> matricul = FormGroup.createTextField("Matricule");
	//-- fields
	
	private Student student;//identite de l'etudiant, encours de modification
	private StudentDao studentDao;
	
	/**
	 * @param mainWindow
	 */
	public FormStudent(MainWindow mainWindow) {
		super(mainWindow);
		
		studentDao = mainWindow.factory.findDao(StudentDao.class);
		setTitle(TITLE_2);
		
		responsiveFileds.add(name);
		responsiveFileds.add(postName);
		responsiveFileds.add(lastName);
		responsiveFileds.add(telephone);
		responsiveFileds.add(email);
		responsiveFileds.add(school);
		responsiveFileds.add(birthDate);
		responsiveFileds.add(birthPlace);
		responsiveFileds.add(groupKind);
		responsiveFileds.add(matricul);
		
		for(Kind k : Student.KINDS) {
			comboKind.addItem(k);
		}
		
		this.getBody().add(responsiveFileds, BorderLayout.CENTER);
	}

	/**
	 * @return the student
	 */
	public Student getStudent() {
		return student;
	}

	/**
	 * @param student the student to set
	 */
	public void setStudent(Student student) {
		this.student = student;
		name.getField().setValue(student.getName());
		postName.getField().setValue(student.getPostName());
		lastName.getField().setValue(student.getFirstName());
		telephone.getField().setValue(student.getTelephone());
		email.getField().setValue(student.getEmail());
		school.getField().setValue(student.getOriginalSchool());
		birthPlace.getField().setValue(student.getBirthPlace());
		birthDate.getField().setValue(DEFAULT_FROMATER.format(student.getBirthDate()));
		matricul.getField().setValue(student.getMatricul());
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
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
		student.setLastUpdate(now);
		student.setKind(comboKind.getItemAt(comboKind.getSelectedIndex()).getShortName());
		
		try {
			this.studentDao.update(student, this.student.getId());
			this.showMessageDialog("Information", "Enregistrement des modifications \ndu profil de l'etudant \""+student.toString()+"\"", JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
