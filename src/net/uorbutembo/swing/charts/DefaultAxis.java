/**
 * 
 */
package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Esaie MUHASA
 *
 */
public class DefaultAxis extends AbstractChartData implements Axis {
	
	/**
	 * @author Esaie MUHASA
	 * Type des axes prise en charge
	 */
	public enum AxisType {
		LINEAR,
		LOGARITHMIC
	};
	
	
	protected double step = 1;
	protected double ration = 1;
	protected AxisType type;
	protected String name;
	protected String shortName;
	protected boolean responsive = true;
	protected String measureUnit = "";
	
	protected double min = Double.NEGATIVE_INFINITY;
	protected double max = Double.POSITIVE_INFINITY;
	
	protected final List<AxisGraduation> graduations = new ArrayList<>();
	protected final List<AxisListener> listeners = new ArrayList<>();
	
	protected final DefaultAxisGraduation zero = new DefaultAxisGraduation(0);
	protected AxisGraduation first = zero;
	protected AxisGraduation last = zero;//derniere graduation a l'heure actuel
	
	protected ChartDataRenderedListener graduationListener = new ChartDataRenderedAdapter() {
		@Override
		public void onChange(ChartData source) {
			emitOnChange();
		};
	};
	
	{
		graduations.add(zero);
		first = zero;
		last = zero;
		zero.addRenderedListener(graduationListener);
	}

	/**
	 * 
	 */
	public DefaultAxis() {
		super();
		step = 1;
		type = AxisType.LINEAR;
	}

	/**
	 * @param step
	 * @param name
	 * @param shortName
	 */
	public DefaultAxis(int step, String name, String shortName) {
		super();
		this.step = step;
		this.name = name;
		this.shortName = shortName;
		type = AxisType.LINEAR;
	}

	/**
	 * @param backgroundColor
	 */
	public DefaultAxis(Color backgroundColor) {
		super(backgroundColor);
		step = 1;
	}

	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param borderColor
	 */
	public DefaultAxis(Color backgroundColor, Color foregroundColor, Color borderColor) {
		super(backgroundColor, foregroundColor, borderColor);
		step = 1;
	}
	
	/**
	 * @return the measureUnit
	 */
	@Override
	public String getMeasureUnit() {
		return measureUnit;
	}

	/**
	 * @param measureUnit the measureUnit to set
	 */
	public void setMeasureUnit(String measureUnit) {
		if(measureUnit == this.measureUnit)
			return;
		
		this.measureUnit = measureUnit;
		emitOnChange();
	}

	@Override
	public void setInterval (double min, double max) {
		if (this.min == min && this.max == max)
			return;
		
		this.min = min;
		this.max = max;
		
		clear();
		emitOnChange();
	}

	@Override
	public void setMin(double min) {
		if(this.min == min)
			return;
		
		this.min = min;
		graduations.clear();
		
		DefaultAxisGraduation gr = new DefaultAxisGraduation(min != Double.NEGATIVE_INFINITY? min : 0, zero);
		graduations.add(gr);
		first = graduations.get(0);
		last = graduations.get(0);
		
		emitOnChange();
	}

	@Override
	public double getMin() {
		return min;
	}
	
	@Override
	public boolean isResponsive() {
		return responsive;
	}

	/**
	 * @param responsive the responsive to set
	 */
	public void setResponsive (boolean responsive) {
		if(this.responsive == responsive)
			return;
			
		this.responsive = responsive;
		emitOnChange();
	}
	
	/**
	 * desactivation de la respostivite du step
	 * @param step
	 */
	public void disableResponsive (double step ) {
		this.step = step;
		setResponsive(false);
	}

	@Override
	public void setMax(double max) {
		if(this.max == max)
			return;
		
		this.max = max;
		DefaultAxisGraduation gr = new DefaultAxisGraduation(min != Double.NEGATIVE_INFINITY? min : 0, zero);
		graduations.add(gr);
		first = graduations.get(0);
		last = graduations.get(0);
		
		emitOnChange();
	}

	@Override
	public double getMax() {
		return max;
	}

	@Override
	public synchronized AxisGraduation getAfter (AxisGraduation graduation) throws IndexOutOfBoundsException{
		if(graduation == getLast()) {
			double next = (graduation.getValue()) + (getStep()) ;
			if(next == Double.POSITIVE_INFINITY)
				throw new IndexOutOfBoundsException("Impossible de creer une nouvelle graduation: Max = "+getMax()+" => "+getName()+" => "+next);
			
			DefaultAxisGraduation g = new DefaultAxisGraduation(next, graduation);
			graduations.add(g);
			last = g;
			g.addRenderedListener(graduationListener);
			emitGraduationInserted(graduations.size()-1);
		}
		return graduations.get(indexOf(graduation)+1);
	}

	@Override
	public AxisGraduation getBefor (AxisGraduation graduation) throws IndexOutOfBoundsException{
		if(graduation == getFirst()) {
			double prev = graduation.getValue() -  getStep();
			if(prev == Double.NEGATIVE_INFINITY)
				throw new IndexOutOfBoundsException("Impossible de creer une nouvelle graduation: "+prev);
			
			DefaultAxisGraduation g = new DefaultAxisGraduation(prev, graduation);
			graduations.add(0, g);
			first = g;
			g.addRenderedListener(graduationListener);
			emitGraduationInserted(0);
		}
		return graduations.get(indexOf(graduation)-1);
	}

	@Override
	public boolean checkBefor(AxisGraduation graduation) {
		double prev = graduation.getValue() - getStep();
		return (prev != Double.NEGATIVE_INFINITY? (prev >= min - getStep()*2) : true);
	}

	@Override
	public boolean checkAfter(AxisGraduation graduation) {
		double next = graduation.getValue()+getStep();
		return (next != Double.POSITIVE_INFINITY? (next <= max + getStep()*2) : true);
	}

	@Override
	public AxisGraduation getFirst() {
		return first;
	}

	@Override
	public AxisGraduation getLast() {
		return last;
	}

	@Override
	public AxisGraduation getByValue (double value) {
		if (value < graduations.get(0).getValue())
			goTo(value, false);
		
		if (value > graduations.get(graduations.size()-1).getValue())
			goTo(value, true);
		
		AxisGraduation old = graduations.get(0);
		for (int i = 0, count = graduations.size(); i < count; i++) {
			AxisGraduation now = graduations.get(i);
			if (value == now.getValue())
				return now;
			
			if(old.getValue() < value &&  value < now.getValue()) {
				int index = i + 1;
				DefaultAxisGraduation $new = new DefaultAxisGraduation(value, now);
				$new.setVisible(false);
				graduations.add(index, $new);
				$new.addRenderedListener(graduationListener);
				emitGraduationInserted(index);
				return $new;
			}
			
			old = now;		
		}
			
		throw new RuntimeException("Impossible de determiner la graduation de "+value);
	}
	
	/**
	 * generation des graduations jusqu'a une valeur x
	 * @param value
	 * @param adding les valeurs doit-elle etre ajouter positivement? 
	 */
	protected synchronized void goTo (double value, boolean adding) {
		boolean run = true;
		if (adding)
			do {
				AxisGraduation g = getAfter(last);
				run = g.getValue() > value;
			} while (run);
		else 
			do {
				AxisGraduation g = getBefor(first);
				run = g.getValue() < value;
			} while (run);
	}
	
	@Override
	public int indexOf (AxisGraduation graduation) {
		return graduations.indexOf(graduation);
	}
	
	@Override
	public double getPixelPlacement(AxisGraduation graduation) {
		return graduation.getValue() * ration;
	}
	
	@Override
	public AxisGraduation getAt(int index) throws IndexOutOfBoundsException {
		return graduations.get(index);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getShortName() {
		return shortName;
	}

	@Override
	public double getStep() {
		return step;
	}

	@Override
	public double getRation() {
		return ration;
	}

	@Override
	public void addListener(AxisListener listener) {
		if(listener != null && !listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public boolean removeListener(AxisListener listener) {
		return listeners.remove(listener);
	}
	
	/**
	 * @return the type
	 */
	public AxisType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(AxisType type) {
		if(type == this.type)
			return;
		
		this.type = type;
		emitOnChange();
	}

	@Override
	public void setStep (double step) {
		if(this.step == step)
			return;
		
		this.step = step;
		
		clear();
		
		emitOnChange();
	}

	/**
	 * @param ration the ration to set
	 */
	public void setRation (double ration) {
		if(this.ration == ration)
			return;
		
		this.ration = ration;
		emitOnChange();
	}
	
	@Override
	public double toRation(double value) {
		return value * ration;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if(this.name == name)
			return;
		
		this.name = name;
		emitOnChange();
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		if(this.shortName == shortName)
			return;
		
		this.shortName = shortName;
		emitOnChange();
	}
	
	@Override
	public void clear() {
		
		for (AxisGraduation ag : graduations)
			ag.removeRenderedListener(graduationListener);
		
		graduations.clear();
		if (min != Double.NEGATIVE_INFINITY)
			graduations.add(new DefaultAxisGraduation(min, zero));
		else
			graduations.add(zero);
		
		graduations.get(0).addRenderedListener(graduationListener);
		first = graduations.get(0);
		last = graduations.get(0);
	}

	/**
	 * emission de l'insersion d'une nouvelle gaduation
	 * @param index
	 */
	protected synchronized void emitGraduationInserted (int index) {
		for (AxisListener ls : listeners) {
			ls.graduationInserted(this, index);
		}
	}
	
	protected synchronized void emitOnChange () {
		for (AxisListener ls : listeners) {
			ls.onChange(this);
		}
	}
	
	

}
