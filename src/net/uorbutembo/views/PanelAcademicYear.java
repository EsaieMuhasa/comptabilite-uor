/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormAcademicYear;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicYear extends Panel implements YearChooserListener{
	private static final long serialVersionUID = -119042779710985760L;
	
	private AcademicYearDao academicYearDao;
	private AcademicFeeDao academicFeeDao;
	private PromotionDao promotionDao;
	private AnnualSpendDao annualSpendDao;

	private PanelConfigAcademicYear config;
	
	private Navbar navbar = new Navbar();
	
	private JButton btnAdd = new JButton(new ImageIcon(R.getIcon("plus")));

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
	};
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
		promotionDao = mainWindow.factory.findDao(PromotionDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);

		academicYearDao.addListener(yearListener);
		academicYearDao.addYearListener(yearListener);
		
		mainWindow.getSidebar().addYearChooserListener(this);

		config = new PanelConfigAcademicYear(mainWindow);
		
		final Panel center = new Panel(new BorderLayout());
		final Panel container = new Panel(new BorderLayout());
		final Panel menu = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		
		menu.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		navbar.createGroup("default", config.getNavbarItems(), config);
		navbar.showGroup("default");
		
		center.add(navbar, BorderLayout.NORTH);
		center.add(config, BorderLayout.CENTER);
		
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
			AcademicYear year = new AcademicYear();
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
			AcademicYear year = new AcademicYear();
			
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
		//popupMenu
		popupMenu.add(itemUpdate);
		popupMenu.add(itemDelete);
		popupMenu.addSeparator();
		popupMenu.add(itemCreate);
		//==
	}

	@Override
	public void onChange(AcademicYear year) {
		setCursor(FormUtil.WAIT_CURSOR);
		config.setCursor(FormUtil.WAIT_CURSOR);
		config.setCurrentYear(year);
		config.setCursor(Cursor.getDefaultCursor());
		setCursor(Cursor.getDefaultCursor());
	}

}
