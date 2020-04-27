package org.reactome.web.fi.events;

import java.util.Set;

import org.reactome.web.fi.handlers.SearchFINodesHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class SearchFINodesEvent extends GwtEvent<SearchFINodesHandler> {
	public static Type<SearchFINodesHandler> TYPE = new Type<>();
	
	private Set<String> searchItems;
	
	public SearchFINodesEvent(Set<String> searchItems) {
		this.searchItems = searchItems;
	}
	
	public Set<String> getSearchItems(){
		return searchItems;
	}
	
	@Override
	public Type<SearchFINodesHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchFINodesHandler handler) {
		handler.onSearchFINodes(this);
	}

	@Override
	public String toString() {
		return "Searching FIs for " + searchItems.toString();
	}
	
}
