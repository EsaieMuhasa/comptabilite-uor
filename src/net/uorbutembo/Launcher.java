/**
 * 
 */
package net.uorbutembo;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.uorbutembo.dao.DAOBaseListener;
import net.uorbutembo.dao.DAOConfigException;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.views.MainWindow;


/**
 * @author Esaie MUHASA
 *
 */
public class Launcher implements DAOBaseListener{

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
			JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du chargement du look-end-feel\n"+e.getMessage(), "Error look and feel", JOptionPane.ERROR_MESSAGE);
		}
		
		//TestDAO.test(System.out);
		
		Launcher launcher = new Launcher();
		
		try {
			DAOFactory factory = DAOFactory.getInstance();
			factory.addListener(launcher);
			StartWindow st = new StartWindow();
			st.setVisible(true);
			
			MainWindow frame = new MainWindow(factory);
			factory.reload();
			st.setVisible(false);
			frame.setVisible(true);
			factory.removeListener(launcher);
		} catch (DAOConfigException e) {
			JOptionPane.showMessageDialog(null, "Une erreur est survenue lors de la connexion à la base de données\n"+e.getMessage(), "Erreur connexion a la BDD", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	@Override
	public void onEvent(DAOEvent event) {
		if(event.getType() == EventType.ERROR) {
			DAOException e = (DAOException) event.getData();
			JOptionPane.showMessageDialog(null, "Une erreur est survenue lors du chargemet de l'application\n"+e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

}
