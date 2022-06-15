/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.tools.FormUtil.BKG_END;
import static net.uorbutembo.tools.FormUtil.BORDER_COLOR;
import static net.uorbutembo.tools.FormUtil.DEFAULT_EMPTY_BORDER;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class DefaultFormPanel extends Panel implements ActionListener {
	private static final long serialVersionUID = 3940623605438304561L;

	protected static final String 
		TITLE_1 = "Formulaire d'enregistrement",
		TITLE_2 = "Formulaire de modification",
		RGX_SIMBLE_DATE = "^(([0-9]{1,2}-){2})([0-9]{4})$",
		RGX_NUMBER = "^([0-9]+)(\\.([0-9]{1,8}))?$",
		RGX_INT = "^-?([0-9]+)$",
		RGX_POSITIV_INT = "^([0-9]+)(\\.([0-9]{1,8}))?$";

	private String title;
	private JLabel label;
	protected JButton btnSave;
	
	private final JPanel middle = new JPanel(new BorderLayout());
	private JPanel header;
	private JPanel body;
	private JPanel footer;
	
	protected final MainWindow mainWindow;
	
	/**
	 * @param mainWindow
	 */
	public DefaultFormPanel(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.title = TITLE_1;
		
		this.setOpaque(false);
		
		//header
		this.header = new JPanel(new BorderLayout());
		this.header.setOpaque(false);
		this.label = FormUtil.createTitle(this.title);
		this.header.add(this.label, BorderLayout.CENTER);
		//--header
		
		//body
		this.body = new  JPanel(new BorderLayout());
		this.body.setBackground(BKG_END);
		//--body
		
		//footer
		if(this.createButtonAccept()) {			
			this.btnSave = new Button(new ImageIcon(R.getIcon("success")), "Enregistrer");
			this.btnSave.addActionListener(this);
			this.footer = new JPanel();	
			this.footer.setOpaque(false);
			this.footer.add(this.btnSave);
			this.footer.setBorder(DEFAULT_EMPTY_BORDER);
		}
		//--footer

		//adding components to panel
		middle.add(this.body, BorderLayout.CENTER);
		if(this.createButtonAccept()) 
			middle.add(this.footer, BorderLayout.SOUTH);
		middle.setOpaque(false);
		middle.setBorder(new LineBorder(BORDER_COLOR, 1));

		middle.add(header, BorderLayout.NORTH);
		add(middle, BorderLayout.NORTH);
	}
	
	/**
	 * Affiche une boite de dialogue
	 * @param title
	 * @param message
	 * @param type
	 */
	public void showMessageDialog (String title, String message, int type) {
		JOptionPane.showMessageDialog(mainWindow, message, title, type);
	}
	
	public String getTitle() {
		return title;
	}
	
	/**
	 * Fait-il ajouter le pour de enregistrement au formulaire?
	 * @return
	 */
	protected boolean createButtonAccept () {
		return true;
	}

	public void setTitle(String title) {
		this.title = title;
		this.label.setText(title);
	}

	public JPanel getHeader() {
		return header;
	}

	/**
	 * Renvoie le corp du formultaire.
	 * Le Layout manager par default d'un corp d'un formulaire est le BorderLayout
	 * @return
	 */
	public JPanel getBody() {
		return body;
	}
	
	/**
	 * Renvoie le panel conteneur du body et du header
	 * @return
	 */
	public JPanel getMiddle () {
		return middle;
	}

	public JPanel getFooter() {
		return footer;
	}

	/**
	 * @return the btnSave
	 */
	public JButton getBtnSave() {
		return btnSave;
	}

}
