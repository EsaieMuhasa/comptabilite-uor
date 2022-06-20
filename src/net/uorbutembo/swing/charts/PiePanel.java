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
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;

/**
 * @author Esaie MUHASA
 *
 */
public class PiePanel extends Panel implements Printable{
	private static final long serialVersionUID = 8834575442903333237L;
	
	private PieRender render;
	private PieCaptionRender caption;
	private JLabel title = FormUtil.createSubTitle("");
	private JScrollPane scroll;
	private Color borderColor;
	
	private final GridLayout layout = new GridLayout(1, 2);
	private final BorderLayout borderLayout = new BorderLayout();
	private final Panel center = new Panel(layout);
	private final Panel tools = new Panel(new BorderLayout());
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
	
	private final JButton btnPrint = new Button(new ImageIcon(R.getIcon("print")), "Imprimer");
	private final JButton btnImg = new Button(new ImageIcon(R.getIcon("saveimg")), "Exporter");
	{
		btnImg.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		btnPrint.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
	}
	
	private final ActionListener btnPrintListener = event -> {
		PrinterJob job = PrinterJob.getPrinterJob();
		
		if(job.printDialog() && job.pageDialog(job.defaultPage()) != null) {

			job.setPrintable(this);
			
			try {
				job.print();
			} catch (PrinterException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur d'impression", JOptionPane.ERROR_MESSAGE);
			}
		}
	};
	
	private final ActionListener btnImgListener = event -> {
		
		int status = ChartPanel.FILE_CHOOSER.showSaveDialog(this);
		if(status == JFileChooser.APPROVE_OPTION) {
			String fileName = ChartPanel.FILE_CHOOSER.getSelectedFile().getAbsolutePath();
			if(!fileName.matches("^(.+)(\\.)(png|jpeg|jpg)$"))
				fileName += ".png";
			
			String type = fileName.substring(fileName.lastIndexOf(".")+1);
			
			Color old = getBackground();
			Toolkit tool = Toolkit.getDefaultToolkit();
			int width = (int)(tool.getScreenSize().getWidth() * 1.2f);
			int height = (int)(tool.getScreenSize().getHeight() * 1.2f);
			
			if (height < caption.getHeight())
				height = caption.getHeight() + 200;
			
			boolean isPng = type.equals("png");
			BufferedImage buffer = new BufferedImage(width, height, isPng? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) buffer.getGraphics();
			Rectangle2D rect = new Rectangle2D.Double(0, 0, width-2, height-2);
			
			render.setBackground(Color.WHITE);
			caption.setBackground(Color.WHITE);
			
			if(!isPng) {
				g.setColor(Color.WHITE);
				g.fill(rect);
			}
			
			render.paint(g, width/2, height);
			caption.paint(g, width/2, 0, width/2, height);
			
			g.setColor(Color.BLACK);
			g.drawString(title.getText(), 10f, height - 20f);
			g.draw(rect);
			
			render.setBackground(old);
			caption.setBackground(old);
			
			File file = new File(fileName);
			try {
				ImageIO.write(buffer, type, file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
			}
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
	
	@Override
	public int print (Graphics graphics, PageFormat page, int pageIndex) throws PrinterException {
		if(pageIndex > 0)
			return NO_SUCH_PAGE;
		
		Color old = getBackground();
		render.setBackground(Color.WHITE);
		caption.setBackground(Color.WHITE);
		
		Graphics2D g2 = (Graphics2D) graphics;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g2.translate(page.getImageableX(), page.getImageableY());
		
		if (page.getOrientation() == PageFormat.LANDSCAPE) {
			render.paint(g2, page.getImageableWidth()/2, page.getImageableHeight());
			caption.paint(g2, page.getImageableWidth()/2, 0, (int)page.getImageableWidth()/2, (int)page.getImageableHeight());
		} else {
			render.paint(g2, page.getImageableWidth(), page.getImageableHeight()/3);
			caption.paint(g2, 5, page.getImageableHeight()/3, (int)page.getImageableWidth(), (int) (page.getImageableHeight() * (2f/3f)));
		}
		
		g2.setColor(borderColor);
		Rectangle2D b = new Rectangle2D.Double(0, 0, page.getImageableWidth(), page.getImageableHeight());
		g2.draw(b);
		g2.setFont(title.getFont());
		g2.drawString(title.getText(), 10f, g2.getFontMetrics().getAscent());
		
		render.setBackground(old);
		caption.setBackground(old);
		
		return PAGE_EXISTS;
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
		
		tools.setOpaque(true);
		if(borderColor != null) {
			tools.setBackground(borderColor.darker());
			this.caption.setBorderColor(borderColor);
		}
		
	}

	
	private void init() {
		this.setLayout(new BorderLayout());
		final Panel panel = new Panel(new BorderLayout());
		panel.add(caption, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(5, 0, 5, 0));
		scroll = FormUtil.createVerticalScrollPane(panel);
		
		add(center, BorderLayout.CENTER);
		add(tools, BorderLayout.SOUTH);
		
		//tools
		final Box box = Box.createHorizontalBox();
		tools.add(title, BorderLayout.CENTER);
		tools.add(box, BorderLayout.EAST);
		tools.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		box.add(btnPrint);
		box.add(btnImg);
		btnPrint.addActionListener(btnPrintListener);
		btnImg.addActionListener(btnImgListener);
		//==
		
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
	protected void paintBorder(Graphics g) {
		super.paintComponent(g);
		
		if(this.borderColor != null) {	
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(this.borderColor);
			g2.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		}
	}
	
}
