/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class PiePanel extends Panel {
	private static final long serialVersionUID = 8834575442903333237L;
	
	private PieRender render;
	private JLabel title = FormUtil.createSubTitle("");
	
	/**
	 * @param model
	 */
	public PiePanel (PieModel model) {
		super ();
		this.render = new PieRender(model);
		this.init();
		model.addListener(new PieModelListener() {
			@Override
			public void repaintPart(PieModel model, int partIndex) {}
			@Override
			public void refresh(PieModel model) {}
			
			@Override
			public void onTitleChange(PieModel  model, String title) {
				PiePanel.this.title.setText(title);
			}
		});
	}

	/**
	 * 
	 */
	public PiePanel() {
		super ();
		this.setOpaque(true);
		this.render = new PieRender();
		this.init();
	}
	
	private void init() {
		this.setLayout(new BorderLayout());
//		this.setOpaque(true);
		this.add(render, BorderLayout.CENTER);
		this.add(title, BorderLayout.NORTH);
		this.render.setBackground(this.getBackground());
	}

	/**
	 * @return the render
	 */
	public PieRender getRender() {
		return render;
	}
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if(this.render != null)
			this.render.setBackground(bg);
	}
	
}
