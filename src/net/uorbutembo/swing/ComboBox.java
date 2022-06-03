package net.uorbutembo.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import net.uorbutembo.views.forms.FormUtil;

/**
 * 
 * @author Esaie MUHASA
 *
 * @param <E>
 */
public class ComboBox<E> extends JComboBox<E> implements InputComponent<E>{

	private static final long serialVersionUID = 1L;
	
	public static final Color DEFAULT_COLOR= Color.LIGHT_GRAY;
	public static final EmptyBorder EMPTY_BORDER =new EmptyBorder(5, 5, 5, 5);
	
    private String label;
    
    private Color lineColor = new Color(3, 155, 216);
    private boolean mouseOver;

    /**
     * construcurter par defaut
     */
    public ComboBox(String label) {
    	super();
        this.init();
        this.setLabel(label);
    }
    
    /**
     * construcurter par defaut
     */
    public ComboBox(String label, ComboBoxModel<E> model) {
    	super(model);
    	this.init();
    	this.setLabel(label);
    }
    
    protected void init() {
    	this.setBackground(FormUtil.BKG_DARK);
    	setBorder(TextField.DEFAULT_EMPTY_BORDER);
    	setUI(new ComboUI(this));
    	setForeground(DEFAULT_COLOR);
    	
    	
    	//on utilise le rendu par de faut du JList
    	setRenderer(new DefaultListCellRenderer() {
    		private static final long serialVersionUID = 1L;
    		
    		@Override
    		public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
    			Component com = super.getListCellRendererComponent(jlist, o, i, bln, bln1);
    			setBorder(EMPTY_BORDER);
    			if (bln) {
    				com.setBackground(FormUtil.BORDER_COLOR);
    			}
    			com.setForeground(DEFAULT_COLOR);
    			return com;
    		}
    	});
	}


    @Override
	public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
	public E getValue() {
		return this.getItemAt(this.getSelectedIndex());
	}


	@Override
	public void setValue(E value) {
		this.setSelectedItem(value);
	}


	@Override
	public JComponent getComponent() {
		return this;
	}


	public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
    
    @Override
    protected void paintBorder(Graphics g) {
//    	super.paintBorder(g);
    	
    	Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
//		g2.setColor(this.hasFocus()? this.borderColorFocus : this.borderColor);
        g2.setColor(DEFAULT_COLOR);
		g2.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
    }

    /**
     * Gestionnaire de l'affichage du combo box
     * @author Esaie MUHASA
     *
     */
    protected class ComboUI extends BasicComboBoxUI {//BasicComboBoxUI

        private final Animator animator;
        private boolean animateHinText = true;
        private float location;
        private boolean show;
        private ComboBox<?> combo;

        public ComboUI(ComboBox<?> combo) {
            this.combo = combo;
            
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
            
            addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent ie) {
                    if (!isFocusOwner()) {
                        if (getSelectedIndex() == -1) {
                            showing(true);
                        } else {
                            showing(false);
                        }
                    }
                }
            });
            
            addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent pme) {
                    arrowButton.setBackground(new Color(200, 200, 200));
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent pme) {
                    arrowButton.setBackground(new Color(150, 150, 150));
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent pme) {
                    arrowButton.setBackground(new Color(150, 150, 150));
                }
            });
            
            TimingTarget target = new TimingTargetAdapter() {
                @Override
                public void begin() {
                    animateHinText = getSelectedIndex() == -1;
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

        @Override
        public void paintCurrentValueBackground(Graphics grphcs, Rectangle rctngl, boolean bln) {

        }

        @Override
        protected JButton createArrowButton () {
            return new ArrowButton();
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = (BasicComboPopup) super.createPopup();
            //popup.getList().setFixedCellHeight(30);
            popup.setBorder(new LineBorder(DEFAULT_COLOR, 1));
            return popup;
        }
        

        @Override
        public void paint(Graphics g, JComponent jc) {
            super.paint(g, jc);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            
            int width = getWidth();
            int height = getHeight();
            
            if (mouseOver) {
                g2.setColor(lineColor);
            } else {
                g2.setColor(DEFAULT_COLOR);
            }
            g2.fillRect(0, height , width, 1);
            createHintText(g2);
            createLineStyle(g2);
//            g2.dispose();
        }

        /**
         * personalisation du text lors de la selection d'un item du combo
         * @param g2
         */
        private void createHintText(Graphics2D g2) {
            Insets in = getInsets();
            g2.setColor(DEFAULT_COLOR);
            FontMetrics ft = g2.getFontMetrics();
            Rectangle2D r2 = ft.getStringBounds(combo.getLabel(), g2);
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
            g2.drawString(combo.getLabel(), in.right, (int) (in.top + textY + ft.getAscent() - size));
        }

        protected void createLineStyle(Graphics2D g2) {
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
         * Le bouton permetant d'afficher le contenue du ComboBox
         * @author Esaie MUHASA
         *
         */
        private class ArrowButton extends JButton {
			private static final long serialVersionUID = 1L;

			public ArrowButton() {
                setContentAreaFilled(false);
                setBorder(EMPTY_BORDER);
                setBackground(new Color(150, 150, 150));
            }
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int width = getWidth();
				int height = getHeight();
				int size = 10;
				int x = (width - size) / 2;
				int y = (height - size) / 2 + 5;
				int px[] = {x, x + size, x + size / 2};
				int py[] = {y, y, y + size};
				g2.setColor(getBackground());
				g2.fillPolygon(px, py, px.length);
				g2.dispose();
			}
        }
    }
}
