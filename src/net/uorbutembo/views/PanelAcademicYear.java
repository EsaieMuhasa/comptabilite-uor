/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.createScrollPane;
import static net.uorbutembo.views.forms.FormUtil.createSubTitle;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAcademicYear extends Panel {
	private static final long serialVersionUID = -119042779710985760L;
	
	private AcademicYearDao academicYearDao;
	private DefaultListModel<AcademicYear> listModel = new DefaultListModel<>();
	private PanelConfigAcademicYear config;
	private JList<AcademicYear> listMenu = new JList<>(listModel);
	
	private Navbar navbar = new Navbar();
	private JLabel title = createSubTitle("");
	
	private JButton btnAdd = new JButton("Nouvelle année");
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicYearDao.addListener(new DAOAdapter<AcademicYear>() {
		});
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
	}
	
	
	private void init() {
		listMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listMenu.setBackground(FormUtil.BKG_END);
		listMenu.setForeground(FormUtil.BORDER_COLOR);
		List<AcademicYear> years = this.academicYearDao.countAll() != 0? this.academicYearDao.findAll() : new ArrayList<>();
		for (AcademicYear year : years)
			listModel.addElement(year);
		
		this.listMenu.addListSelectionListener(event ->{
			int index = listMenu.getSelectedIndex();
			AcademicYear year = (index == -1)? null : listModel.get(index);
			title.setText(year != null? year.toString() : "");
			config.setCurrentYear(year);
		});
		
		if(!years.isEmpty())
			this.listMenu.setSelectedIndex(0);
		
		title.setOpaque(true);
		title.setBackground(FormUtil.BKG_START);
	}

}
