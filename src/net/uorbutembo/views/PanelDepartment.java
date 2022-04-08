/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormDepartment;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.DepartmentTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelDepartment extends Panel {

	private static final long serialVersionUID = -2959155526810028774L;
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter un département");
	private FormDepartment form;
	private Dialog formDialog;
	private DepartmentDao departmentDao;
	private PromotionDao promotionDao;
	
	private final JMenuItem itemUpdate = new  JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private final JMenuItem itemDelete = new  JMenuItem("Suprimer", new ImageIcon(R.getIcon("close")));
	private final JPopupMenu popupMenu = new JPopupMenu();
	
	private JTabbedPane tabbedPane;
	private MainWindow mainWindow;

	/**
	 * 
	 */
	public PanelDepartment(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		this.promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		
		this.departmentDao.addListener(new DAOAdapter<Department> () {
			@Override
			public void onCreate(Department e, int requestId) {
				formDialog.setVisible(false);
			}
		});
		
		Panel top = new Panel(new BorderLayout());
		top.add(btnNew, BorderLayout.EAST);
		top.add(FormUtil.createTitle("Départements"), BorderLayout.CENTER);
		top.setBorder(new EmptyBorder(0, 0, 10, 0));
		this.add(top, BorderLayout.NORTH);
		
		this.btnNew.addActionListener(event -> {
			createDialog();
			formDialog.setTitle("Enregistrement d'un nouveau departement");
			formDialog.setVisible(true);
		});
		
		this.tabbedPane = new JTabbedPane(JTabbedPane.RIGHT);
		
		final List<Faculty> faculties = mainWindow.factory.findDao(FacultyDao.class).findAll();
		final MouseAdapter listener = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() ) {
					Table table = (Table) e.getSource();
					if(table.getSelectedRow() != -1) {						
						popupMenu.show(table, e.getX(), e.getY());
					} else {
						JOptionPane.showMessageDialog(null, "Impossible d'effectuer cette operations\nselectionner d'abord une faculté", "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
		
		for (Faculty faculty : faculties) {
			DepartmentTableModel tableModel = new DepartmentTableModel(this.departmentDao, faculty);
			Table table = new Table(tableModel);
			TablePanel tablePanel = new TablePanel(table, faculty.getName(), false);
			tabbedPane.addTab(faculty.getAcronym(), tablePanel);
			
			table.addMouseListener(listener);
		}
		
		itemDelete.addActionListener(event -> {
			TablePanel panel = (TablePanel) tabbedPane.getSelectedComponent();
			DepartmentTableModel model = (DepartmentTableModel) panel.getTable().getModel();
			Department dep = model.getRow(panel.getTable().getSelectedRow());
			try {						
				if(promotionDao.checkByDepartment(dep.getId())) {
					JOptionPane.showMessageDialog(null, "Impossible de supprimer le departement '"+dep.toString()+"', \ncar certain promotions y font references", "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				} else {
					int status = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ce département?\n=>"+dep.toString(), "Supression du département", JOptionPane.YES_NO_OPTION);
					if(status == JOptionPane.OK_OPTION) {
						departmentDao.delete(dep.getId());
					}
				}
			} catch (DAOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		itemUpdate.addActionListener(event -> {
			TablePanel panel = (TablePanel) tabbedPane.getSelectedComponent();
			DepartmentTableModel model = (DepartmentTableModel) panel.getTable().getModel();
			createDialog();
			form.setDepartment(model.getRow(panel.getTable().getSelectedRow()));
			formDialog.setTitle("Modification de la description d'un département");
			formDialog.setVisible(true);
		});
		
		this.add(tabbedPane, BorderLayout.CENTER);
		this.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		initPopup();
	}
	
	private void createDialog() {
		if(formDialog == null) {
			form = new FormDepartment(mainWindow, this.departmentDao);
			formDialog = new Dialog(mainWindow);
			formDialog.getContentPane().add(this.form, BorderLayout.CENTER);
			formDialog.setSize(600, 320);
		}
		this.formDialog.setLocationRelativeTo(mainWindow);
	}
	
	/**
	 * Initialisation des composants graphique du popup menu
	 */
	private void initPopup() {
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
	}


}
