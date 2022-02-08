/**
 * 
 */
package net.uorbutembo.views.forms;

import static net.uorbutembo.views.forms.FormUtil.DEFAULT_H_GAP;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_V_GAP;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.Department;
import net.uorbutembo.beans.StudyClass;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.components.DefaultFormPanel;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class FormPromotion extends DefaultFormPanel {
	private static final long serialVersionUID = 8664772794012293257L;
	
	private JList<Department> departments = new JList<Department>();
	private JList<Department> selectedDepartments = new JList<Department>();
	private JList<StudyClass> studyClass = new JList<StudyClass>();
	private JList<StudyClass> selectedStudyClass = new JList<StudyClass>();
	
	
	private Button btnAddDepartments = new Button(new ImageIcon(R.getIcon("asc")));
	private Button btnRemoveDepartments = new Button(new ImageIcon(R.getIcon("desc")));
	
	private Button btnAddStudyClass = new Button(new ImageIcon(R.getIcon("asc")));
	private Button btnRemoveStudyClass = new Button(new ImageIcon(R.getIcon("desc")));
	
	private JSplitPane splitGeneral;
	
	/**
	 * 
	 */
	public FormPromotion() {
		super();
		this.setTitle("Formulaire de configuration des promotions");
		this.init();
	}
	
	/**
	 */
	private void init() {
		Panel center = new  Panel(new BorderLayout());
		
		Panel left = new Panel(new GridLayout(1, 1, DEFAULT_V_GAP, DEFAULT_H_GAP));
		Panel right = new Panel(new GridLayout(1, 1, DEFAULT_V_GAP, DEFAULT_H_GAP));
		
		
		//department
		Panel departments = new Panel(new BorderLayout());
		Box boxDepartment = Box.createVerticalBox();
		JLabel labelDepartment = new JLabel("Départements");
		
		labelDepartment.setHorizontalAlignment(JLabel.CENTER);
		
		this.btnRemoveDepartments.setPreferredSize(FormUtil.createDimensionSmCare());
		this.btnAddDepartments.setPreferredSize(FormUtil.createDimensionSmCare());
		boxDepartment.add(Box.createVerticalGlue());
		boxDepartment.add(this.btnAddDepartments);
		boxDepartment.add(this.btnRemoveDepartments);
		boxDepartment.add(Box.createVerticalGlue());

		
		Panel departmentLists = new Panel(new GridLayout(2, 1, DEFAULT_H_GAP, DEFAULT_V_GAP));
		JScrollPane scrollDepartments = new JScrollPane(this.departments);
		JScrollPane scrollSelectedDepartments = new JScrollPane(this.selectedDepartments);
		departmentLists.add(scrollDepartments);
		departmentLists.add(scrollSelectedDepartments);
		
		departments.add(labelDepartment, BorderLayout.NORTH);
		departments.add(departmentLists, BorderLayout.CENTER);
		//-- department
		
		//class
		Panel studyClass = new Panel(new BorderLayout());
		Box boxStudy = Box.createVerticalBox();
		JLabel labelStudyClass = new JLabel("Classe d'études");
		
		labelStudyClass.setHorizontalAlignment(JLabel.CENTER);
		
		this.btnRemoveStudyClass.setPreferredSize(FormUtil.createDimensionSmCare());
		this.btnAddStudyClass.setPreferredSize(FormUtil.createDimensionSmCare());
		boxStudy.add(Box.createVerticalGlue());
		boxStudy.add(this.btnAddStudyClass);
		boxStudy.add(this.btnRemoveStudyClass);
		boxStudy.add(Box.createVerticalGlue());

		
		Panel studyLists = new Panel(new GridLayout(2, 1, DEFAULT_H_GAP, DEFAULT_V_GAP));
		JScrollPane scrollStudyClass = new JScrollPane(this.studyClass);
		JScrollPane scrollSelectedStudyClass = new JScrollPane(this.selectedStudyClass);
		studyLists.add(scrollStudyClass);
		studyLists.add(scrollSelectedStudyClass);
		
		
		studyClass.add(labelStudyClass, BorderLayout.NORTH);
		studyClass.add(studyLists, BorderLayout.CENTER);
		//--class
		
		left.add(departments, BorderLayout.CENTER);
		right.add(studyClass, BorderLayout.CENTER);
		this.splitGeneral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		
		center.add(boxDepartment, BorderLayout.WEST);//bouton de commandes pour deparmetement
		center.add(boxStudy, BorderLayout.EAST);//bouton de commande pour classe d'etude
		center.add(this.splitGeneral, BorderLayout.CENTER);
		center.setBorder(new EmptyBorder(5, 5, 10, 5));
		this.getBody().add(center, BorderLayout.CENTER);
	}
	
	
	@Override
	public void doLayout() {
		super.doLayout();
		if(this.splitGeneral != null) {			
			splitGeneral.setDividerLocation(this.getWidth()/2);
		}
	}
	

}
