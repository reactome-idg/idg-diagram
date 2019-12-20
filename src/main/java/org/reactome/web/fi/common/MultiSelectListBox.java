package org.reactome.web.fi.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author brunsont
 *
 */
public class MultiSelectListBox extends ListBox{

	private List<String> totalItems;
	private List<String> selectedItems;
	
	public MultiSelectListBox() {
	}	

	public List<String> getSelectedItemsText(){
		List<String> result = new ArrayList<>();
		for(int i=0; i<getItemCount(); i++) {
			if(isItemSelected(i))
				result.add(this.getItemText(i));
		}
		return result;
	}

	public List<Integer> getSelectedItemsIndexs(){
		List<Integer> result = new ArrayList<>();
		for(int i=0; i<getItemCount(); i++) {
			if(isItemSelected(i))
				result.add(i);
		}
		return result;
	}
	
	public void filter(String filter) {
		if(totalItems == null) return;
		this.clear();
		for(String item : totalItems)
			if(item.contains(filter))
				this.addItem(item);
		
		resetSelected();
	}
	
	private void resetSelected() {
		for(int i=0; i<this.getItemCount(); i++) {
			if(selectedItems.contains(this.getItemText(i)))
				this.setItemSelected(i, true);
		}
	}

	public void setListItems(List<String> items) {
		this.totalItems = items;
		
		for(String item : items)
			this.addItem(item);
	}
	
	
}
