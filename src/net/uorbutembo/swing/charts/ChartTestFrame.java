/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.views.forms.FormUtil;

/**
 * @author Esaie MUHASA
 *
 */
public class ChartTestFrame extends JFrame {
	private static final long serialVersionUID = -8373859408934961744L;

	/**
	 * @throws HeadlessException
	 */
	public ChartTestFrame() throws HeadlessException {
		super("Graphiques de type polylines");
		setSize(700, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		DefaultAxis xAxis = new DefaultAxis(50, "Abs", "X");
		DefaultAxis yAxis = new DefaultAxis(50, "Ord", "Y");
		
		DefaultPointCloud cloud = new DefaultPointCloud();
		DefaultPointCloud cloud2 = new DefaultPointCloud();
		DefaultPointCloud cloud3 = new DefaultPointCloud();
		DefaultPointCloud cloud4 = new DefaultPointCloud();
		cloud.setBorderColor(FormUtil.COLORS[0]);
		cloud2.setBorderColor(FormUtil.COLORS[1]);
		cloud3.setBorderColor(FormUtil.COLORS[4]);
		cloud4.setBorderColor(FormUtil.COLORS[6]);
		
		cloud.setBackgroundColor(new Color(0x50FFCE30, true));
		cloud2.setBackgroundColor(new Color(0x50E83845, true));
		cloud3.setBackgroundColor(new Color(0x5A7F50FF, true));
		cloud4.setBackgroundColor(new Color(0x50FF7F50, true));
		
		cloud.setFill(true);
		cloud2.setFill(true);
		cloud3.setFill(true);
		cloud4.setFill(true);
		
		cloud.addPoint(new Point2d(-50, -5));
		cloud.addPoint(new Point2d(-40, 10));
		cloud.addPoint(new Point2d(-30, -5));
		cloud.addPoint(new Point2d(-20, 10));
		for (int i = 0; i < 10; i++) {
			cloud.addPoint(randPoint(0, 10, i*20));
		}
		
		List<DefaultPointCloud> listCos = new ArrayList<>();
		List<DefaultPointCloud> listSin = new ArrayList<>();
		int nombreCourbes = 18;
		for (int i = 0; i < nombreCourbes; i++) {
			DefaultPointCloud c = new DefaultPointCloud();
			DefaultPointCloud s = new DefaultPointCloud();
			c.setBorderColor(FormUtil.COLORS[0]);
			s.setBorderColor(FormUtil.COLORS[1]);
			listCos.add(c);
			listSin.add(s);
			c.setBorderWidth(1);
			s.setBorderWidth(1);
		}
		
		double amplitude = 2, angle, sin, cos;
		for (int i = -1000; i <= -20; i+=1) {
			for (int j = 0; j < nombreCourbes; j++) {
				angle = (i+(j*-5)) * (Math.PI / 180);
				sin = amplitude * Math.sin(angle);
				cos = amplitude * Math.cos(angle);
				listCos.get(j).addPoint(new Point2d(i, cos));
				listSin.get(j).addPoint(new Point2d(i, sin));
			}
		}
		
		cloud2.addPoint(new Point2d(10, -5));
		cloud2.addPoint(new Point2d(20, 8));
		cloud2.addPoint(new Point2d(30, -5));
		cloud2.addPoint(new Point2d(40, 5));
		cloud2.addPoint(new Point2d(50, 0));
		for (int i = 6; i < 10; i++) {
			cloud2.addPoint(randPoint(-15, 15, i*20));
		}
		
		for (int i = -10; i < 10; i++) {
			cloud3.addPoint(randPoint(2, 10, i*20));
		}
		
		for (int i = -10; i < 10; i++) {
			cloud4.addPoint(randPoint(-8, -5, i*20));
		}
		
		DefaultLineChartModel model = new DefaultLineChartModel(xAxis, yAxis);
		DefaultLineChartModel model2 = new DefaultLineChartModel();
		DefaultLineChartModel model3 = new DefaultLineChartModel();
		DefaultLineChartModel model4 = new DefaultLineChartModel();
		DefaultLineChartModel model5 = new DefaultLineChartModel();
		DefaultLineChartModel model6 = new DefaultLineChartModel();
		
		model.addChart(cloud);
		model.addChart(cloud3);
		model.addChart(cloud4);
		model.addChart(cloud2);
		
		model2.addChart(cloud);
		model3.addChart(cloud2);
		model4.addChart(cloud3);
		model5.addChart(cloud4);
		
		for (DefaultPointCloud c : listCos) {
			model6.addChart(c);
		}
		for (DefaultPointCloud c : listSin) {
			model6.addChart(c);
		}
		
		JPanel content = new JPanel(new GridLayout(3, 2, 10, 10));
		
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.add(new LineChartRender(model));
		content.add(new LineChartRender(model2));
		content.add(new LineChartRender(model3));
		content.add(new LineChartRender(model4));
		content.add(new LineChartRender(model5));
		content.add(new LineChartRender(model6));
		
		setContentPane(content);
	}
	
	public static void main(String[] args) {
		ChartTestFrame f = new ChartTestFrame();
		f.setVisible(true);
	}
	
	public static Point randPoint (double min, double max, double x) {
		double y = Math.random()*max;
		
		if(y < min)
			y += min;
		
		return new Point2d(x, y, 5);
	}



}
