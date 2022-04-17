/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.createScrollPane;
import static net.uorbutembo.views.forms.FormUtil.createSubTitle;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

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
	
	private DefaultListModel<AcademicYear> listModel = new DefaultListModel<>();
	private PanelConfigAcademicYear config;
	private JList<AcademicYear> listMenu = new JList<>(listModel);
	
	private Navbar navbar = new Navbar();
	private JLabel title = createSubTitle("");
	
	private JButton btnAdd = new JButton("Nouvelle année");
	
	private JMenuItem itemDelete = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
	private JMenuItem itemUpdate = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
	private JMenuItem itemCreate = new JMenuItem("Nouvelle année", new ImageIcon(R.getIcon("plus")));
	private JPopupMenu popupMenu = new JPopupMenu();
	
	private JDialog dialogYear;
	private FormAcademicYear formYear;
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		
		final DAOAdapter<AcademicYear> yearListener = new DAOAdapter<AcademicYear>() {
			@Override
			public void onCreate(AcademicYear e, int requestId) {
				if(dialogYear != null) 
					dialogYear.setVisible(false);
			}
			
			@Override
			public void onUpdate(AcademicYear e, int requestId) {
				if(dialogYear != null) 
					dialogYear.setVisible(false);
				
				if(listMenu.getSelectedIndex() != -1 && listModel.get(listMenu.getSelectedIndex()).getId() == e.getId())
					title.setText(e.toString());
				
				for (int i = 0, count = listModel.getSize(); i < count; i++) {
					if(listModel.get(i).getId() == e.getId()) {
						listModel.set(i, e);
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
		
		academicYearDao.addListener(yearListener);
		academicYearDao.addYearListener(yearListener);

		config = new PanelConfigAcademicYear(mainWindow);
		
		final Panel center = new Panel(new BorderLayout());
		final Panel container = new Panel(new BorderLayout());
		final Panel menu = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menu, container);
		
		JScrollPane scroll = createScrollPane(listMenu);
		menu.add(scroll, BorderLayout.CENTER);
		menu.add(btnAdd, BorderLayout.SOUTH);
		menu.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		navbar.createGroup("default", config.getNavbarItems(), config);
		navbar.showGroup("default");
		
		center.add(navbar, BorderLayout.NORTH);
		center.add(config, BorderLayout.CENTER);
		
		container.add(title, BorderLayout.NORTH);
		container.add(center, BorderLayout.CENTER);
		
		this.add(split, BorderLayout.CENTER);
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
			AcademicYear year = listModel.get(listMenu.getSelectedIndex());
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
			AcademicYear year = listModel.get(listMenu.getSelectedIndex());
			
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
		formYear = new FormAcademicYear(parent, academicYearDao);
		
		dialogYear.setTitle("Année académique");
		dialogYear.getContentPane().add(formYear, BorderLayout.CENTER);
		dialogYear.getContentPane().setBackground(FormUtil.BKG_START);
		
		dialogYear.pack();
		dialogYear.setLocationRelativeTo(parent);
	}
	
	private void init() {
		listMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listMenu.setBackground(FormUtil.BKG_END);
		listMenu.setForeground(FormUtil.BORDER_COLOR);
		
		this.listMenu.addListSelectionListener(event ->{
			int index = listMenu.getSelectedIndex();
			AcademicYear year = (index == -1)? null : listModel.get(index);
			setSelectedYear(year);
		});
		
		title.setOpaque(true);
		title.setBackground(FormUtil.BKG_START);
		
		//popupMenu
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
		popupMenu.addSeparator();
		popupMenu.add(itemCreate);
		
		listMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased (MouseEvent e) {
				if(e.isPopupTrigger()) {
					itemUpdate.setEnabled(listMenu.getSelectedIndex() != -1);
					itemDelete.setEnabled(listMenu.getSelectedIndex() != -1);
					popupMenu.show(listMenu, e.getX(), e.getY());
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
		listMenu.setEnabled(false);
		Thread t = new Thread(() -> {
			title.setText(year != null? year.toString() : "");
			config.setCurrentYear(year);
			config.setCursor(Cursor.getDefaultCursor());
			setCursor(Cursor.getDefaultCursor());
			listMenu.setEnabled(true);
		});
		t.start();
	}
	
	/**
	 * Actalisation des donnees du menu
	 */
	private synchronized void reload() {
		listModel.removeAllElements();
		
		List<AcademicYear> years = academicYearDao.countAll() != 0? this.academicYearDao.findAll() : new ArrayList<>();
		for (AcademicYear year : years)
			listModel.addElement(year);
		
		if(!years.isEmpty())
			this.listMenu.setSelectedIndex(0);
	}

}
