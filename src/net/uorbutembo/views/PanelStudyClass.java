/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
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
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormStudyClass;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.StudyClassTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelStudyClass extends Panel {
	private static final long serialVersionUID = 1827370862407233020L;

	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Nouvelle classe d'étude");
	private Dialog formDialog;
	private FormStudyClass form;
	private final Table table;
	private final StudyClassTableModel tableModel;
	
	private StudyClassDao studyClassDao;
	private PromotionDao promotionDao;
	
	private final JMenuItem itemUpdate = new  JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private final JMenuItem itemDelete = new  JMenuItem("Suprimer", new ImageIcon(R.getIcon("close")));
	private final JPopupMenu popupMenu = new JPopupMenu();
	
	private final MainWindow mainWindow;
	private final DAOAdapter<StudyClass> classAdapter = new DAOAdapter<StudyClass>() {
		@Override
		public void onCreate(StudyClass e, int requestId) {
			formDialog.setVisible(false);
		}
		
		@Override
		public void onUpdate(StudyClass e, int requestId) {
			formDialog.setVisible(false);
		}
	};
	
	public PanelStudyClass(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		
		studyClassDao.addListener(classAdapter);
		
		Panel top = new Panel(new BorderLayout());
		top.add(btnNew, BorderLayout.EAST);
		top.add(FormUtil.createTitle("Classes d'étude"), BorderLayout.CENTER);
		
		btnNew.setEnabled(false);
		btnNew.addActionListener(event -> {
			createDialog();
			this.formDialog.setTitle("Enregistrement d'une nouvelle classe d'étude");
			this.formDialog.setVisible(true);
		});
		
		final Panel center = new Panel(new BorderLayout());
		tableModel = new StudyClassTableModel(this.studyClassDao);
		table = new Table(tableModel);
		table.setShowVerticalLines(true);
		table.getColumnModel().getColumn(0).setWidth(130);
		table.getColumnModel().getColumn(0).setMinWidth(130);
		table.getColumnModel().getColumn(0).setMaxWidth(130);
		table.getColumnModel().getColumn(0).setResizable(false);
		
		center.add(new TablePanel(table, "Liste des classes d'étude", false), BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		initPopup();
	}
	
	public synchronized void load () {
		tableModel.reload();
		btnNew.setEnabled(true);
	}
	
	private void createDialog() {
		if(formDialog == null) {
			form = new FormStudyClass(mainWindow);
			formDialog = new Dialog(mainWindow);
			formDialog.getContentPane().add(this.form, BorderLayout.CENTER);
			formDialog.pack();
			formDialog.setSize(600, formDialog.getHeight());
			formDialog.setResizable(false);
		}
		formDialog.setLocationRelativeTo(mainWindow);
	}
	
	private void initPopup() {
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
					popupMenu.show(table, e.getX(), e.getY());
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
