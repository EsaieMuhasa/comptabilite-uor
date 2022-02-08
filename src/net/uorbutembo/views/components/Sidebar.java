/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.views.forms.FormUtil.BKG_END;
import static net.uorbutembo.views.forms.FormUtil.BKG_START;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.Faculty;
import net.uorbutembo.swing.Panel;
import resources.net.uorbutembo.R;
/**
 * @author Esaie MUHASA
 *
 */
public class Sidebar extends Panel {
	private static final long serialVersionUID = -1925348829690899865L;
	
	
	
	private final JLabel logo = new JLabel(new ImageIcon(R.getIcon("x32")));
	private final JLabel title = new JLabel("U.O.R");
	
	private final Panel header = new Panel(new BorderLayout());
	private final Panel body = new Panel();//conteneur des items du menu
	private final JScrollPane scroll = new JScrollPane();
	public final MigLayout layout = new MigLayout("wrap, fillx, insets 0", "[fill]", "[]0[]");	
	private MenuItemListener listener;
	
	private List<MenuItem> items = new ArrayList<>();
	
	/**
	 * constructeur d'initilisation du Sidebar
	 */
	public Sidebar(MenuItemListener listener) {
		super(new BorderLayout());
		this.listener = listener;
		
		body.setLayout(layout);
		
		scroll.getViewport().setOpaque(false);
//		scroll.setVerticalScrollBar(new ScrollBar());
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		scroll.setViewportBorder(null);
		scroll.setViewportView(body);
		
		
		this.add(scroll, BorderLayout.CENTER);
        
        this.initItems();
        this.initHeader();
	}
	
	/**
	 * initialisation du header du sidebar
	 */
	private void initHeader() {
		this.header.add(logo, BorderLayout.WEST);
		this.header.add(this.title, BorderLayout.CENTER);
		
		this.title.setFont(new Font("Arial", Font.PLAIN, 40));
		this.title.setForeground(Color.LIGHT_GRAY);
		this.header.setBorder(new EmptyBorder(0, 5, 0, 10));
		
		this.add(header, BorderLayout.NORTH);
	}
	
	/**
	 * test du rendu du sidebar
	 */
	private void initItems() {
		MenuItemModel<String> dashbord = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("dashboard")), "Tableau de bord", "dashboard");
		MenuItemModel<String> exportData = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("export")), "Exporter", "export");
		MenuItemModel<String> importData = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("import")), "Importer", "import");
		MenuItemModel<AcademicYear> years = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("events")), "Annee Academiques", "year");
		MenuItemModel<Faculty> facultys = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("database")), "Facultés", "faculty");

		MenuItemModel<String> inscription = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("student")), "Inscription", "inscription");
		MenuItemModel<String> sheet = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("card")), "Fiches individuels", "sheets");
		MenuItemModel<String> story = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("classeur")), "Palmaresse", "archive");
		MenuItemModel<String> help = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("help")), "Manuel d'utilisation", "help");
		MenuItemModel<String> param = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("console")), "Configuration logiciel", "params");
		MenuItemModel<String> journal = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("status")), "Journal d'erreurs", "logg");
		MenuItemModel<String> config = new DefaultMenuItemModel<>(new ImageIcon(R.getIcon("cog")), "Autres configuration", "more");
		
		//sub config
		config.addItems("Rubriques bidgetaires", "Frais universitaire", "Classe d'études");
		importData.addItems("Exel 2007 ou plus (.xlsx)", "Exel 2003 (.xls)", "SQL File");
		// --sub config
		
		this.addMenu(dashbord);
		this.addMenu(years);	
		this.addMenu(facultys);
		this.addMenu(inscription);
		this.addMenu(sheet);
		this.addMenu(story);
		this.addMenu(exportData);
		this.addMenu(importData);
		this.addMenu(config);
		this.addMenu(help);
		this.addMenu(param);
		this.addMenu(journal);
	}
	
	public void onItemClicked (MenuItem item) {
		if(item.isCurrent()) {
			return;
		}
		
		for (MenuItem i : items) {
			if (i.isCurrent()) {
				i.setCurrent(false);
				break;
			}
		}
		
		item.setCurrent(true);
	}
	
	/**
	 * Ajout d'un element au menu
	 * @param <H>
	 * @param model
	 * @return
	 */
	public  <H> MenuItem addMenu (MenuItemModel<H> model) {
		MenuItem item = new MenuItem  (this, model, this.listener);
//        panel.add(item, "h "+(40 * model.getItems().size())+"!");
		body.add(item, "h 40!");
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
	
    @Override
    protected void paintComponent(Graphics grphcs) {
    	super.paintComponent(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gra = new GradientPaint(0, 0, BKG_START, this.getWidth(), 0, BKG_END);
        g2.setPaint(gra);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

}
