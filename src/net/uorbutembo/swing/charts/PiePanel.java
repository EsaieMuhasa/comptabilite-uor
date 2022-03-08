/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class PiePanel extends Panel {
	private static final long serialVersionUID = 8834575442903333237L;
	
	private PieRender render;
	private PiePartCaption caption;
	private JLabel title = FormUtil.createSubTitle("");
	private Color borderColor;
	
	private final GridLayout layout = new GridLayout(1, 2);
	
	/**
	 * @param model
	 */
	public PiePanel (PieModel model) {
		super ();
		this.render = new PieRender(model);
		this.caption = new PiePartCaption(model);
		this.title.setText(model.getTitle());
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
	
	public void setHorizontalPlacement (boolean horizontalPlacement) {
		if(horizontalPlacement) {
			this.layout.setColumns(2);
			this.layout.setRows(1);
			caption.setPaddingLeft(false);
		} else {
			this.layout.setColumns(1);
			this.layout.setRows(2);
			caption.setPaddingLeft(true);
		}
	}
	

	/**
	 * @param borderColor
	 */
	public PiePanel(PieModel model, Color borderColor) {
		this(model);
		this.setBorderColor(borderColor);
	}



	/**
	 * @return the borderColor
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 * @param borderColor the borderColor to set
	 */
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
		
		if(title.getText() != null && !title.getText().trim().isEmpty()) {
			this.title.setOpaque(true);
			if(borderColor != null) {
				this.title.setBackground(borderColor.darker());
				this.caption.setBorderColor(borderColor);
			}
		}
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
		Panel center = new Panel(layout);
		
		this.add(center, BorderLayout.CENTER);
		this.add(title, BorderLayout.SOUTH);
		
		this.setBackground(FormUtil.BKG_DARK);
		title.setFont(Card.FONT_INFO);
		render.setBackground(this.getBackground());
		
		center.add(render);
		center.add(caption);
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(this.borderColor != null) {	
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(this.borderColor);
			g2.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		}
	}
	
}
