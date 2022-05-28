/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.views.forms.FormUtil.COLORS;
import static net.uorbutembo.views.forms.FormUtil.createTitle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.SearchField;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.DialogStudentExportConfig;
import net.uorbutembo.views.components.DialogStudentExportConfig.ExportConfig;
import net.uorbutembo.views.components.DialogStudentExportConfig.ExportConfigListener;
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
	private final InscriptionDao inscriptionDao;
	//==dao

	//espace de traival
	private final WorkspaceTabbedPanel workspaceTabbedPanel;
	private final TabbedPanelContainer container;
	private SidebarStudents navigation;
	
	private final DialogStudentExportConfig exportConfig;
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
	
	private ExportConfigListener exportExcelListener = new ExportConfigListener() {
		
		@Override
		public void onValiate(ExportConfig config) {
			
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
				datatablePanel.getDatatableView().exportToExcel(filename, config);
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
		}
		
		@Override
		public void onCancel() {
			
		}
	};

	public PanelStudents(MainWindow mainWindow) {
		super("Etudiants", new ImageIcon(R.getIcon("student")), mainWindow, false);//la scene gere les scrollbars	
		inscriptionDao = mainWindow.factory.findDao(InscriptionDao.class);
		
		exportConfig = new DialogStudentExportConfig(mainWindow);
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
		formInscription = new FormInscription(mainWindow);
				
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
		formRegister = new FormReRegister(mainWindow, true);
		
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
			exportConfig.show(exportExcelListener);
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
	
	private final class TabDatachartPanel extends Panel {
		private static final long serialVersionUID = -1438881851640344547L;
		
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel);
		private final Panel panelRadios = new Panel();
		private final Panel panelRadiosType = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		private int filterIndex = 3;
		private final JRadioButton [] radios = {
				new JRadioButton("Faculté"),
				new JRadioButton("Département"),
				new JRadioButton("Classe d'étude"),
				new JRadioButton("Promotion", true)
		};
		
		private int filterTypeIndex = 2;
		private final JRadioButton [] radiosType = {
				new JRadioButton("Soldes"),
				new JRadioButton("Dettes"),
				new JRadioButton("Effectifs des étudiants", true),
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
			EventQueue.invokeLater(() -> {		
				new Thread( () -> { reload(filterIndex, filterTypeIndex); } ).start();
			});
		};
		
		private final ChangeListener radioTypeListener = (event) -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if(index == filterTypeIndex)
				return;
			
			filterTypeIndex = index;
			EventQueue.invokeLater(() -> {		
				new Thread( () -> { reload(filterIndex, filterTypeIndex); } ).start();
			});
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
		
		{//radion permetant de choisir le type de graphique a afficher (solde, dete, ...)
			final ButtonGroup group = new  ButtonGroup();
			for (int i = 0; i < radiosType.length; i++) {
				JRadioButton radio = radiosType[i];
				panelRadiosType.add(radio);
				radio.setName(i+"");
				group.add(radio);
				radio.setEnabled(false);
				radio.setForeground(Color.WHITE);
				radio.addChangeListener(radioTypeListener);
			}
			panelRadiosType.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}
		
		private final PromotionDao promotionDao;
		private final PaymentFeeDao paymentFeeDao;
		
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
			paymentFeeDao = factory.findDao(PaymentFeeDao.class);
			
			pieModel.setTitle("Graphique");
			piePanel.setBorderColor(FormUtil.BORDER_COLOR);

			add(panelRadiosType, BorderLayout.NORTH);
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
				reload(filterIndex, filterTypeIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * chargement des parts (mis en jours du model)
		 * Cette methode soit etre appeler dans un thread different du thread UI car on y fait des traitements lordes.
		 * lors du chargement des donnees, la sequances est la suivante:
		 * - netoyage du model du graphique
		 * - desactivation des radios de l'UI
		 * - verification de valeur du @param group dans un switch... case
		 * - organisation des indexs des donnees dans la base de donnees (preparation des metadonnees)
		 * - verification dela valeur du @param type dans un autre switch... case
		 * - chargement final du graphique
		 * - activation des radios de l'UI
		 * Pendant toute ces operations on touches l'etat du progressbar dans la mesure du possible
		 * 
		 * @param group : groupement des parts du graphique. sa valeur doit etre compris entre 0 et 3
		 * <ul>
		 * <li>0: groupement par faculte</li>
		 * <li>1: groupement par departement </li>
		 * <li>2: groupement par classe d'etude</li>
		 * <li>3: groupement par promotion</li>
		 * </ul>
		 * @param type : type de graphique (0, 1, 2)
		 * <ul>
		 * <li>0: grapique des payements</li>
		 * <li>1: graphique des dettes</li>
		 * <li>2: graphique des effectifs des etudiants</li>
		 * </ul>
		 */
		private synchronized void reload (int group, int type) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			for (int i = 0; i < radios.length; i++){
				radios[i].removeChangeListener(radioListener);
				radios[i].setEnabled(false);
			}
			
			for (int i = 0; i < radiosType.length; i++){
				radiosType[i].removeChangeListener(radioTypeListener);
				radiosType[i].setEnabled(false);
			}
			
			pieModel.removeAll();
			if(type == 2) 
				pieModel.setSuffix(":");
			else 
				pieModel.setSuffix(FormUtil.UNIT_MONEY_SYMBOL);
			
			FacultyFilter [] filters = lastFilters == null? rootFilters : lastFilters;
			progress.setValue(0);
			progress.setString("Chargement du graphique");
			progress.setVisible(true);
			progress.setIndeterminate(false);
			
			switch (group) {
				case 0:{//par faculte
					progress.setMaximum(filters.length);
					PiePart [] parts = new PiePart[filters.length];
					List<List<Promotion>> proms = new ArrayList<>();
					
					//prepare metadata
					for (int i = 0; i < filters.length; i++) {
						FacultyFilter filter = filters[i];
						List <Promotion> tmp = new ArrayList<>();
						for (int j = 0, count = filter.getDepartments().size(); j < count; j++) {
							DepartmentFilter d = filter.getDepartments().get(j);
							for (int k = 0, countClasses = d.getClasses().size(); k < countClasses; k++){
								tmp.add(promotionDao.find(currentYear, d.getDepartment(), d.getClasses().get(k)));
								progress.setValue(progress.getValue() + 1);
								progress.setMaximum(progress.getMaximum()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Préparation des données...");
							}
						}
						proms.add(tmp);
					}
					
					Promotion [][] promotions = new Promotion[parts.length][0];
					for (int i = 0; i < promotions.length; i++) {
						promotions[i] = new Promotion[proms.get(i).size()];
						for (int j = 0; j < proms.get(i).size(); j++)
							promotions[i][j] = proms.get(i).get(j);
					}
					//== end metadata
					
					switch (type) {
						case 0: {//soldes
							for (int i = 0; i < filters.length; i++) {
								FacultyFilter filter = filters[i];
								double sold = paymentFeeDao.getSoldByPromotions(promotions[i]);
								Color color = COLORS[i % COLORS.length];
								DefaultPiePart part = new DefaultPiePart(color, sold, filter.getFaculty().toString());
								part.setData(filter.getFaculty());
								parts[i] = part;
								progress.setValue(progress.getValue() + 1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Solde des étudiants groupé par faculté");
						}break;
						case 1: {//dettes
							for (int i = 0; i < filters.length; i++) {
								FacultyFilter filter = filters[i];
								double sold = paymentFeeDao.getSoldByPromotions(promotions[i]), total = 0;
								for(Promotion p : promotions[i]) {
									double count = inscriptionDao.countByPromotion(p);
									total += (p.getAcademicFee().getAmount() * count);
									progress.setValue(progress.getValue() + 1);
									progress.setMaximum(progress.getMaximum() + 1);
									progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
								}
								Color color = COLORS[i % COLORS.length];
								DefaultPiePart part = new DefaultPiePart(color, total - sold, filter.getFaculty().toString());
								part.setData(filter.getFaculty());
								parts[i] = part;
								progress.setValue(progress.getValue() + 1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Dettes des étudiants groupé par faculté");
						}break;
						case 2: {//effectifs
							for (int i = 0; i < filters.length; i++) {
								FacultyFilter filter = filters[i];
								int count = inscriptionDao.countByPromotions(promotions[i]);
								Color color = COLORS[i % COLORS.length];
								DefaultPiePart part = new DefaultPiePart(color, count, filter.getFaculty().toString());
								part.setData(filter.getFaculty());
								parts[i] = part;
								progress.setValue(progress.getValue() + 1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Effectifs des étudiants groupé par faculté");
						}break;
						default :
							throw new IndexOutOfBoundsException("Index out of range: "+filterTypeIndex);
					}
					pieModel.addParts(parts);
				}break;
				
				case 1:{//par departement
					List<Department> deps = new ArrayList<>();
					List<List<StudyClass>> tmpClasses = new ArrayList<>();
					//prepare metadata
					for (int i = 0; i < filters.length; i++)
						for(int j = 0, countDepartment = filters[i].getDepartments().size(); j < countDepartment; j++){
							tmpClasses.add(filters[i].getDepartments().get(j).getClasses());
							deps.add(filters[i].getDepartments().get(j).getDepartment());
						}
					
					StudyClass [][] classes = new StudyClass [deps.size()] [0];
					for (int i = 0; i < classes.length; i++) {
						classes[i] = new StudyClass[tmpClasses.get(i).size()];
						for (int j =0, count = tmpClasses.get(i).size(); j < count; j++)
							classes[i][j] = tmpClasses.get(i).get(j);
					}
					// end metadata
					
					int max = deps.size();
					progress.setMaximum(max);
					PiePart [] parts = new PiePart[max];
					switch (type) {
						case 0: {//soldes
							for (int i = 0; i < max; i++) {
								Color color = COLORS[ i % (COLORS.length - 1)];
								double sold = paymentFeeDao.getSoldByPromotions(classes[i], deps.get(i), currentYear);
								DefaultPiePart part = new DefaultPiePart(color, sold, deps.get(i).toString());
								part.setData(deps.get(i));
								parts[i] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Solde des étudiants groupé par département");
						}break;
						case 1: {//dettes
							for (int i = 0; i < max; i++) {
								Color color = COLORS[ i % (COLORS.length - 1)];
								double sold = paymentFeeDao.getSoldByPromotions(classes[i], deps.get(i), currentYear);
								double total = 0;
								for(StudyClass s : classes[i]) {
									Promotion p = promotionDao.find(currentYear, deps.get(i), s);
									double count  = inscriptionDao.countByPromotion(p);
									total += count * p.getAcademicFee().getAmount();
									progress.setValue(progress.getValue() + 1);
									progress.setMaximum(progress.getMaximum() + 1);
									progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
								}
								DefaultPiePart part = new DefaultPiePart(color, total - sold, deps.get(i).toString());
								part.setData(deps.get(i));
								parts[i] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Dette des étudiants groupé par département");
						}break;
						case 2: {//effectifs
							for (int i = 0; i < max; i++) {
								Color color = COLORS[ i % (COLORS.length - 1)];
								int count = inscriptionDao.countByPromotions(classes[i], deps.get(i), currentYear);
								DefaultPiePart part = new DefaultPiePart(color, count, deps.get(i).toString());
								part.setData(deps.get(i));
								parts[i] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Effectif des étudiants groupé par département");
						}break;
						default :
							throw new IndexOutOfBoundsException("Index out of range: "+filterTypeIndex);
					}
					pieModel.addParts(parts);
				}break;
				
				case 2:{//par classe d'etude					
					final List<StudyClass> classes = new ArrayList<>();
					final List<Department> deps = new ArrayList<>();
					
					//prepare metadata
					for (int i = 0; i < filters.length; i++) {
						for (int j = 0, count = filters[i].getDepartments().size(); j < count; j++){
							deps.add(filters[i].getDepartments().get(j).getDepartment());
							for (int k = 0, ccount = filters[i].getDepartments().get(j).getClasses().size(); k < ccount; k++) {
								StudyClass sc = filters[i].getDepartments().get(j).getClasses().get(k);
								boolean addable = true;
								
								for (StudyClass s : classes) {
									if(s.getId() == sc.getId()) {
										addable = false;
										break;
									}
								}
								
								if (addable)
									classes.add(sc);
							}
						}
					}
					progress.setMaximum(classes.size());
					final PiePart [] parts = new PiePart[classes.size()];
					final String [] labels = new String[classes.size()];
					final Department[][] classement = new Department[classes.size()][0];;
					
					for (int j = 0; j < classes.size(); j++) {					
						String label = "";
						List<Department>  tmpDeps = new ArrayList<>();
						for (int i = 0; i < deps.size(); i++){
							if(promotionDao.check(currentYear, deps.get(i), classes.get(j))) {								
								tmpDeps.add(deps.get(i));
								label += " "+deps.get(i).getAcronym()+",";
							}
							progress.setValue(progress.getValue() + 1);
							progress.setMaximum(progress.getMaximum() + 1);
							progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Préparation des données...");
						}
						final Department[] departments = new Department[tmpDeps.size()];
						if (!tmpDeps.isEmpty()) {
							for (int i = 0; i < departments.length; i++) 
								departments[i] = tmpDeps.get(i);
							label = label.substring(0, label.length()-1);
							label = "("+label+")";
						} 
						labels[j] = label;
						classement[j] = departments;
					}
					//== end metadata
					
					switch (type) {
						case 0: {//soldes
							for (int index = 0; index < classes.size(); index++) {								
								StudyClass s = classes.get(index);
								Color color = COLORS[ (index) % (COLORS.length-1)];
								double sold = paymentFeeDao.getSoldByPromotions(s, classement[index], currentYear);
								PiePart part = new DefaultPiePart(color, sold, s.getAcronym()+" "+labels[index]);
								part.setData(s);
								parts[index] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Solde des étudiants groupé par classe d'étude");
						}break;
						case 1: {//dettes
							for (int index = 0; index < classes.size(); index++) {								
								StudyClass s = classes.get(index);
								Color color = COLORS[ (index) % (COLORS.length-1)];
								double total = 0;
								for(Department d : classement[index]) {
									Promotion p = promotionDao.find(currentYear, d, s);
									double count  = inscriptionDao.countByPromotion(p);
									total += count * p.getAcademicFee().getAmount();
									progress.setValue(progress.getValue() + 1);
									progress.setMaximum(progress.getMaximum() + 1);
									progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
								}
								double sold = paymentFeeDao.getSoldByPromotions(s, classement[index], currentYear);
								PiePart part = new DefaultPiePart(color, total - sold, s.getAcronym()+" "+labels[index]);
								part.setData(s);
								parts[index] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Dettes des étudiants groupé par classe d'étude");
						}break;
						case 2: {//effectifs
							for (int index = 0; index < classes.size(); index++) {								
								StudyClass s = classes.get(index);
								Color color = COLORS[ (index) % (COLORS.length-1)];
								int count = inscriptionDao.countByPromotions(s, classement[index], currentYear);
								PiePart part = new DefaultPiePart(color, count, s.getAcronym()+" "+labels[index]);
								part.setData(s);
								parts[index] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Effectifs des étudiants groupé par classe d'étude");
						}break;
						default :
							throw new IndexOutOfBoundsException("Index out of range: "+filterTypeIndex);
					}
					pieModel.addParts(parts);
					
				}break;
				
				case 3:{//par promotion
					List<Promotion> proms = new ArrayList<>();
					
					//prepare metadata
					for (int i = 0; i < filters.length; i++) {
						FacultyFilter filter = filters[i];
						for (int j = 0, count = filter.getDepartments().size(); j < count; j++) {
							DepartmentFilter d = filter.getDepartments().get(j);
							for (int k = 0, countClasses = d.getClasses().size(); k < countClasses; k++) {
								proms.add(promotionDao.find(currentYear, d.getDepartment(), d.getClasses().get(k)));
								progress.setValue(progress.getValue() + 1);
								progress.setMaximum(progress.getMaximum() + 1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") préparation des données...");
							}
						}
					}
					//== end metadata
					
					int max = proms.size();
					progress.setMaximum(max);
					PiePart [] parts = new PiePart[max];
					
					switch (type) {
						case 0: {//soldes
							for (int i = 0; i < max; i++) {
								Promotion p = proms.get(i);
								Color color = COLORS[ i % (COLORS.length-1)];
								double sold = paymentFeeDao.getSoldByPromotion(p);
								DefaultPiePart part = new DefaultPiePart(color, sold, p.toString());
								part.setData(p);
								parts[i] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Solde des étudiants groupé par promotion");
						}break;
						case 1: {//dettes
							for (int i = 0; i < max; i++) {
								Promotion p = proms.get(i);
								Color color = COLORS[ i % (COLORS.length-1)];
								double count = inscriptionDao.countByPromotion(p);
								double total = p.getAcademicFee().getAmount() * count;
								double sold = paymentFeeDao.getSoldByPromotion(p);
								DefaultPiePart part = new DefaultPiePart(color, total-sold, p.toString());
								part.setData(p);
								parts[i] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Dettes des étudiants groupé par promotion");
						}break;
						case 2: {//effectifs
							for (int i = 0; i < max; i++) {
								Promotion p = proms.get(i);
								Color color = COLORS[ i % (COLORS.length-1)];
								int count = inscriptionDao.countByPromotion(p);
								DefaultPiePart part = new DefaultPiePart(color, count, p.toString());
								part.setData(p);
								parts[i] = part;
								progress.setValue(progress.getValue()+1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
							}
							pieModel.setTitle("Effectifs des étudiants groupé par promotion");
						}break;
						default :
							throw new IndexOutOfBoundsException("Index out of range: "+filterTypeIndex);
					}
					pieModel.addParts(parts);
				}break;
				
				default: {
					throw new IllegalArgumentException("Operation non pris en charge, group = "+group);
				}
			}
			
			for (int i = 0; i < radios.length; i++){
				radios[i].setEnabled(true);
				radios[i].addChangeListener(radioListener);
			}
			
			for (int i = 0; i < radiosType.length; i++){
				radiosType[i].setEnabled(true);
				radiosType[i].addChangeListener(radioTypeListener);
			}
			
			progress.setVisible(false);
			setCursor(Cursor.getDefaultCursor());
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
