package net.uorbutembo.swing.charts;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultTimeAxis extends DefaultAxis implements TimeAxis {
	
	private DateFormat formater;
	
	protected void initZero (){
		long time = (long)(System.currentTimeMillis() - (60 * 60 * 1000 * 24));
		Date now = new Date(time);
		zero.setData(now);
		zero.setLabel(formater.format(now));
		System.out.println(zero);
	}

	/**
	 * 
	 */
	public DefaultTimeAxis() {
		super();
		formater = new SimpleDateFormat("dd/MM/yyyy");
		initZero();
	}

	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param borderColor
	 */
	public DefaultTimeAxis(Color backgroundColor, Color foregroundColor, Color borderColor) {
		super(backgroundColor, foregroundColor, borderColor);
		formater = new SimpleDateFormat("dd/MM/yyyy");
		initZero();
	}

	/**
	 * @param backgroundColor
	 */
	public DefaultTimeAxis(Color backgroundColor) {
		super(backgroundColor);
		formater = new SimpleDateFormat("dd/MM/yyyy");
		initZero();
	}

	/**
	 * @param formater
	 */
	public DefaultTimeAxis(DateFormat formater) {
		super();
		this.formater = formater;
		initZero();
	}

	/**
	 * @param step
	 * @param name
	 * @param shortName
	 */
	public DefaultTimeAxis(int step, String name, String shortName) {
		super(step, name, shortName);
		formater = new SimpleDateFormat("dd/MM/yyyy");
		initZero();
	}
	
	public DefaultTimeAxis(DateFormat formater, int step, String name, String shortName) {
		super(step, name, shortName);
		this.formater = formater;
		initZero();
	}

	@Override
	public void setFormater(DateFormat formater) {
		if(this.formater == formater)
			return;
		
		this.formater = formater;
		emitOnChange();
	}
	
	@Override
	public void setStep(double step) {
		super.setStep(step);
	}

	/**
	 * @return the formater
	 */
	public DateFormat getFormater() {
		return formater;
	}

	@Override
	public synchronized AxisGraduation getAfter(AxisGraduation graduation) throws IndexOutOfBoundsException {
		if(graduation == getLast()) {
			if(step == 0) {
				step = 1;
			}
			double next = graduation.getValue() + getStep()  ;
			if(next == Double.POSITIVE_INFINITY)
				throw new IndexOutOfBoundsException("Impossible de creer une nouvelle graduation: Max = "+getMax()+" => "+getName()+" => "+next);
			long time = (long)(System.currentTimeMillis() + (next * (60 * 60 * 1000 * 24)));
			Date date = new Date(time);
			DefaultAxisGraduation g = new DefaultAxisGraduation(formater.format(date), next, graduation);
			g.setData(date);
			graduations.add(g);
			last = g;
			emitGraduationInserted(graduations.size()-1);
		}
		return graduations.get(indexOf(graduation)+1);
	}

	@Override
	public synchronized AxisGraduation getBefor(AxisGraduation graduation) throws IndexOutOfBoundsException {
		if(graduation == getFirst()) {
			if(step == 0) {
				step = 1;
			}
			double prev = graduation.getValue() -  getStep();
			if(prev == Double.NEGATIVE_INFINITY)
				throw new IndexOutOfBoundsException("Impossible de creer une nouvelle graduation: "+prev);
			
			long time = (long)(System.currentTimeMillis() - (prev * (60 * 60 * 1000 * 24)));
			Date date = new Date(time);
			DefaultAxisGraduation g = new DefaultAxisGraduation(formater.format(date), prev, graduation);
			g.setData(date);
			graduations.add(0, g);
			first = g;
			emitGraduationInserted(0);
		}
		return graduations.get(indexOf(graduation)-1);
	}
	
	@Override
	public synchronized void clear() {
		for (AxisGraduation ag : graduations)
			ag.removeRenderedListener(graduationListener);
		
		graduations.clear();
		Date date = (min != Double.NEGATIVE_INFINITY)? new Date((long) (System.currentTimeMillis() + (min * 60 * 60 * 1000 * 24))) : (Date) zero.getData();
		graduations.add(new DefaultAxisGraduation(formater.format(date), min, zero));
		graduations.get(0).addRenderedListener(graduationListener);
		graduations.get(0).setData(date);
		first = graduations.get(0);
		last = graduations.get(0);
		System.out.println(date);
	}

	@Override
	public AxisGraduation getByValue(double value) {
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
				Date date = new Date((long)value);
				DefaultAxisGraduation $new = new DefaultAxisGraduation(formater.format(date), value, now);
				$new.setData(date);
				$new.setVisible(false);
				graduations.add(index, $new);
				emitGraduationInserted(index);
				return $new;
			}
			
			old = now;	
		}
			
		throw new RuntimeException("Impossible de determiner la graduation de "+value);
	}
	
	@Override
	protected synchronized void emitGraduationInserted(int index) {
		super.emitGraduationInserted(index);
//		System.out.println(graduations.get(index));
	}

}
