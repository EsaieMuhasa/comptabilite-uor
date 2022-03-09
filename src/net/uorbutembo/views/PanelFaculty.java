/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.Faculty;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.FacultyDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormFaculty;
import net.uorbutembo.views.models.FacultyTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelFaculty extends Panel {
	private static final long serialVersionUID = 6683302991865603147L;
	
	private FormFaculty form;
	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter une faculté");
	private Dialog dialogForm;
	private FacultyDao facultyDao;

	/**
	 * @param mainWindow
	 */
	public PanelFaculty(MainWindow mainWindow) {
		super(new BorderLayout());
		this.facultyDao = mainWindow.factory.findDao(FacultyDao.class);
		
		this.facultyDao.addListener(new DAOAdapter<Faculty>() {
			@Override
			public void onCreate(Faculty e, int requestId) {
				dialogForm.setVisible(false);
			}
		});
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		this.add(top, BorderLayout.NORTH);
		
		this.btnNew.addActionListener(event -> {
			if(this.dialogForm == null) {
				this.dialogForm = new Dialog(mainWindow);
				this.dialogForm.setTitle("Enregistrememnt d'une faculté");
				this.form = new FormFaculty(mainWindow, this.facultyDao);
				this.dialogForm.getContentPane().add(this.form, BorderLayout.CENTER);
				this.dialogForm.setSize(600, 250);
			}
			this.dialogForm.setLocationRelativeTo(mainWindow);
			this.dialogForm.setVisible(true);
		});
		
		Panel center = new Panel(new BorderLayout());
		Table table = new Table(new FacultyTableModel(mainWindow.factory.findDao(FacultyDao.class)));
		center.add(new TablePanel(table, "Liste des facultés"), BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		this.add(center, BorderLayout.CENTER);
	}

}
