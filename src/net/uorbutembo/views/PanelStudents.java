/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButton;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.forms.FormInscription;
import net.uorbutembo.views.forms.FormReRegister;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * 
 */
public class PanelStudents extends DefaultScenePanel{
	private static final long serialVersionUID = -356861410803019685L;

	//dao
	private InscriptionDao inscriptionDao;
	private StudentDao studentDao;
	//==dao
	
	private Table table;
	private JLabel title = FormUtil.createTitle("");
	private Panel workspace = new Panel(new BorderLayout());

	private Panel scene = new Panel(new BorderLayout());
	private JScrollPane scrollTable;
	private Button btnClose = new Button(new ImageIcon(R.getIcon("close")));
	
	private JPopupMenu popup = new JPopupMenu();
	
	private JMenuItem itemStudentFiche = new JMenuItem("Ouvrir la fiche individuel", new ImageIcon(R.getIcon("card")));
	private JMenuItem itemStudentUpdate = new JMenuItem("Editer l'identité", new ImageIcon(R.getIcon("usredit")));

	private JMenuItem itemListSolde = new JMenuItem("Solde des comptes", new ImageIcon(R.getIcon("report")));
	private JMenuItem itemListExportPDF = new JMenuItem("Exporter la liste au format PDF", new ImageIcon(R.getIcon("pdf")));
	private JMenuItem itemListExportEXEL = new JMenuItem("Exporter la liste au format Excel", new ImageIcon(R.getIcon("export")));

	public PanelStudents(MainWindow mainWindow) {
		super("Inscription", new ImageIcon(R.getIcon("student")), mainWindow, false);//la scene gere les scrollbars	
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		studentDao = mainWindow.factory.findDao(StudentDao.class);
		
		this.setTitle("Etudiants");
		
		table = new Table();
		table.setBackground(FormUtil.BKG_DARK);
		
		final Panel inscriptionFormPanel = new Panel(new BorderLayout());
		final Panel registerFormPanel = new Panel(new BorderLayout());
		final Panel listPanel = new PanelListStudents(mainWindow);
		
		final FormInscription inscription = new FormInscription(mainWindow, inscriptionDao, studentDao);
		final FormReRegister register = new FormReRegister(mainWindow, inscriptionDao, studentDao, true);
		
		inscriptionFormPanel.add(inscription, BorderLayout.NORTH);
		inscriptionFormPanel.setBorder(BODY_BORDER);
		
		registerFormPanel.add(register, BorderLayout.NORTH);
		registerFormPanel.setBorder(BODY_BORDER);
		
		final JScrollPane inscriptionFormScroll = FormUtil.createVerticalScrollPane(inscriptionFormPanel);
		final JScrollPane registerFormScroll = FormUtil.createVerticalScrollPane(registerFormPanel);
		
		//menu secondaire
		this
			.addItemMenu(new NavbarButtonModel("inscrits", "Liste des inscrits"), listPanel)
			.addItemMenu(new NavbarButtonModel("newStudent", "Inscription"), inscriptionFormScroll)
			.addItemMenu(new NavbarButtonModel("oldStudent", "Re-inscription"), registerFormScroll);
		
		this.initWorkspace();
	}
	
	@Override
	public boolean hasHeader() {
		return false;
	}
	
	/**
	 * Personnalisaion de l'espce de travaille
	 */
	private void initWorkspace() {
		Box top = Box.createHorizontalBox();
		top.add(title);
		top.add(Box.createHorizontalGlue());
		top.add(btnClose);
		top.add(Box.createHorizontalStrut(2));
		
		btnClose.setVisible(false);
		btnClose.addActionListener(event -> {
			scene.removeAll();
			scene.add(scrollTable, BorderLayout.CENTER);
			scene.revalidate();
			scene.repaint();
			scene.setBorder(null);
			title.setText(title.getName());
			btnClose.setVisible(false);
		});
		
		workspace.add(top, BorderLayout.NORTH);
		
		Panel panel = new Panel (new BorderLayout());
		scrollTable = FormUtil.createVerticalScrollPane(panel);
		panel.setBorder(BODY_BORDER);
		panel.add(table, BorderLayout.CENTER);
		
		table.getTableHeader().setVisible(false);
		workspace.add(scene, BorderLayout.CENTER);
		
		scene.add(scrollTable, BorderLayout.CENTER);
		
		//popup
		popup.add(itemStudentFiche);
		popup.add(itemStudentUpdate);
		popup.addSeparator();
		popup.add(itemListSolde);
		popup.add(itemListExportPDF);
		popup.add(itemListExportEXEL);
		//--popup
		
		
	}

	@Override
	public String getNikeName() {
		return "students";
	}
	
	@Override
	public void onAction(NavbarButton view) {
		super.onAction(view);
	}
}
