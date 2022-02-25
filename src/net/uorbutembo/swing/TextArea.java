/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class TextArea extends JTextArea implements InputComponent<String> {
	private static final long serialVersionUID = -1217222153891185810L;
	
	private String hint;
	private Animator animator;
    private boolean animateHinText = true;
    private float location;
    private boolean show;

	/**
	 * @param hint
	 */
	public TextArea(String hint) {
		super();
		this.hint = hint;
		this.init();
	}

	/**
	 * @param rows
	 * @param columns
	 */
	public TextArea(int rows, int columns) {
		super(rows, columns);
		this.init();
	}

	/**
	 * @param text
	 * @param rows
	 * @param columns
	 */
	public TextArea(String hint, int rows, int columns) {
		super(rows, columns);
		this.hint = hint;
		this.init();
	}
	
	private void init() {
		this.setBorder(TextField.DEFAULT_EMPTY_BORDER);
		this.setBackground(FormUtil.BKG_DARK);
		this.setForeground(FormUtil.BORDER_COLOR);
		TimingTarget target = new TimingTargetAdapter() {
			@Override
			public void begin() {
				animateHinText = getText().equals("");
			}
			
			@Override
			public void timingEvent(float fraction) {
				location = fraction;
				repaint();
			}
		};
		
		animator = new Animator(300, target);
		animator.setResolution(0);
		animator.setAcceleration(0.5f);
		animator.setDeceleration(0.5f);
		
		addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                showing(false);
            }

            @Override
            public void focusLost(FocusEvent fe) {
                showing(true);
            }
        });
	}
	
	private void showing(boolean action) {
        if (animator.isRunning()) {
            animator.stop();
        } else {
            location = 1;
        }
        animator.setStartFraction(1f - location);
        show = action;
        location = 1f - location;
        animator.start();
    }
	
	/**
	 * @return the hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * @param hint the hint to set
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		int width = getWidth();
		int height = getHeight();
		g2.setColor(FormUtil.BORDER_COLOR);
		g2.fillRect(2, height - 1, width - 4, 1);
		createHintText(g2);
		g2.dispose();
	}

	/**
	 * @param g2
	 */
    private void createHintText(Graphics2D g2) {
        Insets in = getInsets();
        g2.setColor(new Color(150, 150, 150));
        FontMetrics ft = g2.getFontMetrics();
        Rectangle2D r2 = ft.getStringBounds(this.getHint(), g2);
        //double height = getHeight() - in.top - in.bottom;
        double textY = 2;//(height - r2.getHeight());
        double size;
        if (animateHinText) {
            if (show) {
                size = 18 * (1 - location);
            } else {
                size = 18 * location;
            }
        } else {
            size = 10;
        }
        
        int x =  in.right;
        int y = (int) (in.top + textY + ft.getAscent() - size);
        g2.drawString (this.getHint(), x, y);
    }

	@Override
	public void setLabel (String label) {
		this.setHint(label);
	}

	@Override
	public String getLabel () {
		return this.getHint();
	}

	@Override
	public String getValue () {
		return this.getText ();
	}

	@Override
	public void setValue (String value) {
		this.setText (value);
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

}
