/**
 * 
 */
package net.uorbutembo;

import java.awt.EventQueue;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.uorbutembo.dao.DAOConfigException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.views.MainWindow;


/**
 * @author Esaie MUHASA
 *
 */
public class Launcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean lookExist = false;
		
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	            if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					lookExist = true;
	                break;
	            }
	        }
			
			//UIManager.setLookAndFeel(new NimbusLookAndFeel());
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			if(!lookExist) {//sinon on utilise le look par defaut de l'OS
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Une erreur est survenue lors de la connexion à la base de données\n"+e.getMessage(), "Error look and feel", JOptionPane.ERROR_MESSAGE);
			//Logger.getLogger(Launcher.class.getName()).log( Level.SEVERE, null, e);
		}
		
		//TestDAO.test(System.out);
		
		try {
			DAOFactory factory = DAOFactory.getInstance();
			
			EventQueue.invokeLater(() -> {
				MainWindow frame = new MainWindow(factory);
				frame.setVisible(true);
			});
		} catch (DAOConfigException e) {
			//Logger.getLogger(Launcher.class.getName()).log( Level.SEVERE, null, e);
			JOptionPane.showMessageDialog(null, "Une erreur est survenue lors de la connexion à la base de données\n"+e.getMessage(), "Erreur connexion a la BDD", JOptionPane.ERROR_MESSAGE);
		}
		
		
	}

}
