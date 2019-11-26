package org.reactome.web.fi.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.ListBox;

/**
 * 
 * @author brunsont
 *
 */
public class MultiSelectListBox extends ListBox{

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
}
