/**
 * 
 */
package net.uorbutembo.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.beans.AcademicYear;
import net.uorbutembo.beans.AnnualSpend;
import net.uorbutembo.beans.OtherRecipe;
import net.uorbutembo.beans.Outlay;
import net.uorbutembo.beans.PaymentFee;
import net.uorbutembo.dao.AcademicYearDao;
import net.uorbutembo.dao.AcademicYearDaoListener;
import net.uorbutembo.dao.AnnualSpendDao;
import net.uorbutembo.dao.OtherRecipeDao;
import net.uorbutembo.dao.OutlayDao;
import net.uorbutembo.dao.PaymentFeeDao;
import net.uorbutembo.swing.Card;
import net.uorbutembo.swing.DefaultCardModel;
import net.uorbutembo.swing.Panel;
import net.uorbutembo.swing.charts.Axis;
import net.uorbutembo.swing.charts.DefaultAxis;
import net.uorbutembo.swing.charts.DefaultLineChartModel;
import net.uorbutembo.swing.charts.DefaultPointCloud;
import net.uorbutembo.swing.charts.DefaultTimeAxis;
import net.uorbutembo.swing.charts.LineChartRender;
import net.uorbutembo.swing.charts.LineChartRender.Interval;
import net.uorbutembo.swing.charts.LineChartRenderListener;
import net.uorbutembo.swing.charts.Point2d;
import net.uorbutembo.views.forms.FormUtil;
import resources.net.uorbutembo.R;

/**
 * @author Esaie MUHASA
 *
 */
public class JournalGeneral extends Panel implements ItemListener, AcademicYearDaoListener, LineChartRenderListener{
	private static final long serialVersionUID = 5001737357466282501L;
	
	private JCheckBox checkBoxGridChart = new JCheckBox("Grille", true);
	
//	private JRadioButton radioLineChart = new JRadioButton("Lignes");
//	private JRadioButton radioHistoChart = new JRadioButton("Histogrammes");
	
	private JRadioButton radioStepDay = new JRadioButton("Par jour");
	private JRadioButton radioStepWeek = new JRadioButton("Par semaine");
	private JRadioButton radioStepMonth = new JRadioButton("Mensuel");
	
	private JCheckBox checkBoxIn = new JCheckBox("Recettes", true);
	private JCheckBox checkBoxOut = new JCheckBox("Dépenses", true);
	private JCheckBox checkBoxSold = new JCheckBox("Solde", true);
	
	private final JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
	private Panel panelChart = new Panel(new BorderLayout());//panel contenant la visualisation graphique
	
	private final DefaultAxis yAxis = new DefaultAxis(24 * 60 * 60 * 1000, "Monain", "$");
	private final DefaultTimeAxis xAxis = new DefaultTimeAxis(0, "Temps", "t");
	
	private final DefaultLineChartModel chartModel = new DefaultLineChartModel(xAxis, yAxis);
	private final LineChartRender chartRender = new LineChartRender(chartModel);
	private final DefaultPointCloud cloudRecipes = new DefaultPointCloud(FormUtil.COLORS_ALPHA[0], FormUtil.COLORS[0], FormUtil.COLORS[0]);
	private final DefaultPointCloud cloudOutlays = new DefaultPointCloud(FormUtil.COLORS_ALPHA[1], FormUtil.COLORS[1], FormUtil.COLORS[1]);
	private final DefaultPointCloud cloudSold = new DefaultPointCloud(FormUtil.COLORS_ALPHA[2], FormUtil.COLORS[2], FormUtil.COLORS[2]);
	
	private final DefaultComboBoxModel<AcademicYear> comboModel = new DefaultComboBoxModel<>();
	private final JComboBox<AcademicYear> comboBox = new JComboBox<>(comboModel);
	
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
	
	private final CardsPanel cards = new CardsPanel();
	private final AnnualSpendDao annualSpendDao;
	private final AcademicYearDao academicYearDao;
	private final OutlayDao outlayDao;
	private final PaymentFeeDao paymentFeeDao;
	private final OtherRecipeDao otherRecipeDao;
	
	public JournalGeneral(MainWindow mainWindow) {
		super(new BorderLayout());
		
		annualSpendDao = mainWindow.factory.findDao(AnnualSpendDao.class);
		outlayDao = mainWindow.factory.findDao(OutlayDao.class);
		otherRecipeDao = mainWindow.factory.findDao(OtherRecipeDao.class);
		paymentFeeDao = mainWindow.factory.findDao(PaymentFeeDao.class);
		academicYearDao = mainWindow.factory.findDao(AcademicYearDao.class);
		
		academicYearDao.addYearListener(this);
		
		final Panel container = new Panel(new BorderLayout()),
				panelCenter = new Panel(new BorderLayout()),
				panelBottom = new Panel(new BorderLayout(5, 0)),
				panelTools = new  Panel(new BorderLayout()),
				panelSteps = new Panel(new FlowLayout(FlowLayout.LEFT)),
				panelCharts = new Panel();
		final Box boxOthers = Box.createHorizontalBox(); 
		
		chartRender.addRenderListener(this);
		chartRender.setDraggable(true);
		panelChart.add(chartRender, BorderLayout.CENTER);
		panelChart.setBorder(FormUtil.DEFAULT_EMPTY_BORDER);
		
		checkBoxGridChart.setForeground(FormUtil.BORDER_COLOR);
		checkBoxGridChart.addChangeListener(event -> {
			chartRender.setVisibleGrid(checkBoxGridChart.isSelected());
		});
		
		radioStepDay.setForeground(Color.WHITE);
		radioStepWeek.setForeground(Color.WHITE);
		radioStepMonth.setForeground(Color.WHITE);
		panelSteps.add(radioStepDay);
		panelSteps.add(radioStepWeek);
		panelSteps.add(radioStepMonth);
		
		comboBox.setPreferredSize(new Dimension(200, 24));
		comboBox.setEnabled(false);
		comboBox.addItemListener(this);
		progressBar.setStringPainted(true);
		progressBar.setVisible(false);
		panelBottom.add(panelSteps, BorderLayout.WEST);
		panelBottom.add(comboBox, BorderLayout.EAST);
		panelBottom.setBorder(new EmptyBorder(0, 0, 0, 5));
		panelBottom.add(progressBar, BorderLayout.CENTER);
		
		checkBoxIn.setForeground(FormUtil.COLORS[0]);
		checkBoxOut.setForeground(FormUtil.COLORS[1]);
		checkBoxSold.setForeground(FormUtil.COLORS[4]);
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
		
		panelCenter.add(panelChart, BorderLayout.CENTER);
		panelCenter.add(panelTools, BorderLayout.NORTH);
		panelCenter.add(panelBottom, BorderLayout.SOUTH);
		
		container.add(panelCenter, BorderLayout.CENTER);
		add(container, BorderLayout.CENTER);
		add(cards, BorderLayout.SOUTH);
	}
	
	private void wait(boolean status) {
		
		if (status) {
			setCursor(FormUtil.WAIT_CURSOR);
		} else {
			setCursor(Cursor.getDefaultCursor());
		}
		
		progressBar.setVisible(status);
		
		checkBoxIn.setEnabled(!status);
		checkBoxOut.setEnabled(!status);
		checkBoxSold.setEnabled(!status);
		comboBox.setEnabled(!status);
		radioStepDay.setEnabled(!status);
		radioStepWeek.setEnabled(!status);
		radioStepMonth.setEnabled(!status);
		checkBoxGridChart.setEnabled(!status);
		
		if(status) {
			progressBar.setIndeterminate(true);
		}
	}
	
	@Override
	public void onCurrentYear(AcademicYear year) {
		wait(true);
		if (year != null) {			
			if(comboModel.getSize() == 0) {
				comboBox.removeItemListener(this);
				List<AcademicYear> years = academicYearDao.findAll();
				for (AcademicYear y : years) {
					comboModel.addElement(y);
					if(y.getId() == year.getId())
						comboBox.setSelectedItem(y);
				}
				comboBox.addItemListener(this);
			}
			
			cards.updateCards(year);
			reloadChart(year);
		}
		wait(false);
	}
	
	@Override
	public void onRequireTranslation(LineChartRender source, Axis axis, Interval interval) {
		if (axis != xAxis)
			return;
		
		AcademicYear year = comboModel.getElementAt(comboBox.getSelectedIndex());
		reloadChart(year);
	}
	
	@Override
	public void onRequireTranslation(LineChartRender source, Interval xInterval, Interval yInterval) {
		onRequireTranslation(source, source.getModel().getXAxis(), xInterval);
	}
	
	private synchronized void reloadChart(AcademicYear year) {
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
		
//		cloudRecipes.toSquareSignal();
//		cloudOutlays.toSquareSignal();
//		cloudSold.toSquareSignal();
	}
	
	@Override
	public void itemStateChanged(ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED) 
			return;
		
		AcademicYear year = comboModel.getElementAt(comboBox.getSelectedIndex());
		onCurrentYear(year);
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
		
		private final DefaultCardModel<Double> cardSoldModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		private final DefaultCardModel<Double> cardOutModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		private final DefaultCardModel<Double> cardInModel = new DefaultCardModel<>(FormUtil.BKG_END, Color.WHITE);
		
		private final Card cardSold = new Card(cardSoldModel);
		private final Card cardOut = new Card(cardOutModel);
		private final Card cardIn = new Card(cardInModel);

		public CardsPanel() {
			
			super(new BorderLayout());
			
			cardSoldModel.setTitle("Solde");
			cardSoldModel.setInfo("Montant disponible");
			cardSoldModel.setSuffix("$");
			cardSoldModel.setIcon(R.getIcon("caisse"));
			cardSoldModel.setValue(0d);
			
			cardOutModel.setTitle("Depenses");
			cardOutModel.setInfo("Montant déjà utiliser");
			cardOutModel.setSuffix("$");
			cardOutModel.setIcon(R.getIcon("btn-cancel"));
			cardOutModel.setValue(0d);
			
			cardInModel.setTitle("Recettes");
			cardInModel.setInfo("Montant déjà collecter");
			cardInModel.setSuffix("$");
			cardInModel.setIcon(R.getIcon("btn-add"));
			cardInModel.setValue(0d);
			
			container.add(cardSold);
			container.add(cardIn);
			container.add(cardOut);
			
			add(container, BorderLayout.CENTER);
			setBorder(new EmptyBorder(10, 10, 10, 10));
		}
		
		/**
		 * mise en jours des valeurs des cards, conformement a l'annee academique en parametre
		 * @param year
		 */
		public void updateCards(AcademicYear year) {
			
			double recipe = 0, used = 0, sold = 0;
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
				BigDecimal bigSold = new BigDecimal(recipe - used).setScale(2, RoundingMode.HALF_UP);
				
				recipe = bigRecipe.doubleValue();
				used = bigUsed.doubleValue();
				sold = bigSold.doubleValue();
			}

			cardInModel.setValue(recipe);
			cardOutModel.setValue(used);
			cardSoldModel.setValue(sold);
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
	
	

}
