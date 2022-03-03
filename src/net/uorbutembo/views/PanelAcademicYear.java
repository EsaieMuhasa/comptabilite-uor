/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;
import static net.uorbutembo.views.forms.FormUtil.createScrollPane;
import static net.uorbutembo.views.forms.FormUtil.createSubTitle;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.swing.Button;
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
	
	private Button btnAdd = new Button("Nouvelle année");
	
	public PanelAcademicYear(MainWindow mainWindow) {
		super(new BorderLayout());
		
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		config = new PanelConfigAcademicYear(mainWindow);
		
		final Panel center = new Panel(new BorderLayout());
		final Panel container = new Panel(new BorderLayout());
		final Panel menu = new Panel(new BorderLayout(DEFAULT_H_GAP, DEFAULT_V_GAP));
		final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menu, container);
		
		JScrollPane scroll = createScrollPane(listMenu);
		menu.add(scroll, BorderLayout.CENTER);
		menu.add(btnAdd, BorderLayout.SOUTH);
		menu.setBorder(new EmptyBorder(DEFAULT_H_GAP, DEFAULT_V_GAP, 0, DEFAULT_V_GAP));
		
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
		this.listMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		List<AcademicYear> years = this.academicYearDao.findAll();
		for (AcademicYear year : years)
			listModel.addElement(year);
		listMenu.setBackground(FormUtil.BKG_END);
		this.listMenu.setSelectedIndex(0);
		this.listMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && listMenu.getSelectedIndex() != -1) {
					AcademicYear year = listModel.get(listMenu.getSelectedIndex());
					title.setText(year.toString());
					config.setCurrentYear(year);
				}
			}
		});
		
		AcademicYear year = listModel.get(listMenu.getSelectedIndex());
		config.setCurrentYear(year);
		
		title.setText(year.toString());
		this.title.setOpaque(true);
		this.title.setBackground(FormUtil.BKG_START);
		
	}

}
