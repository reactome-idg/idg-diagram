package org.reactome.web.fi.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author brunsont
 *
 */
public class IDGListBox extends ListBox{

	private List<String> totalItems;
	private Set<String> selectedItems;
	
	public IDGListBox() {
		totalItems = new ArrayList<>();
		selectedItems = new HashSet<>();
		this.addChangeHandler(e -> onChange(e));
	}	

	/**
	 * Manipulates selectedItems variable every time an item is selected or de-selected.
	 * This means that even with the filter function manipulating the items in the list box, 
	 * user selected items stay selected.
	 * 
	 * @param e
	 */
	private void onChange(ChangeEvent e) {
		//add all selected items to selectedItems set if not already present
		List<String> add = new ArrayList<>();
		for(int i=0; i<getItemCount(); i++) {
			if(isItemSelected(i))
				add.add(this.getItemText(i));
		}
		selectedItems.addAll(add);
		
		//Functionally removes an item if it is unselected by trying to removed all items from selectedItems that aren't currently selected in view
		List<String> remove = new ArrayList<>();
		for(int i=0; i<getItemCount(); i++) {
			if(!isItemSelected(i))
				remove.add(this.getItemText(i));
		}
		selectedItems.removeAll(remove);
	}

	/**
	 * Method returns a list of all user selected items regardless of filter state.
	 * @return
	 */
	public List<String> getSelectedItemsText(){
		return selectedItems.stream().collect(Collectors.toList());
	}
	
	/**
	 * Filteres IDGListBox items based on a passed in String.
	 * @param filter
	 */
	public void filter(String filter) {
		if(totalItems == null) return;
		
		this.clear();
		for(String item : totalItems)
			if(item.toLowerCase().contains(filter.toLowerCase()))
				this.addItem(item);
		
		resetSelected();
	}
	
	/**
	 * Makes sure all user selected items are selected in gui after a filter event is complete
	 */
	private void resetSelected() {
		for(int i=0; i<this.getItemCount(); i++) {
			if(selectedItems.contains(this.getItemText(i)))
				this.setItemSelected(i, true);
		}
	}

	/**
	 * Call this function to add a List of string items to IDGListBox.
	 * Adding items 1-by-1 will break filter and getSelectedItemsText functionality.
	 * @param items
	 */
	public void setListItems(List<String> items) {
		this.totalItems = items;
		selectedItems.clear();
		
		for(String item : items)
			this.addItem(item);
	}
	
	/**
	 * Allows for adding a Set of items instead of a list
	 * @param items
	 */
	public void setListItems(Set<String> items) {
		setListItems(items.stream().collect(Collectors.toList()));
	}
	
	@Override
	public void clear() {
		super.clear();
		this.totalItems.clear();
		this.selectedItems.clear();
	}
}
