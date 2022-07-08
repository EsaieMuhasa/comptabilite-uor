/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRowListener;

/**
 * @author Esaie MUHASA
 *
 */
public class IndividualSheetHeader extends JComponent {
	private static final long serialVersionUID = 2470599563059921305L;
	public static final Dimension 
				MAX_SIZE = new Dimension(1123, 110),
				SIZE = new Dimension(1100, 105),
				MIN_SIZE = new Dimension(1100, 100);
	
	private InscriptionDataRow inscription;
	private final InscriptionDataRowListener inscriptionRowListener = new InscriptionDataRowListener() {
		
		@Override
		public void onReload(InscriptionDataRow row) {
			if(inscription == null || row.getInscription().getId() != inscription.getInscription().getId())
				return;
			
			inscription = row;
			repaint();
		}
		
		@Override
		public void onLoad(InscriptionDataRow row) {}
		
		@Override
		public void onDispose(InscriptionDataRow row) {}
	};

	/**
	 * constructeur par defaut
	 */
	public IndividualSheetHeader() {
		super();
		init();
	}
	
	/**
	 * @param inscription
	 */
	public IndividualSheetHeader (InscriptionDataRow inscription) {
		super();
		this.inscription = inscription;
	}

	private void init() {
		setPreferredSize(SIZE);
		setMaximumSize(MAX_SIZE);
		setMinimumSize(MIN_SIZE);
	}
	
	/**
	 * @return the inscription
	 */
	public InscriptionDataRow getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(InscriptionDataRow inscription) {
		
		if(this.inscription != null)
			this.inscription.removeDataRowListener(inscriptionRowListener);
		
		if(this.inscription == null || inscription == null || inscription.getInscription().getId() != this.inscription.getInscription().getId()) {			
			this.inscription = inscription;
			this.repaint();
		}
		
		if(inscription != null)
			inscription.addDataRowListener(inscriptionRowListener);
	}

	@Override
	protected void paintBorder(Graphics g) {
		super.paintBorder(g);
		
		g.setColor(Color.BLACK);
		g.drawLine(0, this.getHeight()-1, this.getWidth(), this.getHeight()-1);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		if(inscription == null)
			return;
		
		int height = getHeight();
		Inscription inscription = this.inscription.getInscription();
		
		//photo de profil
		String picture = inscription.getPicture() != null? R.getConfig().get("workspace")+""+inscription.getPicture(): null;
		try {
			Image image = ImageIO.read(new File(picture !=null? picture : R.getIcon("personne")));
			g2.drawImage (image, 4, 4, height-10, height-10, null);
			g2.drawRoundRect(1, 1, height-5, height-5, 5, 5);
		} catch (IOException e) {}
		
		// ==
		
		int startLine =  g2.getFont().getSize()+4;

		//identite
		g2.setFont(g2.getFont().deriveFont(Font.BOLD));
		g2.drawString("Noms: "+inscription.getStudent().getFullName(), height, startLine);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
		g2.drawString("Faculté: "+inscription.getPromotion().getDepartment().getFaculty().toString(), height, startLine * 2);
		g2.drawString("Département: "+inscription.getPromotion().getDepartment().toString(), height, startLine * 3);
		g2.drawString("Classe d'étude: "+inscription.getPromotion().getStudyClass().toString(), height, startLine * 4);
		g2.drawString("E-mail: "+inscription.getStudent().getEmail(), height, startLine * 5);
		g2.drawString("Téléphone: "+inscription.getStudent().getTelephone(), height, startLine * 6);
		//==
		
	}

}
