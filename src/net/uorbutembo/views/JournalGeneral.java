/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualRecipe;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.beans.PaymentLocation;
import net.uorbutembo.dao.AnnualRecipeDao;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.DAOAdapter;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.Axis;
import net.uorbutembo.swing.charts.DefaultAxis;
import net.uorbutembo.swing.charts.DefaultLineChartModel;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.DefaultPointCloud;
import net.uorbutembo.swing.charts.DefaultTimeAxis;
import net.uorbutembo.swing.charts.LineChartRender;
import net.uorbutembo.swing.charts.LineChartRender.Interval;
import net.uorbutembo.swing.charts.LineChartRenderListener;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.Point2d;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalGeneral extends Panel implements YearChooserListener{
	private static final long serialVersionUID = 5001737357466282501L;
	
	private final CenterContainer container;	
	private final CardsPanel cards;
	private final JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);

	private final AnnualRecipeDao annualRecipeDao;
	private final AnnualSpendDao annualSpendDao;
	private final OutlayDao outlayDao;
	private final PaymentFeeDao paymentFeeDao;
	private final OtherRecipeDao otherRecipeDao;
	private final PaymentLocationDao paymentLocationDao;
	
	private AcademicYear year;//annee actuelement selectionner dans le combo du sidebar
	private final MainWindow mainWindow;
	
	public JournalGeneral(MainWindow mainWindow) {
		super(new BorderLayout());
		
		this.mainWindow = mainWindow;
		
		annualRecipeDao = mainWindow.factory.findDao(AnnualRecipeDao.class);
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		outlayDao = mainWindow.factory.findDao(OutlayDao.class);
		otherRecipeDao = mainWindow.factory.findDao(OtherRecipeDao.class);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		paymentLocationDao = mainWindow.factory.findDao(PaymentLocationDao.class);
		
		mainWindow.getSidebar().addYearChooserListener(this);
		
		container = new CenterContainer();
		cards = new CardsPanel();
		
		final Panel content = new Panel(new BorderLayout()),
				bottom = new Panel(new BorderLayout(5, 0));
		
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);

		bottom.add(progressBar, BorderLayout.CENTER);
		bottom.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		bottom.setOpaque(true);
		bottom.setBackground(FormUtil.BKG_END);
		
		content.add(container, BorderLayout.CENTER);
//		content.add(bottom, BorderLayout.SOUTH);
		
		add(content, BorderLayout.CENTER);
		add(cards, BorderLayout.SOUTH);
	}
	
	private void wait (boolean status) {
		
		if (status) {
			setCursor(FormUtil.WAIT_CURSOR);
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		
		progressBar.setVisible(status);
		progressBar.setIndeterminate(status);
	}
	
	@Override
	public void onChange(AcademicYear year) {	
		this.year = year;
		wait(true);
		cards.updateCards();
		container.reload();
		wait(false);
	}
	
	/**
	 * panel d'affichage des etats des operations de la caisse
	 * dans des cards
	 * @author Esaie MUHASA
	 *
	 */
	private class CardsPanel extends Panel {
		private static final long serialVersionUID = 3083304692320583350L;
		
		private final Panel container = new Panel(new GridLayout(1, 3, 10, 10));
		
		private final DefaultCardModel<Double> cardOutModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		private final DefaultCardModel<Double> cardInModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		{
			cardOutModel.setTitle("Dépenses");
			cardOutModel.setInfo("Montant déjà utiliser");
			cardOutModel.setSuffix("$");
			cardOutModel.setIcon(R.getIcon("btn-cancel"));
			cardOutModel.setValue(0d);
			
			cardInModel.setTitle("Recettes");
			cardInModel.setInfo("Montant déjà collecter");
			cardInModel.setSuffix("$");
			cardInModel.setIcon(R.getIcon("btn-add"));
			cardInModel.setValue(0d);
		}
		private final Card cardSold = new Card(mainWindow.getWorkspace().getDashboard().getGlobalModel().getCardModelCaisse());
		private final Card cardOut = new Card(cardOutModel);
		private final Card cardIn = new Card(cardInModel);
		
		private final DAOAdapter<PaymentFee> feeAdapter = new DAOAdapter<PaymentFee>() {

			@Override
			public synchronized void onCreate(PaymentFee e, int requestId) {
				if(e.getInscription().getPromotion().getAcademicYear().getId() != year.getId())
					return;
				
				cardInModel.setValue(cardInModel.getValue() + e.getAmount());
			}

			@Override
			public synchronized void onUpdate(PaymentFee e, int requestId) {
				if(e.getInscription().getPromotion().getAcademicYear().getId() != year.getId())
					return;
				updateCards();
			}

			@Override
			public synchronized void onDelete(PaymentFee e, int requestId) {
				if(e.getInscription().getPromotion().getAcademicYear().getId() != year.getId())
					return;
				
				cardInModel.setValue(cardInModel.getValue() - e.getAmount());
			}
			
		};
		
		private final DAOAdapter<OtherRecipe> otherAdapter = new DAOAdapter<OtherRecipe>() {

			@Override
			public synchronized void onCreate(OtherRecipe e, int requestId) {
				if(e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cardInModel.setValue(cardInModel.getValue() + e.getAmount());
			}

			@Override
			public synchronized void onUpdate(OtherRecipe e, int requestId) {
				if(e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				updateCards();
			}

			@Override
			public synchronized void onDelete(OtherRecipe e, int requestId) {
				if(e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cardInModel.setValue(cardInModel.getValue() - e.getAmount());
			}
			
		};
		
		private final DAOAdapter<Outlay> outlayAdaapter = new  DAOAdapter<Outlay>() {

			@Override
			public synchronized void onCreate(Outlay e, int requestId) {
				if (e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cardOutModel.setValue(cardOutModel.getValue() + e.getAmount());
			}

			@Override
			public synchronized void onUpdate(Outlay e, int requestId) {
				if (e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				updateCards();
			}

			@Override
			public synchronized void onDelete(Outlay e, int requestId) {
				if (e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cardOutModel.setValue(cardOutModel.getValue() - e.getAmount());
			}
			
		};

		public CardsPanel() {
			super(new BorderLayout());
			
			container.add(cardSold);
			container.add(cardIn);
			container.add(cardOut);
			
			add(container, BorderLayout.CENTER);
			setBorder(new EmptyBorder(10, 10, 10, 10));
			
			otherRecipeDao.addListener(otherAdapter);
			paymentFeeDao.addListener(feeAdapter);
			outlayDao.addListener(outlayAdaapter);
		}
		
		/**
		 * mise en jours des valeurs des cards, conformement a l'annee academique en parametre
		 */
		public void updateCards () {
			
			double recipe = 0, used = 0;
			progressBar.setValue(0);
			
			if(annualSpendDao.checkByAcademicYear(year.getId())) {
				List<AnnualSpend> spends = annualSpendDao.findByAcademicYear(year);
				
				progressBar.setIndeterminate(false);
				progressBar.setMaximum(spends.size());
				
				for (AnnualSpend sp : spends) {
					recipe += sp.getCollectedCost() + sp.getCollectedRecipe();
					used += sp.getUsed();
					progressBar.setValue(progressBar.getValue() + 1);
				}
				
				BigDecimal bigRecipe = new BigDecimal(recipe).setScale(2, RoundingMode.HALF_UP);
				BigDecimal bigUsed = new BigDecimal(used).setScale(2, RoundingMode.HALF_UP);
				
				recipe = bigRecipe.doubleValue();
				used = bigUsed.doubleValue();
			}

			cardInModel.setValue(recipe);
			cardOutModel.setValue(used);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			
			g2.setColor(FormUtil.BORDER_COLOR);
			g2.drawRoundRect(5, 5, getWidth()-10, getHeight()- 10, 10, 10);
			
			super.paintComponent(g);
		}
	}
	
	/**
	 * Conteneur principale de journal.
	 * contien des onclets pour faciltier la manipulation des donnees.
	 * 2 oclets graphiques et 2 oclets contenant des jtable
	 * @author Esaie MUHASA
	 */
	private class CenterContainer extends Panel {
		private static final long serialVersionUID = -2449570582834436476L;
		
		private final LineChartPanel linePanel = new LineChartPanel ();
		private final PieChartPanel piePanel = new PieChartPanel();
		private final RecipePanel recipePanel = new RecipePanel();
		private final OutlayPanel outlayPanel = new OutlayPanel();
		
		public CenterContainer() {
			super (new BorderLayout());
			final JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
			
			tabbed.addTab("", new ImageIcon(R.getIcon("pie")), piePanel, "Graphiques de repartition");
			tabbed.addTab("", new ImageIcon(R.getIcon("chart")), linePanel, "Evolution dans le temps");
			tabbed.addTab("", new ImageIcon(R.getIcon("plus")), recipePanel, "Recettes");
			tabbed.addTab("", new ImageIcon(R.getIcon("moin")), outlayPanel, "Dépenses");
			
			add(tabbed, BorderLayout.CENTER);
			setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}
		
		/**
		 * echargement des donnees des modeles des graphiques
		 */
		public void reload () {
			linePanel.reloadChart();
			piePanel.reloadChart();
		}
		
	}
	
	/**
	 * panel de visualisation des operations d'entree sotie en caissse
	 * @author Esaie MUHASA
	 */
	private class LineChartPanel extends Panel implements LineChartRenderListener{
		private static final long serialVersionUID = 2734887735495665253L;
		
		private Panel panelChart = new Panel(new BorderLayout());//panel contenant la visualisation graphique
		
		private final DefaultAxis yAxis = new DefaultAxis(24 * 60 * 60 * 1000, "Monain", "$");
		private final DefaultTimeAxis xAxis = new DefaultTimeAxis(0, "Temps", "t");
		
		private final DefaultLineChartModel chartModel = new DefaultLineChartModel(xAxis, yAxis);
		private final LineChartRender chartRender = new LineChartRender(chartModel);
		private final DefaultPointCloud cloudRecipes = new DefaultPointCloud(FormUtil.COLORS_ALPHA[0], FormUtil.COLORS[0], FormUtil.COLORS[0]);
		private final DefaultPointCloud cloudOutlays = new DefaultPointCloud(FormUtil.COLORS_ALPHA[1], FormUtil.COLORS[1], FormUtil.COLORS[1]);
		private final DefaultPointCloud cloudSold = new DefaultPointCloud(FormUtil.COLORS_ALPHA[2], FormUtil.COLORS[2], FormUtil.COLORS[2]);
		
		private final JCheckBox checkBoxGridChart = new JCheckBox("Grille", true);
		
		private final JCheckBox checkBoxIn = new JCheckBox("Recettes", true);
		private final JCheckBox checkBoxOut = new JCheckBox("Dépenses", true);
		private final JCheckBox checkBoxSold = new JCheckBox("Solde", true);
		
		{
			chartModel.addChart(cloudRecipes);
			chartModel.addChart(cloudSold);
			chartModel.addChart(cloudOutlays);
			
			cloudRecipes.setFill(true);
			cloudSold.setFill(true);
			cloudOutlays.setFill(true);
			
			xAxis.setResponsive(false);
			xAxis.setInterval(-10, 0);
			xAxis.setStep(2);
			yAxis.setMeasureUnit(" $");
		}
		
		public LineChartPanel() {
			super(new BorderLayout());
			
			final Panel 
					panelTools = new  Panel(new BorderLayout()),
					panelCharts = new Panel();
			final Box boxOthers = Box.createHorizontalBox(); 
			
			chartRender.addRenderListener(this);
			chartRender.setDraggable(true);
			panelChart.add(chartRender, BorderLayout.CENTER);
			
			checkBoxGridChart.setForeground(FormUtil.BORDER_COLOR);
			checkBoxGridChart.addChangeListener(event -> {
				chartRender.setVisibleGrid(checkBoxGridChart.isSelected());
			});
			
			checkBoxIn.setForeground(FormUtil.COLORS[0]);
			checkBoxOut.setForeground(FormUtil.COLORS[1]);
			checkBoxSold.setForeground(FormUtil.COLORS[2]);
			panelCharts.add(checkBoxIn);
			panelCharts.add(checkBoxOut);
			panelCharts.add(checkBoxSold);
			checkBoxIn.addChangeListener(event -> {
				cloudRecipes.setVisible(checkBoxIn.isSelected());
			});
			checkBoxOut.addChangeListener(event -> {
				cloudOutlays.setVisible(checkBoxOut.isSelected());
			});
			checkBoxSold.addChangeListener(event -> {
				cloudSold.setVisible(checkBoxSold.isSelected());
			});

			boxOthers.add(checkBoxGridChart);
			
			panelTools.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			panelTools.add(panelCharts, BorderLayout.EAST);
			panelTools.add(boxOthers, BorderLayout.WEST);
			
			add(panelChart, BorderLayout.CENTER);
			add(panelTools, BorderLayout.NORTH);
		}
		
		private void wait (boolean status) {
			
			if (status) {
				setCursor(FormUtil.WAIT_CURSOR);
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
			
			checkBoxIn.setEnabled(!status);
			checkBoxOut.setEnabled(!status);
			checkBoxSold.setEnabled(!status);
			checkBoxGridChart.setEnabled(!status);
		}
		
		private synchronized void reloadChart() {
			wait(true);
			progressBar.setValue(0);
			progressBar.setIndeterminate(true);

			double first = chartRender.getLastxInterval() != null? chartRender.getLastxInterval().getMin() : -12;
			double last = chartRender.getLastxInterval() != null? chartRender.getLastxInterval().getMax() : 0;
			
			int min = (int) first;
			int max = (int) last;
			
			cloudRecipes.removePoints();
			cloudOutlays.removePoints();
			cloudSold.removePoints();
			
			
			long time = 0, now = System.currentTimeMillis();
			
			for(int day = min; day <= max; day++) {
				time = now + (day * 60 * 60 * 1000 * 24);
				Date date = new Date(time);
				
				double y = 0;
				//recettes
				if (otherRecipeDao.countByAcademicYear(year.getId(), date) != 0) {
					List<OtherRecipe> recipes = otherRecipeDao.findByAcademicYear(year.getId(), date);
					for (OtherRecipe r : recipes) {
						y += r.getAmount();
					}
				}
				
				if(paymentFeeDao.countByAcademicYear(year.getId(), date) != 0) {
					List<PaymentFee> fees = paymentFeeDao.findByAcademicYear(year.getId(), date);
					for (PaymentFee p : fees) {
						y += p.getAmount();
					}
				}
				Point2d pointIn = new Point2d(day, y);
				pointIn.setLabel(y+" USD");
				cloudRecipes.addPoint(pointIn);
				
				//depense
				y = 0;
				if(outlayDao.checkByAcademicYear(year.getId(), date)) {
					List<Outlay> outlays = outlayDao.findByAcademicYear(year.getId(), date);
					for (Outlay o : outlays) {
						y += o.getAmount();
					}
				}
				
				Point2d pointOut = new Point2d(day, -y);
				pointOut.setLabel(y+" USD");
				cloudOutlays.addPoint(pointOut);
				//==
				
				y = 0;
				if (otherRecipeDao.checkByAcademicYearBeforDate(year, date)) {
					List<OtherRecipe> recipes = otherRecipeDao.findByAcademicYearBeforDate(year, date);
					for (OtherRecipe r : recipes) {
						y += r.getAmount();
					}
				}
				
				if(paymentFeeDao.checkByAcademicYearBeforDate(year.getId(), date)) {
					List<PaymentFee> fees = paymentFeeDao.findByAcademicYearBeforDate(year.getId(), date);
					for (PaymentFee p : fees) {
						y += p.getAmount();
					}
				}
				Point2d pointSold = new Point2d(day, y - pointOut.getY());
				pointSold.setLabel(y+" USD");
				cloudSold.addPoint(pointSold);
				//==
			}
			
//			cloudRecipes.toSquareSignal();
//			cloudOutlays.toSquareSignal();
//			cloudSold.toSquareSignal();
			wait(false);
		}
		
		
		@Override
		public void onRequireTranslation(LineChartRender source, Axis axis, Interval interval) {
			if (axis != xAxis)
				return;
			
			reloadChart();
		}
		
		@Override
		public void onRequireTranslation(LineChartRender source, Interval xInterval, Interval yInterval) {
			onRequireTranslation(source, source.getModel().getXAxis(), xInterval);
		}
		
		
		
	}
	
	/**
	 * Panel de presentation du graphique pie de la caisse
	 * @author Esaie MUHASA
	 */
	private class PieChartPanel extends Panel {
		private static final long serialVersionUID = -8328178244978488613L;
		
		private final JRadioButton [] types = {
				new JRadioButton("Soldes", true),
				new JRadioButton("Recettes"),
				new JRadioButton("Dépenses")
		};
		
		private final JRadioButton [] locations = {
				new JRadioButton("Comptes", true),
				new JRadioButton("Localisations")
		};
		
		private final Box box = Box.createHorizontalBox();
		
		//index
		private int indexType = 0;
		private int indexLocation = 0;
		//==
		
		private final ChangeListener typeListener = event -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if (index == indexType)
				return;
			
			if (index != 1) 
				locations[0].setText("Comptes");
			else
				locations[0].setText("Sources");
			
			indexType = index;
			EventQueue.invokeLater(() -> {reloadChart();}); 
		};
		
		private final ChangeListener locationListener = event -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if (index == indexLocation)
				return;
			
			indexLocation = index;
			EventQueue.invokeLater(() -> {reloadChart();}); 
		};
		
		{
			ButtonGroup group = new ButtonGroup();
			for (int i = 0; i < locations.length; i++) {
				JRadioButton radio = locations[i];
				radio.setForeground(Color.WHITE);
				radio.setName(i+"");
				radio.addChangeListener(locationListener);
				group.add(radio);
				box.add(radio);
			}
			
			box.add(Box.createHorizontalGlue());
			
			group = new ButtonGroup();
			for (int i = 0; i < types.length; i++) {
				JRadioButton radio = types[i];
				radio.setForeground(Color.WHITE);
				radio.setName(i+"");
				radio.addChangeListener(typeListener);
				group.add(radio);
				box.add(radio);
			}
		}
		
		private final DefaultPieModel pieModel = new DefaultPieModel();
		private final PiePanel piePanel = new PiePanel(pieModel, FormUtil.BORDER_COLOR);
		{
			pieModel.setRealMaxPriority(true);
			pieModel.setSuffix(FormUtil.UNIT_MONEY_SYMBOL);
		}
		
		public PieChartPanel() {
			super(new BorderLayout());
			
			final Panel top = new Panel(new BorderLayout());
			top.setBorder(new EmptyBorder(5, 0, 5, 0));
			top.add(box, BorderLayout.CENTER);
			
			add(top, BorderLayout.NORTH);
			add(piePanel, BorderLayout.CENTER);
		}
		
		/**
		 * Changement d'etat des boutons radio
		 * @param enable
		 */
		private void setEnableButtons (boolean enable) {
			for (int i = 0; i < types.length; i++)
				types[i].setEnabled(enable);
			
			for (int i = 0; i < locations.length; i++)
				locations[i].setEnabled(enable);
			
			setCursor(enable? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		
		/**
		 * rechargement du graphique
		 */
		private synchronized void reloadChart () {
			setEnableButtons(false);
			
			pieModel.removeAll();
			int colorIndex = 0;
			
			switch (indexType) {
				case 0 : {//pour le solde
					if (indexLocation == 0) {//comptes
						//on recupere directement le model tout cuit du dash board
						piePanel.setModel(mainWindow.getWorkspace().getDashboard().getGlobalModel().getPieModelCaisse());
						setEnableButtons(true);
						return;
					} else {//lieux de payment
						final List<PaymentLocation> all = paymentLocationDao.countAll() != 0? paymentLocationDao.findAll() : new ArrayList<>();
						final List<AnnualRecipe> rps = annualRecipeDao.checkByAcademicYear(year)? annualRecipeDao.findByAcademicYear(year) : new ArrayList<>();
						final List<AnnualSpend> sps = annualSpendDao.checkByAcademicYear(year.getId())? annualSpendDao.findByAcademicYear(year) : new ArrayList<>();
						
						final long [] sAccounts = new long[sps.size()];
						final long [] aAccounts = new long[rps.size()];
						
						for (int i = 0; i < sAccounts.length; i++) //pour les depense annuel
							sAccounts[i] = sps.get(i).getId();
						
						for (int i = 0; i < aAccounts.length; i++) //pour les autres recettes annuel
							aAccounts[i] = rps.get(i).getId();
						
						for (int i = 0, count = all.size(); i < count; i++) {
							
							double amount = paymentFeeDao.getSoldByLocation(all.get(i), year);
							amount += otherRecipeDao.getSoldByAccounts(aAccounts, all.get(i).getId());
							amount -= outlayDao.getSoldByAccounts (sAccounts, all.get(i).getId());
							
							pieModel.addPart(
									new DefaultPiePart (
											FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ],
											amount,
											all.get(i).getName()
										)
									);
						}
						pieModel.setTitle("Solde disponible pour chaque lieux de perception");
					}
				}break;
				case 1 : {//les recettes
					if (indexLocation == 0) {//sources de la recette
						
						double fees = paymentFeeDao.getSoldByAcademicYear(year);//montant deja payer par tout les etudant
						pieModel.addPart(new DefaultPiePart(FormUtil.COLORS[colorIndex++], fees, "Frais académiques"));
						
						if (annualRecipeDao.checkByAcademicYear(year)) {
							List<AnnualRecipe> recipes = annualRecipeDao.findByAcademicYear(year);
							for (int i = 0, count = recipes.size(); i < count; i++) {					
								pieModel.addPart(
										new DefaultPiePart(
											FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ],
											recipes.get(i).getCollected(),
											recipes.get(i).getUniversityRecipe().getTitle()
										)
									); 
							}
						} else {
							pieModel.addPart(new DefaultPiePart(FormUtil.COLORS[colorIndex++], 0d, "Autres recettes"));				
						}
						pieModel.setTitle("Repartition des recettes selons les sources de financement");
					} else {//lieux de payment
						
						final List<PaymentLocation> all = paymentLocationDao.countAll() != 0? paymentLocationDao.findAll() : new ArrayList<>();
						final List<AnnualRecipe> rps = annualRecipeDao.checkByAcademicYear(year)? annualRecipeDao.findByAcademicYear(year) : new ArrayList<>();
						
						final long [] accounts = new long[rps.size()];
						for (int i = 0; i < accounts.length; i++) 
							accounts[i] = rps.get(i).getId();
						
						for (int i = 0, count = all.size(); i < count; i++) {
							
							double amount = paymentFeeDao.getSoldByLocation(all.get(i), year);
							amount += otherRecipeDao.getSoldByAccounts(accounts, all.get(i).getId());
							
							pieModel.addPart(
									new DefaultPiePart (
											FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ],
											amount,
											all.get(i).getName()
										)
									); 
						}
						
						pieModel.setTitle("Repartition des recettes selon les lieux de perception");
					}
				}break;
				case 2 : {//les depenses
					if (indexLocation == 0) {//comptes
						final List<AnnualSpend> all = annualSpendDao.checkByAcademicYear(year.getId())? annualSpendDao.findByAcademicYear(year) : new ArrayList<>();
						
						for (int i = 0, count = all.size(); i < count; i++) {
							double amount = outlayDao.getSoldByAccount(all.get(i).getId());
							pieModel.addPart(
									new DefaultPiePart (
											FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ],
											amount,
											all.get(i).getUniversitySpend().getTitle()
										)
									); 
						}
						pieModel.setTitle("Repartition des dépenses pour chaque compte");
					} else {//lieux de retrait
						final List<PaymentLocation> all = paymentLocationDao.countAll() != 0? paymentLocationDao.findAll() : new ArrayList<>();
						final List<AnnualSpend> rps = annualSpendDao.checkByAcademicYear(year.getId())? annualSpendDao.findByAcademicYear(year) : new ArrayList<>();
						
						final long [] accounts = new long[rps.size()];
						for (int i = 0; i < accounts.length; i++) 
							accounts[i] = rps.get(i).getId();
						
						for (int i = 0, count = all.size(); i < count; i++) {
							
							double amount = outlayDao.getSoldByAccounts(accounts, all.get(i).getId());
							
							pieModel.addPart(
									new DefaultPiePart (
											FormUtil.COLORS[ (colorIndex++) % (FormUtil.COLORS.length-1) ],
											amount,
											all.get(i).getName()
										)
									); 
						}
						pieModel.setTitle("Repartition des dépenses, selon le lieux de payments");
					}
					
				}break;
			}
			
			piePanel.setModel(pieModel);
			setEnableButtons(true);
		}
		
	}
	
	private class RecipePanel extends Panel {
		private static final long serialVersionUID = 1744540939252963612L;
		
	}
	
	private class OutlayPanel extends Panel {
		private static final long serialVersionUID = -2786706226334778839L;
		
	}
	
}
