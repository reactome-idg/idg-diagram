package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.SearchFINodesEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface SearchFINodesHandler extends EventHandler{
	void onSearchFINodes(SearchFINodesEvent event);
}
