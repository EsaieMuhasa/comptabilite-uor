package net.uorbutembo.views.forms;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.FormGroup;
import net.uorbutembo.views.MainWindow;
import resources.net.uorbutembo.R;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class FormInscription extends AbstractInscriptionForm{
	private static final long serialVersionUID = -7867072774750271198L;

	
	// fields
	private FormGroup<String> name;
	private FormGroup<String> postName;
	private FormGroup<String> lastName;
	private FormGroup<String> telephone;
	private FormGroup<String> email;
	private FormGroup<String> school;
	private FormGroup<String> birthPlace;
	private FormGroup<String> birthDate;
	//-- fields
	
	{
		name = FormGroup.createTextField("Nom");
		postName = FormGroup.createTextField("Post-nom");
		lastName = FormGroup.createTextField("Prénom");
		telephone = FormGroup.createTextField("Téléphone");
		email = FormGroup.createTextField("E-mail");
		school = FormGroup.createTextField("Ecole d'origine");
		birthPlace = FormGroup.createTextField("Leux de naissance");
		birthDate = FormGroup.createTextField("Date de naissance");
	}
	
	private AcademicYear currentYear;
	

	public FormInscription(MainWindow mainWindow, InscriptionDao inscriptionDao, StudentDao studentDao) {
		super(mainWindow, inscriptionDao, studentDao);
		this.currentYear = inscriptionDao.getFactory().findDao(AcademicYearDao.class).findCurrent();		
		
		this.setTitle("Formulaire d'inscription");

		init();
		this.loadData(currentYear);
	}
	
	@Override
	protected void init() {
		super.init();
		responsiveFileds.add(name);
		responsiveFileds.add(postName);
		responsiveFileds.add(lastName);
		responsiveFileds.add(telephone);
		responsiveFileds.add(email);
		responsiveFileds.add(school);
		responsiveFileds.add(birthDate);
		responsiveFileds.add(birthPlace);
		responsiveFileds.add(matricul);
	}


	@Override
	public void doLayout() {
		super.doLayout();
		
	}
	
	@Override
	protected void onResize(int width) {
		super.onResize(width);
		
		if(width <= 650 && width > 100) {
			if(fieldsLayout.getRows()<=6) {
				fieldsLayout.setRows(12);
				fieldsLayout.setColumns(1);
			}
		} else {
			if(width >= 700 && fieldsLayout.getRows() != 6) {
				fieldsLayout.setRows(6);
				fieldsLayout.setColumns(2);
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
			return;
		}
		
		//ecriture de la photo sur le disque dur
		String fileName = inscription.getId()+"-"+System.currentTimeMillis()+"."+imagePicker.getImageType();
		try  {
			File file = new File(R.getConfig().get("workspace")+fileName);
			BufferedImage image = imagePicker.getImage();
			ImageIO.write(image, imagePicker.getImageType(), file);
			inscriptionDao.updatePicture(inscription.getId(), fileName);
			studentDao.updatePicture(fileName, inscription.getStudent().getId());
		} catch (Exception e) {
			this.showMessageDialog("Erreur d'ecriture du fichier", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
	}

}
