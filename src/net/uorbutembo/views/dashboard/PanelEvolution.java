/**
 * 
 */
package net.uorbutembo.views.dashboard;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.dashboard.PanelNavigation.FacultyFilter;
import net.uorbutembo.views.dashboard.PanelNavigation.NavigationListener;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelEvolution extends Panel {
	private static final long serialVersionUID = 2483552038817065024L;
	
	private AcademicYear currentYear;
	private DAOFactory factory;
	
	private final Panel center  = new Panel(new BorderLayout());
	private final Panel right = new Panel(new BorderLayout());
	private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, right);
	private PanelNavigation navigation;
	
	private JProgressBar progress = new JProgressBar();
	
	//btn d'exportation
	private JButton btnToExcel = new JButton("Excel", new ImageIcon(R.getIcon("export")));
	private JButton btnToPdf = new JButton("PDF", new ImageIcon(R.getIcon("pdf")));
	private JButton btnToPrint = new JButton("Imprimer", new ImageIcon(R.getIcon("print")));
	//==
	
	private DatatableView datatableView;
	
	/**
	 * la construction du panel de manipulation de l'evolution des pyements
	 * une reference vers le frame principale est requise
	 * @param mainWindow
	 */
	public PanelEvolution(MainWindow mainWindow) {
		super(new BorderLayout());
		factory = mainWindow.factory;
		navigation = new PanelNavigation(factory);
		init();
		
		factory.findDao(AcademicYearDao.class).addYearListener(new DAOAdapter<AcademicYear>() {
			@Override
			public void onCurrentYear(AcademicYear year) {
				currentYear = year;
				navigation.setCurrentYear(year);
				navigation.reload();
			}
		});
	}

	/**
	 * initialisation de l'intierface graphique,
	 * et ecote des evenement de l'interface graphique principale
	 */
	private void init() {
		right.add(navigation, BorderLayout.CENTER);
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		final Panel panelData = new Panel(new BorderLayout());
		final Panel listContainer = new Panel(new BorderLayout());
		
		panelData.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		//btn expo
		Box box = Box.createHorizontalBox();
		
		box.add(progress);
		box.add(Box.createHorizontalGlue());
		box.add(btnToExcel);
		box.add(btnToPdf);
		box.add(btnToPrint);
		box.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		progress.setBorderPainted(false);
		progress.setStringPainted(true);
		//==
		
		listContainer.add(FormUtil.createVerticalScrollPane(panelData), BorderLayout.CENTER);
		listContainer.add(box, BorderLayout.SOUTH);
		
		tabbed.addTab("Liste", new ImageIcon(R.getIcon("list")), listContainer, "Liste des états des compte des étudiants");
		tabbed.addTab("Graphique", new ImageIcon(R.getIcon("chart")), new Panel(), "Evolution des payements dans le temps");
		
		
		center.add(tabbed, BorderLayout.CENTER);
		this.add(split, BorderLayout.CENTER);
		
		split.setOneTouchExpandable(true);
		split.setDividerLocation(550);
		
		navigation.addListener(new NavigationListener() {			
			@Override
			public void onFilter(FacultyFilter [] filters) {
				
				//disable exports buttons
				btnToExcel.setEnabled(false);
				btnToPdf.setEnabled(false);
				btnToPrint.setEnabled(false);
				//==
				
				if(datatableView == null) {
					datatableView = new DatatableView(factory, progress);
					datatableView.firstLoad(filters, currentYear);
					panelData.add(datatableView, BorderLayout.CENTER);
				} else {
					datatableView.setFilter(filters);
				}
				
				//enable exports buttons
				btnToExcel.setEnabled(true);
				btnToPdf.setEnabled(true);
				btnToPrint.setEnabled(true);
				//==
			}
		});
	}
}
