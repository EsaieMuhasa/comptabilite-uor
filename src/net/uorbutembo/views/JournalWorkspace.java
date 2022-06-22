/**
 * 
 */
package net.uorbutembo.views;


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;

import net.uorbutembo.swing.TableModel;
import net.uorbutembo.swing.TableModel.ExportationProgressListener;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.components.NavbarButtonModel;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalWorkspace extends DefaultScenePanel {
	private static final long serialVersionUID = 5081093701813722107L;
	
	private final JProgressBar progress = new JProgressBar();
	{		
		progress.setMinimum(0);
		progress.setStringPainted(true);
		progress.setPreferredSize(new Dimension(400, progress.getPreferredSize().height));
	}
	private JDialog progressDialog;//boite de diaogue d'exportation des donnees 
	private final ExportationProgressListener exportationListener = new ExportationProgressListener() {
		
		@Override
		public void onStart(TableModel<?> model) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			createProgresseDialog();
			progressDialog.setVisible(true);
		}
		
		@Override
		public void onProgress(TableModel<?> model, int current, int max) {
			progress.setValue(current);
			progress.setMaximum(max);
		}
		
		@Override
		public void onFinish(TableModel<?> model, File file) {
			setCursor(Cursor.getDefaultCursor());
			progressDialog.setVisible(false);
			progressDialog.dispose();
		}
		
		@Override
		public void onError(TableModel<?> model, Exception e) {
			setCursor(Cursor.getDefaultCursor());
			JOptionPane.showMessageDialog(mainWindow, e.getMessage(), "Erreur lors de l'exportation des données", JOptionPane.ERROR_MESSAGE);
			progressDialog.setVisible(false);
			progressDialog.dispose();
		}
	};
	private final JournalGeneral home = new JournalGeneral(mainWindow);
	private final JournalSpecific more = new JournalSpecific(mainWindow);
	
	/**
	 * @param mainWindow
	 */
	public JournalWorkspace(MainWindow mainWindow) {
		super("Journal des opérations financières", new ImageIcon(R.getIcon("calendar")), mainWindow, false);

		NavbarButtonModel itemIndex = new NavbarButtonModel("index", "Général");
		NavbarButtonModel itemAccounts = new NavbarButtonModel("bilan", "Spécifiques");
		
		
		addItemMenu(itemIndex, home);
		addItemMenu(itemAccounts, more);
		home.setExportationListener(exportationListener);
		more.setExportationListener(exportationListener);
	}
	
	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		home.setCursor(cursor);
		more.setCursor(cursor);
	}
	
	
	/**
	 * initiliasation de l'interface graphique de la boite de dialogue
	 * de progression d'un tache lourde
	 */
	private void createProgresseDialog() {
		if(progressDialog == null) {
			progressDialog = new JDialog(mainWindow, false);
			progressDialog.setUndecorated(true);
			
			final JPanel panel = new JPanel(new BorderLayout());
			panel.setBackground(FormUtil.BKG_DARK);
			Border line = BorderFactory.createLineBorder(FormUtil.BORDER_COLOR, 3);
			((JPanel)progressDialog.getContentPane()).setBorder(line);
			
			panel.add(progress, BorderLayout.CENTER);
			panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			progressDialog.getContentPane().add(panel, BorderLayout.CENTER);
			progressDialog.pack();
		}
		progressDialog.setLocationRelativeTo(mainWindow);
	}
	
	
	@Override
	public boolean hasHeader() {
		return false;
	}

	@Override
	public String getNikeName() {
		return "journal";
	}
	
}
