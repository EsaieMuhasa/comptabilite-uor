/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.tools.FormUtil.BKG_END;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.AllocationRecipeDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.DAOFactory;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.PieRender;
import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalMenuItem extends Panel {
	private static final long serialVersionUID = -8967984877919518052L;
	
	private static final Color
			BKG_1 = new Color(0x330F0F0F, true),
			BKG_2 = new Color(0x707070),
			TEXT_COLOR = new Color(0xCCCCCC);

	private static final Font FONT_TITLE = new Font("Arial", Font.PLAIN, 14);
	private static final Font FONT_MONEY = new Font("Arial", Font.BOLD, 14);
	private static final Font FONT_PERCENT = new Font("Arial", Font.PLAIN, 18);
	private static final Dimension PREF_SIZE = new Dimension(300, 60),
			MIN_SIZE = new Dimension(280, 50),
			MAX_SIZE = new Dimension(320, 70);
	
	private final List<ActionListener> actionListeners = new ArrayList<>();
	private final List<JournalMenuItemListener> itemListeners = new ArrayList<>();
	
	private boolean hover = false;
	private boolean active = false;
	private final MouseAdapter mouseAdapter = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			if(actionListeners.isEmpty() || !isEnabled())
				return;
			
			ActionEvent event = new ActionEvent(JournalMenuItem.this, e.getID(), "click");
			for (ActionListener ls : actionListeners) {
				ls.actionPerformed(event);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			hover = true;
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			hover = false;
			repaint();
		}
		
	};
	
	private final AnnualSpend account;
	
	private final AnnualSpendDao annualSpendDao;
	private final AllocationRecipeDao allocationRecipeDao;
	private final OtherRecipeDao otherRecipeDao;
	private final OutlayDao outlayDao;
	private final PaymentFeeDao paymentFeeDao;
	
	private final DAOAdapter<PaymentFee> paymentAdapter = new DAOAdapter<PaymentFee>() {

		@Override
		public synchronized void onCreate(PaymentFee e, int requestId) {
			annualSpendDao.reload(account);
			emitOnReload();
		}

		@Override
		public synchronized void onUpdate(PaymentFee e, int requestId) {
			annualSpendDao.reload(account);
			emitOnReload();
		}

		@Override
		public synchronized void onDelete(PaymentFee e, int requestId) {
			annualSpendDao.reload(account);
			emitOnReload();
		}
		
	};
	
	private final DAOAdapter<OtherRecipe> autherAdapter = new DAOAdapter<OtherRecipe>() {

		@Override
		public synchronized void onCreate(OtherRecipe e, int requestId) {
			if(allocationRecipeDao.check(e.getAccount().getId(), account.getId())) {
				annualSpendDao.reload(account);
				repaint();
				emitOnReload();
			}
		}

		@Override
		public synchronized void onUpdate(OtherRecipe e, int requestId) {
			if(allocationRecipeDao.check(e.getAccount().getId(), account.getId())) {
				annualSpendDao.reload(account);
				repaint();
				emitOnReload();
			}
		}

		@Override
		public synchronized void onDelete(OtherRecipe e, int requestId) {
			if(allocationRecipeDao.check(e.getAccount().getId(), account.getId())) {
				annualSpendDao.reload(account);
				repaint();
				emitOnReload();
			}
		}
		
	};
	
	private final DAOAdapter<Outlay> outlayAdapter = new DAOAdapter<Outlay>() {

		@Override
		public synchronized void onCreate(Outlay e, int requestId) {
			if(e.getAccount().getId() == account.getId()){
				account.setUsed(account.getUsed() + e.getAmount());
				repaint();
				emitOnReload();
			}
		}

		@Override
		public synchronized void onUpdate(Outlay e, int requestId) {
			annualSpendDao.reload(account);
			repaint();
			emitOnReload();
		}

		@Override
		public synchronized void onDelete(Outlay e, int requestId) {
			if(e.getAccount().getId() == account.getId()){
				account.setUsed(account.getUsed() - e.getAmount());
				repaint();
				emitOnReload();
			}
		}
	};

	/**
	 * @param account
	 * @param factory
	 */
	public JournalMenuItem (AnnualSpend account, DAOFactory factory) {
		super();
		this.account = account;
		setPreferredSize(PREF_SIZE);
		setMaximumSize(MAX_SIZE);
		setMinimumSize(MIN_SIZE);
		addMouseListener(mouseAdapter);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		annualSpendDao = factory.findDao(AnnualSpendDao.class);
		allocationRecipeDao = factory.findDao(AllocationRecipeDao.class);
		outlayDao = factory.findDao(OutlayDao.class);
		otherRecipeDao = factory.findDao(OtherRecipeDao.class);
		paymentFeeDao = factory.findDao(PaymentFeeDao.class);
		
		otherRecipeDao.addListener(autherAdapter);
		outlayDao.addListener(outlayAdapter);
		paymentFeeDao.addListener(paymentAdapter);
	}
	
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive (boolean active) {
		if (this.active == active) 
			return;
		
		this.active = active;
		repaint();
	}

	/**
	 * Ajout d'un action
	 */
	public void addActionListener (ActionListener listener) {
		if(!actionListeners.contains(listener))
			actionListeners.add(listener);
	}
	
	/**
	 * ajout ecouteur de changement d'etat (mis en jours des donnees du model)
	 * @param listener
	 */
	public void addItemListener (JournalMenuItemListener listener) {
		if (!itemListeners.contains(listener))
			itemListeners.add(listener);
	}
	
	/**
	 * supression d'un listener
	 * @param listener
	 */
	public void removeActionListener (ActionListener listener) {
		actionListeners.remove(listener);
	}
	
	/**
	 * supression d'un itemListener
	 * @param listener
	 */
	public void removeItemListener (JournalMenuItemListener listener) {
		itemListeners.remove(listener);
	}
	
	/**
	 * emission du changement du menu
	 */
	protected void emitOnReload () {
		for (JournalMenuItemListener listener : itemListeners)
			listener.onReload(this);
	}
	
	/**
	 * reconnection de la vue au DAO
	 */
	public void dispose() {
		otherRecipeDao.removeListener(autherAdapter);
		outlayDao.removeListener(outlayAdapter);
		paymentFeeDao.removeListener(paymentAdapter);
	}
	
	/**
	 * @return the account
	 */
	public AnnualSpend getAccount() {
		return account;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setCursor(enabled? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int x = 0, y = 3, w = 0, h = 0;
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        
        g2.setColor(BKG_1.darker());
        g2.fillRect(0, 0, getWidth(), getHeight());
        double wDispo = 0;
        
        if (account.getCollectedRecipe() != 0 || account.getCollectedCost() != 0) {	        	
        	BigDecimal big = new BigDecimal(100.0 - (( account.getUsed() / (account.getCollectedRecipe() + account.getCollectedCost()) ) * 100.0) ).setScale(2, RoundingMode.FLOOR);
        	wDispo = big.doubleValue();
        }
        FontMetrics metrics = g2.getFontMetrics(FONT_TITLE);
        
        String title = account.getUniversitySpend().getTitle();
        x = w = getWidth()/3 - getHeight()/3;
        y = getHeight()/2 - FONT_TITLE.getSize()/2;
        g2.setColor(TEXT_COLOR);
        g2.setFont(FONT_TITLE);
        g2.drawString(title, x, y);
        
        metrics = g2.getFontMetrics(FONT_MONEY);
        final double amount = ((account.getCollectedRecipe() + account.getCollectedCost()) - account.getUsed());
        String money =  PieRender.DECIMAL_FORMAT.format(amount)+" "+FormUtil.UNIT_MONEY;
        y = getHeight() - (FONT_MONEY.getSize()/2 + 2);
        g2.setFont(FONT_MONEY);
        g2.drawString(money, x, y);
        
        //rect money
        x = 0;
        y = 3;
        w = getWidth()/3;
        h = getHeight() - (y * 2) -1;
        
        if (active) 
        	g2.setColor(FormUtil.ACTIVE_COLOR);
        else {        	
        	if (hover && isEnabled())
        		g2.setColor(BKG_2);
        	else
        		g2.setColor(BKG_END);
        }
        
        g2.fillRoundRect(-h, y, w+h/3, h, h, h);
        g2.setFont(FONT_PERCENT);
        String percent = wDispo+"%";
        
        metrics = g2.getFontMetrics(FONT_PERCENT);
        x = 3;
        y = getHeight() - metrics.getHeight() - metrics.getHeight()/8;
        g2.setColor(TEXT_COLOR);
        g2.drawString(percent, x, y);
        //==
        
        //line
        x = w - 20;
        w = getWidth() - w;
        h = 5;
        y = getHeight()/2 - h/2;
        
        if (active) 
        	g2.setColor(FormUtil.ACTIVE_COLOR);
        else {        	
        	if (hover)
        		g2.setColor(BKG_2);
        	else 
        		g2.setColor(BKG_END.darker());
        }
        
        double bar = (wDispo/100.0) * w; 
        g2.drawRoundRect(x, y, w, h, h, h);
        g2.fillRoundRect(x+1, y+1, (int)bar, h-1, h, h);
        //==

        if (active) 
        	g2.setColor(FormUtil.ACTIVE_COLOR);
        else {
        	if (hover)
        		g2.setColor(BKG_2);
        	else 
        		g2.setColor(BKG_END);
        }
        g2.drawRect(0, 0, getWidth()-2, getHeight()-2);
        
	}
}
