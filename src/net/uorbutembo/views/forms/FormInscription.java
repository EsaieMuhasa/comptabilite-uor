package net.uorbutembo.views.forms;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.ParseException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.beans.User.Kind;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.ComboBox;
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


	private ComboBox<Kind> comboKind = new ComboBox<>("Sexe");
	// fields
	private FormGroup<String> name = FormGroup.createTextField("Nom");
	private FormGroup<String> postName = FormGroup.createTextField("Post-nom");
	private FormGroup<String> lastName = FormGroup.createTextField("Prénom");
	private FormGroup<String> telephone = FormGroup.createTextField("Téléphone");
	private FormGroup<String> email = FormGroup.createTextField("E-mail");
	private FormGroup<String> school = FormGroup.createTextField("Ecole d'origine");
	private FormGroup<String> birthPlace = FormGroup.createTextField("Leux de naissance");
	private FormGroup<String> birthDate = FormGroup.createTextField("Date de naissance (jj-mm-aaaa)");
	private FormGroup<Kind> groupKind = FormGroup.createComboBox(comboKind);
	//-- fields

	public FormInscription(MainWindow mainWindow, InscriptionDao inscriptionDao, StudentDao studentDao) {
		super(mainWindow, inscriptionDao, studentDao);	
		
		this.setTitle("Formulaire d'inscription");

		init();
		setEnabled(false);
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
		responsiveFileds.add(adresse);
		responsiveFileds.add(matricul);
		responsiveFileds.add(groupKind);
		
		for(Kind k : Student.KINDS) {
			comboKind.addItem(k);
		}
	}


	@Override
	public void doLayout() {
		super.doLayout();
	}
	
	@Override
	protected void onResize(int width) {
		super.onResize(width);
		
		if(width <= 650 && width > 100) {
			if(fieldsLayout.getRows()<=7) {
				fieldsLayout.setRows(13);
				fieldsLayout.setColumns(1);
			}
		} else {
			if(width >= 700 && fieldsLayout.getRows() != 7) {
				fieldsLayout.setRows(7);
				fieldsLayout.setColumns(2);
			}
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		name.setEnabled(enabled);
		postName.setEnabled(enabled);
		lastName.setEnabled(enabled);
		telephone.setEnabled(enabled);
		email.setEnabled(enabled);
		school.setEnabled(enabled);
		birthPlace.setEnabled(enabled);
		birthDate.setEnabled(enabled);
		groupKind.setEnabled(enabled);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
		Promotion promotion = this.promotionDao.find(currentYear, 
				this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()), 
				this.modelComboStudyClass.getElementAt(this.comboStudyClass.getSelectedIndex()));
		String adress = this.adresse.getField().getValue();
		
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
		student.setKind(comboKind.getItemAt(comboKind.getSelectedIndex()).getShortName());
		
		Inscription inscription = new Inscription();
		inscription.setStudent(student);
		inscription.setPromotion(promotion);
		inscription.setRecordDate(now);
		inscription.setAdress(adress);
		
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
