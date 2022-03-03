/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import net.uorbutembo.beans.Inscription;
import net.uorbutembo.beans.PaymentFee;
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
	private PaymentFeeDao paymentFeeDao;
	private JLabel title = FormUtil.createSubTitle("");
	
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem itemPayement = new JMenuItem("Nouveau payement", new ImageIcon(R.getIcon("add")));
	private JMenuItem itemUpdatePayement = new JMenuItem("Modifier le payement selectionné", new ImageIcon(R.getIcon("edit")));
	private JMenuItem itemDeletePayement = new JMenuItem("Suprimer le payement selectionné", new ImageIcon(R.getIcon("close")));
	private JMenuItem itemPrint = new JMenuItem("Imprimer la fiche", new ImageIcon(R.getIcon("print")));
	private JMenuItem itemExportPDF = new JMenuItem("Exporter en PDF", new ImageIcon(R.getIcon("pdf")));
	private JMenuItem itemExportEXEL = new JMenuItem("Exporter en Excel", new ImageIcon(R.getIcon("export")));
	private JMenuItem itemUpdate = new JMenuItem("Editer l'identité", new ImageIcon(R.getIcon("usredit")));
	private JMenuItem itemPalmaresse = new JMenuItem("Consulter le palmaresse", new ImageIcon(R.getIcon("report")));
	
	private MouseListener popupListener = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) {
				itemUpdatePayement.setEnabled(table.getSelectedRow() != -1);
				itemDeletePayement.setEnabled(itemUpdatePayement.isEnabled());
				
				popup.show((JComponent)e.getSource(), e.getX(), e.getY());
			}
		}
	};
	
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
		
		this.paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		
		tableModel = new PaymentFeeTableModel(paymentFeeDao);
		table = new Table(tableModel);
		
		formDialog = new JDialog(mainWindow, "Payement de frais universitaire");
		form = new FormPaymentFee(formDialog, mainWindow.factory.findDao(PaymentFeeDao.class));
		formDialog.getContentPane().setBackground(FormUtil.BKG_DARK);
		formDialog.getContentPane().add(form, BorderLayout.CENTER);
		formDialog.pack();
		formDialog.setSize(500, formDialog.getHeight());
		formDialog.setLocationRelativeTo(mainWindow);
		formDialog.setModal(true);
		formDialog.setResizable(false);
		formDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		
		this.add(scroll, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		
		page.add(container, BorderLayout.CENTER);
		
		initPopup();
		init();
	}
	
	private void init () {
		container.setBackground(Color.WHITE);
		
//		title.setBackground(Color.LIGHT_GRAY);
		title.setOpaque(true);
		title.setForeground(FormUtil.BKG_DARK);
		title.setHorizontalAlignment(JLabel.CENTER);
		
		table.setShowVerticalLines(true);
		table.setBackground(Color.WHITE);
		table.setForeground(Color.BLACK);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setSelectionBackground(new Color(0xFFE0E0E0));
		table.setAlignmentX(CENTER_ALIGNMENT);
		
		Panel panel = new Panel(new BorderLayout());
		Panel panelTable = new Panel(new BorderLayout());
		
		panel.add(title, BorderLayout.NORTH);
		panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		panel.add(panelTable, BorderLayout.CENTER);
		
		panelTable.add(table.getTableHeader(), BorderLayout.NORTH);
		panelTable.add(table, BorderLayout.CENTER);
		
		container.add(panel, BorderLayout.CENTER);
		container.add(header, BorderLayout.NORTH);
		
	}
	
	private void initPopup() {

		popup.add(itemPayement);
		popup.add(itemUpdatePayement);
		popup.add(itemDeletePayement);
		popup.addSeparator();
		popup.add(itemPrint);
		popup.add(itemExportEXEL);
		popup.add(itemExportPDF);
		popup.addSeparator();
		popup.add(itemUpdate);
		popup.add(itemPalmaresse);
		
		itemPayement.addActionListener(event -> {
			formDialog.setVisible(true);
		});
		itemUpdatePayement.addActionListener(event -> {
			form.setFee(tableModel.getRow(table.getSelectedRow()));
			formDialog.setVisible(true);
		});
		itemDeletePayement.addActionListener(event -> {
			
			PaymentFee fee = tableModel.getRow(table.getSelectedRow());
			
			String message = String.format("Voulez-vous vraiment suprimer %s USD \nde la fiche de l'étudiant %s?.\nN.B: cette opération est ireversible.", fee.getAmount()+"", fee.getInscription().getStudent().toString());
			int status = JOptionPane.showConfirmDialog(null, message, "Supression des frais", JOptionPane.OK_CANCEL_OPTION);
			if(status == JOptionPane.OK_OPTION) {
				this.paymentFeeDao.delete(fee.getId());
			}
		});
		
		this.addMouseListener(popupListener);
		this.table.addMouseListener(popupListener);
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
	public void setInscription (Inscription inscription) {
		this.inscription = inscription;
		popup.setLabel(inscription.getStudent().toString());
		header.setInscription(inscription);
		tableModel.setInscription(inscription);
		form.setInscription(inscription);
		
		String txt = TITLE+inscription.getPromotion().getAcademicYear().toString();
		title.setText(txt.toUpperCase());
	}

}
