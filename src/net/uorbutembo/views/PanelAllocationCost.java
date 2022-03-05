/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.dao.AllocationCostDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormAllocationCost;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelAllocationCost extends Panel {
	private static final long serialVersionUID = -6573773499254033089L;
	
	private final Button btnGraphics = new Button(new ImageIcon(R.getIcon("dashboard")), "Grahiques");
	private final Button btnUpdate = new Button(new ImageIcon(R.getIcon("edit")), "Modifier");
	private final Panel center = new Panel(new BorderLayout());
	private final Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
	private FormAllocationCost form;
	private final Panel chartPanel = new Panel();
	private AllocationCostDao allocationCostDao;
	
	private AcademicYear currentYear;
	
	/**
	 * @param mainWindow
	 */
	public PanelAllocationCost(MainWindow mainWindow) {
		super(new BorderLayout());
		allocationCostDao = mainWindow.factory.findDao(AllocationCostDao.class);
		init();
	}
	
	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(AcademicYear currentYear) {
		this.currentYear = currentYear;
	}

	private void init() {
		
		//top
		top.add(btnGraphics);
		top.add(btnUpdate);
		
		btnUpdate.addActionListener(event -> {
			center.removeAll();
			if(form == null) {
				this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				Thread t = new Thread(() -> {
					center.removeAll();
					form = new FormAllocationCost(allocationCostDao);
					center.add(form, BorderLayout.CENTER);
					center.revalidate();
					center.repaint();
					btnGraphics.setVisible(true);
					btnUpdate.setVisible(false);
					setCursor(Cursor.getDefaultCursor());
				});
				
				t.start();
			} else {		
				center.removeAll();
				center.add(form, BorderLayout.CENTER);
				center.revalidate();
				center.repaint();
				btnGraphics.setVisible(true);
				btnUpdate.setVisible(false);
			}
			
		});
		
		btnGraphics.setVisible(false);
		btnGraphics.addActionListener(event -> {
			center.removeAll();
			center.add(chartPanel, BorderLayout.CENTER);
			center.revalidate();
			center.repaint();
			btnGraphics.setVisible(false);
			btnUpdate.setVisible(true);
		});
		//--top
		
		center.add(chartPanel, BorderLayout.CENTER);
		
		this.add(top, BorderLayout.NORTH);
		this.add(center, BorderLayout.CENTER);

	}

}
