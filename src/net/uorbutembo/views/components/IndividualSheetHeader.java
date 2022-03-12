/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import net.uorbutembo.beans.Inscription;
import resources.net.uorbutembo.R;

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
	
	private Inscription inscription;

	/**
	 * constructeur par defaut
	 */
	public IndividualSheetHeader() {
		super();
		this.init();
	}
	
	/**
	 * @param inscription
	 */
	public IndividualSheetHeader(Inscription inscription) {
		super();
		this.inscription = inscription;
	}

	private void init() {
		this.setPreferredSize(SIZE);
		this.setMaximumSize(MAX_SIZE);
		this.setMinimumSize(MIN_SIZE);
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
		if(this.inscription == null || inscription.getId() != this.inscription.getId()) {			
			this.inscription = inscription;
			this.repaint();
		}
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
		
		int width = this.getWidth(), height = this.getHeight();
		
		
		//photo de profil
		String picture = inscription.getStudent().getPicture() != null? R.getConfig().get("workspace")+""+inscription.getStudent().getPicture(): null;
		try {
			Image image = ImageIO.read(new File(picture !=null? picture : R.getIcon("personne")));
			g2.drawImage (image, 4, 4, height-10, height-10, null);
			g2.drawRoundRect(1, 1, height-5, height-5, 5, 5);
		} catch (IOException e) {
			System.out.println("> Error: "+e.getMessage()+" => "+picture);
		}
		
		if(this.inscription == null)
			return;
		
		// ==
		
		int startLine =  g2.getFont().getSize()+4;
		
		//identite
		g2.drawString("Noms: "+inscription.getStudent().toString(), height, startLine);
		g2.drawString("Faculté: "+inscription.getPromotion().getDepartment().getFaculty().toString(), height, startLine * 2);
		g2.drawString("Département: "+inscription.getPromotion().getDepartment().toString(), height, startLine * 3);
		g2.drawString("Class d'étude: "+inscription.getPromotion().getStudyClass().toString(), height, startLine * 4);
		g2.drawString("E-mail: "+inscription.getStudent().getEmail(), height, startLine * 5);
		g2.drawString("Téléphone: "+inscription.getStudent().getTelephone(), height, startLine * 6);
		//==
		
	}

}
