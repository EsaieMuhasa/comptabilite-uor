/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultLineChartModel extends AbstractChartData implements LineChartModel{
	
	protected final List<PointCloud> clouds = new ArrayList<>();
	protected final List<LineChartModelListener> listeners = new  ArrayList<>();
	
	protected Axis xAxis;
	protected Axis yAxis;
	
	private Point xMax;
	private Point yMax;
	private Point xMin;
	private Point yMin;

	protected final PointCloudListener cloudListener = new PointCloudListener() {
		
		@Override
		public synchronized void onRemovePoint (PointCloud cloud, int index, Point point) {
			for (LineChartModelListener ls : listeners) 
				ls.onRemovePoint(DefaultLineChartModel.this, indexOf(cloud), index, point);
		}
		
		@Override
		public synchronized void onInsertPoint (PointCloud cloud, int index) {
			for (LineChartModelListener ls : listeners) 
				ls.onInsertPoint(DefaultLineChartModel.this, indexOf(cloud), index);
		}
		
		@Override
		public synchronized void onChange(PointCloud cloud) {
			for (LineChartModelListener ls : listeners) 
				ls.onCloudChange(DefaultLineChartModel.this, indexOf(cloud));
		}

		@Override
		public void onPointChange(PointCloud cloud, int index) {
			for (LineChartModelListener ls : listeners) 
				ls.onPointChange(DefaultLineChartModel.this, indexOf(cloud), index);
			
		}
	};
	
	/**
	 * 
	 */
	public DefaultLineChartModel() {
		super();
		xAxis = new DefaultAxis();
		yAxis = new DefaultAxis();
	}

	/**
	 * @param xAxis
	 * @param yAxis
	 */
	public DefaultLineChartModel(Axis xAxis, Axis yAxis) {
		super();
		this.xAxis = xAxis;
		this.yAxis = yAxis;
	}

	@Override
	public synchronized void addChart(PointCloud cloud) {
		if(clouds.contains(cloud))
			return;
		
		clouds.add(cloud);
		cloud.addListener(cloudListener);
		checkXMax();
		checkXMin();
		checkYMax();
		checkYMin();
	}
	
	@Override
	public int indexOf(PointCloud cloud) {
		return clouds.indexOf(cloud);
	}

	@Override
	public synchronized void addChart(PointCloud cloud, int index) {
		if(clouds.contains(cloud))
			return;
			
		clouds.add(index, cloud);
		cloud.addListener(cloudListener);
	}

	@Override
	public int countChars() {
		return clouds.size();
	}

	@Override
	public PointCloud getChartAt(int index) {
		return clouds.get(index);
	}

	@Override
	public synchronized void removeChartAt(int index) {
		clouds.get(index).removeListener(cloudListener);
		clouds.remove(index);
	}
	
	@Override
	public void removeAll() {
		for (PointCloud cloud : clouds)
			cloud.removeListener(cloudListener);
		
		clouds.clear();
	}

	@Override
	public void addListener(LineChartModelListener listener) {
		if(!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public boolean removeListener(LineChartModelListener listener) {
		return listeners.remove(listener);
	}

	@Override
	public Axis getXAxis() {
		return xAxis;
	}

	@Override
	public Axis getYAxis() {
		return yAxis;
	}

	@Override
	public Point getXMax() {
		return xMax;
	}
	
	protected void checkXMax () {
		Point point = clouds.get(0).getXMax();
		for (PointCloud c : clouds) {
			if (c.getXMax().getX() > point.getX())
				point = c.getXMax();
		}
		xMax = point;		
	}

	@Override
	public Point getYMax() {
		return yMax;
	}
	
	protected void checkYMax () {
		Point point = clouds.get(0).getYMax();
		for (PointCloud c : clouds) {
			if (c.getYMax().getY() > point.getY())
				point = c.getYMax();
		}
		yMax = point;		
	}

	@Override
	public Point getXMin() {
		return xMin;
	}
	
	protected void checkXMin () {
		Point point = clouds.get(0).getXMin();
		for (PointCloud c : clouds) {
			if (c.getXMin().getX() < point.getX())
				point = c.getXMin();
		}
		xMin = point;		
	}

	@Override
	public Point getYMin() {
		return yMin;
	}
	
	protected void checkYMin () {
		Point point = clouds.get(0).getYMin();
		for (PointCloud c : clouds) {
			if (c.getYMin().getY() < point.getY())
				point = c.getYMin();
		}
		yMin = point;
		
	}

}
