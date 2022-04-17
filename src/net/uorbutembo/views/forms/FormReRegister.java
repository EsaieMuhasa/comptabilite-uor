/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
import net.uorbutembo.swing.Button;
import net.uorbutembo.views.MainWindow;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class FormReRegister extends AbstractInscriptionForm {
	private static final long serialVersionUID = -3235229997664179423L;
	
	private Inscription inscription;//dans le cas d'edition d'une inscription
	
	private Button btnSaveUpdate;//bouton d'enregistrement des modications
	private Button btnCancel;//lors de la modification il est possible d'ennuller les modifications faites avant confirmation
	
	/**
	 * constructeur d'initialisation
	 * @param mainWindow
	 * @param inscriptionDao
	 * @param studentDao
	 * @param registerForm, true: formulaire de re-inscription. false: formulaire de modication d'une inscription
	 */
	public FormReRegister(MainWindow mainWindow, InscriptionDao inscriptionDao, StudentDao studentDao, boolean registerForm) {
		super(mainWindow, inscriptionDao, studentDao);
		this.setTitle("Formulaire de re-inscription");
		init();
		this.fieldsLayout.setRows(4);
		
		if(!registerForm) {
			btnSaveUpdate = new Button(btnSave.getIcon(), "Enregistrer");
			btnCancel = new Button(new ImageIcon(R.getIcon("close")), "Annuler les modifications");
			
			btnSave.removeActionListener(this);
			btnSave.setVisible(false);
			btnSaveUpdate.addActionListener(this);
			
			this.getFooter().add(btnSaveUpdate);
			this.getFooter().add(btnCancel);
			
			
			btnCancel.addActionListener(event -> {
				matricul.getField().setValue("");
				adresse.getField().setValue("");
				imagePicker.show(null);
				inscription = null;
				setVisible(false);
			});
		}
		
		setEnabled(false);
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
		adresse.getField().setValue(inscription.getAdress());
		
		if(inscription.getStudent().getPicture() != null && !inscription.getStudent().getPicture().isEmpty())
			imagePicker.show(R.getConfig().get("workspace")+inscription.getStudent().getPicture());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Inscription in = new Inscription();
		final Date now = new Date();
		
		Promotion promotion = this.promotionDao.find(currentYear, 
				this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()), 
				this.modelComboStudyClass.getElementAt(this.comboStudyClass.getSelectedIndex()));
		
		Student student = studentDao.findByMatricul(matricul.getField().getValue());
		
		in.setPromotion(promotion);
		in.setStudent(student);
		in.setAdress(adresse.getField().getValue());
		
		try {
			if(event.getSource() == btnSave) {
				in.setRecordDate(now);
				this.inscriptionDao.create(in);
			} else {
				in.setLastUpdate(now);
				in.setId(inscription.getId());
				this.inscriptionDao.update(in, inscription.getId());
			}
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
		
		matricul.getField().setValue("");
		adresse.getField().setValue("");
		imagePicker.show(null);
		inscription = null;
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if(btnCancel != null)
			btnCancel.setEnabled(enabled);
		if(btnSaveUpdate != null)
			btnSaveUpdate.setEnabled(enabled);
	}

}
