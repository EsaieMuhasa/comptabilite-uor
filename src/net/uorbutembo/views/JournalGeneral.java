/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
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
import net.uorbutembo.dao.DAOException;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.dao.PaymentLocationDao;
import net.uorbutembo.swing.Button;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.Table;
import net.uorbutembo.swing.TableModel;
import net.uorbutembo.swing.TableModel.ExportationProgressListener;
import net.uorbutembo.swing.TablePanel;
import net.uorbutembo.swing.charts.Axis;
import net.uorbutembo.swing.charts.ChartPanel;
import net.uorbutembo.swing.charts.CloudChartRender;
import net.uorbutembo.swing.charts.CloudChartRender.ChartRenderTranslationListener;
import net.uorbutembo.swing.charts.CloudChartRender.Interval;
import net.uorbutembo.swing.charts.DateAxis;
import net.uorbutembo.swing.charts.DefaultAxis;
import net.uorbutembo.swing.charts.DefaultCloudChartModel;
import net.uorbutembo.swing.charts.DefaultMaterialPoint;
import net.uorbutembo.swing.charts.DefaultPieModel;
import net.uorbutembo.swing.charts.DefaultPiePart;
import net.uorbutembo.swing.charts.DefaultPointCloud;
import net.uorbutembo.swing.charts.PiePanel;
import net.uorbutembo.swing.charts.PieRender;
import net.uorbutembo.swing.charts.PointCloud.CloudType;
import net.uorbutembo.tools.Config;
import net.uorbutembo.tools.FormUtil;
import net.uorbutembo.tools.R;
import net.uorbutembo.views.components.Sidebar.YearChooserListener;
import net.uorbutembo.views.forms.FormOtherRecipe;
import net.uorbutembo.views.forms.FormOutlay;
import net.uorbutembo.views.models.OtherRecipeTableModel;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalGeneral extends Panel implements YearChooserListener{
	private static final long serialVersionUID = 5001737357466282501L;
	
	private final CenterContainer container;	
	private final CardsPanel cards;
	
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

		add(container, BorderLayout.CENTER);
		add(cards, BorderLayout.SOUTH);
	}
	
	private void wait (boolean status) {
		if (status) {
			setCursor(FormUtil.WAIT_CURSOR);
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
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
	 * Injection du listener d'exportation des donnees au format XLSX
	 * @param exportationListener
	 */
	public void setExportationListener(ExportationProgressListener exportationListener) {
		container.getRecipePanel().getTableModelFees().addExportationProgressListener(exportationListener);
		container.getRecipePanel().getTableModelOther().addExportationProgressListener(exportationListener);
		container.getOutlayPanel().getTableModel().addExportationProgressListener(exportationListener);
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

		public CardsPanel() {
			super(new BorderLayout());
			
			container.add(cardSold);
			container.add(cardIn);
			container.add(cardOut);
			
			add(container, BorderLayout.CENTER);
			setBorder(new EmptyBorder(10, 10, 10, 10));
			
		}
		
		/**
		 * mise en jours des valeurs des cards, conformement a l'annee academique en parametre
		 */
		public void updateCards () {
			
			double recipe = 0, used = 0;
			
			if(annualSpendDao.checkByAcademicYear(year.getId())) {
				List<AnnualSpend> spends = annualSpendDao.findByAcademicYear(year);
				
				for (AnnualSpend sp : spends) {
					recipe += sp.getCollectedCost() + sp.getCollectedRecipe();
					used += sp.getUsed();
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
		
		private final DAOAdapter<PaymentFee> feeAdapter = new DAOAdapter<PaymentFee>() {

			@Override
			public synchronized void onCreate(PaymentFee e, int requestId) {
				if(e.getInscription().getPromotion().getAcademicYear().getId() != year.getId())
					return;
				
				cards.cardInModel.setValue(cards.cardInModel.getValue() + e.getAmount());
				linePanel.reloadChart();
				piePanel.reloadChart();
			}

			@Override
			public synchronized void onUpdate(PaymentFee e, int requestId) {
				if(e.getInscription().getPromotion().getAcademicYear().getId() != year.getId())
					return;
				cards.updateCards();
				linePanel.reloadChart();
				piePanel.reloadChart();
			}

			@Override
			public synchronized void onDelete(PaymentFee e, int requestId) {
				if(e.getInscription().getPromotion().getAcademicYear().getId() != year.getId())
					return;
				
				cards.cardInModel.setValue(cards.cardInModel.getValue() - e.getAmount());
				linePanel.reloadChart();
				piePanel.reloadChart();
			}
			
		};
		
		private final DAOAdapter<OtherRecipe> otherAdapter = new DAOAdapter<OtherRecipe>() {

			@Override
			public synchronized void onCreate(OtherRecipe e, int requestId) {
				if(e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cards.cardInModel.setValue(cards.cardInModel.getValue() + e.getAmount());
				linePanel.reloadChart();
				piePanel.reloadChart();
			}

			@Override
			public synchronized void onUpdate(OtherRecipe e, int requestId) {
				if(e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cards.updateCards();
				linePanel.reloadChart();
				piePanel.reloadChart();
			}

			@Override
			public synchronized void onDelete(OtherRecipe e, int requestId) {
				if(e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cards.cardInModel.setValue(cards.cardInModel.getValue() - e.getAmount());
				linePanel.reloadChart();
				piePanel.reloadChart();
			}
			
		};
		
		private final DAOAdapter<Outlay> outlayAdaapter = new  DAOAdapter<Outlay>() {

			@Override
			public synchronized void onCreate(Outlay e, int requestId) {
				if (e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cards.cardOutModel.setValue(cards.cardOutModel.getValue() + e.getAmount());
				linePanel.reloadChart();
				piePanel.reloadChart();
			}

			@Override
			public synchronized void onUpdate(Outlay e, int requestId) {
				if (e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cards.updateCards();
				linePanel.reloadChart();
				piePanel.reloadChart();
			}

			@Override
			public synchronized void onDelete(Outlay e, int requestId) {
				if (e.getAccount().getAcademicYear().getId() != year.getId())
					return;
				
				cards.cardOutModel.setValue(cards.cardOutModel.getValue() - e.getAmount());
				linePanel.reloadChart();
				piePanel.reloadChart();
			}
			
		};
		
		public CenterContainer() {
			super (new BorderLayout());
			final JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
			
			tabbed.addTab("", new ImageIcon(R.getIcon("pie")), piePanel, "Graphiques de repartition");
			tabbed.addTab("", new ImageIcon(R.getIcon("chart")), linePanel, "Evolution dans le temps");
			tabbed.addTab("", new ImageIcon(R.getIcon("plus")), recipePanel, "Recettes");
			tabbed.addTab("", new ImageIcon(R.getIcon("moin")), outlayPanel, "Dépenses");
			
			add(tabbed, BorderLayout.CENTER);
			setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			
			otherRecipeDao.addListener(otherAdapter);
			paymentFeeDao.addListener(feeAdapter);
			outlayDao.addListener(outlayAdaapter);
		}
		
		/**
		 * echargement des donnees des modeles
		 * Cette methode doit etre executer unquemet lors du changement de l'annee academique
		 */
		public void reload () {
			linePanel.reloadChart();
			piePanel.reloadChart();
			recipePanel.reload(year);
			outlayPanel.reload(year);
		}

		/**
		 * @return the recipePanel
		 */
		public RecipePanel getRecipePanel() {
			return recipePanel;
		}

		/**
		 * @return the outlayPanel
		 */
		public OutlayPanel getOutlayPanel() {
			return outlayPanel;
		}
		
		
	}
	
	/**
	 * panel de visualisation des operations d'entree sotie en caissse
	 * @author Esaie MUHASA
	 */
	private class LineChartPanel extends Panel implements ChartRenderTranslationListener{
		private static final long serialVersionUID = 2734887735495665253L;
		

		private final DefaultAxis yAxis = new DefaultAxis("Montant", "", "$");
		private final DateAxis xAxis = new DateAxis("Temps", "t", "");
		private final Interval interval = new Interval(Integer.parseInt(Config.find("defaultDateMin")), 0);
		
		private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel(xAxis, yAxis);
		private final DefaultPointCloud cloudRecipes = new DefaultPointCloud("Recette", FormUtil.COLORS_ALPHA[0], FormUtil.COLORS[0], FormUtil.COLORS[0]);
		private final DefaultPointCloud cloudOutlays = new DefaultPointCloud("Dépense", FormUtil.COLORS_ALPHA[1], FormUtil.COLORS[1], FormUtil.COLORS[1]);
		private final DefaultPointCloud cloudSold = new DefaultPointCloud("Solde", FormUtil.COLORS_ALPHA[2], FormUtil.COLORS[2], FormUtil.COLORS[2]);
		private final ChartPanel chartPanel = new ChartPanel(chartModel);
		
		{
			chartModel.addChart(cloudRecipes);
			chartModel.addChart(cloudSold);
			chartModel.addChart(cloudOutlays);
			
			cloudSold.setFill(true);
		}
		
		public LineChartPanel() {
			super(new BorderLayout());			
			add(chartPanel, BorderLayout.CENTER);
			chartPanel.getChartRender().setVerticalTranslate(false);
			chartPanel.getChartRender().addTranslationListener(this);
			chartPanel.setOwner(mainWindow);
		}
		
		private void wait (boolean status) {
			
			if (status) {
				setCursor(FormUtil.WAIT_CURSOR);
			} else {
				setCursor(Cursor.getDefaultCursor());
			}
		}
		
		@Override
		public void onRequireTranslation(CloudChartRender source, Axis axis, Interval interval) {
			double min = interval.getMin(), max = interval.getMax();
			
			if(max > 0) {
				max = 0;
				min = Integer.parseInt(Config.find("defaultDateMin"));
			}
			
			if(this.interval.getMin() == min && this.interval.getMax() == max)
				return;
			
			this.interval.setInterval(min, max);
			reloadChart();
		}

		@Override
		public void onRequireTranslation(CloudChartRender source, Interval xInterval, Interval yInterval) {
			onRequireTranslation(source, xAxis, xInterval);
		}

		private synchronized void reloadChart() {
			wait(true);
			chartPanel.getChartRender().setVisible(false);
			
			cloudRecipes.removePoints();
			cloudOutlays.removePoints();
			cloudSold.removePoints();
			
			long min = (long)interval.getMin();
			long max = (long)interval.getMax();
			
			long time = 0, now = System.currentTimeMillis();
			
			for(long day = min; day <= max; day += 1l) {
				time = now + (day * 60l * 60l * 1000l * 24l);
				Date date = new Date(time);
				
				double y = 0;
				//recettes
				y = otherRecipeDao.getSoldByAcademicYear(year, date);
				y += paymentFeeDao.getSoldByAcademicYear(year, date);

				DefaultMaterialPoint pointIn = new DefaultMaterialPoint(day, y);
				pointIn.setLabelX(DateAxis.DEFAULT_DATE_FORMAT.format(date));
				pointIn.setLabelY(y+" "+FormUtil.UNIT_MONEY);
				cloudRecipes.addPoint(pointIn);
				
				//depense
				y = outlayDao.getSoldByAcademicYear(year, date);
				
				DefaultMaterialPoint pointOut = new DefaultMaterialPoint(day, -y);
				pointOut.setLabelX(DateAxis.DEFAULT_DATE_FORMAT.format(date));
				pointOut.setLabelY(y+" "+FormUtil.UNIT_MONEY);
				cloudOutlays.addPoint(pointOut);
				//==
				
				//sold
				y = otherRecipeDao.getSoldByAcademicYearBeforDate(year, date);
				y += paymentFeeDao.getSoldByAcademicYearBeforDate(year, date);
				y -= outlayDao.getSoldByAcademicYearBeforDate(year, date);

				DefaultMaterialPoint pointSold = new DefaultMaterialPoint(day, y, 10f);
				pointSold.setLabelX(DateAxis.DEFAULT_DATE_FORMAT.format(date));
				pointSold.setLabelY(y+" "+FormUtil.UNIT_MONEY);
				pointSold.setBorderColor(cloudSold.getBorderColor().darker());
				cloudSold.addPoint(pointSold);
				//==
			}
			
			cloudRecipes.setType(CloudType.STICK_CHART);
			cloudOutlays.setType(CloudType.STICK_CHART);
			chartPanel.getChartRender().setVisible(true);
			
			wait(false);
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
			piePanel.setOwner(mainWindow);
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
			
			piePanel.setRenderVisible(false);
			
			pieModel.removeAll();
			int colorIndex = 0;
			
			switch (indexType) {
				case 0 : {//pour le solde
					if (indexLocation == 0) {//comptes
						//on recupere directement le model tout cuit du dash board
						piePanel.setModel(mainWindow.getWorkspace().getDashboard().getGlobalModel().getPieModelCaisse());
						setEnableButtons(true);
						piePanel.setRenderVisible(true);
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
						pieModel.setTitle("Solde disponible pour chaque lieux de perception de l'argent");
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
						pieModel.setTitle("Répartition des recettes selons les sources de financement");
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
						
						pieModel.setTitle("Répartition des recettes selon les lieux de perception");
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
						pieModel.setTitle("Répartition des dépenses pour chaque compte (rubrique du budget)");
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
						pieModel.setTitle("Répartition des dépenses, selon le lieux de payments");
					}
					
				}break;
			}
			
			piePanel.setModel(pieModel);
			piePanel.setRenderVisible(true);
			setEnableButtons(true);
		}
	}
	
	private final class RecipePanel extends Panel {
		private static final long serialVersionUID = 1744540939252963612L;
		
		private JDialog dialogRecipe;
		private FormOtherRecipe formRecipe;
		private DAOAdapter<OtherRecipe> recipeAdapter;
		private final JournalPaymentFeeTableModel tableModelFees = new JournalPaymentFeeTableModel();
		private final OtherRecipeTableModel tableModelOther = new OtherRecipeTableModel(otherRecipeDao);
		private final Table table = new Table(tableModelFees);
		private final TablePanel tablePanel = new TablePanel(table, "Payements des frais académiques");
		
		private final MouseListener  tableMouseListener = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger() && table.getSelectedRow() != -1) {
					initPopup();
					popupRecipe.show(table, e.getX(), e.getY());
				}
			}
		};
		
		private final JLabel labelCount = FormUtil.createSubTitle("");
		private final JButton btnToExcel = new Button(new ImageIcon(R.getIcon("export")), "Excel");
		private final Button btnNext = new Button(new ImageIcon(R.getIcon("next")));
		private final Button btnPrev = new Button(new ImageIcon(R.getIcon("prev")));
		private final Button btnRecipe = new Button(new ImageIcon(R.getIcon("new")), "Nouvelle recette");
		private final Box boxRadios = Box.createHorizontalBox();
		private final JRadioButton [] radioModels = {
				new JRadioButton("Frais academique", true),
				new JRadioButton("Autres recettes")
		}; 
		
		private final TableModel<?> [] models = { tableModelFees, tableModelOther};
		private final ChangeListener radionListener = event -> {
			JRadioButton radio = (JRadioButton) event.getSource();
			if(!radio.isSelected())
				return;
			
			int index = Integer.parseInt(radio.getName());
			if (index == 0) {
				table.removeMouseListener(tableMouseListener);
				tablePanel.setTitle("Payements des frais academiques");
			} else {
				table.addMouseListener(tableMouseListener);
				tablePanel.setTitle("Autres recettes");
			}
			models[index].setOffset(0);
			table.setModel(models[index]);
			btnNext.setEnabled(models[index].hasNext());
			btnPrev.setEnabled(models[index].hasPrevious());
			labelCount.setText(models[index].getCount()+" opérations");
		};
		
		private final ActionListener navigationListener = event -> {
			JButton btn = (JButton) event.getSource();
			boolean next = btn == btnNext;
			TableModel<?> model = (TableModel<?>) table.getModel();
			if (next && model.hasNext()) 
				model.next();
			else if(!next && model.hasPrevious()) 
				model.previous();
			
			btnPrev.setEnabled(model.hasPrevious());
			btnNext.setEnabled(model.hasNext());
		};
		
		private final ActionListener btnExportListener = event -> {
			TableModel<?> model = (TableModel<?>) table.getModel();
			boolean rps = Table.XLSX_FILE_CHOOSER.showSaveDialog(mainWindow);
			if(rps) {
				Thread t = new Thread(() -> {
					model.exportToExcel(Table.XLSX_FILE_CHOOSER.getSelectedFile());
				});
				t.start();
			}
		};
		
		{
			final ButtonGroup group = new ButtonGroup();
			btnRecipe.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			boxRadios.add(labelCount);
			boxRadios.add(Box.createHorizontalGlue());
			for (int i = 0; i < radioModels.length; i++) {
				boxRadios.add(radioModels[i]);
				radioModels[i].setName(i+"");
				radioModels[i].addChangeListener(radionListener);
				radioModels[i].setForeground(Color.WHITE);
				group.add(radioModels[i]);
			}
			boxRadios.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			tablePanel.getScroll().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			btnNext.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnPrev.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnToExcel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnPrev.setEnabled(false);
			btnNext.addActionListener(navigationListener);
			btnPrev.addActionListener(navigationListener);
			btnToExcel.addActionListener(btnExportListener);
		}
		
		private final Box tools = Box.createHorizontalBox();
		private JPopupMenu popupRecipe;
		private JMenuItem itemDeleteRecipe;
		private JMenuItem itemUpdateRecipe;
		
		public RecipePanel() {
			super(new BorderLayout());
			
			table.setShowVerticalLines(true);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			tools.add(btnPrev);
			tools.add(Box.createHorizontalStrut(5));
			tools.add(btnNext);
			
			tools.add(Box.createHorizontalGlue());
			tools.add(btnToExcel);
			tools.add(Box.createHorizontalStrut(5));
			tools.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			tools.setBackground(FormUtil.BKG_END);
			tools.setOpaque(true);
			tools.add(btnRecipe);
			
			add(tablePanel, BorderLayout.CENTER);
			add(tools, BorderLayout.SOUTH);
			add(boxRadios, BorderLayout.NORTH);
			btnRecipe.addActionListener(event -> {
				createRecipe();
			});
			
		}
		
		/**
		 * @return the tableModelFees
		 */
		public JournalPaymentFeeTableModel getTableModelFees() {
			return tableModelFees;
		}


		/**
		 * @return the tableModelOther
		 */
		public OtherRecipeTableModel getTableModelOther() {
			return tableModelOther;
		}


		/**
		 * rechargement des donnees pour l'annees enn parametre
		 * @param currentYear
		 */
		public void reload (AcademicYear currentYear) {
			tableModelOther.setCurrentYear(currentYear);
			tableModelFees.setCurrentYear(currentYear);
			
			TableModel<?> model = (TableModel<?>) table.getModel();
			labelCount.setText(model.getCount()+" opérations");
			btnToExcel.setEnabled(model.getRowCount() != 0);
		}
		
		/**
		 * creation du popup menu permetant de modifier/supprimer recette (autres, que les frais academique)
		 */
		private void initPopup() {
			if (popupRecipe != null)
				return;
			
			popupRecipe = new JPopupMenu();
			
			itemDeleteRecipe = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
			itemUpdateRecipe = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
			popupRecipe.add(itemDeleteRecipe);
			popupRecipe.add(itemUpdateRecipe);
			
			itemDeleteRecipe.addActionListener(event -> {
				OtherRecipe recipe = tableModelOther.getRow(table.getSelectedRow());
				int status = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer \nla dite recette??", "Suppression", JOptionPane.OK_CANCEL_OPTION);
				if(status == JOptionPane.OK_OPTION) {
					try {					
						otherRecipeDao.delete(recipe.getId());
					} catch (DAOException e) {
						JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			itemUpdateRecipe.addActionListener(event -> {
				OtherRecipe recipe = tableModelOther.getRow(table.getSelectedRow());
				updateRecipe(recipe);
			});
		}
		
		/**
		 * Ajout d'une nouvelle recette (autre que les frais acadmique)
		 */
		public void createRecipe () {		
			createRecipeDialog();
			
			dialogRecipe.setLocationRelativeTo(mainWindow);
			dialogRecipe.setVisible(true);
		}
		
		/**
		 * mis en jours d'une recette
		 * @param recipe
		 */
		public void updateRecipe (OtherRecipe recipe) {		
			createRecipeDialog();
			
			formRecipe.setOtherRecipe(recipe);
			dialogRecipe.setLocationRelativeTo(mainWindow);
			dialogRecipe.setVisible(true);
		}
		
		/**
		 * creation du boite de dialogue d'ajout d'une recete
		 */
		private void createRecipeDialog () {
			if(dialogRecipe != null)
				return;
			
			formRecipe = new FormOtherRecipe(mainWindow);
			dialogRecipe = new JDialog(mainWindow, "Entrée en caisse", true);
			dialogRecipe.setIconImage(mainWindow.getIconImage());
			
			dialogRecipe.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogRecipe.getContentPane().add(formRecipe, BorderLayout.CENTER);
			dialogRecipe.getContentPane().setBackground(FormUtil.BKG_DARK);
			dialogRecipe.pack();
			Dimension size = new Dimension(dialogRecipe.getWidth() < 800? 800 : dialogRecipe.getWidth() , dialogRecipe.getHeight());
			dialogRecipe.setSize(size);
			dialogRecipe.setMinimumSize(size);
			
			recipeAdapter = new DAOAdapter<OtherRecipe>() {
				
				@Override
				public synchronized void onCreate(OtherRecipe e, int requestId) {
					dialogRecipe.setVisible(false);
				}
				
				@Override
				public synchronized void onUpdate(OtherRecipe e, int requestId) {
					dialogRecipe.setVisible(false);
				}
				
			};
			
			otherRecipeDao.addListener(recipeAdapter);
		}
	}
	
	private class JournalPaymentFeeTableModel extends TableModel<PaymentFee> {
		private static final long serialVersionUID = 7032190897666231971L;

		private AcademicYear currentYear;
		
		public JournalPaymentFeeTableModel() {
			super(paymentFeeDao);
		}
		
		@Override
		public synchronized void reload() {
			data.clear();
			if(currentYear != null && paymentFeeDao.checkByAcademicYear(currentYear, offset)) {
				data = paymentFeeDao.findByAcademicYear(currentYear, limit, offset);
			}
			fireTableDataChanged();
		};
		
		/**
		 * @param currentYear the currentYear to set
		 */
		public void setCurrentYear(AcademicYear currentYear) {
			this.currentYear = currentYear;
			offset = 0;
			reload();
			setTitle("Payement des frais académique "+currentYear);
		}

		@Override
		protected List<PaymentFee> getExportableData() {
			return paymentFeeDao.findByAcademicYear(currentYear);
		}

		@Override
		protected Object getCellValue(List<PaymentFee> exportables, int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return FormUtil.DEFAULT_FROMATER.format(exportables.get(rowIndex).getReceivedDate());
				case 1:
					return FormUtil.DEFAULT_FROMATER.format(exportables.get(rowIndex).getSlipDate());
				case 2:
					return exportables.get(rowIndex).getInscription().getStudent().getFullName();
				case 3:
					return exportables.get(rowIndex).getInscription().getPromotion();
				case 4:
					return exportables.get(rowIndex).getSlipNumber();
				case 5:
					return exportables.get(rowIndex).getReceiptNumber();
				case 6:
					return exportables.get(rowIndex).getWording();
				case 7:
					return exportables.get(rowIndex).getLocation();
				case 8:
					return exportables.get(rowIndex).getAmount();
			}
			return null;
		}

		@Override
		public void onCreate (PaymentFee e, int requestId) {
			if(e.getInscription().getPromotion().getAcademicFee().getId() == currentYear.getId())
				reload();
		}

		@Override
		public int getColumnCount() {
			return 9;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getReceivedDate());
				case 1:
					return FormUtil.DEFAULT_FROMATER.format(data.get(rowIndex).getSlipDate());
				case 2:
					return data.get(rowIndex).getInscription().getStudent().getFullName();
				case 3:
					return data.get(rowIndex).getInscription().getPromotion();
				case 4:
					return data.get(rowIndex).getSlipNumber();
				case 5:
					return data.get(rowIndex).getReceiptNumber();
				case 6:
					return data.get(rowIndex).getWording();
				case 7:
					return data.get(rowIndex).getLocation();
				case 8:
					return data.get(rowIndex).getAmount()+" "+FormUtil.UNIT_MONEY;
			}
			return null;
		}
		

		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0:
					return "Date reçu";
				case 1:
					return "Date du bordereau";
				case 2:
					return "Nom, Port-nom et prénom";
				case 3:
					return "Promotion";
				case 4:
					return "N° du borderau";
				case 5:
					return "N° reçu";
				case 6:
					return "Libele";
				case 7:
					return "Lieux de payment";
				case 8:
					return "Montant";
			}
			return super.getColumnName(column);
		}

		@Override
		public int getCount() {
			return paymentFeeDao.countByAcademicYear(currentYear);
		}
		
	}
	
	private class OutlayPanel extends Panel {
		private static final long serialVersionUID = -2786706226334778839L;
		
		private final JournalOutlayTableModel tableModel = new JournalOutlayTableModel();
		private final Table table = new Table(tableModel);
		private final TablePanel tablePanel = new TablePanel(table, "Liste des opérations de sortie");
		private final JButton btnOutlay = new Button(new ImageIcon(R.getIcon("drop")), "Nouveau dépense");
		private final JButton btnNext = new Button( new ImageIcon(R.getIcon("next")));
		private final JButton btnPrev = new Button( new ImageIcon(R.getIcon("prev")));
		private final JLabel labelCount = FormUtil.createSubTitle("");//afiche le nombre doperation
		
		private final JButton btnToExcel = new Button(new ImageIcon(R.getIcon("export")), "Excel");
		private final ActionListener navigationListener = event -> {
			JButton btn = (JButton) event.getSource();
			boolean next = btn == btnNext;
			if (next && tableModel.hasNext()) 
				tableModel.next();
			else if(!next && tableModel.hasPrevious()) 
				tableModel.previous();
			int opt = tableModel.getCount();
			labelCount.setText(opt+" opération"+(opt > 1? "s":""));
			btnPrev.setEnabled(tableModel.hasPrevious());
			btnNext.setEnabled(tableModel.hasNext());
		};
		private final ActionListener btnExportListener = event -> {
			boolean rps = Table.XLSX_FILE_CHOOSER.showSaveDialog(mainWindow);
			if(rps) {
				Thread t = new Thread(() -> {
					tableModel.exportToExcel(Table.XLSX_FILE_CHOOSER.getSelectedFile());
				});
				t.start();
			}
		};
		{
			btnNext.addActionListener(navigationListener);
			btnPrev.addActionListener(navigationListener);
			btnToExcel.addActionListener(btnExportListener);
			btnNext.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnPrev.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnToExcel.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
			btnOutlay.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		}
		
		private FormOutlay formOutlay;
		private JDialog dialogOutlay;
		
		private JPopupMenu popupOutlay;
		private JMenuItem itemDeleteOutlay;
		private JMenuItem itemUpdateOutlay;
		private DAOAdapter<Outlay> outlayAdapter;
		
		private final Box tools = Box.createHorizontalBox();
		private final MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					createPopupOutlay();
					popupOutlay.show(tablePanel, e.getX(), e.getY());
				}
			}
		};
		
		public OutlayPanel() {
			super(new BorderLayout());
			
			tools.add(labelCount);
			tools.add(Box.createHorizontalGlue());
			tools.setBackground(FormUtil.BKG_END);
			tools.setOpaque(true);
			
			tools.add(btnNext);
			tools.add(Box.createHorizontalStrut(5));
			tools.add(btnPrev);
			tools.add(Box.createHorizontalStrut(20));
			tools.add(btnToExcel);
			tools.add(Box.createHorizontalStrut(5));
			tools.add(btnOutlay);
			tools.add(Box.createHorizontalStrut(5));
			
			add(tablePanel, BorderLayout.CENTER);
			add(tools, BorderLayout.SOUTH);
			
			btnOutlay.addActionListener(event -> {
				createOutlay();
			});
			
			table.addMouseListener(mouseListener);
		}

		/**
		 * @return the tableModel
		 */
		public JournalOutlayTableModel getTableModel() {
			return tableModel;
		}

		public void reload (AcademicYear currentYear) {
			tableModel.setCurrentYear(currentYear);
			
			int opt = tableModel.getCount();
			labelCount.setText(opt+" opération"+(opt > 1? "s":""));
			btnPrev.setEnabled(tableModel.hasPrevious());
			btnNext.setEnabled(tableModel.hasNext());
			
			btnToExcel.setEnabled(tableModel.getRowCount() != 0);			
		}
		
		/**
		 * Ouverture de la boiter de dialogue d'enregistrement d'une sortie
		 */
		public void createOutlay () {
			createOutlayDialog();
			
			dialogOutlay.setLocationRelativeTo(mainWindow);
			dialogOutlay.setVisible(true);
		}
		
		/**
		 * mis enn jour d'un operation sortie dans un compte
		 * @param outlay
		 */
		public void updateOutlay (Outlay outlay) {
			createOutlayDialog();
			
			formOutlay.setOutlay(outlay);
			dialogOutlay.setLocationRelativeTo(mainWindow);
			dialogOutlay.setVisible(true);
		}

		/**
		 * creation de la boite de dialogue de d'enregistrement/modification des depences
		 */
		private void createOutlayDialog () {
			if(dialogOutlay != null)
				return;
			
			formOutlay = new FormOutlay(mainWindow);
			dialogOutlay = new JDialog(mainWindow, "Sortie en caisse", true);
			dialogOutlay.setIconImage(mainWindow.getIconImage());
			
			dialogOutlay.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogOutlay.getContentPane().add(formOutlay, BorderLayout.CENTER);
			dialogOutlay.getContentPane().setBackground(FormUtil.BKG_DARK);
			dialogOutlay.pack();
			final Dimension size = new Dimension(dialogOutlay.getWidth() < 650? 650 : dialogOutlay.getWidth(), dialogOutlay.getHeight() + 250);
			dialogOutlay.setSize(size);
			dialogOutlay.setMinimumSize(size);
			outlayAdapter = new DAOAdapter<Outlay>() {

				@Override
				public synchronized void onCreate(Outlay e, int requestId) {
					dialogOutlay.setVisible(false);
				}

				@Override
				public synchronized void onUpdate(Outlay e, int requestId) {
					dialogOutlay.setVisible(false);
				}			
			};
			
			outlayDao.addListener(outlayAdapter);
		}
		
		/**
		 * creation du popup menu qui permet de modifier/supprimer une sortie
		 */
		private void createPopupOutlay() {
			if (popupOutlay != null)
				return;
			popupOutlay = new JPopupMenu();
			
			itemDeleteOutlay = new JMenuItem("Supprimer", new ImageIcon(R.getIcon("close")));
			itemUpdateOutlay = new JMenuItem("Modifier", new ImageIcon(R.getIcon("edit")));
			popupOutlay.add(itemDeleteOutlay);
			popupOutlay.add(itemUpdateOutlay);
			
			itemDeleteOutlay.addActionListener(event -> {
				Outlay out = tableModel.getRow(table.getSelectedRow());
				int status = JOptionPane.showConfirmDialog(dialogOutlay, "Voulez-vous vraiment supprimer ce dépense??", "Suppression", JOptionPane.OK_CANCEL_OPTION);
				if(status == JOptionPane.OK_OPTION) {
					try {					
						outlayDao.delete(out.getId());
					} catch (DAOException e) {
						JOptionPane.showMessageDialog(dialogOutlay, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			itemUpdateOutlay.addActionListener (event -> {
				Outlay out = tableModel.getRow(table.getSelectedRow());
				updateOutlay(out);
			});
		}
		
	}
	
	/**
	 * @author Esaie MUHASA
	 */
	private class JournalOutlayTableModel extends TableModel<Outlay> {
		private static final long serialVersionUID = -90393497836126101L;
		
		private AcademicYear currentYear;

		public JournalOutlayTableModel() {
			super(outlayDao);
			limit = 20;
		}

		/**
		 * @param currentYear
		 */
		public void setCurrentYear(AcademicYear currentYear) {
			this.currentYear = currentYear;
			offset = 0;
			reload();
			setTitle("Dépenses pour l'année académique "+currentYear);
		}
		
		@Override
		public synchronized void reload () {
			data.clear();
			if(currentYear != null && outlayDao.checkByAcademicYear(currentYear, offset))
				data = outlayDao.findByAcademicYear(currentYear, limit, offset);
			fireTableDataChanged();
		}
		
		@Override
		public void onUpdate(Outlay e, int requestId) {
			reload();
		}

		@Override
		public int getColumnCount() {
			return 5;
		}
		
		@Override
		public String getColumnName(int column) {
			switch (column) {
				case 0 : return "Date";
				case 1 : return "Compte";
				case 2 : return "Lieux de livraison";
				case 3 : return "libelé";
				case 4 : return "Montant";
			}
			return super.getColumnName(column);
		}


		@Override
		protected List<Outlay> getExportableData() {
			return outlayDao.findByAcademicYear(currentYear);
		}

		@Override
		protected Object getCellValue(List<Outlay> exportables, int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0: return DEFAULT_DATE_FORMAT.format(exportables.get(rowIndex).getDeliveryDate());
				case 1: return exportables.get(rowIndex).getAccount();
				case 2 : return exportables.get(rowIndex).getLocation();
				case 3: return exportables.get(rowIndex).getWording();
				case 4: return PieRender.DECIMAL_FORMAT.format(exportables.get(rowIndex).getAmount());
			}
			return null;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex) {
				case 0: return DEFAULT_DATE_FORMAT.format(data.get(rowIndex).getDeliveryDate());
				case 1: return data.get(rowIndex).getAccount();
				case 2 : return data.get(rowIndex).getLocation();
				case 3: return data.get(rowIndex).getWording();
				case 4: return PieRender.DECIMAL_FORMAT.format(data.get(rowIndex).getAmount())+" "+FormUtil.UNIT_MONEY;
			}
			return null;
		}
		
		@Override
		public void onCreate(Outlay e, int requestId) {
			if(currentYear != null && e.getAcademicYear().getId() == currentYear.getId())
				super.onCreate(e, requestId);
		}

	}
	
}
