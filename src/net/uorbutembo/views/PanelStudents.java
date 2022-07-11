/**
 * 
 */
package net.uorbutembo.views;

import static net.uorbutembo.tools.FormUtil.COLORS;
import static net.uorbutembo.tools.FormUtil.createTitle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.SearchField;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.PieModel;
import net.uorbutembo.swing.charts.PieModelListener;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PiePart;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
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
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;

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
	private final JButton btnToExcel = new Button(new ImageIcon(R.getIcon("export")), "Excel");
	{
		btnToExcel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
	}
	//==
	
	private final JProgressBar progress = new JProgressBar();
	private TabDatatablePanel datatablePanel;
	private TabDatachartPanel datachartPanel;
	
	private JDialog dialogSheet;//boite de dialogue de consultation des fiches de payements
	private IndividualSheet sheet;
	
	private FormInscription formInscription;
	private FormReRegister formRegister;
	
	private JDialog dialogInscription;
	private JDialog dialogRegister;
	
	private ExportConfigListener exportExcelListener = new ExportConfigListener() {
		
		@Override
		public void onValiate(ExportConfig config) {
			
			if(!Table.XLSX_FILE_CHOOSER.showSaveDialog(mainWindow))
				return;
			
			File file = Table.XLSX_FILE_CHOOSER.getSelectedFile();
			if(file == null )
				return;
			
			String filename = file.getAbsolutePath()+ (file.getName().endsWith(".xlsx")?  "" : ".xlsx");
			navigation.wait(true);
			statusButtonsExport(false);
			setCursor(FormUtil.WAIT_CURSOR);
			
			Thread t = new Thread(()-> {
				datatablePanel.getDatatableView().exportToExcel(filename, config, datachartPanel.getPieModelSold(), datachartPanel.getPieModelDebt(), datachartPanel.getPieModelStudent());
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
		navigation = new SidebarStudents(mainWindow);
		
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
		
		dialogInscription = new JDialog(mainWindow, "Inscription d'un nouvel étudiant", true);
		dialogInscription.setContentPane(inscriptionFormScroll);
		dialogInscription.pack();
		dialogInscription.setSize(900, dialogInscription.getHeight());
		dialogInscription.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		formInscription.setCurrentYear(navigation.getCurrentYear());
	}
	
	/**
	 * utilitaire de creation du boite de dialogue de re-inscription
	 */
	private void createDialogueReRegister () {
		if(dialogRegister != null)
			return;
		
		dialogRegister = new JDialog(mainWindow, "Ré-inscription d'un étudiant", true);
		
		final JPanel registerFormPanel = new JPanel(new BorderLayout());
		formRegister = new FormReRegister(mainWindow, true);
		
		registerFormPanel.setBackground(FormUtil.BKG_DARK);
		registerFormPanel.add(formRegister, BorderLayout.NORTH);
		registerFormPanel.setBorder(BODY_BORDER);
		final JScrollPane registerFormScroll = FormUtil.createVerticalScrollPane(registerFormPanel);
		
		dialogRegister.getContentPane().add(registerFormScroll, BorderLayout.CENTER);
		dialogRegister.getContentPane().setBackground(FormUtil.BKG_DARK);
		dialogRegister.pack();
		dialogRegister.setSize(750, dialogRegister.getHeight());
		dialogRegister.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
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
		dialogSheet = new JDialog(mainWindow,"Fiche de paiement", true);
		dialogSheet.setSize(mainWindow.getWidth()-mainWindow.getWidth()/5, mainWindow.getHeight()-mainWindow.getHeight()/5);
		dialogSheet.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
		box.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		center.add(box, BorderLayout.SOUTH);
		
		progress.setBorderPainted(false);
		progress.setStringPainted(true);
		progress.setVisible(false);
		statusButtonsExport(false);
		//==
				
		btnToExcel.addActionListener(event -> {
			exportConfig.show(exportExcelListener);
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
		dialogInscription.setTitle(navigation.getCurrentYear()+" - Inscription d'un nouvel étudiant"); 
		dialogInscription.setLocationRelativeTo(mainWindow);
		dialogInscription.setVisible(true);
	}

	@Override
	public void onReinscription(ActionEvent event) {
		createDialogueReRegister();
		dialogRegister.setTitle(navigation.getCurrentYear()+" - Ré-inscription d'un étudiant");
		dialogRegister.setLocationRelativeTo(mainWindow);
		dialogRegister.setVisible(true);
	}
	
	@Override
	public void onAction(InscriptionDataRow row) {
		createDialogIndiviualSheet();
		dialogSheet.setTitle(navigation.getCurrentYear()+"- Fiche induviduelle de paiement - "+row.getInscription().getStudent().getFullName());
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
	
	public final class TabDatachartPanel extends Panel {
		private static final long serialVersionUID = -1438881851640344547L;
		
		private final DefaultPieModel pieModelStudent = new DefaultPieModel();
		private final DefaultPieModel pieModelSold = new DefaultPieModel();
		private final DefaultPieModel pieModelDebt = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModelStudent);
		private final Panel panelRadios = new Panel();
		private final Panel panelRadiosType = new Panel(new BorderLayout());
		private final JLabel labelCount = FormUtil.createSubTitle("");//total du model encours de consultation
		
		private int filterIndex = 3;
		private final JRadioButton [] radios = {
				new JRadioButton("Faculté"),
				new JRadioButton("Département"),
				new JRadioButton("Classe d'étude"),
				new JRadioButton("Promotion", true)
		};
		
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
				new Thread( () -> { reload(filterIndex); } ).start();
			});
		};
		
		private final PieModelListener pieModelListener = new PieModelListener() {
			
			@Override
			public void repaintPart(PieModel model, int partIndex) {
				refresh(model);
			}
			
			@Override
			public void refresh(PieModel model) {
				if (piePanel.getModel() == model) {
					if (model.getSuffix() == null || model.getSuffix().trim().isEmpty()) {
						int count = (int) model.getRealMax();
						labelCount.setText(count+" étudiant"+(count>1? "s":""));
					} else
						labelCount.setText(model.getRealMax()+" "+model.getSuffix());
				}
			}
			
			@Override
			public void onSelectedIndex(PieModel model, int oldIndex, int newIndex) {}
		};
		
		private int lastIndexType = 2;
		private final ChangeListener radioTypeListener = (event) -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if (lastIndexType == index)
				return;
			
			if(index == 0) {
				piePanel.setModel(pieModelSold);
				pieModelListener.refresh(pieModelSold);
			} else if (index == 1){
				piePanel.setModel(pieModelDebt);
				pieModelListener.refresh(pieModelDebt);
			}else {
				piePanel.setModel(pieModelStudent);
				pieModelListener.refresh(pieModelStudent);
			}
			
			lastIndexType = index;
		};
		
		private final DAOAdapter<Inscription> inscriptionAdapter = new DAOAdapter<Inscription>() {

			@Override
			public synchronized void onCreate(Inscription e, int requestId) {
				this.reloadingProcess();
			}

			@Override
			public synchronized void onUpdate(Inscription e, int requestId) {
				this.reloadingProcess();
			}

			@Override
			public synchronized void onDelete(Inscription e, int requestId) {
				this.reloadingProcess();
			}
			
			private void reloadingProcess () {
				reload(filterIndex);
			}
			
		};
		
		private final DAOAdapter<PaymentFee> paymentAdapter = new DAOAdapter<PaymentFee>() {

			@Override
			public synchronized void onCreate(PaymentFee e, int requestId) {
				this.reloadingProcess();
			}

			@Override
			public synchronized void onUpdate(PaymentFee e, int requestId) {
				this.reloadingProcess();
			}

			@Override
			public synchronized void onDelete(PaymentFee e, int requestId) {
				this.reloadingProcess();
			}
			
			private void reloadingProcess () {
				reload(filterIndex);
			}
			
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

			pieModelSold.setRealMaxPriority(true);
			pieModelDebt.setRealMaxPriority(true);
			pieModelStudent.setRealMaxPriority(true);
			
			pieModelSold.addListener(pieModelListener);
			pieModelDebt.addListener(pieModelListener);
			pieModelStudent.addListener(pieModelListener);
			
			pieModelSold.setSuffix(FormUtil.UNIT_MONEY_SYMBOL);
			pieModelDebt.setSuffix(FormUtil.UNIT_MONEY_SYMBOL);
		}
		
		{//radion permetant de choisir le type de graphique a afficher (solde, dete, ...)
			final ButtonGroup group = new  ButtonGroup();
			final Box box = Box.createHorizontalBox();
			for (int i = 0; i < radiosType.length; i++) {
				JRadioButton radio = radiosType[i];
				box.add(radio);
				radio.setName(i+"");
				group.add(radio);
				radio.setEnabled(false);
				radio.setForeground(Color.WHITE);
				radio.addChangeListener(radioTypeListener);
			}
			
			panelRadiosType.add(labelCount, BorderLayout.WEST);
			panelRadiosType.add(box, BorderLayout.EAST);
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
			
			piePanel.setBorderColor(FormUtil.BORDER_COLOR);
			piePanel.setOwner(mainWindow);
			
			paymentFeeDao.addListener(paymentAdapter);
			inscriptionDao.addListener(inscriptionAdapter);

			add(panelRadiosType, BorderLayout.NORTH);
			add(panelRadios, BorderLayout.SOUTH);
			add(piePanel, BorderLayout.CENTER);
		}
		
		/**
		 * @return the pieModelStudent
		 */
		public DefaultPieModel getPieModelStudent() {
			return pieModelStudent;
		}

		/**
		 * @return the pieModelSold
		 */
		public DefaultPieModel getPieModelSold() {
			return pieModelSold;
		}

		/**
		 * @return the pieModelDebt
		 */
		public DefaultPieModel getPieModelDebt() {
			return pieModelDebt;
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
		 * Cette methode doit etre appeler dans un thread different du thread UI car on y fait des traitements lourdes.
		 * lors du chargement des donnees, la sequances est la suivante:
		 * - netoyage des models du graphique
		 * - desactivation des radios de l'UI
		 * - verification de valeur du @param group dans un switch... case
		 * - organisation des indexs des donnees dans la base de donnees (preparation des metadonnees)
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
		 * 
		 * <br/>
		 * cette methode est loin d'etre optimiser car pour chaque evenement du DAO, on recharge tout les models
		 * des graphiques. il serait mieux voir quel part a chagner pour eviter les gaspillage des ressources
		 */
		private synchronized void reload (int group) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			piePanel.setRenderVisible(false);
			for (int i = 0; i < radios.length; i++){
				radios[i].removeChangeListener(radioListener);
				radios[i].setEnabled(false);
			}
			
			for (int i = 0; i < radiosType.length; i++){
				radiosType[i].removeChangeListener(radioTypeListener);
				radiosType[i].setEnabled(false);
			}
			
			pieModelStudent.removeAll();
			pieModelSold.removeAll();
			pieModelDebt.removeAll();
			
			FacultyFilter [] filters = lastFilters == null? rootFilters : lastFilters;
			progress.setValue(0);
			progress.setString("Chargement du graphique");
			progress.setVisible(true);
			progress.setIndeterminate(false);
			
			//tableau des donnee
			PiePart [] partsSold = null;
			PiePart [] partsDebt = null;
			PiePart [] partsStudent = null;
			
			switch (group) {
				case 0:{//par faculte
					progress.setMaximum(filters.length);
					partsSold = new PiePart[filters.length];
					partsDebt = new PiePart[filters.length];
					partsStudent = new PiePart[filters.length];
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
					
					Promotion [][] promotions = new Promotion[filters.length][0];
					for (int i = 0; i < promotions.length; i++) {
						promotions[i] = new Promotion[proms.get(i).size()];
						for (int j = 0; j < proms.get(i).size(); j++)
							promotions[i][j] = proms.get(i).get(j);
					}
					//== end metadata
					
					for (int i = 0; i < filters.length; i++) {//soldes
						FacultyFilter filter = filters[i];
						double sold = paymentFeeDao.getSoldByPromotions(promotions[i]), total = 0;
						Color color = COLORS[i % COLORS.length];
			
						//sold
						DefaultPiePart partSold = new DefaultPiePart(color, sold, filter.getFaculty().toString());
						partSold.setData(filter.getFaculty());
						partsSold[i] = partSold;
						//==
						
						//dettes
						for(Promotion p : promotions[i]) {//recherche du total, pour mieux calculer la dette
							double count = inscriptionDao.countByPromotion(p);
							total += p.getAcademicFee() != null? (p.getAcademicFee().getAmount() * count) : 0;
							progress.setValue(progress.getValue() + 1);
							progress.setMaximum(progress.getMaximum() + 1);
							progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
						}
						DefaultPiePart partDebt = new DefaultPiePart(color, total - sold, filter.getFaculty().toString());
						partDebt.setData(filter.getFaculty());
						partsDebt[i] = partDebt;
						//==
						
						//effectifs
						int count = inscriptionDao.countByPromotions(promotions[i]);
						DefaultPiePart partStudent = new DefaultPiePart(color, count, filter.getFaculty().toString());
						partStudent.setData(filter.getFaculty());
						partsStudent[i] = partStudent;
						//==
						
						progress.setValue(progress.getValue() + 1);
						progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
					}
					pieModelSold.setTitle("Solde des étudiants groupé par faculté");
					pieModelDebt.setTitle("Dettes des étudiants groupé par faculté");
					pieModelStudent.setTitle("Effectifs des étudiants groupé par faculté");
					
					
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
					partsSold = new PiePart[max];
					partsDebt = new PiePart[max];
					partsStudent = new PiePart[max];
					
					for (int i = 0; i < max; i++) {
						Color color = COLORS[ i % (COLORS.length - 1)];
						final double sold = paymentFeeDao.getSoldByPromotions(classes[i], deps.get(i), currentYear);
						
						//soldes
						DefaultPiePart partSold = new DefaultPiePart(color, sold, deps.get(i).toString());
						partSold.setData(deps.get(i));
						partsSold[i] = partSold;
						//==
						
						//dettes
						double total = 0;
						for(StudyClass s : classes[i]) {
							Promotion p = promotionDao.find(currentYear, deps.get(i), s);
							double count  = inscriptionDao.countByPromotion(p);
							total += p.getAcademicFee() != null? (count * p.getAcademicFee().getAmount()) : 0;
							progress.setValue(progress.getValue() + 1);
							progress.setMaximum(progress.getMaximum() + 1);
							progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
						}
						DefaultPiePart part = new DefaultPiePart(color, total - sold, deps.get(i).toString());
						part.setData(deps.get(i));
						partsDebt[i] = part;
						//==
						
						//effectifs
						int count = inscriptionDao.countByPromotions(classes[i], deps.get(i), currentYear);
						DefaultPiePart partStudent = new DefaultPiePart(color, count, deps.get(i).toString());
						partStudent.setData(deps.get(i));
						partsStudent[i] = partStudent;
						//==
						
						progress.setValue(progress.getValue()+1);
						progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
					}
					pieModelSold.setTitle("Solde des étudiants groupé par département");
					pieModelDebt.setTitle("Dette des étudiants groupé par département");
					pieModelStudent.setTitle("Effectif des étudiants groupé par département");
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
					
					partsSold = new PiePart[classes.size()];
					partsDebt = new PiePart[classes.size()];
					partsStudent = new PiePart[classes.size()];
					
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
					
					for (int index = 0; index < classes.size(); index++) {								
						StudyClass s = classes.get(index);
						Color color = COLORS[ (index) % (COLORS.length-1)];
						double sold = paymentFeeDao.getSoldByPromotions(s, classement[index], currentYear);
						
						//soldes
						PiePart partSold = new DefaultPiePart(color, sold, s.getAcronym()+" "+labels[index]);
						partSold.setData(s);
						partsSold[index] = partSold;
						//==
						
						//dettes
						double total = 0;
						for(Department d : classement[index]) {
							Promotion p = promotionDao.find(currentYear, d, s);
							double count  = inscriptionDao.countByPromotion(p);
							total += p.getAcademicFee() != null? count * p.getAcademicFee().getAmount() : 0;
							progress.setValue(progress.getValue() + 1);
							progress.setMaximum(progress.getMaximum() + 1);
							progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
						}
						PiePart partDebt = new DefaultPiePart(color, total - sold, s.getAcronym()+" "+labels[index]);
						partDebt.setData(s);
						partsDebt[index] = partDebt;
						//==
						
						//effectifs
						int count = inscriptionDao.countByPromotions(s, classement[index], currentYear);
						PiePart partStudent = new DefaultPiePart(color, count, s.getAcronym()+" "+labels[index]);
						partStudent.setData(s);
						partsStudent[index] = partStudent;
						//==
						
						progress.setValue(progress.getValue()+1);
						progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
					}
					pieModelSold.setTitle("Solde des étudiants groupé par classe d'étude");
					pieModelDebt.setTitle("Dettes des étudiants groupé par classe d'étude");
					pieModelStudent.setTitle("Effectifs des étudiants groupé par classe d'étude");					
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
					partsSold = new PiePart[max];
					partsDebt = new PiePart[max];
					partsStudent = new PiePart[max];
					
					for (int i = 0; i < max; i++) {
						Promotion p = proms.get(i);
						Color color = COLORS[ i % (COLORS.length-1)];
						
						double sold = paymentFeeDao.getSoldByPromotion(p);
						double count = inscriptionDao.countByPromotion(p);
						double total = p.getAcademicFee() != null? p.getAcademicFee().getAmount() * count : 0;
						
						//soldes
						DefaultPiePart partSold = new DefaultPiePart(color, sold, p.toString());
						partSold.setData(p);
						partsSold[i] = partSold;
						//==
						
						//dettes
						DefaultPiePart partDebt = new DefaultPiePart(color, total-sold, p.toString());
						partDebt.setData(p);
						partsDebt[i] = partDebt;
						//==
						
						//effectifs
						DefaultPiePart partStudent = new DefaultPiePart(color, count, p.toString());
						partStudent.setData(p);
						partsStudent[i] = partStudent;
						//==
						
						progress.setValue(progress.getValue()+1);
						progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Chargement du graphique...");
					}
					
					pieModelSold.setTitle("Solde des étudiants groupé par promotion");
					pieModelDebt.setTitle("Dettes des étudiants groupé par promotion");
					pieModelStudent.setTitle("Effectifs des étudiants groupé par promotion");
					
				}break;
				
				default: {
					throw new IllegalArgumentException("Operation non pris en charge, group = "+group);
				}
			}
			
			pieModelDebt.addParts(partsDebt);
			pieModelSold.addParts(partsSold);
			pieModelStudent.addParts(partsStudent);
			
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
			
			piePanel.setRenderVisible(true);
		}
		
	}
}
