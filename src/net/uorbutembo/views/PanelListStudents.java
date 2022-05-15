/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

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
	
	private JFileChooser fileChooser = new JFileChooser();
	private ExcelFileFilter filterExcel = new ExcelFileFilter();
	
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
		dialog.setLocationRelativeTo(mainWindow);
		dialog.setTitle("Fiche de payement");
		dialog.getContentPane().add(sheet, BorderLayout.CENTER);
	}
	
	/**
	 * Changement d'etat des boutons d'exportation des donnees
	 * @param enable
	 */
	private synchronized void statusButtonsExport (boolean enable) {
		btnToExcel.setEnabled(enable);
		btnToPdf.setEnabled(enable);
		btnToPrint.setEnabled(enable);
	}

	/**
	 * initialisation de l'intierface graphique,
	 * et ecote des evenement de l'interface graphique principale
	 */
	private void init() {
		right.add(navigation, BorderLayout.CENTER);
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
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		btnToExcel.addActionListener(event -> {
			Date now = new Date();
			File old = fileChooser.getCurrentDirectory();
			File newFile = new File(old.getAbsolutePath()+"/uor-data-manager-export-"+FormUtil.DEFAULT_FROMATER.format(now)+".xlsx");
			
			fileChooser.setFileFilter(filterExcel);
			fileChooser.setSelectedFile(newFile);
			
			int status = fileChooser.showSaveDialog(mainWindow);
			if(status != JFileChooser.APPROVE_OPTION)
				return;
			
			File file = fileChooser.getSelectedFile();
			if(file == null )
				return;
			
			String filename = file.getAbsolutePath()+ (file.getName().endsWith(".xlsx")?  "" : ".xlsx");
			navigation.wait(true);
			statusButtonsExport(false);
			setCursor(FormUtil.WAIT_CURSOR);
			
			Thread t = new Thread(()-> {
				datatableView.exportToExcel(filename, true);
				statusButtonsExport(true);
				navigation.wait(false);
				setCursor(Cursor.getDefaultCursor());
				
				int response = JOptionPane.showConfirmDialog(mainWindow, "Succès d'exportation des données\nau format excel dans le fichier \n"+filename+
						"\nVoulez-vous l'ouvrir?", "Ouvrir le fichier exporter", JOptionPane.YES_NO_OPTION);
				if(response == JOptionPane.OK_OPTION) {
					Runtime run = Runtime.getRuntime();
					try {
						run.exec("excel \""+filename+"\"");
					} catch (IOException e) {
						JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			t.start();
		});
		
		btnToPdf.addActionListener(event -> {
			JOptionPane.showMessageDialog(mainWindow, "Aucune implémentation d'exportation\n des données au format PDF", "Information", JOptionPane.WARNING_MESSAGE);
		});
		btnToPrint.addActionListener(event -> {
			JOptionPane.showMessageDialog(mainWindow, "Aucune implémentation d'envoie \ndu flux des données vers une imprimante", "Information", JOptionPane.WARNING_MESSAGE);
		});
		
		progress.setBorderPainted(false);
		progress.setStringPainted(true);
		progress.setVisible(false);
		statusButtonsExport(false);
		//==
		
		//search
		panelSearch.add(search, BorderLayout.CENTER);
		panelSearch.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		//==
		
		JScrollPane scroll = FormUtil.createVerticalScrollPane(panelData);
		scroll.setBorder(new LineBorder(FormUtil.BORDER_COLOR));
		
		listContainer.add(scroll, BorderLayout.CENTER);
		listContainer.add(box, BorderLayout.SOUTH);
		
		final Panel subCenter = new Panel(new BorderLayout());
		
		subCenter.add(listContainer, BorderLayout.CENTER);
		subCenter.setBorder(new EmptyBorder(0, 5, 0, 5));
		
		center.add(panelSearch, BorderLayout.NORTH);
		center.add(subCenter, BorderLayout.CENTER);
		
		this.add(split, BorderLayout.CENTER);
		
		split.setOneTouchExpandable(true);
		split.setDividerLocation(550);
		
		navigation.addListener(new NavigationListener() {		
			@Override
			public void onFilter(FacultyFilter [] filters) {
				if(datatableView == null) {
					onFilter(navigation.getCurrentYear(), filters);
				} else {
					statusButtonsExport(false);//disable exports buttons
					datatableView.setFilter(filters);
					statusButtonsExport(datatableView.hasData());//enable exports buttons if datatable has data match filters				
				}
			}
			
			@Override
			public void onFilter(AcademicYear year, FacultyFilter [] filters) {

				statusButtonsExport(false);//disable exports buttons
				if(datatableView == null) {
					datatableView = new StudentsDatatableView(mainWindow, progress, PanelListStudents.this);
					datatableView.firstLoad(filters, year);
					panelData.add(datatableView, BorderLayout.CENTER);
				} else {
					datatableView.firstLoad(filters, year);
				}
				statusButtonsExport(datatableView.hasData());//enable exports buttons if datatable has data match filter
			}
		});
	}
	
	@Override
	public void onAction(InscriptionDataRow row) {
		sheet.setInscription(row);
		dialog.setLocationRelativeTo(mainWindow);
		dialog.setVisible(true);
	}
	
	/**
	 * @author Esaie MUHASA
	 * Filtre de detection des fichiers excels
	 */
	private static class ExcelFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if(f.getName().endsWith(".xlsx"))
				return true;
			return f.isDirectory();
		}

		@Override
		public String getDescription() {
			return "Fichier excel 2007 ou plus (.xlsx)";
		}
		
	}
}
