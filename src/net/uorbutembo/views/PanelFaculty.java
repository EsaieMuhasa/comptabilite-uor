/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.forms.FormFaculty;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.FacultyTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFaculty extends Panel {
	private static final long serialVersionUID = 6683302991865603147L;
	
	private FormFaculty form;
	private final Table table;
	private final FacultyTableModel tableModel;
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter une faculté");
	private Dialog dialogForm;
	private FacultyDao facultyDao;
	private DepartmentDao departmentDao;
	
	private final JMenuItem itemUpdate = new  JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private final JMenuItem itemDelete = new  JMenuItem("Suprimer", new ImageIcon(R.getIcon("close")));
	private final JPopupMenu popupMenu = new JPopupMenu();
	
	private MainWindow mainWindow;

	/**
	 * @param mainWindow
	 */
	public PanelFaculty(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		this.departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		
		this.facultyDao.addListener(new DAOAdapter<Faculty>() {
			@Override
			public void onCreate(Faculty e, int requestId) {
				dialogForm.setVisible(false);
			}
		});
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		this.add(top, BorderLayout.NORTH);
		
		this.btnNew.addActionListener(event -> {
			createDialog();
			this.dialogForm.setTitle("Enregistrememnt d'une faculté");
			this.dialogForm.setVisible(true);
		});
		
		Panel center = new Panel(new BorderLayout());
		
		tableModel = new FacultyTableModel(mainWindow.factory.findDao(FacultyDao.class));
		table = new Table(tableModel);
		
		center.add(FormUtil.createVerticalScrollPane(table), BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		this.add(center, BorderLayout.CENTER);
		
		initPopup();
	}
	
	/**
	 * Creation d ela boite de dialogue d'enregistrement et de modification des facultes
	 * @return void
	 */
	private void createDialog() {
		if(dialogForm == null) {
			dialogForm = new Dialog(mainWindow);
			form = new FormFaculty(mainWindow, facultyDao);
			form.setDialog(dialogForm);
			dialogForm.getContentPane().add(form, BorderLayout.CENTER);
			dialogForm.setSize(600, 250);
		}
		
		this.dialogForm.setLocationRelativeTo(mainWindow);
	}
	
	/**
	 * Initialisation des composants graphique du popup menu
	 */
	private void initPopup() {
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() ) {
					if(table.getSelectedRow() != -1) {						
						popupMenu.show(table, e.getX(), e.getY());
					} else {
						JOptionPane.showMessageDialog(null, "Impossible d'effectuer cette operations\nselectionner d'abord une faculté", "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		itemDelete.addActionListener(event -> {
			Faculty fac = tableModel.getRow(table.getSelectedRow());
			try {						
				if(departmentDao.checkByFaculty(fac.getId())) {
					JOptionPane.showMessageDialog(null, "Impossible de supprimer la faculté '"+fac.toString()+"', \ncar certains départements y font references", "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				} else {
					int status = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cette faculté?\n=>"+fac.toString(), "Supression de la facutlé", JOptionPane.YES_NO_OPTION);
					if(status == JOptionPane.OK_OPTION) {
						facultyDao.delete(fac.getId());
					}
				}
			} catch (DAOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		itemUpdate.addActionListener(event -> {
			createDialog();
			form.setFaculty(tableModel.getRow(table.getSelectedRow()));
			dialogForm.setTitle("Modification de la description d'une faculté");
			dialogForm.setVisible(true);
		});
		
	}

}
