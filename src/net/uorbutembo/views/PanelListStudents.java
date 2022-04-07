/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.SearchField;
import net.uorbutembo.views.components.IndividualSheet;
import net.uorbutembo.views.components.SidebarStudents;
import net.uorbutembo.views.components.SidebarStudents.FacultyFilter;
import net.uorbutembo.views.components.SidebarStudents.NavigationListener;
import net.uorbutembo.views.components.StudentsDatatableView;
import net.uorbutembo.views.components.StudentsDatatableView.DatatableViewListener;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelListStudents extends Panel implements DatatableViewListener{
	private static final long serialVersionUID = 2483552038817065024L;

	private DAOFactory factory;
	private MainWindow mainWindow;
	
	private final Panel center  = new Panel(new BorderLayout());
	private final Panel right = new Panel(new BorderLayout());
	private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, right);
	private SidebarStudents navigation;
	
	private JProgressBar progress = new JProgressBar();
	
	//btn d'exportation
	private JButton btnToExcel = new JButton("Excel", new ImageIcon(R.getIcon("export")));
	private JButton btnToPdf = new JButton("PDF", new ImageIcon(R.getIcon("pdf")));
	private JButton btnToPrint = new JButton("Imprimer", new ImageIcon(R.getIcon("print")));
	//==
	
	private SearchField search = new SearchField("Recherche");
	private StudentsDatatableView datatableView;
	
	private JDialog dialog;//boite de dialogue de consultation des fiches de payements
	private IndividualSheet sheet;
	
	/**
	 * la construction du panel de manipulation de l'evolution des pyements
	 * une reference vers le frame principale est requise
	 * @param mainWindow
	 */
	public PanelListStudents(MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		factory = mainWindow.factory;
		navigation = new SidebarStudents(factory);
		init();
		
		this.sheet = new IndividualSheet(mainWindow);
		this.dialog = new JDialog(mainWindow, true);
		dialog.setSize(mainWindow.getWidth()-mainWindow.getWidth()/4, mainWindow.getHeight()-mainWindow.getHeight()/4);
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialog.setLocationRelativeTo(null);
		dialog.setTitle("Fiche de payement");
		dialog.getContentPane().add(sheet, BorderLayout.CENTER);
	}

	/**
	 * initialisation de l'intierface graphique,
	 * et ecote des evenement de l'interface graphique principale
	 */
	private void init() {
		right.add(navigation, BorderLayout.CENTER);
		JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		final Panel panelData = new Panel(new BorderLayout());
		final Panel panelSearch = new Panel(new BorderLayout());
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
		
		//search
		panelSearch.add(search, BorderLayout.CENTER);
		panelSearch.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		//==
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(panelData);
		scroll.setBorder(new LineBorder(FormUtil.BORDER_COLOR));
		
		listContainer.add(scroll, BorderLayout.CENTER);
		listContainer.add(box, BorderLayout.SOUTH);
		
		tabbed.addTab("Liste", new ImageIcon(R.getIcon("list")), listContainer, "Liste des états des compte des étudiants");
		tabbed.addTab("Graphique", new ImageIcon(R.getIcon("chart")), new Panel(), "Evolution des payements dans le temps");
		
		final Panel subCenter = new Panel(new BorderLayout());
		
		subCenter.add(tabbed, BorderLayout.CENTER);
		subCenter.setBorder(new EmptyBorder(0, 5, 0, 5));
		
		center.add(panelSearch, BorderLayout.NORTH);
		center.add(subCenter, BorderLayout.CENTER);
		
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
				datatableView.setFilter(filters);
				
				//enable exports buttons
				btnToExcel.setEnabled(true);
				btnToPdf.setEnabled(true);
				btnToPrint.setEnabled(true);
				//==
			}
			
			@Override
			public void onFilter(AcademicYear year, FacultyFilter [] filters) {
				
				//disable exports buttons
				btnToExcel.setEnabled(false);
				btnToPdf.setEnabled(false);
				btnToPrint.setEnabled(false);
				//==
				
				if(datatableView == null) {
					datatableView = new StudentsDatatableView(mainWindow, progress, PanelListStudents.this);
					datatableView.firstLoad(filters, year);
					panelData.add(datatableView, BorderLayout.CENTER);
				} else {
					datatableView.firstLoad(filters, year);
				}
				
				//enable exports buttons
				btnToExcel.setEnabled(true);
				btnToPdf.setEnabled(true);
				btnToPrint.setEnabled(true);
				//==
			}
		});
	}
	
	@Override
	public void onAction(InscriptionDataRow row) {
		sheet.setInscription(row);
		dialog.setVisible(true);
	}
}