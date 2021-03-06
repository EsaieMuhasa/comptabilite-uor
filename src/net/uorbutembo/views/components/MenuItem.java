package net.uorbutembo.views.components;

import static net.uorbutembo.tools.FormUtil.BKG_END;
import static net.uorbutembo.tools.FormUtil.BKG_END_2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.views.components.MenuItemModel.MenuItemModelListener;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class MenuItem extends JPanel{
	private static final long serialVersionUID = 1591472286070600257L;
	
	private boolean current = false;
	private float alpha = 1f;
	private boolean open = false;
	private MenuItemModel<?>  model;
	private MenuItemAnimation animation=null;
	
	private final List<MenuItemButton> buttons = new ArrayList<>();
	private final List<ActionListener> buttonActions = new ArrayList<>();
	
	private MenuItemButton firstItem;
	private MenuItemListener listener;
	
	private final MenuItemModelListener modelListener = new MenuItemModelListener() {
		
		@Override
		public void onChange(MenuItemModel<?> model, int index) {
			MenuItemButton btn = buttons.get(index);
			btn.setText(model.getItem(index).toString());
			if(model.getCurrentItem() == index)
				btn.setActive(true);
			else
				btn.setActive(false);
		}
		
		@Override
		public void onChange(MenuItemModel<?> model) {
			
			for(int i = 0; i < buttons.size(); i++)
				buttons.get(i).removeActionListener(buttonActions.get(i));
			
			removeAll();
			buttonActions.clear();
			buttons.clear();
			
			add(firstItem);
			generateSubMenu();
			
			revalidate();
			repaint();
		}
	};
	
	/**
	 * @param side
	 * @param model
	 * @param listener
	 */
	public MenuItem(Sidebar side, MenuItemModel<?> model, MenuItemListener listener) {
		super();
		this.listener = listener;
		this.initComponents();
        this.model = model;
        setOpaque(false);
        setLayout(new MigLayout("wrap, fillx, insets 0", "[fill]", "[fill, 40!]0[fill, 35!]"));
        firstItem = new MenuItemButton(model.getIcon(), model.getLabel(), model.getName());
        
        if(model.countItems() != 0) {        	
        	animation = new MenuItemAnimation(side.layout, this);
        }
        firstItem.addActionListener(event -> {
            if (model.countItems() != 0) {
            	open = !open;
            	listener.onAction(this);            	
            	if (open) {
            		listener.onOpen(this);
            	} else {
            		listener.onClose(this);
            	}
            	animation.toggleMenu();
            } else {
            	listener.onAction(this);
            }
        });
        
        add(firstItem);//on ajoute le premier item-menu
        generateSubMenu();
        
        model.addModelListener(modelListener);
	}
	
	/**
	 * generation du sous menu
	 */
	private void generateSubMenu () {
		if(model.countItems() != 0) {//pour des menus qui ont des sous-menus  	
			for (int index = 0, max = model.countItems(); index < max; index++) {
				MenuItemButton itemMenu = new MenuItemButton(model.getItem(index).toString());
				ActionListener  action = (event) -> {
					listener.onAction(this, itemMenu.getIndex(), itemMenu);        			
				};
				itemMenu.setIndex(index);
				itemMenu.addActionListener(action);
				buttons.add(itemMenu);
				add(itemMenu);
			}
		}
	}
	
	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
		if (!current)
			model.setSelectedItem(-1);
		
		repaint();
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public MenuItemModel<?> getModel() {
		return model;
	}

	/**
	 * mis au point du layout du menu
	 */
	private void initComponents () {
		GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE)
        );
	}
	
	@Override
    protected void paintComponent(Graphics grphcs) {
		 super.paintComponent(grphcs);
		 int width = getWidth();
		 int height = getPreferredSize().height;
		 Graphics2D g2 = (Graphics2D) grphcs;
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		 g2.setColor(BKG_END);
		 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.isOpen()? this.alpha : 1f));
		 g2.fillRect(0, 2, width, 38);
		 g2.setComposite(AlphaComposite.SrcOver);
		 g2.fillRect(0, 40, width, height - 40);
		 
		 if (model.countItems() != 0) {//pour uniquement les menus qui ont des sous-menu
			 g2.setColor(BKG_END_2);
			 g2.drawLine(20, 40, 20, height - 17);
			 
			 for (int i = 0, max = model.countItems(); i < max; i++) {
				 int y = ((i + 1) * 35 + 40) - 17;
				 g2.drawLine(20, y, 30, y);
			 }
			 createArrowButton(g2);
		 }
		 
		 //si menu curent
		 
		 if(this.isCurrent()) {
			 int xs [] = {this.getWidth(), this.getWidth()-19, this.getWidth()};
			 int ys [] = {2, 19, 38};
			 
			 g2.setColor(FormUtil.BKG_DARK);
			 g2.fillPolygon(xs, ys, 3);
			 
			 g2.setColor(FormUtil.BORDER_COLOR);
			 g2.drawPolygon(xs, ys, 3);
		 }
    }
	
	 /**
	  * pour desinner la fleche
	  * @param g2
	  */
    private void createArrowButton(Graphics2D g2) {
        int size = 5;
        int y = 19;
        int x = this.getWidth()-30;
        g2.setColor(new Color(230, 230, 230));
        float ay = alpha * size;
        float ay1 = (1f - alpha) * size;
        g2.drawLine(x, (int) (y + ay), x + size, (int) (y + ay1));
        g2.drawLine(x + size, (int) (y + ay1), x + (size * 2), (int) (y + ay));
    }

}
