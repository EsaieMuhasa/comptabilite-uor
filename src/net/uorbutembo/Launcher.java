/**
 * 
 */
package net.uorbutembo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import net.uorbutembo.dao.DAOBaseListener;
import net.uorbutembo.dao.DAOConfigException;
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.tools.Config;
import net.uorbutembo.views.MainWindow;


/**
 * @author Esaie MUHASA
 *
 */
public class Launcher implements DAOBaseListener{
	
	private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		boolean lookExist = false;
		
		Date now = new Date();
		File loggDir = new File(Config.find("workspace")+"logg/");
		
		if(!loggDir.isDirectory())
			loggDir.mkdirs();
		
		String filename = DATE_FORMATER.format(now)+".txt";
		File logg = new File(loggDir.getAbsolutePath()+filename);
		
		try {
			PrintStream err = new PrintStream(logg);
			System.setErr(err);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		try {
			DAOFactory factory = DAOFactory.getInstance();
			factory.addListener(launcher);
			StartWindow st = new StartWindow();
			st.setVisible(true);
			
			File workspace = new File(Config.find("workspace"));
			if(!workspace.isDirectory())
				workspace.mkdirs();
			
			MainWindow frame = new MainWindow(factory);
			factory.reload();
			st.setVisible(false);
			st.dispose();
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
