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
			checkMinMax();
			for (LineChartModelListener ls : listeners) 
				ls.onRemovePoint(DefaultLineChartModel.this, indexOf(cloud), index, point);
		}
		
		@Override
		public synchronized void onInsertPoint (PointCloud cloud, int index) {
			checkMinMax();
			for (LineChartModelListener ls : listeners) 
				ls.onInsertPoint(DefaultLineChartModel.this, indexOf(cloud), index);
		}
		
		@Override
		public synchronized void onChange(PointCloud cloud) {
			checkMinMax();
			for (LineChartModelListener ls : listeners) 
				ls.onCloudChange(DefaultLineChartModel.this, indexOf(cloud));
		}

		@Override
		public void onPointChange(PointCloud cloud, int index) {
			checkMinMax();
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
	public synchronized void bindFrom (LineChartModel model) {
		boolean update = false;
		for (int i = 0, count = model.getSize(); i < count; i++) {
			if(!clouds.contains(model.getChartAt(i))){
				clouds.add(model.getChartAt(i));
				model.getChartAt(i).addListener(cloudListener);
				update = true;
			}
		}
		
		if (update) {			
			checkMinMax();
			emitOnChange();
		}
	}

	@Override
	public void bindFrom(LineChartModel model, int index) {
		boolean update = false;
		for (int i = 0, count = model.getSize(); i < count; i++) {
			if(!clouds.contains(model.getChartAt(i))){
				clouds.add(index+i, model.getChartAt(i));
				model.getChartAt(i).addListener(cloudListener);
				update = true;
			}
		}
		
		if (update) {			
			checkMinMax();
			emitOnChange();
		}
	}

	@Override
	public void unbindFrom(LineChartModel model) {
		boolean update = false;
		for (int i = 0, count = model.getSize(); i < count; i++) {
			int index = indexOf(model.getChartAt(i));
			if(index != -1){
				clouds.get(index).removeListener(cloudListener);
				clouds.remove(index);
				update = true;
			}
		}
		
		if (update) {			
			checkMinMax();
			emitOnChange();
		}
	}

	@Override
	public synchronized void addChart(PointCloud cloud) {
		if(clouds.contains(cloud))
			return;
		
		clouds.add(cloud);
		cloud.addListener(cloudListener);
		emitOnInserted(clouds.size()-1);
	}
	
	@Override
	public boolean hasVisibleChart() {
		for (PointCloud cl : clouds) {
			if(cl.isVisible() && cl.countPoints() != 0)
				return true;
		}
		return false;
	}

	@Override
	public boolean hasHiddenChart() {
		for (PointCloud cl : clouds) {
			if(!cl.isVisible())
				return true;
		}
		return false;
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
		emitOnInserted(index);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible != this.isVisible()){
			super.setVisible(visible);
			for (PointCloud cl : clouds) {
				cl.setVisible(visible);
			}
			
			emitOnChange();
		}
	}

	@Override
	public int getSize() {
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
		emitOnRemove(index);
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
		Point point = null;
		
		for (PointCloud c : clouds)
			if(c.isVisible() && c.countPoints() != 0) {
				point = c.getXMax();
				break;
			}
		
		if(point == null)
			xMax = null;
		
		for (PointCloud c : clouds) {
			if(!c.isVisible() || c.getXMax() == null)
				continue;
			
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
		Point point = null;
		
		for (PointCloud c : clouds)
			if(c.isVisible() && c.countPoints() != 0) {
				point = c.getYMax();
				break;
			}
		
		if(point == null)
			yMax = null;
		
		for (PointCloud c : clouds) {
			if(!c.isVisible() || c.getYMax() == null)
				continue;
			
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
		Point point = null;
		for (PointCloud c : clouds)
			if(c.isVisible() && c.countPoints() != 0) {
				point = c.getXMin();
				break;
			}
		
		if(point == null)
			xMin = null;
		
		for (PointCloud c : clouds) {
			if(!c.isVisible() || c.getXMin() == null)
				continue;
			
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
		Point point = null;
		for (PointCloud c : clouds)
			if(c.isVisible() && c.countPoints() != 0) {
				point = c.getYMin();
				break;
			}
		
		if(point == null)
			yMin = null;
		
		for (PointCloud c : clouds) {
			if(!c.isVisible() || c.getYMin() == null)
				continue;
			
			if (c.getYMin().getY() < point.getY())
				point = c.getYMin();
		}
		yMin = point;
	}
	
	/**
	 * Recherche du min et du max
	 */
	protected synchronized void checkMinMax () {
		checkXMax();
		checkXMin();
		checkYMax();
		checkYMin();
	}
	
	@Override
	protected synchronized void emitOnChange () {
		for (LineChartModelListener ls : listeners) {
			ls.onChange(this);
		}
	}
	
	protected synchronized void emitOnInserted (int index) {
		checkMinMax();
		for (LineChartModelListener ls : listeners) {
			ls.onInsertCloud(this, index);
		}
	}
	
	protected synchronized void emitOnRemove (int index) {
		checkMinMax();
		for (LineChartModelListener ls : listeners) {
			ls.onRemoveCloud(this, index);
		}
	}

}
