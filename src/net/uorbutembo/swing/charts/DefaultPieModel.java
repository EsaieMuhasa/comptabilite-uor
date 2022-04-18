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
public class DefaultPieModel implements PieModel, PiePartListener{
	
	protected final List<PiePart> parts;
	protected final List<PieModelListener> listeners = new ArrayList<>();
	protected double max;
	protected double realMax;
	protected String title;
	protected String suffix = "";
	protected int selectedIndex = -1;

	public DefaultPieModel() {
		parts = new ArrayList<>();
	}
	
	/**
	 * Constucteur d'initialisation de la valeur max du pie
	 * @param max
	 */
	public DefaultPieModel(double max) {
		this();
		this.max = max;
	}
	
	public DefaultPieModel(double max, String title) {
		this();
		this.max = max;
		this.title = title;
	}
	
	/**
	 * @param parts
	 */
	public DefaultPieModel(final List<PiePart> parts) {
		this.parts = parts;
		for (PiePart part : parts) {
			part.addListener(this);
		}
		this.calculRealMax();
		this.emitRefresh();
	}
	
	/**
	 * Ajoute les items du model en parametre a son model des parts
	 * @param model
	 */
	public void bind (DefaultPieModel model) {
		this.addParts(model.getParts());
	}
	
	@Override
	public synchronized void setSelectedIndex (int index)  throws IndexOutOfBoundsException{
		if(index != -1 && (index >= getCountPart() || index < 0))
			throw new IndexOutOfBoundsException("Index out of bound : "+index);
		
		if(selectedIndex == index)
			return;
		
		int old = selectedIndex;
		selectedIndex = index;
		for (PieModelListener ls : listeners) {
			ls.onSelectedIndex(this, old, selectedIndex);
		}		
	}

	@Override
	public int getSelectedIndex() {
		return selectedIndex;
	}

	@Override
	public PiePart getPartByName(String name) {
		for (PiePart part : parts) {
			if(part.getName().equals(name)) 
				return part;
		}
		return null;
	}

	@Override
	public String getTitle() {
		return title;
	}
	
	@Override
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @param suffix the suffix to set
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		for (PieModelListener listener : listeners) {
			listener.onTitleChange(this, title);
		}
	}

	@Override
	public void addPart(PiePart part) {
		if(this.parts.contains(part))
			return;
		
		this.parts.add(part);
		part.addListener(this);
		this.calculRealMax();
		this.emitRefresh();
	}

	@Override
	public void addPart(int index, PiePart part) {
		if(this.parts.contains(part))
			return;
		
		this.parts.add(index, part);
		part.addListener(this);
		this.calculRealMax();
		this.emitRefresh();
	}

	@Override
	public void addParts(PiePart... parts) {
		for (PiePart part : parts) {
			if(!this.parts.contains(part)){
				this.parts.add(part);
				part.addListener(this);
			}
		}
		this.calculRealMax();
		this.emitRefresh();
	}

	@Override
	public double getMax() {
		return max;
	}
	
	@Override
	public double getRealMax() {
		return realMax;
	}
	
	@Override
	public void setMax(double max) {
		if(this.max != max) {
			this.max = max;
			this.emitRefresh();
		}
	}

	@Override
	public synchronized void removePartAt(int index) {
		this.parts.get(index).removeListener(this);
		this.parts.remove(index);
		if(index == selectedIndex)
			setSelectedIndex(-1);
		
		if(index < selectedIndex)
			selectedIndex -= 1;
		calculRealMax();
		this.emitRefresh();
	}

	@Override
	public synchronized void removeAll() {
		for (PiePart part : parts) {
			part.removeListener(this);
		}
		this.parts.clear();
		selectedIndex = -1;
		calculRealMax();
		this.emitRefresh();
	}

	@Override
	public PiePart getPartAt(int index) {
		return parts.get(index);
	}
	
	@Override
	public  PiePart findByData(Object data) {
		if(data == null)
			return null;
		
		for (PiePart part : parts) {
			if(part == data || part.getData() == data)
				return part;
		}
		return null;
	}

	@Override
	public int getCountPart() {
		return parts.size();
	}

	@Override
	public synchronized double getPercentOf(int index) {
		double m = (this.realMax <= this.max )? this.max : this.realMax;
		if(m == 0 )
			return 0;
		return ((this.getPartAt(index).getValue() * 100.0)/m);
	}

	@Override
	public synchronized double getPercentOf(PiePart part) {
		if (max == 0)
			return 0;
		if(this.parts.contains(part)) {
			double m = (this.realMax <= this.max )? this.max : this.realMax;
			return ((part.getValue() * 100.0)/m);
		}
		return 0;
	}

	@Override
	public PiePart [] getParts() {
		int count = parts.size();
		PiePart [] data = new PiePart[count];
		for (int i = 0; i<count; i++) {
			data[i] = parts.get(i);
		}
		return data;
	}
	
	@Override
	public int indexOf (PiePart part) {
		return parts.indexOf(part);
	}
	
	@Override
	public boolean removeByData(Object data) {
		PiePart part = findByData(data);
		if(part != null) {
			int index = indexOf(part);
			if(index != -1){
				removePartAt(index);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removePart(PiePart part) {
		int index = indexOf(part);
		if(index != -1) {
			removePartAt(index);
			return true;
		}
		return false;
	}

	protected synchronized void emitRefresh () {
		for (PieModelListener listener : listeners) {
			listener.refresh(this);
		}
	}
	
	protected synchronized void emitRepaint (PiePart part) {
		int index =  this.parts.indexOf(part);
		for (PieModelListener listener : listeners) {
			listener.repaintPart(this, index);
		}
	}

	protected synchronized void calculRealMax () {
		this.realMax = 0;
		for (PiePart part : parts) {
			this.realMax += part.getValue();
		}
	}

	@Override
	public synchronized void addListener(PieModelListener listener) {
		if(!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			listener.refresh(this);
		}
	}

	@Override
	public synchronized void removeListener(PieModelListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public synchronized void onChange(PiePart part) {
		this.calculRealMax();
		this.emitRepaint(part);
	}

}
