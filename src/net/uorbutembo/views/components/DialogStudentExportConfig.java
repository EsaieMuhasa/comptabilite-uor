/**
 * 
 */
package net.uorbutembo.views.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;

import net.uorbutembo.swing.Dialog;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;

/**
 * @author Esaie MUHASA
 *
 */
public class DialogStudentExportConfig extends Dialog {
	private static final long serialVersionUID = 8621534153067911599L;
	
	private final JCheckBox [][] checkFields = {
			{
				new JCheckBox("Nom, Post-nom et Prénom", true),
				new JCheckBox("Matricule", true),
				new JCheckBox("Téléphone", true),
				new JCheckBox("E-mail"),
				new JCheckBox("Date de naissance"),
				new JCheckBox("Lieux de naissance"),
				new JCheckBox("Photo"),
				new JCheckBox("Solde", true),
				new JCheckBox("Dette", true)				
			},
			{
				new JCheckBox("Effectifs", true),
				new JCheckBox("Soldes", true),
				new JCheckBox("Dettes", true),
				new JCheckBox("Graphique des soldes", true),
				new JCheckBox("Graphique des dettes", true),
				new JCheckBox("Graphique des effectifs des étudiants", true)
			},
			{
				new JCheckBox("Par faculté", true),
				new JCheckBox("Par départements", true),
				new JCheckBox("Par classe d'étude", true),
			}
	};
	
	private final JCheckBox labels [] = {
			new JCheckBox("Vue détaillée", true), 
			new JCheckBox("Vue globale", true),
			new JCheckBox("Groupement des résultats", true)
	};
	
	private final JButton btnValidate = new JButton("Valider", new ImageIcon(R.getIcon("success")));
	private final JButton btnCancel = new JButton("Annuler", new ImageIcon(R.getIcon("close")));
	
	private ExportConfigListener exportListener;
	private final ExportConfig config;
	{//initialisation de la configuration de selection
		boolean groups [] = new boolean [labels.length];
		boolean groupsConfig [][] = new boolean [groups.length][0];
		for (int i = 0; i < groups.length; i++) {
			boolean fields [] = new boolean [checkFields[i].length];
			for (int j = 0; j < fields.length; j++) {
				fields[j] = checkFields[i][j].isSelected();
			}
			groupsConfig[i] = fields;
			groups[i] = labels[i].isSelected();
		}
		
		config = new ExportConfig(groups, groupsConfig);
	}
	
	/**
	 * ecouteur des boutns qui commande la fentre
	 */
	private final ActionListener btnListener = (event) -> {
		setVisible(false);
		if (exportListener == null)
			return;
		
		if (event.getSource() == btnValidate) 
			exportListener.onValiate(config);
		else
			exportListener.onCancel();
	};
	
	private final ChangeListener titleListener = (event) -> {
		JCheckBox source = (JCheckBox) event.getSource();
		int index = Integer.parseInt(source.getName());
		config.groups[index] = source.isSelected();
		for (int i = 0; i < checkFields[index].length; i++) 
			checkFields[index][i].setEnabled(source.isSelected());
		
		boolean validable = source.isSelected();
		if(!validable) {
			for (JCheckBox label : labels) {
				if(label.isSelected()) {
					validable = true;
					break;
				}
			}
		}
		
		btnValidate.setEnabled(validable);
	};
	
	private final ChangeListener fieldsListener = (event) -> {
		JCheckBox source = (JCheckBox) event.getSource();
		String [] indexs = source.getName().split(",");
		config.groupsConfig[Integer.parseInt(indexs[0])][Integer.parseInt(indexs[1])] = source.isSelected();
	};
	
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing (WindowEvent e) {
			btnCancel.doClick();
		}
	};

	/**
	 * @param owner
	 */
	public DialogStudentExportConfig (Frame owner) {
		super(owner, "Configuration des données à exporter", true);
		
		final Panel container = new Panel(new GridLayout(1, 2, FormUtil.DEFAULT_H_GAP, FormUtil.DEFAULT_V_GAP));
		final Panel bottom = new Panel(new FlowLayout(FlowLayout.RIGHT));
		final LineBorder border = new LineBorder(FormUtil.BORDER_COLOR);
		
		for (int i = 0; i < labels.length; i++) {
			final Panel panel = new Panel(new BorderLayout()),
					header = new Panel(new FlowLayout(FlowLayout.LEFT));
			final JCheckBox label = labels[i];
			final Box box = Box.createVerticalBox();
			
			for (int j = 0; j< checkFields[i].length; j++) {
				JCheckBox check = checkFields[i][j];
				box.add(check);
				check.setName(i+","+j);
				check.setForeground(Color.WHITE);
				check.addChangeListener(fieldsListener);
			}
			
			label.setName(i+"");
			label.setForeground(Color.WHITE);
			label.addChangeListener(titleListener);
			
			box.setOpaque(false);
			box.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			header.add(label);
			header.setOpaque(true);
			header.setBackground(border.getLineColor());
			
			container.add(panel);
			panel.setBorder(border);
			panel.add(box, BorderLayout.CENTER);
			panel.add(header, BorderLayout.NORTH);
		}//
		
		bottom.add(btnValidate);
		bottom.add(btnCancel);
		bottom.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		btnValidate.addActionListener(btnListener);
		btnCancel.addActionListener(btnListener);
		
		container.setBorder(bottom.getBorder());
		
		getContentPane().add(container, BorderLayout.CENTER);
		getContentPane().add(bottom, BorderLayout.SOUTH);
		getContentPane().setBackground(FormUtil.BKG_DARK);
		
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowAdapter);
		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
		setIconImage(owner.getIconImage());
	}
	
	/**
	 * visualisation de la boite de dialogue
	 * @param listener
	 */
	public void show (ExportConfigListener listener) {
		exportListener = listener;
		setLocationRelativeTo(getOwner());
		if(!isVisible())
			setVisible(true);
	}
	
	/**
	 * @return the config
	 */
	public ExportConfig getConfig() {
		return config;
	}

	/**
	 * Envelope de la configuration choisie
	 * @author Esaie MUHASA
	 */
	public static final class ExportConfig {
		private final boolean [] groups;
		private final boolean [][] groupsConfig;
		private static final String [] COLUMN_NAMES = {"Soldes", "Dettes", "Effectifs"};
		
		/**
		 * @param groups
		 * @param groupsConfig
		 */
		public ExportConfig(boolean [] groups, boolean [][] groupsConfig) {
			this.groups = groups;
			this.groupsConfig = groupsConfig;
		}
		
		/**
		 * @return the groups
		 */
		public boolean [] getGroups() {
			return groups;
		}
		
		/**
		 * Verifie si le groupe est selectionner
		 * @param groupIndex
		 * @return
		 */
		public boolean isSelected (int groupIndex) {
			return groups[groupIndex];
		}
		
		/**
		 * @return the groupsConfig
		 */
		public boolean [][] getGroupsConfig () {
			return groupsConfig;
		}
		
		public boolean [] getGroupConfig (int index) {
			return groupsConfig[index];
		}
		
		/**
		 * Comptage des elements d'un groupe
		 * @param index
		 * @return
		 */
		public int countAt (int index) {
			return groupsConfig[index].length;
		}
		
		/**
		 * Renvoie le statut d'une case a chaucher
		 * @param index
		 * @param box
		 * @return
		 */
		public boolean getStatusAt (int index, int box) {
			return groupsConfig[index][box];
		}
		
		/**
		 * renvoie le titre d'une colonne
		 * @param index
		 * @param col
		 * @return
		 */
		public String getTitleCofingGroup (int index, int col) {
			return COLUMN_NAMES[col];
		}
		
		/**
		 * il y-il aumoin une case caucher pour le group dont l'index est en parametre??
		 * @param groupIndex
		 * @return
		 */
		public boolean hasChecked (int groupIndex) {
			for (boolean bool : groupsConfig[groupIndex]) {
				if (bool) return true;
			}
			return false;
		}
		
		/**
		 * comptage des case caucher pour un groupe
		 * @param groupIndex
		 * @return
		 */
		public int countChecked (int groupIndex) {
			int count = 0;
			for (boolean b : groupsConfig[groupIndex]){
				if(b) count++;
			}
			return count;
		}
	}
	
	/**
	 * Interface d'emission des evenements
	 * @author Esaie MUHASA
	 */
	public static interface ExportConfigListener {
		/**
		 * Losque l'utilisateur clique sur le bouton valider
		 * @param config
		 */
		void onValiate (ExportConfig config);
		
		/**
		 * Lorsque l'utlisateur lique sur le bouton cancel
		 * ou il ferme carement la boite de dialogue
		 */
		void onCancel ();
	}
}
