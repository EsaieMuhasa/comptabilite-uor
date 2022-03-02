/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormPaymentFee;
import net.uorbutembo.views.forms.FormUtil;
import net.uorbutembo.views.models.PaymentFeeTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class IndividualSheet extends Panel {

	private static final long serialVersionUID = 4342531142487279020L;
	public static final Dimension A4_LAND = new Dimension(1123, 794);
	private static final String TITLE = "Fiche individuelle de paiement des frais académiques exercice ";
	
	private Panel page = new Panel(new BorderLayout());
	private JScrollPane scroll = FormUtil.createScrollPane(page);
	private JPanel container = new JPanel(new BorderLayout());
	private IndividualSheetHeader header = new IndividualSheetHeader();
	
	private PaymentFeeTableModel tableModel;
	private Table table;
	
	private Inscription inscription;
	
	private JLabel title = FormUtil.createSubTitle("");
	
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem itemPayement = new JMenuItem("Nouveau payement", new ImageIcon(R.getIcon("add")));
	private JMenuItem itemPrint = new JMenuItem("Imprimer la fiche", new ImageIcon(R.getIcon("print")));
	private JMenuItem itemExportPDF = new JMenuItem("Exporter en PDF", new ImageIcon(R.getIcon("pdf")));
	private JMenuItem itemExportEXEL = new JMenuItem("Exporter en Excel", new ImageIcon(R.getIcon("export")));
	private JMenuItem itemUpdate = new JMenuItem("Editer l'identité", new ImageIcon(R.getIcon("usredit")));
	private JMenuItem itemPalmaresse = new JMenuItem("Consulter le palmaresse", new ImageIcon(R.getIcon("report")));
	
	
	private FormPaymentFee form;
	private JDialog formDialog;

	/**
	 * constucteur d'initialisation
	 * @param mainWindow
	 */
	public IndividualSheet (MainWindow mainWindow) {
		super(new BorderLayout());
		page.setPreferredSize(A4_LAND);
		page.setSize(A4_LAND);
		page.setMaximumSize(A4_LAND);
		page.setMinimumSize(A4_LAND);
		page.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		tableModel = new PaymentFeeTableModel(mainWindow.factory.findDao(PaymentFeeDao.class));
		table = new Table(tableModel);
		
		form = new FormPaymentFee(mainWindow.factory.findDao(PaymentFeeDao.class));
		formDialog = new JDialog(mainWindow, "Payement de frais universitaire");
		formDialog.getContentPane().setBackground(FormUtil.BKG_DARK);
		formDialog.getContentPane().add(form, BorderLayout.CENTER);
		formDialog.pack();
		formDialog.setLocationRelativeTo(mainWindow);
		formDialog.setModal(true);
		formDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		this.add(scroll, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		
		page.add(container, BorderLayout.CENTER);
		page.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					popup.show(page, e.getX(), e.getY());
				}
			}
		});
		
		initPopup();
		init();
	}
	
	private void init () {
		container.setBackground(Color.WHITE);
		
		title.setOpaque(true);
		title.setBackground(Color.LIGHT_GRAY);
		title.setForeground(FormUtil.BKG_DARK);
		
		Panel panel = new Panel(new BorderLayout());
		panel.add(title, BorderLayout.NORTH);
		panel.add(table, BorderLayout.CENTER);
		
		container.add(panel, BorderLayout.CENTER);
		container.add(header, BorderLayout.NORTH);
		
	}
	
	
	private void initPopup() {

		popup.add(itemPayement);
		popup.add(itemPrint);
		popup.add(itemExportEXEL);
		popup.add(itemExportPDF);
		popup.addSeparator();
		popup.add(itemUpdate);
		popup.add(itemPalmaresse);
		
		itemPayement.addActionListener(event -> {
			formDialog.setVisible(true);
		});
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger())
					popup.show(IndividualSheet.this, e.getX(), e.getY());
			}
		});
	}


	/**
	 * @return the inscription
	 */
	public Inscription getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription(Inscription inscription) {
		this.inscription = inscription;
		header.setInscription(inscription);
		popup.setLabel(inscription.getStudent().toString());
		
		String txt = TITLE+inscription.getPromotion().getAcademicYear().toString();
		title.setText(txt.toUpperCase());
	}
	


}
