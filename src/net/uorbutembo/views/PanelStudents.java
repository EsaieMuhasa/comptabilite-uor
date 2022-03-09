/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.IndividualSheet;
import net.uorbutembo.views.components.NavbarButton;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.components.SidebarStudents;
import net.uorbutembo.views.components.SidebarStudents.SidebarListener;
import net.uorbutembo.views.forms.FormInscription;
import net.uorbutembo.views.forms.FormReRegister;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.InscritTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * 
 */
public class PanelStudents extends DefaultScenePanel implements SidebarListener{
	private static final long serialVersionUID = -356861410803019685L;

	//dao
	private InscriptionDao inscriptionDao;
	private StudentDao studentDao;
	//==dao
	
	private Table table;
	private InscritTableModel tableModel;
	private JLabel title = FormUtil.createTitle("");
	private SidebarStudents sidebar;
	private Panel workspace = new Panel(new BorderLayout());
	private IndividualSheet sheet;

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
		
		sheet = new IndividualSheet(mainWindow);
		
		tableModel = new InscritTableModel(inscriptionDao);
		table = new Table(tableModel);
		table.setBackground(FormUtil.BKG_DARK);
		
		sidebar = new SidebarStudents(mainWindow, this, tableModel);
		
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.sidebar, workspace);
		split.setDividerLocation(300);
		
		final Panel inscriptionFormPanel = new Panel(new BorderLayout());
		final Panel registerFormPanel = new Panel(new BorderLayout());
		final Panel listPanel = new Panel(new BorderLayout());
		
		final FormInscription inscription = new FormInscription(mainWindow, inscriptionDao, studentDao);
		final FormReRegister register = new FormReRegister(mainWindow, inscriptionDao, studentDao);
		
		inscriptionFormPanel.add(inscription, BorderLayout.NORTH);
		inscriptionFormPanel.setBorder(BODY_BORDER);
		
		registerFormPanel.add(register, BorderLayout.NORTH);
		registerFormPanel.setBorder(BODY_BORDER);
		
		final JScrollPane inscriptionFormScroll = FormUtil.createVerticalScrollPane(inscriptionFormPanel);
		final JScrollPane registerFormScroll = FormUtil.createVerticalScrollPane(registerFormPanel);
		
		listPanel.add(split, BorderLayout.CENTER);
		
		//menu secondaire
		this
			.addItemMenu(new NavbarButtonModel("inscrits", "Liste des inscrits"), listPanel)
			.addItemMenu(new NavbarButtonModel("newStudent", "Inscription"), inscriptionFormScroll)
			.addItemMenu(new NavbarButtonModel("oldStudent", "Re-inscription"), registerFormScroll);
		
		this.initWorkspace();
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
		
		itemStudentFiche.addActionListener(event -> {
			Inscription in = tableModel.getRow(table.getSelectedRow());
			onSelectInscription(in);
		});
		// --popup
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					Inscription in = tableModel.getRow(table.getSelectedRow());
					onSelectInscription(in);
				}
				
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					
					if(tableModel.getRowCount() == 0)
						return;
					
					if(table.getSelectedRow() <= -1) {
						itemStudentFiche.setEnabled(false);
						itemStudentUpdate.setEnabled(false);
					} else {
						Inscription in = tableModel.getRow(table.getSelectedRow());
						popup.setLabel(in.getStudent().toString());
						itemStudentFiche.setEnabled(true);
						itemStudentUpdate.setEnabled(true);
					}
					
					int x = e.getX(), y = e.getY();
					popup.show(table, x, y);
				}
			}
			
		});
	}

	@Override
	public String getNikeName() {
		return "students";
	}
	
	@Override
	public void onAction(NavbarButton view) {
		super.onAction(view);
	}
	
	
	//interfacage avec le sidebar
	//=========================================

	@Override
	public void onSelectAcademicYear(AcademicYear year) {
		this.title.setText("Etudiants inscrit pour l'année " + year.getLabel());
		
		if(btnClose.isVisible())
			btnClose.doClick();
	}
	
	@Override
	public void onSelectFaulty(Faculty faculty, AcademicYear year) {
		this.title.setText(year.toString()+" / "+faculty);
		if(btnClose.isVisible())
			btnClose.doClick();
	}

	@Override
	public void onSelectDepartment(Department department, AcademicYear year) {
		this.title.setText(year.toString()+" / "+department.getFaculty().getAcronym()+" / "+department);
		if(btnClose.isVisible())
			btnClose.doClick();
	}

	@Override
	public void onSelectPromotion(Promotion promotion) {
		this.title.setText(promotion.getAcademicYear().toString()+" / "+promotion.getDepartment().getFaculty().getAcronym()+" / "+promotion);
		if(btnClose.isVisible())
			btnClose.doClick();
	}
	
	private static final EmptyBorder BORDER_CONTENT_SHEET = new EmptyBorder(0, 5, 5, 0);

	@Override
	public void onSelectInscription(Inscription inscription) {
		Promotion promotion = inscription.getPromotion();
		
		title.setName(title.getText());
		this.title.setText(promotion.getAcademicYear().toString()+" / "+promotion.getDepartment().getFaculty().getAcronym()+" / "+promotion+" / "+inscription.getStudent().getMatricul());
		
		scene.removeAll();
		scene.add(sheet, BorderLayout.CENTER);
		scene.revalidate();
		scene.repaint();
		scene.setBorder(BORDER_CONTENT_SHEET);
		btnClose.setVisible(true);
		sheet.setInscription(inscription);
	}

}
