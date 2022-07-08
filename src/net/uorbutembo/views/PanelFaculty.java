/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DepartmentDao;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.forms.FormDepartment;
import net.uorbutembo.views.forms.FormFaculty;
import net.uorbutembo.views.models.DepartmentTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFaculty extends Panel {
	private static final long serialVersionUID = 6683302991865603147L;
	
	private Button btnNewFaculty = new Button(new ImageIcon(R.getIcon("plus")), "Nouvelle faculté");
	private FormFaculty formFaculty;
	private JDialog dialogFaculty;
	
	private Button btnNewDepartment = new Button(new ImageIcon(R.getIcon("plus")), "Nouveau département");
	private FormDepartment formDepartment;
	private JDialog dialogDepartment;
	
	private final FacultyDao facultyDao;
	private final DepartmentDao departmentDao;
	private final PromotionDao promotionDao;
	
	private final MainWindow mainWindow;
	private final List<FacultyGroup> facultyGroups = new ArrayList<>();
	private final Box center = Box.createVerticalBox();
	
	private final DAOAdapter<Faculty> facultyAdapter = new DAOAdapter<Faculty>() {
		@Override
		public synchronized void onCreate(Faculty e, int requestId) {
			dialogFaculty.setVisible(false);
			FacultyGroup g = new FacultyGroup(e);
			center.add(g);
			center.add(Box.createVerticalStrut(10));
			facultyGroups.add(g);
		}
		
		@Override
		public synchronized void onUpdate(Faculty e, int requestId) {
			dialogFaculty.setVisible(false);
			formFaculty.setFaculty(null);
		}
		
		@Override
		public synchronized void onDelete(Faculty e, int requestId) {
			for (FacultyGroup group : facultyGroups) {
				if(group.faculty.getId() == e.getId()) {
					center.remove(group);
					center.revalidate();
					center.repaint();
					group.dispose();
					return;
				}
			}
		};
	};
	
	private final DAOAdapter<Department> departmentAdapter = new DAOAdapter<Department>() {

		@Override
		public synchronized void onCreate(Department e, int requestId) {
			dialogDepartment.setVisible(false);
		}

		@Override
		public synchronized void onUpdate(Department e, int requestId) {
			dialogDepartment.setVisible(false);
			formDepartment.setDepartment(null);
		}
		
	};

	/**
	 * @param mainWindow
	 */
	public PanelFaculty(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		
		facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		departmentDao = mainWindow.factory.findDao(DepartmentDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		
		facultyDao.addListener(facultyAdapter);
		departmentDao.addListener(departmentAdapter);
		btnNewFaculty.setEnabled(false);
		btnNewFaculty.addActionListener(event -> {
			createFaculty();
		});
		
		btnNewDepartment.setEnabled(false);
		btnNewDepartment.addActionListener(event -> {
			createDepartment();
		});
		
		Panel top = new Panel(new BorderLayout());
		Box box = Box.createHorizontalBox();
		box.add(btnNewFaculty);
		box.add(btnNewDepartment);
		top.add(box, BorderLayout.EAST);
		top.add(FormUtil.createTitle("Facultés"), BorderLayout.CENTER);
		
		
		center.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		top.setBorder(center.getBorder());
		add(FormUtil.createVerticalScrollPane(center), BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
	}
	
	/**
	 * Chargement panel qui affiche chaque faculté et ces départements
	 */
	public synchronized void load () {
		for (FacultyGroup group : facultyGroups)
			group.dispose();
		
		center.removeAll();
		if(facultyDao.countAll() != 0) {			
			List<Faculty> facs = facultyDao.findAll();
			for (Faculty faculty : facs) {
				FacultyGroup g = new FacultyGroup(faculty);
				center.add(g);
				center.add(Box.createVerticalStrut(10));
				facultyGroups.add(g);
			}
			center.add(Box.createVerticalGlue());
			btnNewDepartment.setEnabled(true);
		} else {
			btnNewDepartment.setEnabled(false);
		}
		btnNewFaculty.setEnabled(true);
	}
	
	/**
	 * Construction de la boite de dialogue 
	 * qui permet d'enregistrer et de modifier l'identite d'une faculte
	 */
	private void buildFacultyDialog () {
		if(dialogFaculty == null) {
			final Panel padding = new Panel(new BorderLayout());
			formFaculty = new FormFaculty(mainWindow);
			dialogFaculty = new JDialog(mainWindow);
			dialogFaculty.getContentPane().setBackground(FormUtil.BKG_DARK);
			dialogFaculty.getContentPane().add(padding, BorderLayout.CENTER);
			dialogFaculty.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			padding.add(formFaculty, BorderLayout.CENTER);
			padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			dialogFaculty.pack();
			dialogFaculty.setSize(600, dialogFaculty.getHeight());
			dialogFaculty.setResizable(false);
			dialogFaculty.addWindowFocusListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					formFaculty.setFaculty(null);
				}
			});
		}		
	}
	
	/**
	 * Construction dela poite de dialogue de creation et modification d'un departement
	 */
	private void buildDepartmentDialog () {
		if(dialogDepartment == null) {
			final Panel padding = new Panel(new BorderLayout());
			formDepartment = new FormDepartment(mainWindow);
			dialogDepartment = new JDialog(mainWindow);
			dialogDepartment.getContentPane().setBackground(FormUtil.BKG_DARK);
			dialogDepartment.getContentPane().add(padding, BorderLayout.CENTER);
			padding.add(formDepartment, BorderLayout.CENTER);
			padding.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			dialogDepartment.pack();
			dialogDepartment.setSize(600, dialogDepartment.getHeight());
			dialogDepartment.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogDepartment.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					formDepartment.setDepartment(null);
				}
			});
		}
	}
	
	/**
	 * demande d'ouverture de la boite de dialogue de creation d'une faculte
	 * @return void
	 */
	private void createFaculty () {
		buildFacultyDialog();
		dialogFaculty.setTitle("Enregistrement d'une faculté");
		dialogFaculty.setLocationRelativeTo(mainWindow);
		dialogFaculty.setVisible(true);
	}
	
	/**
	 * Demande d'ouverture de la boite de dialogue de modification 
	 * de l'identite d'une faculte
	 * @param faculty
	 */
	private void updateFaculty (Faculty faculty) {
		buildFacultyDialog();
		formFaculty.setFaculty(faculty);
		dialogFaculty.setTitle("Modification d'une faculté");
		dialogFaculty.setLocationRelativeTo(mainWindow);
		dialogFaculty.setVisible(true);
	}
	
	/**
	 * Demande d'ouverture de la poite de dialogue de modification de 
	 * l'identite d'un departement
	 * @param department
	 */
	private void updateDepartment (Department department) {
		buildDepartmentDialog();
		formDepartment.setDepartment(department);
		dialogDepartment.setTitle("Modification d'un département");
		dialogDepartment.setLocationRelativeTo(mainWindow);
		dialogDepartment.setVisible(true);
	}
	
	/**
	 * Demande de chargement de la boite de dialogue qui permet
	 * d'enregistrer un nouveau departement
	 */
	private void createDepartment() {
		if (facultyDao.countAll() == 0) {
			JOptionPane.showMessageDialog(mainWindow, "Impossible d'éffetuer cette operations \ncar aucune faculté n'est déjà enregistrer.", "Information", JOptionPane.WARNING_MESSAGE);
			return;
		}
		buildDepartmentDialog();
		dialogDepartment.setTitle("Enregistrement d'un département");
		dialogDepartment.setLocationRelativeTo(mainWindow);
		dialogDepartment.setVisible(true);
	}
	
	protected class FacultyGroup extends Panel{
		private static final long serialVersionUID = 6347417595220397581L;
		
		private Faculty faculty;
		private DepartmentTableModel tableModel;
		private Table table;
		private TablePanel tablePanel;
		private JLabel labelAbb = FormUtil.createSubTitle("");
		
		private final JMenuItem itemUpdateFaculty = new  JMenuItem("Modifier la faculté", new ImageIcon(R.getIcon("edit")));
		private final JMenuItem itemDeleteFaculty = new  JMenuItem("Supprimer la faculté", new ImageIcon(R.getIcon("close")));
		private final JMenuItem itemUpdateDepartment = new  JMenuItem("Modifier le département", new ImageIcon(R.getIcon("edit")));
		private final JMenuItem itemDeleteDepartment = new  JMenuItem("Supprimer département", new ImageIcon(R.getIcon("close")));
		
		private final JPopupMenu popupDepartment = new JPopupMenu();
		private final JPopupMenu popupFaculty = new JPopupMenu();
		
		private final MouseAdapter listenerPopupFaculty = new MouseAdapter() {
			@Override
			public void mouseReleased (MouseEvent e) {
				if (e.isPopupTrigger()) {
					itemDeleteFaculty.setEnabled(tableModel.getRowCount() == 0);
					popupFaculty.show(tablePanel.getHeader(), e.getX(), e.getY());
				}
			}
		};
		
		private final MouseAdapter listenerPopupDepartement = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
					popupDepartment.show(table, e.getX(), e.getY());
				}
			}
		};
		
		private final DAOAdapter<Faculty> daoAdapter = new DAOAdapter<Faculty>() {
			@Override
			public synchronized void onUpdate(Faculty e, int requestId) {
				if(faculty.getId() == e.getId()) {
					faculty = e;
					tablePanel.setTitle(e.getName());
					labelAbb.setText(e.getAcronym()+":");
				}
			}
		};

		/**
		 * @param faculty
		 */
		public FacultyGroup(Faculty faculty) {
			super(new BorderLayout());
			this.faculty = faculty;
			tableModel = new DepartmentTableModel(departmentDao, faculty);
			table = new Table(tableModel);
			tablePanel = new TablePanel(table, faculty.getName(), false);
			tablePanel.getHeader().add(labelAbb, BorderLayout.WEST);
			
			table.setShowVerticalLines(true);
			table.setShowVerticalLines(true);
			final int w = 140;
			for (int i = 2; i <= 3; i++) {			
				table.getColumnModel().getColumn(i).setWidth(w);
				table.getColumnModel().getColumn(i).setMinWidth(w);
				table.getColumnModel().getColumn(i).setMaxWidth(w);
				table.getColumnModel().getColumn(i).setResizable(false);
			}
			facultyDao.addListener(daoAdapter);
			
			add(tablePanel, BorderLayout.CENTER);
			tableModel.reload();
			initPopup();
			labelAbb.setText(faculty.getAcronym()+":");
			labelAbb.setPreferredSize(new Dimension(120, labelAbb.getHeight()));
			table.getColumnModel().getColumn(0).setWidth(120);
			table.getColumnModel().getColumn(0).setMaxWidth(120);
			table.getColumnModel().getColumn(0).setMinWidth(120);
			table.getColumnModel().getColumn(0).setResizable(false);
		}
		
		/**
		 * liberation des ressources et deconnection au DAO
		 */
		public void dispose() {
			table.removeMouseListener(listenerPopupDepartement);
			tablePanel.removeMouseListener(listenerPopupFaculty);
			facultyDao.removeListener(daoAdapter);
			departmentDao.removeListener(tableModel);
		}
		
		/**
		 * Initialisation des composants graphique du popup menu
		 */
		private void initPopup() {
			popupFaculty.add(itemUpdateFaculty);
			popupFaculty.add(itemDeleteFaculty);
			
			popupDepartment.add(itemUpdateDepartment);
			popupDepartment.add(itemDeleteDepartment);
			
			tablePanel.addMouseListener(listenerPopupFaculty);
			table.addMouseListener(listenerPopupDepartement);
			
			itemDeleteFaculty.addActionListener(event -> {
				Faculty fac = faculty;
				try {						
					if(tableModel.getRowCount() != 0) {
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
			
			itemUpdateFaculty.addActionListener(event -> {
				updateFaculty(faculty);
			});
			
			itemDeleteDepartment.addActionListener(event -> {
				Department dep = tableModel.getRow(table.getSelectedRow());
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
			
			itemUpdateDepartment.addActionListener(event -> {
				updateDepartment(tableModel.getRow(table.getSelectedRow()));
			});
		}
		
	}

}
