/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.views.forms.FormUtil.BKG_END;
import static net.uorbutembo.views.forms.FormUtil.BORDER_COLOR;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_EMPTY_BORDER;

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
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class DefaultFormPanel extends Panel implements ActionListener {
	private static final long serialVersionUID = 3940623605438304561L;

	private String title;
	private JLabel label;
	protected JButton btnSave;
	
	private JPanel header;
	private JPanel body;
	private JPanel footer;
	
	public DefaultFormPanel() {
		super(new BorderLayout());
		this.title = "";
		
		this.setOpaque(false);
		this.setBorder(new LineBorder(BORDER_COLOR, 1));
		
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
		this.btnSave = new Button(new ImageIcon(R.getIcon("success")), "Enregistrer");
		this.btnSave.addActionListener(this);
		this.footer = new JPanel();	
		this.footer.setOpaque(false);
		this.footer.add(this.btnSave);
		this.footer.setBorder(DEFAULT_EMPTY_BORDER);
		//--footer

		//adding components to panel
		final JPanel middle = new JPanel(new BorderLayout());
		middle.add(this.body, BorderLayout.NORTH);
		middle.add(this.footer, BorderLayout.CENTER);
		middle.setOpaque(false);

		this.add(header, BorderLayout.NORTH);
		this.add(middle, BorderLayout.CENTER);
	}
	
	/**
	 * Affiche une boite de dialogue
	 * @param title
	 * @param message
	 * @param type
	 */
	public void showMessageDialog (String title, String message, int type) {
		JOptionPane.showMessageDialog(this, message, title, type);
	}
	
	public String getTitle() {
		return title;
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

	public JPanel getFooter() {
		return footer;
	}

}
