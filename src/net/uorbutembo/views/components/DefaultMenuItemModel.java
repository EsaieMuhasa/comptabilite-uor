package net.uorbutembo.views.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

public class DefaultMenuItemModel <T> implements MenuItemModel<T> {
	
	private Icon icon;
	private String name;
	private String label;
	private List<T> items = new ArrayList<>();
	private int selectedItem = -1;
	
	private final List<MenuItemModelListener> listeners = new ArrayList<>();
	
	@SafeVarargs
	public DefaultMenuItemModel (Icon icon, String label, T... items){
		this.icon = icon;
		this.label = label;
		this.name = label;
		for (T item : items) {			
			this.items.add(item);
		}
	}
	
	@SafeVarargs
	public DefaultMenuItemModel (Icon icon, String label, String name, T... items){
		this.icon = icon;
		this.label = label;
		this.name = name;
		for (T item : items) {			
			this.items.add(item);
		}
	}
	
	public DefaultMenuItemModel (Icon icon, String label, List<T> items){
		this.icon = icon;
		this.label = label;
		this.name = label;
		for (T item : items) {			
			this.items.add(item);
		}
	}
	
	public DefaultMenuItemModel (Icon icon, String label, String name, List<T> items){
		this.icon = icon;
		this.label = label;
		this.name = name;
		for (T item : items) {			
			this.items.add(item);
		}
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel (String label) {
		if(label == this.label)
			return;

		this.label = label;
		emitOnChange();
	}

	@Override
	public Icon getIcon() {
        return icon;
    }
	
	@Override
    public void setIcon(Icon icon) {
        this.icon = icon;
        emitOnChange();
    }

	@Override
    public String getName() {
        return name;
    }

	@Override
    public void setName(String name) {
        this.name = name;
    }

	@SuppressWarnings("unchecked")
	@Override
    public T[] getItems() {
		Object [] is = new Object[items.size()];
		for (int i = 0; i < items.size(); i++)
			is[i] = items.get(i);
        return (T[]) is;
    }

    
	@Override
    public int countItems () {
    	return this.items.size();
    }
    
	@Override
	public int getCurrentItem() {
		return selectedItem;
	}

	@Override
	public void setSelectedItem (int index) throws IndexOutOfBoundsException {
		if(index  > countItems())
			throw new IndexOutOfBoundsException("Index out of range: "+index);
		
		if(selectedItem == index)
			return;
		
		int old = this.selectedItem;
		
		selectedItem = index;
		if(old != -1)
			emitOnChange(old);
		
		if(selectedItem != -1)
			emitOnChange(index);

	}

	@Override
    public MenuItemModel<T> addItem (T item , int index) {
    	this.items.add(index, item);
    	emitOnChange();
    	return this;
    }
    
	@Override
    public MenuItemModel<T> addItem (T item) {
    	this.items.add(item);
    	emitOnChange();
    	return this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public void addItems (T... items) {
    	for (T t : items) 
			this.items.add(t);
    	emitOnChange();
    }

	@Override
	public void updateItem(T item, int index) {
		items.set(index, item);
		emitOnChange(index);
	}

	@Override
	public T getItem(int index) {
		return items.get(index);
	}

	@Override
	public void removeItem(int index) {
		items.remove(index);
		emitOnChange();
	}

	@Override
	public void addModelListener(MenuItemModelListener ls) {
		if(!listeners.contains(ls))
			listeners.add(ls);
	}

	@Override
	public void removeModelListener(MenuItemModelListener ls) {
		listeners.remove(ls);
	}
	
	/**
	 * informe tout les ecouteurs du changement du model
	 */
	protected void emitOnChange () {
		for (MenuItemModelListener ls : listeners)
			ls.onChange(this);
	}
	
	/**
	 * @param index
	 */
	protected void emitOnChange (int index) {
		for (MenuItemModelListener ls : listeners)
			ls.onChange(this, index);
	}

}
