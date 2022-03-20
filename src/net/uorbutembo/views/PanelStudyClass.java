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

import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.forms.FormStudyClass;
import net.uorbutembo.views.models.StudyClassTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelStudyClass extends Panel {
	private static final long serialVersionUID = 1827370862407233020L;

	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter une classe d'étude");
	private Dialog formDialog;
	private FormStudyClass form;
	private final Table table;
	private final StudyClassTableModel tableModel;
	
	private StudyClassDao studyClassDao;
	private PromotionDao promotionDao;
	
	private final JMenuItem itemUpdate = new  JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private final JMenuItem itemDelete = new  JMenuItem("Suprimer", new ImageIcon(R.getIcon("close")));
	private final JPopupMenu popupMenu = new JPopupMenu();
	
	private MainWindow mainWindow;
	
	/**
	 * 
	 */
	public PanelStudyClass(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		this.promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		
		this.studyClassDao.addListener(new DAOAdapter<StudyClass>() {
			@Override
			public void onCreate(StudyClass e, int requestId) {
				formDialog.setVisible(false);
			}
			
			@Override
			public void onUpdate(StudyClass e, int requestId) {
				formDialog.setVisible(false);
			}
		});
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		this.add(top, BorderLayout.NORTH);
		
		this.btnNew.addActionListener(event -> {
			createDialog();
			this.formDialog.setTitle("Enregistrement d'une nouvelle classe d'étude");
			this.formDialog.setVisible(true);
		});
		
		Panel center = new Panel(new BorderLayout());
		tableModel = new StudyClassTableModel(this.studyClassDao);
		table = new Table(tableModel);
		
		center.add(table.getTableHeader(), BorderLayout.NORTH);
		center.add(table, BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		this.add(center, BorderLayout.CENTER);
		
		initPopup();
	}
	
	private void createDialog() {
		if(formDialog == null) {
			form = new FormStudyClass(mainWindow, this.studyClassDao);
			this.formDialog = new Dialog(mainWindow);
			this.formDialog.getContentPane().add(this.form, BorderLayout.CENTER);
			this.formDialog.setSize(600, 270);
		}
		this.formDialog.setLocationRelativeTo(mainWindow);
	}
	
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
			StudyClass sc = tableModel.getRow(table.getSelectedRow());
			try {						
				if(promotionDao.checkByStudyClass(sc.getId())) {
					JOptionPane.showMessageDialog(null, "Impossible de supprimer la classe d'étude '"+sc.toString()+"', \ncar certains promotions y font references", "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				} else {
					int status = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cette classe d'étude?\n=>"+sc.toString(), "Supression d'une classe d'étude", JOptionPane.YES_NO_OPTION);
					if(status == JOptionPane.OK_OPTION) {
						studyClassDao.delete(sc.getId());
					}
				}
			} catch (DAOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		});
		
		itemUpdate.addActionListener(event -> {
			createDialog();
			form.setStudyClass(tableModel.getRow(table.getSelectedRow()));
			formDialog.setTitle("Modification de la description d'une classe d'étude");
			formDialog.setVisible(true);
		});
		
	}

}
