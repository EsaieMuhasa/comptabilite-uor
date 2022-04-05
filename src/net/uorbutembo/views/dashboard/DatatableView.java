/**
 * 
 */
package net.uorbutembo.views.dashboard;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.Promotion;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.InscriptionDao;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.dashboard.PanelNavigation.DepartmentFilter;
import net.uorbutembo.views.dashboard.PanelNavigation.FacultyFilter;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PromotionPaymentTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class DatatableView extends Panel {
	private static final long serialVersionUID = 1491067953369363653L;
	
	private static final Border EMPTY_BOTTOM_BORDER = new EmptyBorder(5, 0, 5, 0);
	
	private List<FacultyData> facultyDatas = new ArrayList<>();
	private DAOFactory daoFactory;
	
	private Box container = Box.createVerticalBox();
	private JScrollPane scroll = FormUtil.createScrollPane(container);
	private AcademicYear currentYear;
	
	private JProgressBar progress;
	private FacultyFilter [] lastFilter;
	
	private InscriptionDao inscriptionDao;
	
	/**
	 * 
	 */
	public DatatableView(DAOFactory daoFactory, JProgressBar progress) {
		super(new BorderLayout());
		this.daoFactory = daoFactory;
		this.inscriptionDao = daoFactory.findDao(InscriptionDao.class);
		this.progress = progress;
		
		this.add(scroll, BorderLayout.CENTER);
		
		this.inscriptionDao.addListener(new DAOAdapter<Inscription>() {
			@Override
			public void onCreate(Inscription e, int requestId) {
				setFilter(lastFilter);
			}
			
			@Override
			public void onUpdate(Inscription e, int requestId) {
				setFilter(lastFilter);
			}
			
			@Override
			public void onDelete(Inscription e, int requestId) {
				setFilter(lastFilter);
			}
		});
	}
	
	/**
	 * il est obligatoire d'appeler cette methode pour le premier chargement du datatable
	 * et de luis fornir la liste complet des filtres des facultes
	 * @param filters
	 */
	public void firstLoad (FacultyFilter [] filters, AcademicYear currentYear) {
		this.currentYear = currentYear;
		this.lastFilter = filters;
		this.doStartFilter(filters);
		for (FacultyFilter filter : filters) {
			FacultyData  dataManager = new FacultyData(filter.getFaculty(), filter.getDepartments());
			container.add(dataManager);
			facultyDatas.add(dataManager);
			dataManager.setShowing(true);
		}
		
		container.add(Box.createVerticalGlue());
		progress.setVisible(false);
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
	public void setFilter (FacultyFilter [] filters) {
		this.lastFilter = filters;
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
					break;
				}
			}
			
			if(!show)
				data.setShowing(false, null);
		}
		
		progress.setVisible(false);
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
		private FacultyFilter lastFilter;//dernier filtre
		
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
			
			if (this.lastFilter == null) {
				for (DepartmentData d : datas) {
					d.setShowing(showing, null);
				}
			} else  {
				
				for (DepartmentData d : datas) {//
					d.setShowing(false, null);
				}
				
				for (DepartmentFilter df : lastFilter.getDepartments()) {
					
					for (DepartmentData d : datas) {
						if(d.getDepartment().getId() == df.getDepartment().getId()){
							d.setShowing(showing, df);
							break;
						}
						
					}
				}
			}
			
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
		private DepartmentFilter lastFilter;
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
			if(this.lastFilter == null) {
				for (PromotionData p : datas) {
					p.setShowing(showing);
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
				}
			}
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
	
}