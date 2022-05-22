/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;
import static net.uorbutembo.views.forms.FormUtil.createTitle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.SearchField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.IndividualSheet;
import net.uorbutembo.views.components.Navbar;
import net.uorbutembo.views.components.NavbarButtonModel;
import net.uorbutembo.views.components.SidebarStudents;
import net.uorbutembo.views.components.SidebarStudents.DepartmentFilter;
import net.uorbutembo.views.components.SidebarStudents.FacultyFilter;
import net.uorbutembo.views.components.SidebarStudents.NavigationListener;
import net.uorbutembo.views.components.StudentsDatatableView;
import net.uorbutembo.views.components.StudentsDatatableView.DatatableViewListener;
import net.uorbutembo.views.forms.FormInscription;
import net.uorbutembo.views.forms.FormReRegister;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 * 
 */
public class PanelStudents extends DefaultScenePanel implements NavigationListener, DatatableViewListener{
	private static final long serialVersionUID = -356861410803019685L;
	
	private final Panel center  = new Panel(new BorderLayout());
	private final Panel right = new Panel(new BorderLayout());
	private final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, center, right);

	//dao
	private InscriptionDao inscriptionDao;
	private StudentDao studentDao;
	//==dao

	//espace de traival
	private final WorkspaceTabbedPanel workspaceTabbedPanel;
	private final TabbedPanelContainer container;
	private SidebarStudents navigation;
	
	//btn d'exportation
	private final JButton btnToExcel = new JButton("Excel", new ImageIcon(R.getIcon("export")));
	private final JButton btnToPdf = new JButton("PDF", new ImageIcon(R.getIcon("pdf")));
	private final JButton btnToPrint = new JButton("Imprimer", new ImageIcon(R.getIcon("print")));
	//==
	
	private final JProgressBar progress = new JProgressBar();
	private TabDatatablePanel datatablePanel;
	private TabDatachartPanel datachartPanel;
	
	private JDialog dialogSheet;//boite de dialogue de consultation des fiches de payements
	private IndividualSheet sheet;
	
	private JFileChooser fileChooser = new JFileChooser();
	private ExcelFileFilter filterExcel = new ExcelFileFilter();
	
	private FormInscription formInscription;
	private FormReRegister formRegister;
	
	private JDialog dialogInscription;
	private JDialog dialogRegister;

	public PanelStudents(MainWindow mainWindow) {
		super("Etudiants", new ImageIcon(R.getIcon("student")), mainWindow, false);//la scene gere les scrollbars	
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		studentDao = mainWindow.factory.findDao(StudentDao.class);
		
		navigation = new SidebarStudents(mainWindow.factory);
		
		container = new TabbedPanelContainer(mainWindow);
		datatablePanel = new TabDatatablePanel();
		datachartPanel = new TabDatachartPanel(mainWindow.factory);
		
		container
			.addItemMenu(new NavbarButtonModel("list", "Liste"), datatablePanel)
			.addItemMenu(new NavbarButtonModel("chart", "Graphique"), datachartPanel);
		
		workspaceTabbedPanel = new WorkspaceTabbedPanel(container);
		navigation.addListener(this);
		
		center.add(workspaceTabbedPanel, BorderLayout.CENTER);
		right.add(navigation, BorderLayout.CENTER);
		
		getBody().add(split);
		
		initSplitPanel();
		initButtonsExport();
	}
	
	/**
	 * Utilitaire de creation d'une boite de dialogue d'inscription
	 */
	private void createDialogueInscription() {
		if(dialogInscription  != null)
			return;
		
		final JPanel inscriptionFormPanel = new JPanel(new BorderLayout());
		formInscription = new FormInscription(mainWindow, inscriptionDao, studentDao);
				
		inscriptionFormPanel.setBackground(FormUtil.BKG_DARK);
		inscriptionFormPanel.add(formInscription, BorderLayout.CENTER);
		inscriptionFormPanel.setBorder(BODY_BORDER);
		final JScrollPane inscriptionFormScroll = FormUtil.createVerticalScrollPane(inscriptionFormPanel);
		
		dialogInscription = new JDialog(mainWindow, "Inscription d'un nouveau étudiant", true);
		dialogInscription.setContentPane(inscriptionFormScroll);
		dialogInscription.pack();
		dialogInscription.setSize(900, dialogInscription.getHeight());
		dialogInscription.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		formInscription.setCurrentYear(navigation.getCurrentYear());
	}
	
	/**
	 * utilitaire de creation du boite de dialogue de re-inscription
	 */
	private void createDialogueReRegister () {
		if(dialogRegister != null)
			return;
		
		final JPanel registerFormPanel = new JPanel(new BorderLayout());
		formRegister = new FormReRegister(mainWindow, inscriptionDao, studentDao, true);
		
		registerFormPanel.setBackground(FormUtil.BKG_DARK);
		registerFormPanel.add(formRegister, BorderLayout.NORTH);
		registerFormPanel.setBorder(BODY_BORDER);
		final JScrollPane registerFormScroll = FormUtil.createVerticalScrollPane(registerFormPanel);
		
		dialogRegister = new JDialog(mainWindow, "Ré-inscription d'un étudiant", true);
		dialogRegister.setContentPane(registerFormScroll);
		dialogRegister.pack();
		dialogRegister.setSize(700, dialogRegister.getHeight());
		dialogRegister.setResizable(false);
		dialogRegister.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		formRegister.setCurrentYear(navigation.getCurrentYear());
	}
	
	/**
	 * Creation du boite de dialogue qui contiens la fiche individuel
	 * de payement
	 */
	protected void createDialogIndiviualSheet() {
		if (sheet != null)
			return;
		
		sheet = new IndividualSheet(mainWindow);
		dialogSheet = new JDialog(mainWindow,"Fiche de payement", true);
		dialogSheet.setSize(mainWindow.getWidth()-mainWindow.getWidth()/4, mainWindow.getHeight()-mainWindow.getHeight()/4);
		dialogSheet.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		dialogSheet.getContentPane().add(sheet, BorderLayout.CENTER);
	}
	
	/**
	 * initialisation des boutons 
	 */
	private void initButtonsExport() {
		//btn expo
		Box box = Box.createHorizontalBox();
		
		box.add(progress);
		box.add(Box.createHorizontalGlue());
		box.add(btnToExcel);
		box.add(btnToPdf);
		box.add(btnToPrint);
		box.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		center.add(box, BorderLayout.SOUTH);
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		
		progress.setBorderPainted(false);
		progress.setStringPainted(true);
		progress.setVisible(false);
		statusButtonsExport(false);
		//==
				
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
				datatablePanel.getDatatableView().exportToExcel(filename, true);
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
	}
	
	private void initSplitPanel() {
		split.setOneTouchExpandable(true);
		split.setDividerLocation(550);
	}
	
	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public String getNikeName() {
		return "students";
	}

	
	@Override
	public void onFilter (FacultyFilter[] filters) {
		if(datatablePanel.getDatatableView() == null) {
			onFilter(navigation.getCurrentYear(), filters);
		} else {
			statusButtonsExport(false);//disable exports buttons
			datatablePanel.getDatatableView().setFilter(filters);
			datachartPanel.reload(filters);
			statusButtonsExport(datatablePanel.getDatatableView().hasData());//enable exports buttons if datatable has data match filters				
		}
	}

	@Override
	public void onFilter(AcademicYear year, FacultyFilter[] filters) {
		workspaceTabbedPanel.setTitle(year.toString());
		
		statusButtonsExport(false);//disable exports buttons
		if(datatablePanel.getDatatableView() == null) {
			datatablePanel.init();
			datatablePanel.getDatatableView().firstLoad(filters, year);
		} else {
			datatablePanel.getDatatableView().firstLoad(filters, year);
		}
		datachartPanel.reload(year, filters);
		statusButtonsExport(datatablePanel.getDatatableView().hasData());//enable exports buttons if datatable has data match filter
	}

	@Override
	public void onInscription(ActionEvent event) {
		createDialogueInscription();
		dialogInscription.setLocationRelativeTo(mainWindow);
		dialogInscription.setVisible(true);
	}

	@Override
	public void onReinscription(ActionEvent event) {
		createDialogueReRegister();
		dialogRegister.setLocationRelativeTo(mainWindow);
		dialogRegister.setVisible(true);
	}
	
	@Override
	public void onAction(InscriptionDataRow row) {
		createDialogIndiviualSheet();
		sheet.setInscription(row);
		dialogSheet.setLocationRelativeTo(mainWindow);
		dialogSheet.setVisible(true);
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
	 * Tabbed panel personnaliser.
	 * conteneur de la scene
	 * @author Esaie MUHASA
	 */
	private static final class WorkspaceTabbedPanel extends Panel{
	
		private static final long serialVersionUID = -6016455453034213438L;
		
		private final Navbar navbar = new Navbar();
		private final JLabel title = createTitle("");
		private final SearchField search = new SearchField("Recherche");

		public WorkspaceTabbedPanel(TabbedPanelContainer container) {
			super(new BorderLayout());
			navbar.createGroup(container.getNikeName(), container.getNavbarItems(), container);
			navbar.showGroup(container.getNikeName());
			
			final Panel top = new Panel(new BorderLayout());
			final Panel titlePanel = new Panel(new BorderLayout());
			
			title.setFont(new Font("Arial", Font.BOLD, 30));
			titlePanel.add(title, BorderLayout.WEST);
			titlePanel.add(search, BorderLayout.CENTER);
			titlePanel.setBorder(new EmptyBorder(5, 10, 5, 10));
			
			top.add(navbar, BorderLayout.CENTER);
			top.add(titlePanel, BorderLayout.NORTH);
			
			add(top, BorderLayout.NORTH);
			add(container, BorderLayout.CENTER);
		}
		
		/**
		 * Modification du titre du panel
		 * @param title
		 */
		void setTitle (String title) {
			this.title.setText(title);
		}
	}
	
	/**
	 * Conteneur du tabbed panel personnaliser
	 * @author Esaie MUHASA
	 */
	private static final class TabbedPanelContainer extends DefaultScenePanel{
		private static final long serialVersionUID = 5210286438716898111L;

		public TabbedPanelContainer(MainWindow mainWindow) {
			super("", new ImageIcon(), mainWindow, false);
		}

		@Override
		public String getNikeName() {
			return "default";
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			
			g2.setColor(FormUtil.BORDER_COLOR);
			g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
		}
		
	}
	
	/**
	 * Panel conteneur du datatable
	 * @author Esaie MUHASA
	 */
	private final class TabDatatablePanel extends Panel{
		private static final long serialVersionUID = 6626468845004012912L;
		
		private final Panel datatableContainer = new Panel(new BorderLayout());
		private StudentsDatatableView datatableView;
		
		public TabDatatablePanel() {
			super(new BorderLayout());
			
			datatableContainer.setBorder(new EmptyBorder(0, 0, 0, 5));
			final Panel panelPadding = new Panel(new BorderLayout());
			panelPadding.setBorder(new EmptyBorder(0, 5, 1, 0));
			panelPadding.add(FormUtil.createVerticalScrollPane(datatableContainer));
			add(panelPadding, BorderLayout.CENTER);
		}

		/**
		 * @return the datatableView
		 */
		public StudentsDatatableView getDatatableView() {
			return datatableView;
		}
		
		/**
		 * Initialisation du datatableView
		 */
		public void init() {
			if(datatableView != null)
				return;
			
			datatableView = new StudentsDatatableView(mainWindow, progress, PanelStudents.this);
			datatableContainer.add(datatableView, BorderLayout.CENTER);
		}
		
	}
	
	private static final class TabDatachartPanel extends Panel {
		private static final long serialVersionUID = -1438881851640344547L;
		
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel);
		private final Panel panelRadios = new Panel();
		
		private int filterIndex = 3;
		private final JRadioButton [] radios = {
				new JRadioButton("Faculté"),
				new JRadioButton("Département"),
				new JRadioButton("Classe d'étude"),
				new JRadioButton("Promotion", true)
		};
		
		/**
		 * Ecouteur de boutons radions de classification des parts
		 */
		private final ChangeListener radioListener = (event) -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if(index == filterIndex)
				return;
			
			filterIndex = index;
			new Thread( () -> { reload(filterIndex); } ).start();
		};
		
		
		{//groupement des boutons radio et ecoute de leurs changement d'etat
			final ButtonGroup group = new ButtonGroup();
			
			for (int i = 0; i < radios.length; i++) {
				JRadioButton radio = radios[i];
				panelRadios.add(radio);
				radio.setName(i+"");
				group.add(radio);
				radio.setEnabled(false);
				radio.setForeground(Color.WHITE);
				radio.addChangeListener(radioListener);
			}

			pieModel.setRealMaxPriority(true);
		}
		
		private final PromotionDao promotionDao;
		private final InscriptionDao inscriptionDao;
		
		private AcademicYear currentYear;//annee selectionner
		private FacultyFilter [] rootFilters;//filtre racine
		private FacultyFilter [] lastFilters;//dernier filtre
		
		/**
		 * Construction de l'utilitaire de controle/visuation du pie chart
		 * @param factory
		 */
		public TabDatachartPanel (DAOFactory factory) {
			super(new BorderLayout());
			promotionDao = factory.findDao(PromotionDao.class);
			inscriptionDao = factory.findDao(InscriptionDao.class);

			add(panelRadios, BorderLayout.SOUTH);
			add(piePanel, BorderLayout.CENTER);
		}
		
		/**
		 * initialisation complete des models
		 * @param year
		 * @param filters
		 */
		public void reload (AcademicYear year, FacultyFilter [] filters) {
			currentYear = year;
			rootFilters = filters;
			reload(null);
		}
		
		/**
		 * filtrage des models deja initialiser
		 * @param filters
		 */
		public void reload(FacultyFilter [] filters) {
			lastFilters = filters;
			try {				
				reload(filterIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * chargement des parts (mis en jours du model)
		 * @param group
		 */
		private synchronized void reload (int group) {
			
			for (int i = 0; i < radios.length; i++){
				radios[i].removeChangeListener(radioListener);
				radios[i].setEnabled(false);
			}
			
			pieModel.removeAll();
			
			FacultyFilter [] filters = lastFilters == null? rootFilters : lastFilters;
			
			switch (group) {
				case 0:{//par faculte
					for (int i = 0; i < filters.length; i++) {
						FacultyFilter filter = filters[i];
						int count = 0;
						
						for(int j = 0, countDepartment = filter.getDepartments().size(); j < countDepartment; j++) {
							DepartmentFilter d = filter.getDepartments().get(j);
							
							for (int k = 0, countClasses = d.getClasses().size(); k < countClasses; k++) {
								Promotion p = promotionDao.find(currentYear, d.getDepartment(), d.getClasses().get(k));
								count += inscriptionDao.countByPromotion(p);
							}
						}
						
						Color color = COLORS[i % COLORS.length];
						DefaultPiePart part = new DefaultPiePart(color, count, filter.getFaculty().toString());
						part.setData(filter.getFaculty());
						pieModel.addPart(part);
					}
				}break;
				
				case 1:{//par departement
					for (int i = 0; i < filters.length; i++) {
						FacultyFilter filter = filters[i];
						
						for(int j = 0, countDepartment = filter.getDepartments().size(); j < countDepartment; j++) {
							DepartmentFilter d = filter.getDepartments().get(j);
							int count = 0;
							
							for (int k = 0, countClasses = d.getClasses().size(); k < countClasses; k++) {
								Promotion p = promotionDao.find(currentYear, d.getDepartment(), d.getClasses().get(k));
								count += inscriptionDao.countByPromotion(p);
							}
							
							Color color = COLORS[ (i+j) % (COLORS.length - 1)];
							DefaultPiePart part = new DefaultPiePart(color, count, d.getDepartment().toString());
							part.setData(d.getDepartment());
							pieModel.addPart(part);
						}
					}
				}break;
				
				case 2:{//par classe d'etude
					for (int i = 0; i < filters.length; i++) {
						FacultyFilter filter = filters[i];
						
						for(int j = 0, countDepartment = filter.getDepartments().size(); j < countDepartment; j++) {
							DepartmentFilter d = filter.getDepartments().get(j);
							
							for (int k = 0, countClasses = d.getClasses().size(); k < countClasses; k++) {
								StudyClass c = d.getClasses().get(k);
								PiePart part = pieModel.findByData(c);
								
								if (part == null) {
									Color color = COLORS[ (i+j+k) % (COLORS.length-1)];
									int count = inscriptionDao.countByStudyClass(c, currentYear);
									part = new DefaultPiePart(color, count, c.toString());
									part.setData(c);
									pieModel.addPart(part);
									//System.out.println(c);
								} 
								//else System.out.println("\t> "+c);
							}
						}
					}
				}break;
				
				case 3:{//par promotion
					for (int i = 0; i < filters.length; i++) {
						FacultyFilter filter = filters[i];
						
						for(int j = 0, countDepartment = filter.getDepartments().size(); j < countDepartment; j++) {
							DepartmentFilter d = filter.getDepartments().get(j);
							
							PiePart [] parts = new PiePart[d.getClasses().size()];
							for (int k = 0, countClasses = d.getClasses().size(); k < countClasses; k++) {
								
								Promotion p = promotionDao.find(currentYear, d.getDepartment(), d.getClasses().get(k));
								Color color = COLORS[ (i+j+k) % (COLORS.length-1)];
								int count = inscriptionDao.countByPromotion(p);
								DefaultPiePart part = new DefaultPiePart(color, count, p.toString());
								part.setData(p);
								parts[k] = part;
							}
							pieModel.addParts(parts);
						}
					}
				}break;
				
				default:
					throw new IllegalArgumentException("Operation non pris en charge, group = "+group);
			}
			
			for (int i = 0; i < radios.length; i++){
				radios[i].setEnabled(true);
				radios[i].addChangeListener(radioListener);
			}
			
			//System.out.println("================["+group+"]=================");
		}
		
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
