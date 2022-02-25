/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import net.uorbutembo.swing.ScrollBar;
import net.uorbutembo.swing.Table;
import net.uorbutembo.views.components.DefaultScenePanel;
import resources.net.uorbutembo.Config;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelConfigSoftware extends DefaultScenePanel {
	private static final long serialVersionUID = -2515621748287799602L;
	
	public PanelConfigSoftware(MainWindow mainWindow) {
		super("Configuration du logiciel", new ImageIcon(R.getIcon("console")), mainWindow);
		
		Map<String, String> config = Config.getInstance().getDictionnary();
		Set<String> keys = config.keySet();
		
		Object [][] data = new Object[keys.size()][2];
		
		int i = 0;
		for (String key : keys) {
			data[i][0] = key;
			data[i][1] = config.get(key);
			i++;
		}
		
		Table table = new Table(data, new String [] {"Parametres", "Valeurs"});
		JScrollPane scroll = new JScrollPane(table);
		scroll.setVerticalScrollBar(new ScrollBar());
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		scroll.getViewport().setBorder(null);
		scroll.setViewportBorder(null);
		
		this.getBody().add(scroll, BorderLayout.CENTER);
	}
	@Override
	public String getNikeName() {
		return "configuration";
	}

}
