/**
 * 
 */
package net.uorbutembo.views.components;

import static net.uorbutembo.views.forms.FormUtil.BKG_END;
import static net.uorbutembo.views.forms.FormUtil.BORDER_COLOR;
import static net.uorbutembo.views.forms.FormUtil.DEFAULT_EMPTY_BORDER;
import static net.uorbutembo.views.forms.FormUtil.createVerticalScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.UniversitySpendDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.ComboBox;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.views.JournalWorkspace;
import net.uorbutembo.views.MainWindow;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class SidebarJournal extends Panel{
	private static final long serialVersionUID = -3295426631162619526L;
	
	private final JPanel panelBottom = new Panel(new FlowLayout(FlowLayout.CENTER));
	private final Box container = Box.createVerticalBox();
	private final Button btnNewOutlay = new Button(new ImageIcon(R.getIcon("moin")), "Sortie");
	private final Button btnNewRecipe = new Button(new ImageIcon(R.getIcon("plus")), "Entrée");
	
	private final MainWindow mainWindow;
	private final JournalWorkspace workspace;
	
	private final UniversitySpendDao universitySpendDao;
	private final AnnualSpendDao annualSpendDao;
	private final AcademicYearDao academicYearDao;
	
	private final DefaultComboBoxModel<AcademicYear> comboModel = new DefaultComboBoxModel<>();
	private final ComboBox<AcademicYear> comboBox = new ComboBox<>("Année academique", comboModel);
	
	private final DAOAdapter<AcademicYear> yearAdapter = new DAOAdapter<AcademicYear>() {
		@Override
		public synchronized void onCurrentYear(AcademicYear year) {
			if(comboModel.getSize() == 0)
				loadYears();
		}
		
	};

	/**
	 * @param mainWindow
	 */
	public SidebarJournal(MainWindow mainWindow, JournalWorkspace workspace) {
		super(new BorderLayout());
		this.mainWindow = mainWindow;
		this.workspace = workspace;
		
		universitySpendDao = mainWindow.factory.findDao(UniversitySpendDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		academicYearDao.addYearListener(yearAdapter);

		this.setPreferredSize(new Dimension(350, 400));
		
		init();
		
	}
	
	private void loadYears() {
		if(academicYearDao.countAll() != 0) {
			List<AcademicYear> years = academicYearDao.findAll();
			for (AcademicYear year : years)
				comboModel.addElement(year);
		}
	}
	
	/**
	 * Initialisation de l'interface graphique
	 */
	private void init () {
		panelBottom.add(btnNewRecipe);
		panelBottom.add(btnNewOutlay);
		
		final Panel panelTop = new Panel(new BorderLayout());
		panelTop.setBorder(DEFAULT_EMPTY_BORDER);
		panelTop.add(comboBox);
		
		add(panelBottom, BorderLayout.SOUTH);
		add(panelTop, BorderLayout.NORTH);
		
		//events
		btnNewOutlay.addActionListener(event -> {
			workspace.createOutlay();
		});
		btnNewRecipe.addActionListener(event -> {
			workspace.createRecipe();
		});
		
		
		comboBox.addItemListener(event -> {
			comboBox.setEnabled(false);
			comboBox.setCursor(FormUtil.WAIT_CURSOR);
			container.setCursor(FormUtil.WAIT_CURSOR);
			container.removeAll();
			int index = comboBox.getSelectedIndex();
			if (index != -1) {
				
				AcademicYear year = comboModel.getElementAt(index);
				Thread t = new Thread(() -> {
					if (annualSpendDao.checkByAcademicYear(year.getId())) {
						List<AnnualSpend> spends = annualSpendDao.findByAcademicYear(year);
						
						for (AnnualSpend spend : spends) {
							container.add(new AccountListItem(spend));
							container.add(Box.createVerticalStrut(10));
						}
					}
					
					container.add(Box.createVerticalGlue());
					comboBox.setCursor(Cursor.getDefaultCursor());
					container.setCursor(Cursor.getDefaultCursor());
					container.revalidate();
					container.repaint();
					comboBox.setEnabled(true);
				});
				t.start();
			} else {
				comboBox.setCursor(Cursor.getDefaultCursor());
				container.setCursor(Cursor.getDefaultCursor());
				container.revalidate();
				container.repaint();
				comboBox.setEnabled(true);
			}
		});
		
		final Panel panel = new Panel(new BorderLayout());
		final Panel contentPanel = new Panel(new BorderLayout());
		
		//panel.setBorder(new LineBorder(BORDER_COLOR));
		panel.add(createVerticalScrollPane(container), BorderLayout.CENTER);
		
		contentPanel.setBorder(DEFAULT_EMPTY_BORDER);
		contentPanel.add(panel, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setColor(BORDER_COLOR);
        g2.drawLine(0, 0, 0, getHeight());
	}
	
	/**
	 * 
	 * @author Esaie MUHASA
	 *
	 */
	protected static class AccountListItem extends JComponent{
		private static final long serialVersionUID = -8967984877919518052L;
		
		private static final Color
			BKG_1 = new Color(0x330F0F0F, true),
			BKG_2 = new Color(0x707070),
			TEXT_COLOR = new Color(0xCCCCCC);
		
		private static final Font FONT_TITLE = new Font("Arial", Font.PLAIN, 14);
		private static final Font FONT_MONEY = new Font("Arial", Font.BOLD, 14);
		private static final Font FONT_PERCENT = new Font("Arial", Font.PLAIN, 18);
		private static final Dimension PREF_SIZE = new Dimension(320, 60),
				MIN_SIZE = new Dimension(300, 50),
				MAX_SIZE = new Dimension(400, 70);
		
		private boolean hover = false;
		private final MouseAdapter mouseAdapter = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
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

		public AccountListItem (AnnualSpend account) {
			super();
			this.account = account;
			setPreferredSize(PREF_SIZE);
			setMaximumSize(MAX_SIZE);
			setMinimumSize(MIN_SIZE);
			addMouseListener(mouseAdapter);
			setCursor(new Cursor(Cursor.HAND_CURSOR));
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
	        	BigDecimal big = new BigDecimal(( account.getUsed() / (account.getCollectedRecipe() + account.getCollectedCost()) ) * 100.0).setScale(2, RoundingMode.FLOOR);
	        	wDispo = 100.0 - big.doubleValue();
	        }
	        FontMetrics metrics = g2.getFontMetrics(FONT_TITLE);
	        
	        String title = account.getUniversitySpend().getTitle();
	        x = w = getWidth()/3 - getHeight()/3;
	        y = getHeight()/2 - FONT_TITLE.getSize()/2;
	        g2.setColor(TEXT_COLOR);
	        g2.setFont(FONT_TITLE);
	        g2.drawString(title, x, y);
	        
	        metrics = g2.getFontMetrics(FONT_MONEY);
	        String money = ((account.getCollectedRecipe() + account.getCollectedCost()) - account.getUsed()) + " USD";
	        y = getHeight() - (FONT_MONEY.getSize()/2 + 2);
	        g2.setFont(FONT_MONEY);
	        g2.drawString(money, x, y);
	        
	        
	        //rect money
	        x = 0;
	        y = 3;
	        w = getWidth()/3;
	        h = getHeight() - (y * 2) -1;
	        
	        if (hover)
	        	g2.setColor(BKG_2);
	        else
	        	g2.setColor(BKG_END);
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
	        if (hover)
	        	g2.setColor(BKG_2);
	        else 
	        	g2.setColor(BKG_END.darker());
	        
	        double bar = (wDispo/100.0) * w; 
	        g2.drawRoundRect(x, y, w, h, h, h);
	        g2.fillRoundRect(x+1, y+1, (int)bar, h-1, h, h);
	        //==
	        
	        if (hover)
	        	g2.setColor(BKG_2);
	        else 
	        	g2.setColor(BKG_END);
	        g2.drawRect(0, 0, getWidth()-2, getHeight()-2);
		}
	}

}
