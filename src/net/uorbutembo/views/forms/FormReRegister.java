/**
 * 
 */
package net.uorbutembo.views.forms;

import java.awt.Cursor;
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
import net.uorbutembo.swing.Button;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;

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
	 * @param registerForm, true: formulaire de re-inscription. false: formulaire de modication d'une inscription
	 */
	public FormReRegister(MainWindow mainWindow, boolean registerForm) {
		super(mainWindow);
		setTitle("Formulaire de réinscription");
		init();
		this.fieldsLayout.setRows(4);
		
		if(!registerForm) {
			btnSaveUpdate = new Button(btnSave.getIcon(), "Enregistrer");
			btnCancel = new Button(new ImageIcon(R.getIcon("close")), "Annuler les modifications");
			
			btnSave.removeActionListener(this);
			btnSave.setVisible(false);
			btnSaveUpdate.addActionListener(this);
			
			getFooter().add(btnSaveUpdate);
			getFooter().add(btnCancel);
			
			
			btnCancel.addActionListener(event -> {
				razFields();
			});
		}
		
		setEnabled(false);
	}

	/**
	 * @return the btnCancel
	 */
	public Button getBtnCancel() {
		return btnCancel;
	}

	@Override
	protected void init() {
		super.init();
		responsiveFileds.add(matricul);
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
		
		if (inscription == null) {
			razFields();
			return;
		}
		
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
		
		if(inscription.getPicture() != null && !inscription.getPicture().isEmpty())
			imagePicker.show(R.getConfig().get("workspace")+inscription.getPicture());
	}
	
	@Override
	protected void doRaz() {
		super.doRaz();
		inscription = null;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Inscription in = new Inscription();
		final Date now = new Date();
		
		Promotion promotion = this.promotionDao.find(currentYear, 
				this.modelComboDepartment.getElementAt(this.comboDepartment.getSelectedIndex()), 
				this.modelComboStudyClass.getElementAt(this.comboStudyClass.getSelectedIndex()));
		
		String matricul = this.matricul.getField().getValue();
		
		if(!studentDao.checkByMatricul(matricul)) {
			showMessageDialog("Matricule invalide", "Ce numero matricule est inconnue dans la base de donnée", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Student student = studentDao.findByMatricul(matricul);
		
		if (event.getSource() == btnSave && inscriptionDao.checkByStudent(student, currentYear)) {
			Inscription i = inscriptionDao.findByStudent(student, currentYear);
			showMessageDialog("Information", "L'étudiant "+student.getFullName()+" \nest déjà inscrit pour l'année académique "+currentYear
					+" \nPromotion: "+i.getPromotion(), JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		
		in.setPromotion(promotion);
		in.setStudent(student);
		in.setAdress(adresse.getField().getValue());
		
		//picture
		String fileName = in.getId()+"-"+System.currentTimeMillis()+"."+imagePicker.getImageType();
		File file = new File(R.getConfig().get("workspace")+fileName);
		BufferedImage image = imagePicker.getImage();
		//==
		
		try {
			in.setPicture(imagePicker.isCropableImage()? fileName : (inscription != null? inscription.getPicture() : null));
			if(event.getSource() == btnSave) {
				in.setRecordDate(now);
				inscriptionDao.create(in);
			} else {
				in.setLastUpdate(now);
				in.setId(inscription.getId());
				inscriptionDao.update(in, inscription.getId());
			}
			showMessageDialog("Information", "Succèss d'enregistrement de l'inscription de\n l'étudiant "+student.toString()+", \ndans la promotion "+promotion.toString(), JOptionPane.INFORMATION_MESSAGE);
		} catch (DAOException e) {
			showMessageDialog("Erreur", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//ecriture de la photo sur le disque dur
		if(image != null && imagePicker.isCropableImage()) {			
			try  {
				ImageIO.write(image, imagePicker.getImageType(), file);				
				if(in.getStudent().getPicture() == null)
					studentDao.updatePicture(fileName, in.getStudent().getId());
			} catch (Exception e) {
				showMessageDialog("Erreur d'ecriture du fichier", e.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
		}
		
		
		if(inscription != null)
			inscription.setPicture(in.getPicture());
		
		razFields();
		
		setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
