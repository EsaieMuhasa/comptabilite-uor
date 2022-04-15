/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.uorbutembo.views.components.DefaultScenePanel;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelHelp extends DefaultScenePanel {
	private static final long serialVersionUID = 2668307309635713218L;

	private JEditorPane panel = new JEditorPane();
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	private JTree nav = new JTree(root);
	
	/**
	 * @param mainWindow
	 */
	public PanelHelp (MainWindow mainWindow) {
		super("Aide d'utilisation", new ImageIcon(R.getIcon("help")), mainWindow, false);
		
		HTMLEditorKit html = new HTMLEditorKit();
		panel.setEditorKit(html);
		panel.setEditable(false);
		
		nav.setRowHeight(35);
		nav.setBackground(FormUtil.BKG_END);
		nav.setForeground(Color.WHITE);
		nav.setEditable(false);
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, FormUtil.createVerticalScrollPane(panel), nav);
		split.setBorder(BODY_BORDER);
		this.getBody().add(split, BorderLayout.CENTER);
		
		File file = new File(R.getHelp("index"));
		try {
			URL url = file.toURI().toURL();
			panel.setPage(url);
		} catch (Exception e) {}
		initNav();
	}
	
	/**
	 * initialisation des contenue du menu
	 */
	private void initNav () {
		DefaultMutableTreeNode nodeDashboard = new DefaultMutableTreeNode("Tableau de board");
		DefaultMutableTreeNode nodeStudent = new DefaultMutableTreeNode("Etudiants");
		DefaultMutableTreeNode nodeConfig = new DefaultMutableTreeNode("Configuration globale");
		
		root.add(nodeDashboard);
		root.add(nodeStudent);
		root.add(nodeConfig);
		TreePath pathDashboard = new TreePath(nodeDashboard);
		TreePath rootPath = new TreePath(root);
		
		nav.expandPath(rootPath);
		nav.setRootVisible(false);
		nav.setSelectionPath(pathDashboard);
	}

	@Override
	public String getNikeName() {
		return "help";
	}

}
