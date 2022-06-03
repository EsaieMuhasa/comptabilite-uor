/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.uorbutembo.dao.DAOBaseListener;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.views.components.Sidebar;
import resources.net.uorbutembo.R;


/**
 * @author Esaie MUHASA
 *
 */
public class MainWindow extends JFrame implements DAOBaseListener{

	private static final long serialVersionUID = 1L;
	private Sidebar sidebar;
	private WorkspacePanel workspace;
	public final DAOFactory factory;
	
	/**
	 * constructeur d'initialisation
	 * configuration elementaire du formulaire
	 */
	public MainWindow(DAOFactory factory) {
		super("U.O.R. Data Manager");
		this.factory = factory;
		factory.addListener(this);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = (int) (screenSize.getWidth() - screenSize.getWidth()/10);
		int h = (int) (screenSize.getHeight() - screenSize.getHeight()/10); 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(w, h);
		setMinimumSize(new Dimension(880, 400));
		setLocationRelativeTo(null);
		
		workspace = new WorkspacePanel(this);
		sidebar = new Sidebar(workspace, factory);
		workspace.init(sidebar);
		
		getContentPane().add(workspace, BorderLayout.CENTER);
		getContentPane().add(sidebar, BorderLayout.WEST);
		getContentPane().setBackground(Color.WHITE);
		
		getLayeredPane().setOpaque(false);		
		
		try {
			this.setIconImage(ImageIO.read(new File(R.getIcon("logo"))));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Une erreur est survenue \nlors de la lecture de l'icone \n"+e.getMessage(), "Erreur icone", JOptionPane.ERROR_MESSAGE);
		}
		
		requestFocus();
		
//		btnExist.addActionListener(event -> {
//			System.exit(MainWindow.NORMAL);
//
//			int state = JOptionPane.showConfirmDialog(this.parent, "Voulez-vous vraiment quitter ce programme??", 
//					"Fermeture du programme", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//			if(state == JOptionPane.OK_OPTION) {
//			}
//		});
	}
	
	/**
	 * @return the sidebar
	 */
	public Sidebar getSidebar() {
		return sidebar;
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
}
