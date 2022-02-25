/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.StudyClassDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.views.forms.FormStudyClass;
import net.uorbutembo.views.models.StudyClassTableModel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelStudyClass extends Panel {
	private static final long serialVersionUID = 1827370862407233020L;

	private Button btnNew = new Button(new ImageIcon(R.getIcon("plus")), "Ajouter une classe d'étude");
	private Dialog formDialog;
	private FormStudyClass form;
	
	private StudyClassDao studyClassDao;
	/**
	 * 
	 */
	public PanelStudyClass(MainWindow mainWindow) {
		super(new BorderLayout());
		this.studyClassDao = mainWindow.factory.findDao(StudyClassDao.class);
		
		this.studyClassDao.addListener(new DAOAdapter<StudyClass>() {
			@Override
			public void onCreate(StudyClass e, int requestId) {
				formDialog.setVisible(false);
			}
		});
		
		Panel top = new Panel(new FlowLayout(FlowLayout.RIGHT));
		top.add(btnNew);
		this.add(top, BorderLayout.NORTH);
		
		this.btnNew.addActionListener(event -> {
			if(formDialog == null) {
				form = new FormStudyClass(this.studyClassDao);
				this.formDialog = new Dialog(mainWindow);
				this.formDialog.setTitle("Enregistrement d'une nouvelle classe d'etude");
				this.formDialog.getContentPane().add(this.form, BorderLayout.CENTER);
				this.formDialog.setSize(600, 270);
			}
			
			this.formDialog.setLocationRelativeTo(mainWindow);
			this.formDialog.setVisible(true);
		});
		
		Panel center = new Panel(new BorderLayout());
		Table table = new Table(new StudyClassTableModel(this.studyClassDao));
		center.add(new TablePanel(table, "Liste des classe d'etudes"), BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(10, 0, 10, 0));
		this.add(center, BorderLayout.CENTER);
	}

}
