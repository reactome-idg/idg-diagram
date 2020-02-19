package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.RequestPairwiseCountsEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface RequestPairwiseCountsHandler extends EventHandler{
	void onRequestPairwiseCountsHandeler(RequestPairwiseCountsEvent event);
}
