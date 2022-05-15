/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.Student;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.SidebarStudents.DepartmentFilter;
import net.uorbutembo.views.components.SidebarStudents.FacultyFilter;
import net.uorbutembo.views.forms.FormReRegister;
import net.uorbutembo.views.forms.FormStudent;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PromotionPaymentTableModel;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class StudentsDatatableView extends Panel {
	private static final long serialVersionUID = 1491067953369363653L;
	
	private static final Border EMPTY_BOTTOM_BORDER = new EmptyBorder(5, 0, 5, 0);
	
	private List<FacultyData> facultyDatas = new ArrayList<>();
	private DAOFactory daoFactory;
	private MainWindow mainWindow;
	
	private Box container = Box.createVerticalBox();
	private JScrollPane scroll = FormUtil.createScrollPane(container);
	private AcademicYear currentYear;//annee encours de consultation
	
	private JProgressBar progress;
	private FacultyFilter [] lastFilter;
	private List<FacultyData> lastFilterData = new ArrayList<>();
	
	private DatatableViewListener listener;
	private JDialog dialogStudent;//dialogue de modification de l'identite d'un etudiant
	private JDialog dialogInscription;//dialogue de modification de l'inscription d'un etudiant
	private FormStudent formStudent;
	private FormReRegister formRegister;
	
	private InscriptionDao inscriptionDao;
	private StudentDao studentDao;
	private AcademicYearDao academicYearDao;
	
	/**
	 * Ecoute du button d'annulation d'annulation des mis en jours 
	 * d'une inscription
	 */
	private final ActionListener cancelUpdateInscription = (event) -> {
		dialogInscription.setVisible(false);
	};
	
	/**
	 * construction d'un datatable
	 * @param daoFactory
	 * @param progress
	 * @param listener l'ecouteur du changement d'etat du datatable
	 */
	public StudentsDatatableView(MainWindow mainWindow, JProgressBar progress, DatatableViewListener listener) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.daoFactory = mainWindow.factory;
		this.inscriptionDao = daoFactory.findDao(InscriptionDao.class);
		this.studentDao = daoFactory.findDao(StudentDao.class);
		this.academicYearDao = daoFactory.findDao(AcademicYearDao.class);
		this.progress = progress;
		this.listener = listener;
		
		this.add(scroll, BorderLayout.CENTER);
		
		this.inscriptionDao.addListener(new DAOAdapter<Inscription>() {
			@Override
			public void onCreate(Inscription e, int requestId) {
				setFilter(lastFilter);
			}
			
			@Override
			public void onUpdate(Inscription e, int requestId) {
				setFilter(lastFilter);
				if(dialogInscription!=null && dialogInscription.isVisible())
					dialogInscription.setVisible(false);//apres mis en jours, on cache la fenetre
			}
			
			@Override
			public void onDelete(Inscription e, int requestId) {
				setFilter(lastFilter);
			}
		});
		
		this.studentDao.addListener(new DAOAdapter<Student>() {
			@Override
			public void onUpdate(Student e, int requestId) {//uniquement pour cacher la boite de dialogue apres mis en jour
				if(dialogStudent != null && dialogStudent.isVisible())
					dialogStudent.setVisible(false);
			}
		});
	}
	
	/**
	 * il est obligatoire d'appeler cette methode pour le premier chargement du datatable
	 * et de luis fornir la liste complet des filtres des facultes
	 * @param filters
	 */
	public synchronized void firstLoad (FacultyFilter [] filters, AcademicYear currentYear) {
		this.currentYear = currentYear;
		this.lastFilter = filters;
		this.lastFilterData.clear();
		container.removeAll();
		
		this.doStartFilter(filters);
		
		for (FacultyData f : facultyDatas) {
			f.dispose();
		}
		facultyDatas.clear();
		
		for (FacultyFilter filter : filters) {
			FacultyData  dataManager = new FacultyData(filter.getFaculty(), filter.getDepartments());
			container.add(dataManager);
			facultyDatas.add(dataManager);
			dataManager.setShowing(true);
		}
		
		container.add(Box.createVerticalGlue());
		progress.setVisible(false);
		
		//pour forcer la reorganisation des composants graphiques
		setFilter(null);
		setFilter(filters);
		//==
	}
	
	/**
	 * Avant de commencer le filtrage
	 * @param filters
	 */
	private void doStartFilter (FacultyFilter [] filters) {
		//determination du max
		progress.setValue(0);
		progress.setIndeterminate(true);
		progress.setVisible(true);
		int max = 0;
		if(filters != null) {//filtrage		
			for (FacultyFilter f : filters) {
				for (DepartmentFilter d : f.getDepartments()) {
					max += d.getClasses().size();
				}
			}
		} else {//anulation du filtre
			for (FacultyData fd : this.facultyDatas) {
				for (DepartmentData d : fd.datas) {
					max += d.classes.size();
				}
			}
		}
		progress.setMaximum(max);
		progress.setIndeterminate(false);
	}
	
	/**
	 * A chaque etape du filtrage
	 * @param promotion
	 * @param value
	 */
	protected void doFilter (Promotion promotion, int value) {
		progress.setValue(value);
		progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") "+promotion.toString());
	}

	/**
	 * Demande de filtrage
	 * @param filters
	 */
	public synchronized void setFilter (FacultyFilter [] filters) {
		
		this.lastFilter = filters;
		this.lastFilterData.clear();
		this.doStartFilter(filters);
		
		if(filters == null) {
			for (FacultyData data : facultyDatas) {
				data.setShowing(true, null);
			}
			return;
		}
		
		boolean show;
		for (int index = 0, count = facultyDatas.size(); index < count; index++) {
			FacultyData data= facultyDatas.get(index);
			show = false;
			
			for (FacultyFilter filter : filters) {
				if(data.getFaculty().getId() == filter.getFaculty().getId()){
					data.setShowing(true, filter);
					show = true;
					if(data.hasData() && !data.isAutohide() && !lastFilterData.contains(data)){
						lastFilterData.add(data);
					}
					break;
				}
			}
			
			if(!show)
				data.setShowing(false, null);
		}
		
		progress.setVisible(false);
		container.repaint();
	}
	
	/**
	 * Verifie s'il y a aumoin un panel visible avec des donnee
	 * @return
	 */
	public boolean hasData () {
		for (FacultyData fData : lastFilterData) {
			if(fData.hasData())
				return true;
		}
		return false;
	}
	
	//EXPORATION DES DONNEE
	//		=== || ====
	
	/**
	 * Exportation des donnees au format excel
	 * @param fileName chemain du fichier de serialisation
	 * @param fullRow faut-il exporter la ligne compte??
	 */
	public synchronized void exportToExcel (String fileName, boolean fullRow) {
		progress.setIndeterminate(true);
		progress.setVisible(true);
		progress.setValue(0);
		
		//collection des facultes
		progress.setString("Préparation des données");
		
		int max = 0;
		for (FacultyData fData : lastFilterData) {
			for (DepartmentData dData : fData.getLastFilterData()) {
				for (PromotionData pData : dData.getLastFilterData()) {
					max += pData.tableModel.getRowCount();
				}
			}
		}
		progress.setMaximum(max);
		progress.setIndeterminate(false);

		try (
				XSSFWorkbook book = new XSSFWorkbook();
				FileOutputStream out = new FileOutputStream(fileName);
			){
			
			String names [] = PromotionPaymentTableModel.COLUMN_NAMES;
			int columnCount = fullRow? names.length : names.length - 2;
			short rowHeight = 400;
			int current = 0;
			
			for (FacultyData fData : lastFilterData) {

				//Pour chaque faculte, on cree un sheet excel
				XSSFSheet sheet = book.createSheet(fData.getFaculty().getAcronym());
				int rowCount = 0;
				
				//faculty name
				XSSFRow row = sheet.createRow(rowCount++);
				XSSFCell cell = row.createCell(0);
				cell.setCellValue(fData.getFaculty().getName()+" ("+currentYear.toString()+")");
				sheet.addMergedRegion(new CellRangeAddress(rowCount-1, rowCount-1, 0, columnCount-1));
				
				XSSFCellStyle style = book.createCellStyle();
				XSSFFont font = book.createFont();
				font.setFontName("Arial");
				font.setFontHeight(18);
				font.setBold(true);
				font.setColor(IndexedColors.WHITE.index);
				
				style.setFillForegroundColor(IndexedColors.DARK_BLUE.index);
				style.setFont(font);
				style.setAlignment(HorizontalAlignment.CENTER);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				cell.setCellStyle(style);
				//==

				for (DepartmentData dData : fData.getLastFilterData()) {
					
					for (PromotionData pData : dData.getLastFilterData()) {
						PromotionPaymentTableModel data = pData.tableModel;
						
						if(data.getRowCount() == 0)
							continue;
						
						//class name
						style = book.createCellStyle();
						font = book.createFont();
						
						font.setFontName("Arial");
						font.setFontHeight(14);
						font.setItalic(true);
						font.setColor(IndexedColors.WHITE.index);
						
						style.setFont(font);
						style.setFillForegroundColor(IndexedColors.BLACK.index);
						style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						
						row = sheet.createRow(rowCount++);
						cell = row.createCell(0);
						cell.setCellValue(pData.getPromotion().getStudyClass().getAcronym()+" "+pData.getPromotion().getDepartment().getName());
						sheet.addMergedRegion(new CellRangeAddress(rowCount-1, rowCount-1, 0, columnCount-1));
						cell.setCellStyle(style);
						//==
						
						//titre des colones
						row = sheet.createRow(rowCount++);
						style = book.createCellStyle();
						style.setBorderBottom(BorderStyle.DOUBLE);
						for (int i = 0; i < columnCount; i++) {					
							cell = row.createCell(i);
							cell.setCellValue(names[i]);
							cell.setCellStyle(style);
						}
						//==
						
						for (int i = 0, count = data.getRowCount(); i < count; i++) {
							row = sheet.createRow(rowCount++);
							row.setHeight(rowHeight);
							
							current++;
							progress.setValue(current);
							progress.setString("("+current+"/"+max + ") "+data.getValueAt(i, 1));
							
							for (int j = 0; j < columnCount; j++) {
								cell = row.createCell(j);
								cell.setCellValue(data.getValueAt(i, j).toString());
							}
						}
						
						rowCount++;
					}
				}
				
				for (int i = 0; i < columnCount; i++) {
					sheet.autoSizeColumn(i);
				}
			}
			
			book.write(out);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
		
		progress.setVisible(false);
	}
	//	=== || ====	export
	
	/**
	 * @author Esaie MUHASA
	 * classe de base du panel representat un group des donnees groupe des donnees
	 */
	private static abstract class DataGroup extends Panel {
		private static final long serialVersionUID = -5774124212163165927L;
		
		protected boolean showing;
		protected boolean autohide;

		/**
		 * 
		 */
		public DataGroup() {
			super(new BorderLayout());
		}

		/**
		 * @return the showing
		 */
		public boolean isShowing() {
			return showing;
		}

		/**
		 * @return the autohide
		 */
		public boolean isAutohide() {
			return autohide;
		}

		/**
		 * @param autohide the autohide to set
		 */
		public void setAutohide(boolean autohide) {
			this.autohide = autohide;
			if(autohide)
				this.setVisible(false);
		}
		
		public void doAutohide () {
			this.setAutohide(!this.hasData() && showing);//dans le cas où il n'y a aucune donnée et on essai d'afficher les contenue		
		}

		/**
		 * @param showing the showing to set
		 */
		public void setShowing(boolean showing) {
			this.showing = showing;
			this.setVisible(showing);
			this.doAutohide();
		}
		
		public abstract void dispose();
		
		/**
		 * Cette methode doit envoyer true dans le cas où le groupe des donnees contiens aumoin une donnee
		 * @return
		 */
		public abstract boolean hasData ();
	}
	
	/**
	 * @author Esaie MUHASA
	 * Panel contenant les informations des payements des etudiants d'une facultee
	 */
	class FacultyData extends DataGroup {

		private static final long serialVersionUID = 5845359552859770355L;

		private final Faculty faculty;
		private FacultyFilter lastFilter;//dernier filtre pour cette faculte
		private List<DepartmentData> lastFilterData = new ArrayList<>();//collection des departements concerner par le derneir filtre
		
		private JLabel title = FormUtil.createTitle("");
		private List<DepartmentData> datas = new ArrayList<>();
		
		private Box container = Box.createVerticalBox();
		
		/** 
		 * Initialisation de la configuration de filtrage d'une faculte
		 * @param faculty
		 * @param deps
		 */
		public FacultyData(Faculty faculty, List <DepartmentFilter> deps) {
			super();
			this.faculty = faculty;
			title.setText(faculty.getName());
			
			for (DepartmentFilter d : deps) {//creation d'un panel pour chaque departement
				DepartmentData panel = new DepartmentData(d.getDepartment(), d.getClasses());
				datas.add(panel);
				container.add(panel);
			}
			
			this.add(title, BorderLayout.NORTH);
			this.add(container, BorderLayout.CENTER);
		}
		
		@Override
		public void dispose() {
			for (DepartmentData data : datas) {
				data.dispose();
			}
		}
		

		/**
		 * @return the faculty
		 */
		public Faculty getFaculty() {
			return faculty;
		}
		
		/**
		 * filtage de l'affichage
		 * @param showing
		 * @param lastFilter
		 */
		public void setShowing(boolean showing, FacultyFilter lastFilter) {
			super.setShowing(showing);
			this.lastFilter = lastFilter;
			lastFilterData.clear();
			
			if (this.lastFilter == null) {
				for (DepartmentData d : datas) {
					d.setShowing(showing, null);
					if(d.hasData() && showing)
						lastFilterData.add(d);
				}
			} else  {
				
				for (DepartmentData d : datas) {//
					d.setShowing(false, null);
				}
				
				for (DepartmentFilter df : lastFilter.getDepartments()) {
					
					for (DepartmentData d : datas) {
						if(d.getDepartment().getId() == df.getDepartment().getId()){
							d.setShowing(showing, df);
							if(d.hasData() && d.isShowing() && !d.isAutohide() && !lastFilterData.contains(d))
								lastFilterData.add(d);
							break;
						}
						
					}
				}
			}
		}
		
		/**
		 * Renvoie le tableau des depatements pris en compte dans le dernier filtee
		 * @return
		 */
		public List<DepartmentData> getLastFilterData () {
			return lastFilterData;
		}
		
		@Override
		public boolean hasData() {
			for (DepartmentData data : datas) {
				if(data.hasData())
					return true;
			}
			return false;
		}
		
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	class DepartmentData extends DataGroup {
		private static final long serialVersionUID = 2417328122226466229L;
		
		private final Department department;
		private final List<StudyClass> classes;
		private final List<PromotionData> datas = new ArrayList<>();
		
		private DepartmentFilter lastFilter;//dernier filtee pour le departement
		private List<PromotionData> lastFilterData = new ArrayList<>();//protions qui ont satisfait au dernier filtre pour le departement
		
		private Box container = Box.createVerticalBox();
		
		private PromotionDao promotionDao;
		
		/**
		 * Initialisation des coditions de filtrage d'un departement
		 * @param department
		 * @param classes
		 */
		public DepartmentData(Department department, List<StudyClass> classes) {
			super();
			this.department = department;
			this.classes = classes;
			promotionDao = daoFactory.findDao(PromotionDao.class);
			this.init();
		}
		
		private void init() {
			for (StudyClass sc : classes) {
				Promotion promotion = promotionDao.find(currentYear, department, sc);
				PromotionData panel = new PromotionData(promotion);
				datas.add(panel);
				container.add(panel);
			}
			
			this.add(container, BorderLayout.CENTER);
		}
		
		@Override
		public void dispose() {
			for (PromotionData data : datas) {
				data.dispose();
			}
		}
		
		/**
		 * @return the department
		 */
		public Department getDepartment() {
			return department;
		}

		/**
		 * @param showing
		 * @param lastFilter
		 */
		public void setShowing (boolean showing, DepartmentFilter lastFilter) {
			super.setShowing(showing);
			this.lastFilter = lastFilter;
			lastFilterData.clear();
			
			if(this.lastFilter == null) {
				for (PromotionData p : datas) {
					p.setShowing(showing);
					if(p.hasData() && showing)
						lastFilterData.add(p);
				}
			} else {
				for (PromotionData p : datas) {
					boolean show = false;
					for (StudyClass sc : lastFilter.getClasses()) {
						if(sc.getId() == p.getPromotion().getStudyClass().getId()) {
							show = true;
							break;
						}
					}
					p.setShowing(show);
					if(show && hasData() && !lastFilterData.contains(p))
						lastFilterData.add(p);
				}
			}
		}
		
		/**
		 * @return the lastFilterData
		 */
		public List<PromotionData> getLastFilterData() {
			return lastFilterData;
		}

		@Override
		public boolean hasData() {
			for (PromotionData p : datas) {
				if(p.hasData())
					return true;
			}
			return false;
		}
	}
	
	class PromotionData  extends DataGroup {
		private static final long serialVersionUID = -6393380830831357749L;
		
		private final Promotion promotion;
		private PromotionPaymentTableModel tableModel;
		private Table table;
		
		private JPopupMenu popupMenu = new JPopupMenu();
		
		private JMenuItem itemEditProfil = new JMenuItem("Mofifier l'identité", new ImageIcon(R.getIcon("usredit")));
		private JMenuItem itemEditInscription = new JMenuItem("Mofifier l'inscription", new ImageIcon(R.getIcon("edit")));
		private JMenuItem itemOpenFile = new JMenuItem("Fiche individuelle", new ImageIcon(R.getIcon("report")));
		private JMenuItem itemDelete = new JMenuItem("Suprimer l'inscription", new ImageIcon(R.getIcon("close")));
		
		/**
		 * @param promotion
		 */
		public PromotionData(Promotion promotion) {
			super();
			this.promotion = promotion;
			tableModel = new PromotionPaymentTableModel(daoFactory);
			tableModel.setPromotion(promotion);
			table = new Table(tableModel);
			
			this.add(new TablePanel(table, promotion.getStudyClass().getAcronym()+" "+promotion.getDepartment().getName(), false), BorderLayout.CENTER);
			this.setBorder(EMPTY_BOTTOM_BORDER);
			
			if(tableModel.getRowCount() == 0) {
				this.setAutohide(true);
			}
			
			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(table.getSelectedRow() != -1 && e.getClickCount() == 2) {
						itemOpenFile.doClick();
					}
 				}
				
				@Override
				public void mouseReleased(MouseEvent e) {
					if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
						itemDelete.setEnabled(academicYearDao.isCurrent(promotion.getAcademicYear()));
						itemEditInscription.setEnabled(academicYearDao.isCurrent(promotion.getAcademicYear()));
						popupMenu.show(table, e.getX(), e.getY());
					}
				}
			});
			
			popupMenu.add(itemOpenFile);
			popupMenu.addSeparator();
			popupMenu.add(itemEditProfil);
			popupMenu.add(itemEditInscription);
			popupMenu.add(itemDelete);
			
			itemOpenFile.addActionListener(event -> {
				listener.onAction(tableModel.getRow(table.getSelectedRow()));
			});
			itemDelete.addActionListener(event ->  {
				InscriptionDataRow row = tableModel.getRow(table.getSelectedRow());
				String message = "Voulez-vous vraiment suprimer\nl'inscription de '"+row.getInscription().getStudent()+"'?";
				message += "\n\nN.B: Cette opération est ireversible";
				int status = JOptionPane.showConfirmDialog(null, message, "Supression de l'inscription", JOptionPane.OK_CANCEL_OPTION);
				if(status == JOptionPane.OK_OPTION) {
					if(row.getPayments().size() != 0) {
						JOptionPane.showMessageDialog(mainWindow, "Impossible d'effectuer cette operation, "
								+ "\ncar la fiche individuel de cette etudiant contiens \naumoin une données",
								"Alert", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					try {
						inscriptionDao.delete(row.getInscription().getId());
					} catch (DAOException e) {
						JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			//inscription
			itemEditInscription.addActionListener(event -> {
				if(dialogInscription == null) {
					dialogInscription = new JDialog();
					dialogInscription.setModal(true);
					dialogInscription.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					
					formRegister = new FormReRegister(mainWindow, inscriptionDao, studentDao, false);
					formRegister.setCurrentYear(currentYear);
					dialogInscription.getContentPane().add(formRegister, BorderLayout.CENTER);
					dialogInscription.getContentPane().setBackground(FormUtil.BKG_START);
					formRegister.getBtnCancel().addActionListener(cancelUpdateInscription);
					dialogInscription.pack();
				}
				
				InscriptionDataRow row = tableModel.getRow(table.getSelectedRow());
				formRegister.setInscription(row.getInscription());
				dialogInscription.setLocationRelativeTo(mainWindow);
				dialogInscription.setVisible(true);
			});
			//==
			
			//student
			itemEditProfil.addActionListener(event -> {
				if(dialogStudent == null) {
					dialogStudent = new JDialog();
					dialogStudent.setModal(true);
					dialogStudent.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
					dialogStudent.setSize(650, 450);
					dialogStudent.setResizable(false);
					dialogStudent.getContentPane().setBackground(FormUtil.BKG_START);
					
					formStudent = new FormStudent(mainWindow);
					dialogStudent.getContentPane().add(formStudent, BorderLayout.CENTER);
				}
				
				InscriptionDataRow row = tableModel.getRow(table.getSelectedRow());
				formStudent.setStudent(row.getInscription().getStudent());
				dialogStudent.setLocationRelativeTo(mainWindow);
				dialogStudent.setVisible(true);
			});
			//==
		}
		
		@Override
		public void dispose() {
			tableModel.dispose();
		}
		
		@Override
		public void setShowing(boolean showing) {
			super.setShowing(showing);
			
			if (showing) {
				doFilter(promotion, progress.getValue()+1);
			}
		}

		@Override
		public boolean hasData() {
			return tableModel != null && tableModel.getRowCount() != 0;
		}

		/**
		 * @return the promotion
		 */
		public Promotion getPromotion() {
			return promotion;
		}
		
	}
	
	/**
	 * @author Esaie MUHASA
	 * Ecouteur des elements du datatable
	 */
	public static interface DatatableViewListener {
		/**
		 * Lorsque l'utilsiateur effectuel une action sur la ligne d'un des table du datatable
		 * @param row
		 */
		void onAction (InscriptionDataRow row) ;
	}
	
}
