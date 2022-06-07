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
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
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
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.dao.StudentDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.swing.charts.PieModel;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.components.DialogStudentExportConfig.ExportConfig;
import net.uorbutembo.views.components.SidebarStudents.DepartmentFilter;
import net.uorbutembo.views.components.SidebarStudents.FacultyFilter;
import net.uorbutembo.views.forms.FormReRegister;
import net.uorbutembo.views.forms.FormStudent;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PromotionPaymentTableModel;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRowListener;
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
	
	private final InscriptionDao inscriptionDao;
	private final StudentDao studentDao;
	private final AcademicYearDao academicYearDao;
	private final PromotionDao promotionDao;
	private final PaymentFeeDao paymentFeeDao;
	
	/**
	 * Ecoute du button d'annulation d'annulation des mis en jours 
	 * d'une inscription
	 */
	private final ActionListener cancelUpdateInscription = (event) -> {
		dialogInscription.setVisible(false);
	};
	
	private final InscriptionDataRowListener dataRowListener = new InscriptionDataRowListener() {
		
		@Override
		public void onReload(InscriptionDataRow row) {}
		
		@Override
		public void onLoad(InscriptionDataRow row) {
			progress.setValue(progress.getValue()+1);
			progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") "+row.getInscription().getStudent().toString());
		}
		
		@Override
		public void onDispose(InscriptionDataRow row) {}
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
		this.promotionDao = daoFactory.findDao(PromotionDao.class);
		this.paymentFeeDao = daoFactory.findDao(PaymentFeeDao.class);
		this.progress = progress;
		this.listener = listener;
		
		add(scroll, BorderLayout.CENTER);
		
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
	 * @return
	 */
	public JTableHeader getTableHeader () {
		for (FacultyData data : facultyDatas) {
			for (DepartmentData dep : data.datas) {
				if(!dep.datas.isEmpty())
					return dep.datas.get(0).table.getTableHeader();
			}
		}
		
		return null;
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
		
		for (FacultyData f : facultyDatas) {
			f.dispose();
		}
		facultyDatas.clear();
		
		progress.setVisible(true);
		progress.setString("Chargement des comptes pour "+currentYear.toString());
		progress.setValue(0);
		int max = inscriptionDao.countByAcademicYear(currentYear);
		progress.setIndeterminate(false);
		progress.setMaximum(max);
		
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
		progress.setString("Filtrage des données");
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
	 * @param config
	 * @param models
	 */
	public synchronized void exportToExcel (String fileName, ExportConfig config, PieModel ...models) {
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
			
			//EXPORTATION GLOBALE
			if(config.isSelected(1)) {
				createGlobalSheet(book, config, models);
			}
			//===
			
			if (config.isSelected(0)) {//si on doit exporter les details
				int 
					colCount = config.countChecked(0) + (config.getStatusAt(0, 1)? 2 : 0),
					step = config.getStatusAt(0, 1)? 2 : 0,
					lastIndex = config.getStatusAt(0, 1)? 3 : 0;
				
				int columnIndex [] = new int [colCount];//index des colonnes qui doivent etre afficher
				if (lastIndex != 0) {
					for(int i = 0; i <= step; i ++)
						columnIndex[i] = i;
				}
				for (int i = lastIndex != 0 ? 1 : 0; i < config.countAt(0); i++) {
					if (config.getStatusAt(0, i))
						columnIndex[lastIndex++] = i + step;
				}
				
				if(config.isSelected(2) && config.hasChecked(2)) {//groupement des donnees					
					if (config.getStatusAt(2, 0)) {//groupement par faculte prioritaire
						for (FacultyData data : lastFilterData)//creation d'une feuille par faculte
							createFacultySheet(data, columnIndex, book, config);
					} else if (config.getStatusAt(2, 1)) {//groupement par departement prioritaire
						for (FacultyData filter : lastFilterData) {							
							for (DepartmentData data : filter.datas)//creation d'une feuille par departement
								createDepartmentSheet(data, book, config);
						}
					} else {//groupement par classe d'etude prioritiare
						final List<StudyClass> classes = new ArrayList<>();
						final List<Department> deps = new ArrayList<>();
						
						//prepare metadata
						for (int i = 0; i < lastFilter.length; i++) {
							for (int j = 0, count = lastFilter[i].getDepartments().size(); j < count; j++){
								deps.add(lastFilter[i].getDepartments().get(j).getDepartment());
								for (int k = 0, ccount = lastFilter[i].getDepartments().get(j).getClasses().size(); k < ccount; k++) {
									StudyClass sc = lastFilter[i].getDepartments().get(j).getClasses().get(k);
									boolean addable = true;
									
									for (StudyClass s : classes) {
										if(s.getId() == sc.getId()) {
											addable = false;
											break;
										}
									}
									
									if (addable) classes.add(sc);
								}
							}
						}
						
						final Department[][] classement = new Department[classes.size()][0];;
						
						for (int j = 0; j < classes.size(); j++) {
							List<Department>  tmpDeps = new ArrayList<>();
							for (int i = 0; i < deps.size(); i++){
								if(promotionDao.check(currentYear, deps.get(i), classes.get(j))) {								
									tmpDeps.add(deps.get(i));
								}
								progress.setValue(progress.getValue() + 1);
								progress.setMaximum(progress.getMaximum() + 1);
								progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Préparation des données...");
							}
							final Department[] departments = new Department[tmpDeps.size()];
							if (!tmpDeps.isEmpty()) {
								for (int i = 0; i < departments.length; i++) 
									departments[i] = tmpDeps.get(i);
							} 
							classement[j] = departments;
						}
						//== metadata
						
						for (int index = 0; index < classes.size(); index++) {
							StudyClass s = classes.get(index);
							createStudyClassSheet(s, classement[index], book, config);//creation d'une feille par classe d'etude
						}
					}
				} else {//pas de groupement des donnees
					
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
	 * exportation de la vue globale du rapport au format excel
	 * @param book
	 * @param config
	 * @param models
	 * @return
	 * @throws Exception
	 */
	private XSSFSheet createGlobalSheet (XSSFWorkbook book, ExportConfig config, PieModel ...models) throws Exception{
		XSSFSheet sheet = book.createSheet("globale");
		int columnCount = 0;
		int rowCount = -1;
		
		for(int i = 0; i < 3; i++) {//comptage des colonnes
			if (config.getStatusAt(1, i))
				columnCount++;
		}
		
		int columns [] = new int [columnCount], lastIndex = 0;
		for(int i = 0; i < 3; i++) {//recuperation des indexs de colone en afficher (solde, dette et effectifs)
			if (config.getStatusAt(1, i))
				columns[lastIndex++] = i;
		}
		columnCount += 1 + +config.countChecked(2);
		
		exportGlobalChart(sheet, columnCount, models);
		
		XSSFRow row = sheet.createRow(++rowCount);
		XSSFCell cell = null;
		//title of columns
		for (int i = 0; i < columns.length; i++) {
			cell = row.createCell(i+config.countChecked(2));
			cell.setCellValue(config.getTitleCofingGroup(1, i));
		}
		//==
		
		List<CellRangeAddress> merges = new ArrayList<>();
		
		//exporation des donnees
		if (config.isSelected(2) && config.hasChecked(2)) {//si on dois grouper les resultat
			//identification du groupement du premier niveau
			if (config.getStatusAt(2, 0)) {
				for (FacultyFilter filter : lastFilter) {
					int facultyRowStart = ++rowCount;
					row = sheet.createRow(rowCount);//titre de la faculte
					cell = row.createCell(0);
					cell.setCellValue(filter.getFaculty().getName());
					
					
					if (config.getStatusAt(2, 1)) {//si groument par departement
						
						for (int i = 0; i < filter.getDepartments().size(); i++) {
							DepartmentFilter subFilter = filter.getDepartments().get(i);
							Department dep = subFilter.getDepartment();
							
							if (config.getStatusAt(2, 2)) {// sous-sous groupement par classe d'etude
								row = (i != 0)? sheet.createRow(++rowCount) : row;//titre du department
								int departmentRowStart = rowCount;
								cell = row.createCell(1);
								cell.setCellValue(dep.getName());
								for (int j = 0; j < subFilter.getClasses().size(); j++) {
									StudyClass s = subFilter.getClasses().get(j);
									Promotion prom = promotionDao.find(currentYear, dep, s);
									row = (j != 0) ? sheet.createRow(++rowCount) : row;//titre de la classe d'etude
									createGlobalPromotionCells(prom, row, 1, columns);
								}
								if (departmentRowStart < rowCount) {
									CellRangeAddress addr = new CellRangeAddress(departmentRowStart, rowCount, 1, 1);
									merges.add(addr);
								}
								row = sheet.createRow(++rowCount);//sous total
							} else {
								createGlobalDepartmentCells(dep, subFilter.getClasses(), row, 0, columns);
							}
						}
						
						row = sheet.createRow(++rowCount);//sous total
					} else if (config.getStatusAt(2, 2)) {//si groupement par classe d'etude
						
						final List<StudyClass> classes = new ArrayList<>();
						final List<Department> deps = new ArrayList<>();
						
						//preparation des metadonnees
						for (int i = 0; i < lastFilter.length; i++) {//filtrage des classes d'etude
							for (int j = 0, count = lastFilter[i].getDepartments().size(); j < count; j++){
								deps.add(lastFilter[i].getDepartments().get(j).getDepartment());
								for (int k = 0, ccount = lastFilter[i].getDepartments().get(j).getClasses().size(); k < ccount; k++) {
									StudyClass sc = lastFilter[i].getDepartments().get(j).getClasses().get(k);
									boolean addable = true;
									
									for (StudyClass s : classes) {
										if(s.getId() == sc.getId()) {
											addable = false;
											break;
										}
									}
									
									if (addable) classes.add(sc);
								}
							}
						}
						//== metadata
						
						progress.setMaximum(classes.size());

						for (int index = 0; index < classes.size(); index++) {
							StudyClass s = classes.get(index);
							row = sheet.createRow(++rowCount);//titre de la classe d'etude
							createGlobalStudyClassCells(s, deps, row, 0, columns);
						}
						row = sheet.createRow(++rowCount);//sous total
					} else {//pas de sous-groument
						createGlobalFacultyCells(filter.getFaculty(), row, 0, columns);
					}
					
					CellRangeAddress addr = new CellRangeAddress(facultyRowStart, rowCount-1, 0, 0);//merging row by faculty data
					merges.add(addr);
				}
			} else if (config.getStatusAt(2, 1)) {//groupement de niveau-1 = department
				List<DepartmentFilter> departments = new ArrayList<>();
				for (FacultyFilter filter : lastFilter) {
					for (DepartmentFilter subFilter : filter.getDepartments())
						departments.add(subFilter);
				}
				
				if(config.getStatusAt(2, 2)) {//groupement de niveau classe d'etude
					for (DepartmentFilter filter : departments){
						int departmentRowStart = rowCount;
						Department dep = filter.getDepartment();
						row = sheet.createRow(++rowCount);//titre du department
						cell = row.createCell(1);
						cell.setCellValue(dep.getName());
						for (StudyClass s : filter.getClasses()) {
							Promotion prom = promotionDao.find(currentYear, dep, s);
							row = sheet.createRow(++rowCount);//titre de la classe d'etude
							createGlobalPromotionCells(prom, row, 2, columns);
						}
						CellRangeAddress addr = new CellRangeAddress(departmentRowStart, rowCount, 1, 1);
						merges.add(addr);
						row = sheet.createRow(++rowCount);//sous total
					}
				} else {//groupement de niveau classe d'etude
					for (DepartmentFilter filter : departments){						
						createGlobalDepartmentCells(filter.getDepartment(), filter.getClasses(), row, 1, columns);
					}
				}
			} else if (config.getStatusAt(2, 2)) {
				for (FacultyFilter filter : lastFilter) {
					for (DepartmentFilter subFilter : filter.getDepartments()) {
						for (StudyClass s : subFilter.getClasses()) {
							Promotion prom = promotionDao.find(currentYear, subFilter.getDepartment(), s);
							row = sheet.createRow(++rowCount);//titre de la classe d'etude
							createGlobalPromotionCells(prom, row, 0, columns);
						}
					}
				}
			}
		} else {//aucun groupement des donnees (resultat monolitique)
			
		}
		//export data

		for (int i = 0; i < columnCount; i++)
			sheet.autoSizeColumn(i);
		
		for (CellRangeAddress address : merges)
			sheet.addMergedRegion(address);
		
		return sheet;
	}
	
	/**
	 * Exportation des donnees sous forme de graphique
	 * @param sheet
	 * @param offsetColumns
	 * @param models
	 */
	private void exportGlobalChart (XSSFSheet sheet, int offsetColumns, PieModel ...models) {
		for (int i = 0; i < models.length; i++) {
			XSSFDrawing drawing = sheet.createDrawingPatriarch();
			
			XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, offsetColumns, (i * 30) + 1, 30, 30);
			
			XSSFChart chart = drawing.createChart(anchor);
			chart.setTitleText(models[i].getTitle());
			chart.setTitleOverlay(false);
			
			XDDFChartLegend legend = chart.getOrAddLegend();
			legend.setPosition(LegendPosition.TOP_RIGHT);
			XDDFDataSource<String> testOutcomes = XDDFDataSourcesFactory.fromArray(PieModel.toLabelsArray(models[i]));
			XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromArray(PieModel.toValuesArray(models[i]));
			
			XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
			chart.displayBlanksAs(null);
			data.setVaryColors(true);
			data.addSeries(testOutcomes, values);
			
			chart.plot(data);
		}
	}
	
	/**
	 * generation d'un feille pour une le detais de payement pour une faculte
	 * @param data
	 * @param book
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private XSSFSheet createFacultySheet (FacultyData data, int [] columnIndex, XSSFWorkbook book, ExportConfig config) throws Exception{
		
		FacultyFilter filter = data.lastFilter;
		
		int columnCount = columnIndex.length;
		short rowHeight = 400;
		int current = 0;
		
		XSSFSheet sheet = book.createSheet(filter.getFaculty().getAcronym());
		int rowCount = -1;
		
		//faculty name
		XSSFRow row = sheet.createRow(++rowCount);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue(data.getFaculty().getName()+" ("+currentYear.toString()+")");
		sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 0, columnCount-1));
		
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
		
		List<CellRangeAddress> merges = new ArrayList<>();

		for (DepartmentData dData : data.getLastFilterData()) {
			Department department = dData.getDepartment();
			
			if (config.getStatusAt(2, 1)) {
				style = book.createCellStyle();
				font = book.createFont();
				
				font.setFontName("Arial");
				font.setFontHeight(14);
				font.setItalic(false);
				font.setColor(IndexedColors.WHITE.index);
				
				style.setFont(font);
				style.setFillForegroundColor(IndexedColors.DARK_RED.index);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
				row = sheet.createRow(++rowCount);
				cell = row.createCell(0);
				cell.setCellValue(department.getName());
				cell.setCellStyle(style);
				merges.add(new CellRangeAddress(rowCount, rowCount, 0, columnCount-1));
			}
			
			for (PromotionData pData : dData.getLastFilterData()) {
				PromotionPaymentTableModel dataModel = pData.tableModel;
				
				if(dataModel.getRowCount() == 0)
					continue;
				
				if (config.getStatusAt(2, 2)) {					
					//class name
					style = book.createCellStyle();
					font = book.createFont();
					
					font.setFontName("Arial");
					font.setFontHeight(12);
					font.setItalic(false);
					font.setColor(IndexedColors.WHITE.index);
					
					style.setFont(font);
					style.setFillForegroundColor(IndexedColors.BLACK.index);
					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					
					row = sheet.createRow(++rowCount);
					cell = row.createCell(0);
					cell.setCellValue(pData.getPromotion().getStudyClass().getName());
					merges.add(new CellRangeAddress(rowCount, rowCount, 0, columnCount-1));
					cell.setCellStyle(style);
					//==
				}
				
				//titre des colones
				row = sheet.createRow(++rowCount);
				style = book.createCellStyle();
				style.setBorderBottom(BorderStyle.DOUBLE);
				for (int i = 0; i < columnIndex.length; i++) {					
					cell = row.createCell(i);
					cell.setCellValue(dataModel.getRow(0).getTitleAt(columnIndex[i]));
					cell.setCellStyle(style);
				}
				//==
				
				for (int i = 0, count = dataModel.getRowCount(); i < count; i++) {
					row = sheet.createRow(++rowCount);
					row.setHeight(rowHeight);
					
					current++;
					progress.setValue(current);
					progress.setString("("+current+"/"+progress.getMaximum() + ") "+dataModel.getValueAt(i, 1));
					
					for (int j = 0; j < columnCount; j++) {
						cell = row.createCell(j);
						cell.setCellValue(dataModel.getRow(i).getStringValueAt(columnIndex[j]));
					}
				}
				
				++rowCount;
			}
		}
		
		for (CellRangeAddress addr : merges)
			sheet.addMergedRegion(addr);
		
		for (int i = 0; i < columnCount; i++)
			sheet.autoSizeColumn(i);
		return sheet;
	}
	
	/**
	 * creation d'une feille pour un departement
	 * @param data
	 * @param book
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private XSSFSheet createDepartmentSheet (DepartmentData data, XSSFWorkbook book, ExportConfig config) throws Exception {
		XSSFSheet sheet = book.createSheet(data.getDepartment().getAcronym());
		
		return sheet;
	}
	
	/**
	 * creation d'une feuille pour une classe d'etude
	 * @param sc
	 * @param departements
	 * @param book
	 * @param config
	 * @return
	 * @throws Exception
	 */
	private XSSFSheet createStudyClassSheet (StudyClass sc, Department [] departements, XSSFWorkbook book, ExportConfig config) throws Exception{
		XSSFSheet sheet = book.createSheet(sc.getAcronym());
		
		return sheet;
	}
	
	/**
	 * creation des cellules d'une ligne contenant les donnees globale d'une classe d'etude
	 * @param sc
	 * @param departments
	 * @param row
	 * @param lastCelIndex : la derniere case deja utiliser pour la ligne
	 * @param columns
	 */
	private void createGlobalStudyClassCells (StudyClass sc, List<Department> departments, XSSFRow row, int lastCelIndex, int [] columns) {
		
		String label = "";
		List<Department>  tmpDeps = new ArrayList<>();
		
		for (int i = 0; i < departments.size(); i++){
			if(promotionDao.check(currentYear, departments.get(i), sc)) {								
				tmpDeps.add(departments.get(i));
				label += " "+departments.get(i).getAcronym()+",";
			}
			progress.setValue(progress.getValue() + 1);
			progress.setMaximum(progress.getMaximum() + 1);
			progress.setString("("+progress.getValue()+"/"+progress.getMaximum()+") Préparation des données...");
		}
		
		final Department[] classement = new Department[tmpDeps.size()];
		if (!tmpDeps.isEmpty()) {
			for (int i = 0; i < classement.length; i++) 
				classement[i] = tmpDeps.get(i);
			label = label.substring(0, label.length()-1);
			label = "("+label+")";
		} 
		
		XSSFCell cell = row.createCell(++lastCelIndex);
		cell.setCellValue(sc.getName());
		
		lastCelIndex++;
		for (int i = 0; i < columns.length; i++) {
			cell = row.createCell(lastCelIndex+i);
			if (i == 0 || i == 1) {
				double amount = paymentFeeDao.getSoldByPromotions(sc, classement, currentYear);
				if (i == 1) {//pour le calcul de dettes
					double total = 0;
					for(Department d : classement) {
						Promotion p = promotionDao.find(currentYear, d, sc);
						double count  = inscriptionDao.countByPromotion(p);
						total += count * p.getAcademicFee().getAmount();
					}
					amount = total - amount;
				}
				cell.setCellValue(amount);
			} else {
				int count = inscriptionDao.countByPromotions(sc, classement, currentYear);
				cell.setCellValue(count);
			}
		}
	}
	
	/**
	 * creation des cellules d'une ligne contenant les donnees globale d'un departement
	 * @param department
	 * @param row
	 * @param lastCelIndex : la derniere case deja utiliser pour la ligne
	 * @param columns
	 */
	private void createGlobalDepartmentCells (Department department, List<StudyClass> classes, XSSFRow row, int lastCelIndex, int [] columns) {
		XSSFCell cell = row.createCell(++lastCelIndex);
		cell = row.createCell(1);
		cell.setCellValue(department.getName());

		cell.setCellValue(department.getName());
		StudyClass classement [] = new StudyClass [classes.size()];
		for (int i = 0; i < classement.length; i++)
			classement[i] = classes.get(i);
		
		lastCelIndex++;
		for (int i = 0; i < columns.length; i++) {
			cell = row.createCell(lastCelIndex+i);
			if (i == 0 || i == 1) {
				double amount = paymentFeeDao.getSoldByPromotions(classement, department, currentYear);
				if (i == 1) {//pour le calcul de dettes
					double total = 0;
					for (StudyClass s : classement) {
						Promotion p = promotionDao.find(currentYear, department, s);
						double count  = inscriptionDao.countByPromotion(p);
						total += count * p.getAcademicFee().getAmount();
					}
					amount = total - amount;
				}
				cell.setCellValue(amount);
			} else {
				int count = inscriptionDao.countByPromotions(classement, department, currentYear);
				cell.setCellValue(count);
			}
		}
	}
	
	private void createGlobalFacultyCells (Faculty faculty, XSSFRow row, int lastCelIndex, int [] columns) {
		
	}
	
	/**
	 * chargement des donnees des cellules  d'une promotions
	 * @param promotion
	 * @param row
	 * @param lastCelIndex
	 * @param columns
	 * @return
	 */
	private XSSFCell createGlobalPromotionCells (Promotion promotion, XSSFRow row, int lastCelIndex, int [] columns) {
		XSSFCell cell = row.createCell(++lastCelIndex);
		cell.setCellValue(promotion.getStudyClass().getName());
		lastCelIndex++;
		for (int i = 0; i < columns.length; i++) {
			cell = row.createCell(lastCelIndex+i);
			if (i == 0 || i == 1) {
				double amount = paymentFeeDao.getSoldByPromotion(promotion);
				if (i == 1) {//pour le calcul de dettes
					double total = 0;
					double count  = inscriptionDao.countByPromotion(promotion);
					total += count * promotion.getAcademicFee().getAmount();
					amount = total - amount;
				}
				cell.setCellValue(amount);
			} else {
				int count = inscriptionDao.countByPromotion(promotion);
				cell.setCellValue(count);
			}
		}
		return cell;
	}
	
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
		public DataGroup () {
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
		private final JLabel labelCount = FormUtil.createSubTitle("0 étidiant");
		
		private JPopupMenu popupMenu = new JPopupMenu();
		
		private JMenuItem itemEditProfil = new JMenuItem("Mofifier l'identité", new ImageIcon(R.getIcon("usredit")));
		private JMenuItem itemEditInscription = new JMenuItem("Mofifier l'inscription", new ImageIcon(R.getIcon("edit")));
		private JMenuItem itemOpenFile = new JMenuItem("Fiche individuelle", new ImageIcon(R.getIcon("report")));
		private JMenuItem itemDelete = new JMenuItem("Suprimer l'inscription", new ImageIcon(R.getIcon("close")));
		
		private final TableModelListener tableModelListener = event -> {
			int count = tableModel.getRowCount();
			labelCount.setText(count+" étudiant"+(count>1? "s":""));
			if(count == 0)
				setAutohide(true);
		};
		
		private final MouseAdapter mouseAdapter = new MouseAdapter() {
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
		};
		
		private final ActionListener itemDeleteListener = event ->  {
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
		};
		
		private final ActionListener itemOpenFileListener = event -> {
			listener.onAction(tableModel.getRow(table.getSelectedRow()));
		};
		
		private final ActionListener itemEditInscriptionListener = event -> {
			if(dialogInscription == null) {
				dialogInscription = new JDialog();
				dialogInscription.setModal(true);
				dialogInscription.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
				
				formRegister = new FormReRegister(mainWindow, false);
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
		};
		
		private final ActionListener itemEditProfilListener = event -> {
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
		};
		
		/**
		 * @param promotion
		 */
		public PromotionData(Promotion promotion) {
			super();
			this.promotion = promotion;
			tableModel = new PromotionPaymentTableModel(daoFactory, dataRowListener);
			tableModel.addTableModelListener(tableModelListener);
			tableModel.setPromotion(promotion);
			table = new Table(tableModel);
			table.setShowVerticalLines(true);
			
			TablePanel tablePanel = new TablePanel(table, promotion.getStudyClass().getAcronym()+" "+promotion.getDepartment().getName(), false);
			tablePanel.getHeader().add(labelCount, BorderLayout.EAST);
			
			
			table.getColumnModel().getColumn(0).setMaxWidth(130);
			table.getColumnModel().getColumn(0).setWidth(130);
			table.getColumnModel().getColumn(0).setResizable(false);
			
			table.getColumnModel().getColumn(3).setMaxWidth(130);
			table.getColumnModel().getColumn(3).setMinWidth(130);
			table.getColumnModel().getColumn(3).setWidth(130);
			table.getColumnModel().getColumn(3).setResizable(false);
			
			table.getColumnModel().getColumn(4).setMaxWidth(140);
			table.getColumnModel().getColumn(4).setMinWidth(140);
			table.getColumnModel().getColumn(4).setWidth(140);
			table.getColumnModel().getColumn(4).setResizable(false);
			
			this.add(tablePanel, BorderLayout.CENTER);
			this.setBorder(EMPTY_BOTTOM_BORDER);
			
			if(tableModel.getRowCount() == 0) {
				this.setAutohide(true);
			}
			
			table.addMouseListener(mouseAdapter);
			
			popupMenu.add(itemOpenFile);
			popupMenu.addSeparator();
			popupMenu.add(itemEditProfil);
			popupMenu.add(itemEditInscription);
			popupMenu.add(itemDelete);
			
			itemOpenFile.addActionListener(itemOpenFileListener);
			itemDelete.addActionListener(itemDeleteListener);
			itemEditInscription.addActionListener(itemEditInscriptionListener);
			itemEditProfil.addActionListener(itemEditProfilListener);
		}
		
		@Override
		public void dispose() {
			tableModel.dispose();
			table.removeMouseListener(mouseAdapter);
			itemOpenFile.removeActionListener(itemOpenFileListener);
			itemDelete.removeActionListener(itemDeleteListener);
			itemEditInscription.removeActionListener(itemEditInscriptionListener);
			itemEditProfil.removeActionListener(itemEditProfilListener);
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
