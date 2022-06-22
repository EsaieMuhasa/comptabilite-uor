/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.swing.TableModel.ExportationProgressListener;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormPaymentFee;
import net.uorbutembo.views.models.PaymentFeeTableModel;
import net.uorbutembo.views.models.PromotionPaymentTableModel.InscriptionDataRow;

/**
 * @author Esaie MUHASA
 *
 */
public class IndividualSheet extends Panel {

	private static final long serialVersionUID = 4342531142487279020L;
	public static final Dimension A4_LAND = new Dimension(1123, 794);
	public static final Dimension MIN_SIZE = new Dimension(1123, 300);
	private static final String TITLE = "Fiche individuelle de paiement des frais académiques exercice ";
	
	private Panel page = new Panel(new BorderLayout());
	private JScrollPane scroll = FormUtil.createScrollPane(page);
	private JPanel container = new JPanel(new BorderLayout());
	private IndividualSheetHeader header = new IndividualSheetHeader();
	
	private PaymentFeeTableModel tableModel;
	private Table table;
	private final TablePanel tablePanel;
	
	private InscriptionDataRow inscription;
	private PaymentFeeDao paymentFeeDao;
	
	private JPopupMenu popup = new JPopupMenu();
	private JMenuItem itemPayement = new JMenuItem("Nouveau payement", new ImageIcon(R.getIcon("add")));
	private JMenuItem itemUpdatePayement = new JMenuItem("Modifier le payement selectionné", new ImageIcon(R.getIcon("edit")));
	private JMenuItem itemDeletePayement = new JMenuItem("Suprimer le payement selectionné", new ImageIcon(R.getIcon("close")));
	private JMenuItem itemExportEXEL = new JMenuItem("Exporter en Excel", new ImageIcon(R.getIcon("export")));
	
	private MouseListener popupListener = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.isPopupTrigger()) {
				itemUpdatePayement.setEnabled(table.getSelectedRow() != -1);
				itemDeletePayement.setEnabled(itemUpdatePayement.isEnabled());
				itemExportEXEL.setEnabled(tableModel.getCount() != 0);
				
				popup.show((JComponent)e.getSource(), e.getX(), e.getY());
			}
		}
	};
	
	private final ExportationProgressListener exportationListener = new ExportationProgressListener() {
		
		@Override
		public void onStart(TableModel<?> model) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		
		@Override
		public void onProgress(TableModel<?> model, int current, int max) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onFinish(TableModel<?> model, File file) {
			setCursor(Cursor.getDefaultCursor());
		}
		
		@Override
		public void onError(TableModel<?> model, Exception e) {
			setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(IndividualSheet.this, e.getMessage(), "Erreur lors de l'exportation des donnees", JOptionPane.ERROR_MESSAGE);
		}
	};
	
	private final Panel panelForm = new Panel(new BorderLayout());
	private final FormPaymentFee form;
	private final JScrollPane formScroll;
	
	private final MainWindow mainWindow;

	/**
	 * constucteur d'initialisation
	 * @param mainWindow
	 */
	public IndividualSheet (MainWindow mainWindow) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		page.setPreferredSize(MIN_SIZE);
		page.setSize(MIN_SIZE);
		page.setMinimumSize(MIN_SIZE);
		page.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		this.paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		
		tableModel = new PaymentFeeTableModel(paymentFeeDao);
		tableModel.addExportationProgressListener(exportationListener);
		table = new Table(tableModel);
		tablePanel = new TablePanel(table, TITLE);
		final Panel padding = new Panel(new BorderLayout());
		
		form = new FormPaymentFee(mainWindow);
		form.getBtnCancel().addActionListener(event -> {
			panelForm.setVisible(false);
		});
		form.setPreferredSize(new Dimension(300, 550));
		padding.add(form, BorderLayout.CENTER);
		padding.setBorder(new EmptyBorder(5, 0, 5, 18));
		formScroll = FormUtil.createVerticalScrollPane(padding);
		panelForm.add(formScroll, BorderLayout.CENTER);
		panelForm.setBackground(Color.BLACK);
		panelForm.setOpaque(true);
		panelForm.setVisible(false);
		
		final Panel pan = new Panel(new BorderLayout());
		pan.add(scroll, BorderLayout.CENTER);
		pan.setBorder(page.getBorder());
		pan.setOpaque(true);
		pan.setBackground(FormUtil.BKG_DARK);
		scroll.setBorder(new LineBorder(FormUtil.BORDER_COLOR));
		
		this.add(pan, BorderLayout.CENTER);
		this.add(panelForm, BorderLayout.EAST);
		this.setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR));
		
		page.add(container, BorderLayout.CENTER);
		container.setBorder(new LineBorder(Color.BLACK));
		
		initPopup();
		init();
	}
	
	private void init () {
		container.setBackground(Color.WHITE);
		
		table.setShowVerticalLines(true);
		table.setBackground(Color.WHITE);
		table.setForeground(Color.BLACK);
		table.setGridColor(FormUtil.BKG_END);
		table.setSelectionBackground(new Color(0xFFE0E0E0));
		table.setAlignmentX(CENTER_ALIGNMENT);
		
		Panel panel = new Panel(new BorderLayout());
		Panel panelTable = new Panel(new BorderLayout());
		
		panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		panel.add(panelTable, BorderLayout.CENTER);
		
		panelTable.add(tablePanel, BorderLayout.CENTER);
		
		container.add(panel, BorderLayout.CENTER);
		container.add(header, BorderLayout.NORTH);
		
	}
	
	private void initPopup() {

		popup.add(itemPayement);
		popup.add(itemUpdatePayement);
		popup.add(itemDeletePayement);
		popup.addSeparator();
		popup.add(itemExportEXEL);
		
		itemPayement.addActionListener(event -> {
			panelForm.setVisible(true);
		});
		itemUpdatePayement.addActionListener(event -> {
			form.setFee(tableModel.getRow(table.getSelectedRow()));
			panelForm.setVisible(true);
		});
		itemDeletePayement.addActionListener(event -> {
			
			PaymentFee fee = tableModel.getRow(table.getSelectedRow());
			
			String message = String.format("Voulez-vous vraiment suprimer %s USD \nde la fiche de l'étudiant %s?.\nN.B: cette opération est ireversible.", fee.getAmount()+"", fee.getInscription().getStudent().toString());
			int status = JOptionPane.showConfirmDialog(null, message, "Supression des frais", JOptionPane.OK_CANCEL_OPTION);
			if(status == JOptionPane.OK_OPTION) {
				this.paymentFeeDao.delete(fee.getId());
			}
		});
		
		itemExportEXEL.addActionListener(event -> {
			int rps = Table.XLSX_FILE_CHOOSER.showSaveDialog(mainWindow);
			if(rps == JFileChooser.APPROVE_OPTION) {
				Thread t = new Thread(() -> {
					tableModel.exportToExcel(Table.XLSX_FILE_CHOOSER.getSelectedFile());
				});
				t.start();
			}
		});
		
		page.addMouseListener(popupListener);
		table.addMouseListener(popupListener);
		tablePanel.addMouseListener(popupListener);
	}


	/**
	 * @return the inscription
	 */
	public InscriptionDataRow getInscription() {
		return inscription;
	}

	/**
	 * @param inscription the inscription to set
	 */
	public void setInscription (InscriptionDataRow inscription) {
		this.inscription = inscription;
		popup.setLabel(inscription.getInscription().getStudent().toString());
		header.setInscription(inscription.getInscription());
		tableModel.setInscription(inscription);
		form.setInscription(inscription.getInscription());
		
		String txt = TITLE+inscription.getInscription().getPromotion().getAcademicYear().toString();
		tablePanel.setTitle(txt.toUpperCase());
	}

}
