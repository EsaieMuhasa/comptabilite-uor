/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;

import net.uorbutembo.swing.Panel;
import net.uorbutembo.tools.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class ChartPanel extends Panel {
	private static final long serialVersionUID = -4145881145979100410L;
	
	private CloudChartModel model;
	private CloudChartRender chartRender;
	private ModelAdapter modelAdapter = new ModelAdapter();
	
	private final JCheckBox xLine = new JCheckBox("Grille X", true);
	private final JCheckBox yLine = new JCheckBox("Grille Y", true);
	private final JCheckBox mouseLine = new JCheckBox("Traces", true);
	
	private final Box checkBox = Box.createHorizontalBox();
	private final Panel checkChart = new Panel(new FlowLayout(FlowLayout.RIGHT));
	private final Panel chartContainer = new Panel(new BorderLayout());
	
	private final List<JCheckBox> chartItems = new ArrayList<>();
	private final ChangeListener chartItemListener = event -> {
		JCheckBox box = (JCheckBox) event.getSource();
		int index = chartItems.indexOf(box);
		model.getChartAt(index).setVisible(box.isSelected());
	};
	
	private final ChangeListener xLineListener = event -> {
		chartRender.setGridXvisible(xLine.isSelected());
	};
	
	private final ChangeListener yLineListener = event -> {
		chartRender.setGridYvisible(yLine.isSelected());
	};
	
	private final ChangeListener mouseLineListener = event -> {
		chartRender.setMouseLineVisible(mouseLine.isSelected());
	};

	/**
	 * 
	 */
	public ChartPanel() {
		super(new BorderLayout());
		chartRender = new CloudChartRender();
		initCheckBox();
	}
	
	/**
	 * constructeur d'initialisation du model des donnees
	 * @param model
	 */
	public ChartPanel (CloudChartModel model) {
		super(new BorderLayout());
		this.model = model;
		chartRender = new CloudChartRender(model);
		model.addListener(modelAdapter);
		initCheckBox();
		initCheckChart();
		init();
	}
	
	/**
	 * @return the model
	 */
	public CloudChartModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(CloudChartModel model) {
		if(this.model == model)
			return;
		if(this.model != null)
			this.model.removeListener(modelAdapter);
		
		this.model = model;
		initCheckChart();
		if(model != null)
			model.addListener(modelAdapter);
	}

	/**
	 * @return the chartRender
	 */
	public CloudChartRender getChartRender() {
		return chartRender;
	}

	/**
	 * initalisation des composants graphique
	 */
	private void init () {
		final JPanel tool = new JPanel(new BorderLayout());
		tool.add(checkBox, BorderLayout.WEST);
		tool.add(checkChart, BorderLayout.CENTER);
		tool.setBackground(FormUtil.BORDER_COLOR);
		chartContainer.add(chartRender, BorderLayout.CENTER);
		chartRender.setLineAxisColor(FormUtil.BORDER_COLOR.brighter().brighter().brighter());
		
		add(tool, BorderLayout.SOUTH);
		add(chartContainer, BorderLayout.CENTER);
		setBorder(new LineBorder(FormUtil.BORDER_COLOR));
	}
	
	/**
	 * initialisation du panel contenant les checks box qui permet d'activer 
	 * ou de desactiver un graphique
	 */
	private void initCheckChart() {
		for (JCheckBox box : chartItems) {
			box.removeChangeListener(chartItemListener);
			checkChart.remove(box);
		}
		
		chartItems.clear();
		if(model == null)
			return;
		
		for (int i = 0; i < model.getSize(); i++) {
			JCheckBox box = new JCheckBox(model.getChartAt(i).getTitle(), model.getChartAt(i).isVisible());
			box.setForeground(model.getChartAt(i).getBorderColor());
			box.addChangeListener(chartItemListener);
			checkChart.add(box);
			chartItems.add(box);
		}
		
		revalidate();
	}
	
	/**
	 * initialisation des composants faciltant la configuration de l'apparence du rendu graphique
	 */
	private void initCheckBox() {
		checkBox.add(xLine);
		checkBox.add(Box.createHorizontalStrut(10));
		checkBox.add(yLine);
		checkBox.add(Box.createHorizontalStrut(10));
		checkBox.add(mouseLine);
		
		xLine.setForeground(Color.WHITE);
		yLine.setForeground(xLine.getForeground());
		mouseLine.setForeground(yLine.getForeground());
		
		xLine.addChangeListener(xLineListener);
		yLine.addChangeListener(yLineListener);
		mouseLine.addChangeListener(mouseLineListener);
	}
	
	private class ModelAdapter implements CloudChartModelListener {

		@Override
		public void onChange(CloudChartModel model) {}

		@Override
		public void onChartChange(CloudChartModel model, int index) {
			JCheckBox box = chartItems.get(index);
			box.setSelected(model.getChartAt(index).isVisible());
			box.setText(model.getChartAt(index).getTitle());
		}

		@Override
		public void onPointChange(CloudChartModel model, int chartIndex, int pointIndex) {}

		@Override
		public void onInsertChart(CloudChartModel model, int chartIndex) {
			JCheckBox box = new JCheckBox(model.getChartAt(chartIndex).getTitle(), model.getChartAt(chartIndex).isVisible());
			box.setForeground(model.getChartAt(chartIndex).getBorderColor());
			box.addChangeListener(chartItemListener);
			checkChart.add(box);
			chartItems.add(box);
		}

		@Override
		public void onRemoveChart(CloudChartModel model, int chartIndex) {
			JCheckBox box = chartItems.get(chartIndex);
			box.removeChangeListener(chartItemListener);
			checkChart.remove(box);
		}

		@Override
		public void onInsertPoint(CloudChartModel model, int chartIndex, int pointIndex) {}

		@Override
		public void onRemovePoint(CloudChartModel model, int chartIndex, int pointIndex, MaterialPoint materialPoint) {}
		
	}

}
