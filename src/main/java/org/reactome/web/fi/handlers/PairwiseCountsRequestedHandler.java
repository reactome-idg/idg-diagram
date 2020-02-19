package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.PairwiseCountsRequestedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseCountsRequestedHandler extends EventHandler{
	void onPairwiseCountsRequested(PairwiseCountsRequestedEvent event);
}
