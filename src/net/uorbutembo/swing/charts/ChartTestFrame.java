/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.uorbutembo.swing.charts.PointCloud.CloudType;
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
		super("Graphiques");
		setSize(700, 450);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		DefaultAxis xAxis = new DateAxis("Abs", "X", "");
		DefaultAxis yAxis = new DefaultAxis("Ord", "Y", "");
		
		DefaultPointCloud cloud = new DefaultPointCloud("Cloud 1");
		DefaultPointCloud cloud2 = new DefaultPointCloud("Cloud 2");
		DefaultPointCloud cloud3 = new DefaultPointCloud("Cloud 3");
		DefaultPointCloud cloud4 = new DefaultPointCloud("Cloud 4");
		DefaultPointCloud cloud5 = new DefaultPointCloud("Cloud 5");
		
		cloud.setBorderColor(FormUtil.COLORS[0]);
		cloud2.setBorderColor(FormUtil.COLORS[1]);
		cloud3.setBorderColor(FormUtil.COLORS[2]);
		cloud4.setBorderColor(FormUtil.COLORS[3]);
		cloud5.setBorderColor(FormUtil.COLORS[4]);
		
		cloud.setBackgroundColor(FormUtil.COLORS_ALPHA[0]);
		cloud2.setBackgroundColor(FormUtil.COLORS_ALPHA[1]);
		cloud3.setBackgroundColor(FormUtil.COLORS_ALPHA[2]);
		cloud4.setBackgroundColor(FormUtil.COLORS_ALPHA[3]);
		cloud5.setBackgroundColor(FormUtil.COLORS_ALPHA[4]);
		
		cloud.setFill(true);
		cloud2.setFill(true);
		cloud3.setFill(true);
		cloud4.setFill(true);
		cloud5.setBorderWidth(1f);
		cloud5.setType(CloudType.STICK_CHART);
		
		cloud.addPoint(new DefaultMaterialPoint(-50, -5));
		cloud.addPoint(new DefaultMaterialPoint(-40, 10));
		cloud.addPoint(new DefaultMaterialPoint(-30, -5));
		cloud.addPoint(new DefaultMaterialPoint(-20, 10));
		for (int i = 0; i < 10; i++) {
			cloud.addPoint(randPoint(0, 10, i*20));
		}
		
		List<DefaultPointCloud> listCos = new ArrayList<>();
		List<DefaultPointCloud> listSin = new ArrayList<>();
		int nombreCourbes = 2;
		for (int i = 0; i < nombreCourbes; i++) {
			DefaultPointCloud c = new DefaultPointCloud("Cos "+i);
			DefaultPointCloud s = new DefaultPointCloud("Sin "+i);
			c.setBorderColor(FormUtil.COLORS[0]);
			s.setBorderColor(FormUtil.COLORS[1]);
			c.setBackgroundColor(FormUtil.COLORS_ALPHA[0]);
			s.setBackgroundColor(FormUtil.COLORS_ALPHA[1]);
			s.setBorderWidth(2f);
			c.setBorderWidth(2f);
			listCos.add(c);
			listSin.add(s);
		}
		listCos.get(0).setBorderWidth(0.8f);
		listSin.get(0).setBorderWidth(0.8f);
		listCos.get(0).setType(CloudType.STICK_CHART);
		listSin.get(0).setType(CloudType.STICK_CHART);
		
		double amplitude = 1000, angle, sin, cos;
		for (int i = -360; i <= 360; i+=10) {
			for (int j = 0; j < nombreCourbes; j++) {
				angle = (i+(j*-30)) * (Math.PI / 180);
				sin = amplitude * Math.sin(angle);
				cos = amplitude * Math.cos(angle);
				listCos.get(j).addPoint(new DefaultMaterialPoint(i, cos, 0));
				listSin.get(j).addPoint(new DefaultMaterialPoint(i, sin, 0));
			}
		}
		
		cloud2.addPoint(new DefaultMaterialPoint(10, -5));
		cloud2.addPoint(new DefaultMaterialPoint(20, 8));
		cloud2.addPoint(new DefaultMaterialPoint(30, -5));
		cloud2.addPoint(new DefaultMaterialPoint(40, 5));
		cloud2.addPoint(new DefaultMaterialPoint(50, 0));
		for (int i = 6; i < 10; i++) {
			cloud2.addPoint(randPoint(-15, 15, i*20));
		}
		
		for (int i = -10; i < 10; i++) {
			cloud3.addPoint(randPoint(2, 10, i*20));
		}
		
		for (int i = -10; i < 10; i++) {
			MaterialPoint p = randPoint(-8, -5, i*20);
			MaterialPoint p2 = new DefaultMaterialPoint(p);
			p2.translateXY(p.getX(), -p.getY());
			cloud4.addPoint(p);
			cloud5.addPoint(p2);
		}
		
		DefaultCloudChartModel model = new DefaultCloudChartModel(xAxis, yAxis);
		DefaultCloudChartModel model2 = new DefaultCloudChartModel();
		DefaultCloudChartModel model3 = new DefaultCloudChartModel();
		DefaultCloudChartModel model4 = new DefaultCloudChartModel();
		DefaultCloudChartModel model5 = new DefaultCloudChartModel();
		DefaultCloudChartModel model6 = new DefaultCloudChartModel();
		
		model.addChart(cloud);
		model.addChart(cloud2);
		model.addChart(cloud3);
		model.addChart(cloud4);
		model.addChart(cloud5);
		
		for (DefaultPointCloud c : listCos) {
			model6.addChart(c);
		}
		for (DefaultPointCloud c : listSin) {
			model6.addChart(c);
		}
		
		//custom
		DefaultPointCloud custom = new DefaultPointCloud("Custom 1");
		DefaultPointCloud custom2 = new DefaultPointCloud("Custom 1");
		DefaultPointCloud custom3 = new DefaultPointCloud("Custom 1");
		DefaultPointCloud custom4 = new DefaultPointCloud("Custom 1");
		custom.setBorderColor(new Color(0x990000));
		custom.setBackgroundColor(new Color(0x55990000, true));
		custom.setFill(true);
//		custom.setType(CloudType.STICK_CHART);
		
		custom2.setBorderColor(new Color(0x009900));
		custom2.setBackgroundColor(new Color(0x55009900, true));
		custom2.setFill(true);
		
		custom3.setBorderColor(new Color(0x000099));
		custom3.setBackgroundColor(new Color(0xAA000099, true));
		
		custom4.setBorderColor(new Color(0x990099));
		custom4.setBackgroundColor(new Color(0xAA990099, true));
		
		int size = 8, step = 1;
		for (double i = 0; i <= 1.1; i += 0.1) {
			double y = rand(0.01, 0.9);
			custom.addPoint(new DefaultMaterialPoint(-1 * ((step * i)), y, size));
			custom2.addPoint(new DefaultMaterialPoint((step * i), y, size));
			custom3.addPoint(new DefaultMaterialPoint(-1 * ((step * i)), -y/2, size));
			custom4.addPoint(new DefaultMaterialPoint((step * i), -y/2, size));
		}		
		custom4.setType(CloudType.STICK_CHART);
		DefaultCloudChartModel c = new DefaultCloudChartModel();
		c.addChart(custom);
		c.addChart(custom2);
		c.addChart(custom3);
		c.addChart(custom4);
		//==
		
		JPanel content = new JPanel(new BorderLayout());
		JPanel content2 = new JPanel(new GridLayout(3, 2, 10, 10));
		
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		content.add(new ChartPanel(model), BorderLayout.CENTER);
		
		content2.setBorder(new EmptyBorder(10, 10, 10, 10));
		content2.add(new CloudChartRender(model));
		content2.add(new CloudChartRender(c));
		content2.add(new CloudChartRender(model3));
		content2.add(new CloudChartRender(model4));
		content2.add(new CloudChartRender(model5));
		content2.add(new CloudChartRender(model6));
		
		JDesktopPane desk = new JDesktopPane();
		JInternalFrame version1 = new JInternalFrame("ChartPanel", true, false, true);
		JInternalFrame version2 = new JInternalFrame("CloudChartRender", true , false, true);
		
		version1.setClosable(false);
		version2.setClosable(false);
		
		version1.setSize(100, 200);
		version2.setSize(550, 400);
		version2.setLocation(getWidth()/4, 100);
		
		version1.setContentPane(content);
		version2.setContentPane(content2);
		version1.setVisible(true);
		version2.setVisible(true);
		
		desk.add(version1);
		desk.add(version2);
		
		setContentPane(desk);
	}
	
	public static void main (String[] args) throws Exception {
//		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		ChartTestFrame f = new ChartTestFrame();
		f.setVisible(true);
	}
	
	public static MaterialPoint randPoint (double min, double max, double x) {
		double y = Math.random()*max;
		
		if(y < min)
			y += min;
		
		return new DefaultMaterialPoint(x, y);
	}
	
	public static double rand (double min, double max)  {
		double val = Math.random()*max;
		
		if(val < min)
			val += min;
		return val;
	}



}
