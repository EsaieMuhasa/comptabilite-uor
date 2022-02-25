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
	protected String title;

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
	
	/**
	 * @param parts
	 */
	public DefaultPieModel(final List<PiePart> parts) {
		this.parts = parts;
		for (PiePart part : parts) {
			part.addListener(this);
		}
		this.emitRefresh();
	}

	@Override
	public String getTitle() {
		return title;
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
		this.emitRefresh();
	}

	@Override
	public void addPart(int index, PiePart part) {
		if(this.parts.contains(part))
			return;
		
		this.parts.add(index, part);
		part.addListener(this);
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
		this.emitRefresh();
	}

	@Override
	public double getMax() {
		return max;
	}
	
	@Override
	public void setMax(double max) {
		if(this.max != max) {
			this.max = max;
			this.emitRefresh();
		}
	}

	@Override
	public void removePartAt(int index) {
		this.parts.get(index).removeListener(this);
		this.parts.remove(index);
		this.emitRefresh();
	}

	@Override
	public void removeAll() {
		for (PiePart part : parts) {
			part.removeListener(this);
		}
		this.parts.clear();
		this.emitRefresh();
	}

	@Override
	public List<PiePart> getParts() {
		return parts;
	}
	
	protected void emitRefresh () {
		for (PieModelListener listener : listeners) {
			listener.refresh(this);
		}
	}
	
	protected void emitRepaint (PiePart part) {
		int index =  this.parts.indexOf(part);
		for (PieModelListener listener : listeners) {
			listener.repaintPart(this, index);
		}
	}

	@Override
	public void addListener(PieModelListener listener) {
		if(!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			listener.refresh(this);
		}
	}

	@Override
	public void removeListener(PieModelListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void onChange(PiePart part) {
		this.emitRepaint(part);
	}

}
