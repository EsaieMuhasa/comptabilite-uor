/**
 * 
 */
package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class EditText <T> extends JTextField  implements InputComponent<T>{
	private static final long serialVersionUID = -8698719412320568232L;
	public static final EmptyBorder DEFAULT_EMPTY_BORDER = new EmptyBorder(15, 5, 5, 5);
	public static final Dimension 
								DEFAULT_PRESERRED_SIZE = new Dimension(100, 45),
								DEFAULT_MIN_SIZE = new Dimension(70, 45),
								DEFAULT_MAX_SIZE = new Dimension(10000, 45);
	
	private final Animator animator;
    private boolean animateHinText = true;
    private float location;
    private boolean show;
    private boolean mouseOver = false;
    private Color lineColor = new Color(3, 155, 216);
	
	private String hint;//le text du label
	
	/**
	 * Contructeur d'initialisation
	 * @param hint, le label du champ de text
	 */
	public EditText(String hint) {
		super();
		this.hint = hint;
		this.setBackground(FormUtil.BKG_DARK);
		this.setForeground(Color.WHITE);
		this.setFont(new Font(this.getFont().getName(), Font.BOLD, 12));
		this.setPreferredSize(DEFAULT_PRESERRED_SIZE);
		this.setMinimumSize(DEFAULT_MIN_SIZE);
		this.setMaximumSize(DEFAULT_MAX_SIZE);
		setBorder(DEFAULT_EMPTY_BORDER);
        setSelectionColor(new Color(76, 204, 255));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent me) {
                mouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent me) {
                mouseOver = false;
                repaint();
            }
        });
        
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
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		int width = getWidth();
		int height = getHeight();
		if (mouseOver) {
			g2.setColor(lineColor);
		} else {
			g2.setColor(FormUtil.BORDER_COLOR);
		}
		g2.fillRect(2, height - 1, width - 4, 1);
		createHintText(g2);
		createLineStyle(g2);
		g2.dispose();
	}

    private void createHintText(Graphics2D g2) {
        Insets in = getInsets();
        g2.setColor(new Color(150, 150, 150));
        FontMetrics ft = g2.getFontMetrics();
        Rectangle2D r2 = ft.getStringBounds(this.getHint(), g2);
        double height = getHeight() - in.top - in.bottom;
        double textY = (height - r2.getHeight()) / 2;
        double size;
        if (animateHinText) {
            if (show) {
                size = 18 * (1 - location);
            } else {
                size = 18 * location;
            }
        } else {
            size = 18;
        }
        g2.drawString(this.getHint(), in.right, (int) (in.top + textY + ft.getAscent() - size));
    }

    private void createLineStyle(Graphics2D g2) {
        if (isFocusOwner()) {
            double width = getWidth() - 4;
            int height = getHeight();
            g2.setColor(lineColor);
            double size;
            if (show) {
                size = width * (1 - location);
            } else {
                size = width * location;
            }
            double x = (width - size) / 2;
            g2.fillRect((int) (x + 2), height - 2, (int) size, 2);
        }
    }

    @Override
    public void setText(String string) {
        if (!getText().equals(string)) {
            showing(string.equals(""));
        }
        super.setText(string);
    }

	@Override
	public void setLabel(String label) {
		this.hint = label;
	}

	@Override
	public String getLabel() {
		return this.hint;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getValue() {
		try {			
			return (T) this.getText();
		} catch (ClassCastException e) {}
		return null;
	}
	
	@Override
	public void setValue(T value) {
		this.setText(value.toString());
	}

	@Override
	public JComponent getComponent() {
		return this;
	}

	/**
	 * Renvoie le label de l'edit text
	 * @return
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * Modification du label de l'edit text
	 * @param hint
	 */
	public void setHint(String hint) {
		this.hint = hint;
		
		if(this.getText() == null || this.getText().trim().isEmpty()) {
			this.repaint();
		}
	}

}
