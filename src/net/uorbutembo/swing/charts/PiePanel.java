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
import javax.swing.JScrollPane;

import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class PiePanel extends Panel {
	private static final long serialVersionUID = 8834575442903333237L;
	
	private PieRender render;
	private PieCaptionRender caption;
	private JLabel title = FormUtil.createSubTitle("");
	private JScrollPane scroll;
	private Color borderColor;
	
	private final GridLayout layout = new GridLayout(1, 2);
	private final BorderLayout borderLayout = new BorderLayout();
	private final Panel center = new Panel(layout);
	private PieModel model;
	
	private PieModelListener modelListener = new PieModelListener() {
		@Override
		public void repaintPart(PieModel model, int partIndex) {}
		@Override
		public void refresh(PieModel model) {
			if(model.getTitle() != null) {				
				title.setText(model.getTitle());
			}
			
			setBorderColor(borderColor);
		}
		
		@Override
		public void onSelectedIndex(PieModel model, int oldIndex, int newIndex) {}
		
		@Override
		public void onTitleChange(PieModel  model, String text) {
			title.setText(text);
		}
	};
	
	/**
	 * 
	 */
	public PiePanel() {
		super ();
		this.setOpaque(true);
		render = new PieRender();
		caption = new  PieCaptionRender();
		init();
	}
	
	/**
	 * @param model
	 */
	public PiePanel (PieModel model) {
		super ();
		this.model = model;
		render = new PieRender(model);
		caption = new PieCaptionRender(model);
		title.setText(model.getTitle());
		init();
		model.addListener(modelListener);
	}
	
	/**
	 * @return the model
	 */
	public PieModel getModel() {
		return model;
	}

	/**
	 * @return the scroll
	 */
	public JScrollPane getScroll() {
		return scroll;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(PieModel model) {
		if(this.model != null)
			this.model.removeListener(modelListener);
		this.model = model;
		
		caption.setModel(model);
		render.setModel(model);
		
		if (model != null) {
			model.addListener(modelListener);
			title.setText(model.getTitle());
			setBorderColor(borderColor);
		} else 
			title.setText("");
	}
	
	/**
	 * Modification de la visibilite du caption
	 * @param visible
	 */
	public void setCaptionVisibility (boolean visible) {
		center.removeAll();
		center.setLayout(visible? layout : borderLayout);
		
		
		if (!visible) {
			model.removeListener(caption);
			center.add(render, BorderLayout.CENTER);
		} else {
			center.add(render);
			center.add(scroll);
			model.addListener(caption);
		}
		
		scroll.setVisible(visible);
		center.repaint();
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

	
	private void init() {
		this.setLayout(new BorderLayout());
		final Panel panel = new Panel(new BorderLayout());
		panel.add(caption, BorderLayout.CENTER);
		scroll = FormUtil.createVerticalScrollPane(panel);
		
		add(center, BorderLayout.CENTER);
		add(title, BorderLayout.SOUTH);
		
		setBackground(FormUtil.BKG_DARK);
		title.setFont(Card.FONT_INFO);
		
		center.add(render);
		center.add(scroll);
		setBorderColor(borderColor);
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
		if(render != null)
			render.setBackground(bg);
		
		if(caption != null)
			caption.setBackground(bg);
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
