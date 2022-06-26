/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.tools.FormUtil.BKG_END;
import static net.uorbutembo.tools.FormUtil.BKG_START;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AcademicFeeDao;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.PromotionDao;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.Config;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormAcademicYear;
import net.uorbutembo.views.models.AcademicYearTableModel;
/**
 * @author Esaie MUHASA
 *
 */
public class Sidebar extends Panel implements ItemListener{
	private static final long serialVersionUID = -1925348829690899865L;

	private static final ImageIcon ICON_FULL_SCREEN = new ImageIcon(R.getIcon("viewInFullscreen"));
	private static final ImageIcon ICON_CLOSE_FULL_SCREEN = new ImageIcon(R.getIcon("exitFullscreen"));
	

	private final JLabel logo = new JLabel(new ImageIcon(Config.find("appMainIcon")));
	private final JLabel title = new JLabel(Config.find("appShortName"));
	
	public final MigLayout layout = new MigLayout("wrap, fillx, insets 0", "[fill]", "[]0[]");
	private final Panel header = new Panel(new BorderLayout());
	private final Panel footer = new Panel(new BorderLayout());
	private final Panel body = new Panel(layout);//conteneur des items du menu
	private final JScrollPane scroll = new JScrollPane(body);
	private final MenuItemListener listener;
	
	private final  List<MenuItem> items = new ArrayList<>();
	
	private final DefaultComboBoxModel<AcademicYear> comboModel = new  DefaultComboBoxModel<AcademicYear>();
	private final ComboBoxTool comboBox = new ComboBoxTool(comboModel);
	private final ButtonTool btnPupup = new ButtonTool();
	private final PopupTool popup = new PopupTool();
	private AcademicYeatDialog yearDialog;
	
	private final List<YearChooserListener> yearChooserListeners = new ArrayList<>();
	private final DAOAdapter<AcademicYear> yearAdapter = new DAOAdapter<AcademicYear>() {

		@Override
		public synchronized void onCreate (AcademicYear e, int requestId) {
			comboBox.removeItemListener(Sidebar.this);
			comboModel.insertElementAt(e, 0);
			if (comboBox.getSelectedIndex() != 0)
				comboBox.setSelectedIndex(0);
			
			AcademicYear year = comboModel.getElementAt(comboBox.getSelectedIndex());
			for (YearChooserListener listener : yearChooserListeners)
				listener.onChange(year);
			
			comboBox.addItemListener(Sidebar.this);
		}

		@Override
		public synchronized void onUpdate (AcademicYear e, int requestId) {
			comboBox.removeItemListener(Sidebar.this);
			comboModel.removeAllElements();
			onCurrentYear(e);
			for (YearChooserListener listener : yearChooserListeners)
				listener.onChange(e);
			comboBox.addItemListener(Sidebar.this);
		}

		@Override
		public synchronized void onDelete (AcademicYear e, int requestId) {
			
		}

		@Override
		public synchronized void onCurrentYear(AcademicYear year) {
			if (comboModel.getSize() == 0) {
				List<AcademicYear> years = academicYearDao.findAll();
				for (AcademicYear y : years) 
					comboModel.addElement(y);
			}
		}
		
	};
	
	private MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			popup.show(Sidebar.this, 10, Sidebar.this.getHeight() - 120);
		}
		
	};
	
	private AcademicYearDao academicYearDao;
	private final MainWindow mainWindow;
	
	/**
	 * @param mainWindow
	 */
	public Sidebar (MainWindow mainWindow) {
		super(new BorderLayout());
		listener = mainWindow.getWorkspace();
		this.mainWindow = mainWindow;
		
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicYearDao.addYearListener(yearAdapter);
		academicYearDao.addListener(yearAdapter);
		
		scroll.getViewport().setOpaque(false);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		scroll.setViewportBorder(null);
		
		add(scroll, BorderLayout.CENTER);
        
        initHeader();
        initFooter();
	}
	
	/**
	 * initialisation du header du sidebar
	 */
	private void initHeader() {
		header.add(title, BorderLayout.CENTER);
		header.add(logo, BorderLayout.WEST);
		
		title.setFont(new Font("Arial", Font.PLAIN, 40));
		title.setForeground(Color.LIGHT_GRAY);
		header.setBorder(new EmptyBorder(0, 5, 0, 10));
		
		add(header, BorderLayout.NORTH);
	}
	
	/**
	 * initialisation du footer du sidebar
	 */
	private void initFooter() {
		footer.add(btnPupup, BorderLayout.WEST);
		footer.add(comboBox, BorderLayout.CENTER);
		footer.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		comboBox.addItemListener(this);
		btnPupup.addMouseListener(mouseAdapter);
		
		add(footer, BorderLayout.SOUTH);
	}
	
	@Override
	public void itemStateChanged (ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED)
			return;
		
		AcademicYear year = comboModel.getElementAt(comboBox.getSelectedIndex());
		for (YearChooserListener listener : yearChooserListeners)
			listener.onChange(year);
	}
	
	/**
	 * Ecoute du combo de selection d'un annee academique
	 * @param ls
	 */
	public void addYearChooserListener (YearChooserListener ls) {
		if(!yearChooserListeners.contains(ls))
			yearChooserListeners.add(ls);
	}
	
	/**
	 * Desabonnement d'un listener
	 * @param ls
	 */
	public void removeYearChooserListener (YearChooserListener ls) {
		yearChooserListeners.remove(ls);
	}
	
	/**
	 * Ajout d'un item au menu
	 * @param item
	 * @return
	 */
	public MenuItem addItem (MenuItemModel<?>  item) {
		return this.addMenu(item);
	}

	
	/**
	 * @return the items
	 */
	public List<MenuItem> getItems() {
		return items;
	}

	/**
	 * Ajout d'un element au menu
	 * @param <H>
	 * @param model
	 * @return
	 */
	public  <H> MenuItem addMenu (MenuItemModel<H> model) {
		MenuItem item = new MenuItem  (this, model, this.listener);
        //body.add(item, "h "+(40 * model.getItems().size())+"!");
		body.add(item, "h 40!");
		
		if(this.items.isEmpty()) {//le premier item est active par defaut
			item.setCurrent(true);
		}
		
		this.items.add(item);
        return item;
    }
	
	/**
	 * Ajout d'un item au menu
	 * @param item
	 * @return
	 */
	public Sidebar addMenu (MenuItem item) {
		this.body.add(item);
		this.items.add(item);
		return this;
	}
	
	/**
	 * Affichage de la fenetre de configuration d'une annee academiques
	 */
	private void showAcademicYearConfig () {
		if(yearDialog == null)
			yearDialog = new AcademicYeatDialog(mainWindow);
		
		yearDialog.setLocationRelativeTo(mainWindow);
		yearDialog.setVisible(true);
	}
	
	/**
	 * mise en plaine ecran de la fennetre principale
	 * @param fullscreen
	 */
	private void setFullScreenMainWindow (boolean fullscreen) {
		mainWindow.setFullScreen(fullscreen);
	}
	
	/**
	 * lors de la demnde de fermeture de l'application
	 */
	private void closeApplication () {
		mainWindow.doClose();
	}
	
    @Override
    protected void paintComponent(Graphics grphcs) {
    	super.paintComponent(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gra = new GradientPaint(0, 0, BKG_START, this.getWidth(), 0, BKG_END);
        g2.setPaint(gra);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
    
    
    private static final class ButtonTool extends MenuItemButton {
		private static final long serialVersionUID = 6585041084631602673L;
    	
		private static final Dimension DEFAULT_SIZE = new Dimension(40, 40);
		private static final BasicStroke STROKE = new BasicStroke(1.4f);
		private final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				hovered = true;
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				hovered = false;
				repaint();
			}
		};
		
		private Image icon;
		private boolean hovered = false;
		
		public ButtonTool() {
			super("");
			setPreferredSize(DEFAULT_SIZE);
			setMinimumSize(DEFAULT_SIZE);
			setMaximumSize(DEFAULT_SIZE);
			
			try {
				icon = ImageIO.read(new File(R.getIcon("menu-tools")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			setOpaque(false);
			setBorder(new EmptyBorder(1, 40, 1, 20));
			addMouseListener(mouseAdapter);
		}
		
		@Override
		protected void paintComponent(Graphics grphcs) {
			int w = 34, h = w, x = 2, y = 2;
			pressedPoint = null;
			super.paintComponent(grphcs);
			
			Graphics2D g2 = (Graphics2D) grphcs;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			GradientPaint gra = new GradientPaint(0, 0, FormUtil.BKG_DARK, getWidth(), 0, FormUtil.BKG_DARK.darker());
			g2.setPaint(gra);
			g2.fillOval(x, y, w+x, h+y);
			g2.setColor(hovered? FormUtil.ACTIVE_COLOR : FormUtil.BORDER_COLOR);
			g2.setStroke(STROKE);
			g2.drawOval(x*2, y*2, w-4, h-4);
			g2.drawImage(icon, 7+x, 4+y, 22, 28, null);
			
		}
    }
    
    private static final class ComboBoxTool extends ComboBox<AcademicYear> {
		private static final long serialVersionUID = -9198030526636496335L;
		
		public static final EmptyBorder DEFAULT_EMPTY_BORDER = new EmptyBorder(2, 15, 2, 15);

		public ComboBoxTool(ComboBoxModel<AcademicYear> model) {
			super("", model);
		}
		
		@Override
		protected void init() {
			setOpaque(false);
			setForeground(DEFAULT_COLOR);
	    	setBorder(DEFAULT_EMPTY_BORDER);
	    	setBackground(FormUtil.BKG_DARK);
	    	setUI(new CustomComboUI(this));
	    	
	    	
	    	//on utilise le rendu par de faut du JList
	    	setRenderer(new DefaultListCellRenderer() {
	    		private static final long serialVersionUID = 1L;
	    		
	    		@Override
	    		public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
	    			Component com = super.getListCellRendererComponent(jlist, o, i, bln, bln1);
	    			setBorder(EMPTY_BORDER);
	    			if (bln) {
	    				com.setBackground(FormUtil.BORDER_COLOR);
	    			}
	    			com.setForeground(DEFAULT_COLOR);
	    			return com;
	    		}
	    	});
		}
		
	    @Override
	    protected void paintBorder(Graphics g) {
	    	
	    	Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

	        g2.setColor(mouseOver? FormUtil.ACTIVE_COLOR : FormUtil.BORDER_COLOR);
			g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 40, 40);
	    }
	    
	    @Override
	    protected void paintComponent(Graphics g) {
	    	Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        
	        g2.setColor(getBackground());
	        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 40, 40);
	        
	    	super.paintComponent(g);
	    }
	    
	    private final class CustomComboUI extends ComboUI {
	    	
	    	public CustomComboUI(ComboBox<?> combo) {
	    		super(combo);
	    	}
	    	
	    	@Override
	    	protected void createLineStyle(Graphics2D g2) {}
	    	
	    }
    }
    
    /**
     * Listener du combobox permetant de selectionner une annee academique
     * @author Esaie MUHASA
     */
    public static interface YearChooserListener {
    	
    	/**
    	 * lors de la selection d'une nouvelle annee academique
    	 * @param year
    	 */
    	void onChange (AcademicYear year);
    }
    
    public final class PopupTool extends JPopupMenu {
		private static final long serialVersionUID = -9121297130688307056L;
		
		private final JMenuItem [] items = {
				new JMenuItem("Année académique", new ImageIcon(R.getIcon("events"))),
				new JMenuItem("Plain écran", ICON_FULL_SCREEN),
				new JMenuItem("Quitter", new ImageIcon(R.getIcon("minus")))
		};
		
		/**
		 * ecoute de items du menu
		 * le name d'un item correpond a son index
		 */
		private final ActionListener itemAction = event -> {
			JMenuItem item = (JMenuItem) event.getSource();
			int itemIndex = Integer.parseInt(item.getName());
			switch (itemIndex) {
				case 0:{
					showAcademicYearConfig();
				}break;
				case 1:{
					boolean fullScreen = item.getIcon() == ICON_FULL_SCREEN;
					if (fullScreen)
						item.setIcon(ICON_CLOSE_FULL_SCREEN);
					else
						item.setIcon(ICON_FULL_SCREEN);
					setFullScreenMainWindow(fullScreen);
				}break;
				case 2:{
					closeApplication();
				}break;
			}
		};
		
		public PopupTool() {
			super();
			setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			for (int i = 0; i < items.length; i++) {
				items[i].setBackground(FormUtil.BKG_DARK);
				items[i].setForeground(Color.WHITE);
				items[i].setName(i+"");
				items[i].addActionListener(itemAction);
				add(items[i]);
				if(i==1)
					addSeparator();
			}
			
			setOpaque(false);
			setBackground(BKG_END);
		}
		
		@Override
		protected void paintBorder(Graphics g) {
			super.paintBorder(g);
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        
	        g2.setColor(FormUtil.ACTIVE_COLOR);
	        g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
	        
	        g2.setColor(FormUtil.BKG_DARK);
	        g2.fillRect(1, 1, getWidth()-2, getHeight()-2);
		}
    	
    }
    
    
    public static class AcademicYeatDialog extends JDialog {
		private static final long serialVersionUID = -5960791940678862938L;
		
		private final JMenuItem itemDelete = new  JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
		private final JMenuItem itemUpdate = new  JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
		private final JPopupMenu menu = new JPopupMenu();
		
		private final AcademicYearTableModel tableModel;
		private final Table table;
		private FormAcademicYear formYear;
		
		private AcademicYearDao academicYearDao;
		private AcademicFeeDao academicFeeDao;
		private PromotionDao promotionDao;
		private AnnualSpendDao annualSpendDao;
		
		private final MouseAdapter mouseListener = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1)
					menu.show(table, e.getX(), e.getY());
			}
		};
		
		/**
		 * @param mainWindow
		 */
		public AcademicYeatDialog(MainWindow mainWindow) {
			super(mainWindow, "Configuration des années académiques", true);
			
			academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
			academicFeeDao = mainWindow.factory.findDao(AcademicFeeDao.class);
			promotionDao = mainWindow.factory.findDao(PromotionDao.class);
			annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
			
			tableModel = new AcademicYearTableModel(academicYearDao);
			table = new Table(tableModel);
			final TablePanel tablePanel = new TablePanel(table, "Liste des années academiques");
			final Panel root = new Panel(new BorderLayout(5, 5));
			
			formYear = new FormAcademicYear(mainWindow);
			
			initPupup();
			
			root.add(formYear, BorderLayout.NORTH);
			root.add(tablePanel, BorderLayout.CENTER);
			root.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			getContentPane().add(root, BorderLayout.CENTER);
			getContentPane().setBackground(FormUtil.BKG_DARK);
			
			pack();
			setMinimumSize(new Dimension(getWidth(), 400));
			setSize(getWidth()+250, 450);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setLocationRelativeTo(mainWindow);
			
			tableModel.reload();
		}
		
		/**
		 * Creation du popup et ecouter des evenements
		 */
		private void initPupup() {
			menu.add(itemUpdate);
			menu.add(itemDelete);
			
			table.addMouseListener(mouseListener);
			
			itemDelete.addActionListener(event -> {
				AcademicYear year = tableModel.getRow(table.getSelectedRow());
				if(annualSpendDao.checkByAcademicYear(year.getId()) || promotionDao.checkByAcademicYear(year.getId())
						|| academicFeeDao.checkByAcademicYear(year.getId() )) {
					String message = "Impossible de supprimer l'année "+year.getLabel()+",\ncar autres données de la base de données \nsont liée à cette occurence.";
					message += "\nEn plus les suppressions récursives ne sont pas pris en charge";
					JOptionPane.showMessageDialog(this, message, "Echec de suppression", JOptionPane.ERROR_MESSAGE);
				} else {
					academicYearDao.delete(year.getId());
				}
			});
			
			itemUpdate.addActionListener(event -> {
				AcademicYear year = tableModel.getRow(table.getSelectedRow());
				formYear.setAcademicYear(year);
			});
		}
    	
    }
}
