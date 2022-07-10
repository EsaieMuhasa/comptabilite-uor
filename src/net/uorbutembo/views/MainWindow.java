/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.uorbutembo.dao.DAOBaseListener;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.Config;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.components.Sidebar;


/**
 * @author Esaie MUHASA
 *
 */
public class MainWindow extends JFrame implements DAOBaseListener{

	private static final long serialVersionUID = 1L;
	private Sidebar sidebar;
	private WorkspacePanel workspace;
	public final DAOFactory factory;
	
	private Dimension lastSize;
	private final DialogProgress progress;
	private AboutDialog aboutDialog;
	
	private final WindowAdapter windowListener = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
			doClose();
		}
		
	};
	
	/**
	 * constructeur d'initialisation
	 * configuration elementaire du formulaire
	 */
	public MainWindow(DAOFactory factory) {
		super(R.getConfig().get("appName"));
		this.factory = factory;
		factory.addListener(this);
		
		progress = new DialogProgress(this);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) (screenSize.getWidth() - screenSize.getWidth()/10);
		int h = (int) (screenSize.getHeight() - screenSize.getHeight()/10); 
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(w, h);
		setMinimumSize(new Dimension(880, 400));
		setLocationRelativeTo(null);
		
		workspace = new WorkspacePanel(this);
		sidebar = new Sidebar(this);
		workspace.init(sidebar);
		
		getContentPane().add(workspace, BorderLayout.CENTER);
		getContentPane().add(sidebar, BorderLayout.WEST);
		getContentPane().setBackground(Color.WHITE);
		
		getLayeredPane().setOpaque(false);		
		
		try {
			this.setIconImage(ImageIO.read(new File(Config.find("appMainIcon"))));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Une erreur est survenue \nlors de la lecture de l'icone \n"+e.getMessage(), "Erreur icone", JOptionPane.ERROR_MESSAGE);
		}
		
		requestFocus();
		
		lastSize = getSize();
		addWindowListener(windowListener);
	}
	
	/**
	 * demande d'arret de l'application
	 */
	public void doClose () {
		int state = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment quitter ce programme??", 
				"Fermeture du programme", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(state == JOptionPane.OK_OPTION) {
			System.exit(MainWindow.NORMAL);
		}		
	}
	
	/**
	 * mis en plain ecran
	 * @param fullScreen
	 */
	public void setFullScreen (boolean fullScreen) {
		dispose();
		setVisible(false);
		if (fullScreen)
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		else {
			setExtendedState(JFrame.NORMAL);
			setSize(lastSize);
			setLocationRelativeTo(null);
		}
		setUndecorated(fullScreen);
		setVisible(true);
	}
	
	/**
	 * disualisation de la fenere d'apropos du soft
	 */
	public void showAbout () {
		if (aboutDialog == null)
			aboutDialog = new AboutDialog(this);
		
		aboutDialog.setLocationRelativeTo(this);
		aboutDialog.setVisible(true);
	}
	
	/**
	 * @return the sidebar
	 */
	public Sidebar getSidebar() {
		return sidebar;
	}
	
	@Override
	public void setCursor(Cursor cursor) {
		super.setCursor(cursor);
		if(cursor.getType() == Cursor.WAIT_CURSOR) {
			progress.setLocationRelativeTo(this);
			progress.setVisible(true);
		} else
			progress.setVisible(false);
	}

	/**
	 * @return the workspace
	 */
	public WorkspacePanel getWorkspace() {
		return workspace;
	}

	@Override
	public void onEvent(DAOEvent event) {
		if(event.getType() == EventType.ERROR) {
			DAOException e = (DAOException) event.getData();
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 */
	public static class DialogProgress extends JDialog {
		private static final long serialVersionUID = -3359842359459731399L;
		
		private final JProgressBar progress = new JProgressBar();
		public DialogProgress(MainWindow parent) {
			super(parent, false);
			setUndecorated(true);
			Panel panel = new Panel(new BorderLayout());
			
			panel.add(progress, BorderLayout.CENTER);
			panel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			((JPanel)getContentPane()).setBorder(BorderFactory.createLineBorder(FormUtil.BORDER_COLOR, 3));
			((JPanel)getContentPane()).setBackground(FormUtil.BKG_DARK);
			getContentPane().add(panel, BorderLayout.CENTER);
			progress.setPreferredSize(new Dimension(400, progress.getPreferredSize().height));
			progress.setIndeterminate(true);
			progress.setString("Long chargement...");
			progress.setStringPainted(true);
			pack();
			setLocationRelativeTo(parent);
			setAlwaysOnTop(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			progress.setCursor(getCursor());
			progress.setForeground(FormUtil.BORDER_COLOR);
		}
	}
}
