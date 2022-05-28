/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.createScrollPane;
import static net.uorbutembo.views.forms.FormUtil.createTitle;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.forms.FormAcademicYear;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicYear extends Panel {
	private static final long serialVersionUID = -119042779710985760L;
	
	private AcademicYearDao academicYearDao;
	private AcademicFeeDao academicFeeDao;
	private PromotionDao promotionDao;
	private AnnualSpendDao annualSpendDao;

	private PanelConfigAcademicYear config;
	
	private Navbar navbar = new Navbar();
	private JLabel title = createTitle("");
	
	private JButton btnAdd = new JButton(new ImageIcon(R.getIcon("plus")));
	private DefaultComboBoxModel<AcademicYear> comboYearModel = new DefaultComboBoxModel<>();
	private JComboBox<AcademicYear> comboYear = new JComboBox<>(comboYearModel);
	
	private JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private JMenuItem itemUpdate = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private JMenuItem itemCreate = new JMenuItem("Nouvelle année", new ImageIcon(R.getIcon("plus")));
	private JPopupMenu popupMenu = new JPopupMenu();
	
	private JDialog dialogYear;
	private FormAcademicYear formYear;
	private final DAOAdapter<AcademicYear> yearListener = new DAOAdapter<AcademicYear>() {
		@Override
		public void onCreate(AcademicYear e, int requestId) {
			if(dialogYear != null) 
				dialogYear.setVisible(false);
		}
		
		@Override
		public void onUpdate(AcademicYear e, int requestId) {
			if(dialogYear != null) 
				dialogYear.setVisible(false);
			
			if(comboYear.getSelectedIndex() != -1 && comboYearModel.getElementAt(comboYear.getSelectedIndex()).getId() == e.getId())
				title.setText(e.toString());
			
			for (int i = 0, count = comboYearModel.getSize(); i < count; i++) {
				if(comboYearModel.getElementAt(i).getId() == e.getId()) {
					comboYearModel.getElementAt(i);
					break;
				}
			}
		}
		
		@Override
		public void onDelete(AcademicYear e, int requestId) {				
			reload();
		}
		
		@Override
		public synchronized void onCurrentYear(AcademicYear year) {
			reload();
		}
	};
	
	private AcademicYear oldYear;//annee derierement selectionner
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);

		academicYearDao.addListener(yearListener);
		academicYearDao.addYearListener(yearListener);

		config = new PanelConfigAcademicYear(mainWindow);
		
		final Panel center = new Panel(new BorderLayout());
		final Panel container = new Panel(new BorderLayout());
		final Panel menu = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		final Panel menuTop = new Panel(new BorderLayout());
		
		JScrollPane scroll = createScrollPane(comboYear);
		menu.add(scroll, BorderLayout.CENTER);
		menu.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		navbar.createGroup("default", config.getNavbarItems(), config);
		navbar.showGroup("default");
		
		Box box = Box.createHorizontalBox();
		comboYear.setPreferredSize(new Dimension(150, 25));
		box.add(comboYear);
		//box.add(btnAdd);
		box.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		center.add(navbar, BorderLayout.NORTH);
		center.add(config, BorderLayout.CENTER);
		
		menuTop.add(title, BorderLayout.CENTER);
		menuTop.add(box, BorderLayout.EAST);
		
		container.add(menuTop, BorderLayout.NORTH);
		container.add(center, BorderLayout.CENTER);
		
		this.add(container, BorderLayout.CENTER);
		this.init();
		btnAdd.addActionListener(event -> {
			if(dialogYear == null) {
				Thread t = new Thread(() -> {
					setCursor(FormUtil.WAIT_CURSOR);
					createDialog(mainWindow);
					setCursor(Cursor.getDefaultCursor());
					dialogYear.setVisible(true);
				});
				t.start();
			} else {
				dialogYear.setVisible(true);
			}
		});
		
		itemCreate.addActionListener(event-> {
			btnAdd.doClick();
		});
		
		itemDelete.addActionListener(event -> {
			AcademicYear year = comboYearModel.getElementAt(comboYear.getSelectedIndex());
			if(annualSpendDao.checkByAcademicYear(year.getId()) || promotionDao.checkByAcademicYear(year.getId())
					|| academicFeeDao.checkByAcademicYear(year.getId() )) {
				String message = "Impossible de supprimer l'année "+year.getLabel()+",\ncar autres données de la base de données \nsont liée à cette occurence.";
				message += "\nEn plus les suppressions récursives ne sont pas pris en charge";
				JOptionPane.showMessageDialog(mainWindow, message, "Echec de suppression", JOptionPane.ERROR_MESSAGE);
			} else {
				academicYearDao.delete(year.getId());
			}
		});
		
		itemUpdate.addActionListener(event -> {
			AcademicYear year = comboYearModel.getElementAt(comboYear.getSelectedIndex());
			
			if(dialogYear == null) {
				Thread t = new Thread(() -> {
					setCursor(FormUtil.WAIT_CURSOR);
					createDialog(mainWindow);
					formYear.setAcademicYear(year);
					setCursor(Cursor.getDefaultCursor());
					dialogYear.setVisible(true);
				});
				t.start();
			} else {
				formYear.setAcademicYear(year);
				dialogYear.setVisible(true);
			}
		});
	}
	
	/**
	 * Cereation du boite de dialogue permetant de 
	 * declarer/modifier une annee academique
	 * @param parent
	 */
	private synchronized void createDialog(MainWindow parent) {
		if(dialogYear != null)
			return;
		
		dialogYear = new JDialog(parent, true);
		formYear = new FormAcademicYear(parent);
		
		dialogYear.setTitle("Année académique");
		dialogYear.getContentPane().add(formYear, BorderLayout.CENTER);
		dialogYear.getContentPane().setBackground(FormUtil.BKG_START);
		
		dialogYear.pack();
		dialogYear.setLocationRelativeTo(parent);
	}
	
	private void init() {		
		this.comboYear.addItemListener(event -> {			
			int index = comboYear.getSelectedIndex();
			AcademicYear year = (index == -1)? null : comboYearModel.getElementAt(index);
			if (oldYear != year){
				oldYear = year;
				setSelectedYear(year);
			}
		});
		
		//popupMenu
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
		popupMenu.addSeparator();
		popupMenu.add(itemCreate);
		
		comboYear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased (MouseEvent e) {
				if(e.isPopupTrigger()) {
					itemUpdate.setEnabled(comboYear.getSelectedIndex() != -1);
					itemDelete.setEnabled(comboYear.getSelectedIndex() != -1);
					popupMenu.show(comboYear, e.getX(), e.getY());
				}
			}
		});
		//==
	}
	
	/**
	 * Selection d'une annee academique
	 * @param year
	 */
	private void setSelectedYear (AcademicYear year) {
		setCursor(FormUtil.WAIT_CURSOR);
		config.setCursor(FormUtil.WAIT_CURSOR);
		comboYear.setEnabled(false);
		Thread t = new Thread(() -> {
			title.setText(year != null? year.toString() : "");
			config.setCurrentYear(year);
			config.setCursor(Cursor.getDefaultCursor());
			setCursor(Cursor.getDefaultCursor());
			comboYear.setEnabled(true);
		});
		t.start();
	}
	
	/**
	 * Actalisation des donnees du menu
	 */
	private synchronized void reload() {
		comboYearModel.removeAllElements();
		
		List<AcademicYear> years = academicYearDao.countAll() != 0? this.academicYearDao.findAll() : new ArrayList<>();
		for (AcademicYear year : years)
			comboYearModel.addElement(year);
		
		if(!years.isEmpty())
			comboYear.setSelectedIndex(0);
	}

}
