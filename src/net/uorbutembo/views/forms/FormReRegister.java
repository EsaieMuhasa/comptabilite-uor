/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.views.MainWindow;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class FormReRegister extends AbstractInscriptionForm {
	private static final long serialVersionUID = -3235229997664179423L;
	
	private Inscription inscription;//dans le cas d'edition d'une inscription
	
	public FormReRegister(MainWindow mainWindow, InscriptionDao inscriptionDao, StudentDao studentDao) {
		super(mainWindow, inscriptionDao, studentDao);
		this.setTitle("Formulaire de re-inscription");
		init();
		this.fieldsLayout.setRows(4);
	}

	@Override
	protected void init() {
		super.init();
		responsiveFileds.add(this.matricul);
		responsiveFileds.add(adresse);
	}

	/**
	 * @return the inscription
	 */
	public Inscription getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(Inscription inscription) {
		this.inscription = inscription;
		
		for (int i = 0, count = modelComboFaculty.getSize(); i < count; i++) {
			Faculty fac = modelComboFaculty.getElementAt(i);
			if(inscription.getPromotion().getDepartment().getFaculty().getId() == fac.getId()) {
				comboFaculty.setSelectedIndex(i);
				break;
			}
		}
		
		for (int j = 0, count = modelComboDepartment.getSize(); j < count; j++) {
			Department dep = modelComboDepartment.getElementAt(j);
			if(dep.getId() == inscription.getPromotion().getDepartment().getId()) {
				comboDepartment.setSelectedIndex(j);
				break;
			}
		}
		
		for (int i = 0, count = modelComboStudyClass.getSize(); i < count; i++) {
			StudyClass sc = modelComboStudyClass.getElementAt(i);
			if(sc.getId() == inscription.getPromotion().getStudyClass().getId()) {
				comboStudyClass.setSelectedIndex(i);
				break;
			}
		}
		
		matricul.getField().setValue(inscription.getStudent().getMatricul());
		
		if(inscription.getStudent().getPicture() != null && !inscription.getStudent().getPicture().isEmpty())
			imagePicker.show(R.getConfig().get("workspace")+inscription.getStudent().getPicture());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Inscription in = new Inscription();
		
		Promotion promotion = this.promotionDao.find(currentYear, 
				this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()), 
				this.modelComboStudyClass.getElementAt(this.comboStudyClass.getSelectedIndex()));
		
		Student student = studentDao.findByMatricul(matricul.getField().getValue());
		
		in.setPromotion(promotion);
		in.setStudent(student);
		
		try {
			if(inscription == null) 
				this.inscriptionDao.create(in);
			else 
				this.inscriptionDao.update(in, inscription.getId());
			this.showMessageDialog("Information", "Success d'enregistrement de l'inscription de\n l'étudiant "+student.toString()+", \ndans la promtion "+promotion.toString(), JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			this.showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//ecriture de la photo sur le disque dur
		String fileName = in.getId()+"-"+System.currentTimeMillis()+"."+imagePicker.getImageType();
		try  {
			File file = new File(R.getConfig().get("workspace")+fileName);
			BufferedImage image = imagePicker.getImage();
			ImageIO.write(image, imagePicker.getImageType(), file);
			inscriptionDao.updatePicture(in.getId(), fileName);
			studentDao.updatePicture(fileName, inscription.getStudent().getId());
		} catch (Exception e) {
			this.showMessageDialog("Erreur d'ecriture du fichier", e.getMessage(), JOptionPane.ERROR_MESSAGE);
		}
		
		inscription = null;
	}
	
	@Override
	protected void onResize(int width) {
		super.onResize(width);
//		if(fieldsLayout.getRows()!=3) {
//			fieldsLayout.setRows(3);
//			fieldsLayout.setColumns(1);
//		}
	}

}
