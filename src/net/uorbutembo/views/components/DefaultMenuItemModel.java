package net.uorbutembo.views.components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

public class DefaultMenuItemModel <T> implements MenuItemModel<T> {
	
	private Icon icon;
	private String name;
	private String label;
	private List<T> items = new ArrayList<>();
	
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
	
	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public Icon getIcon() {
        return icon;
    }
	
	@Override
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

	@Override
    public String getName() {
        return name;
    }

	@Override
    public void setName(String name) {
        this.name = name;
    }

	@Override
    public List<T> getItems() {
        return this.items;
    }

	@Override
    public void setItems(List<T> items) {
        this.items = items;
    }
    
	@Override
    public int countItems () {
    	return this.items.size();
    }
    
	@Override
    public MenuItemModel<T> addItem (T item , int index) {
    	this.items.add(index, item);
    	return this;
    }
    
	@Override
    public MenuItemModel<T> addItem (T item) {
    	this.items.add(item);
    	return this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
	public void addItems (T... items) {
    	for (T t : items) {
			this.items.add(t);
		}
    }
}
