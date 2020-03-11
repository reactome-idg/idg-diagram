package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.PairwiseNumbersLoadedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseNumbersLoadedHandler extends EventHandler{
	void onPairwiseNumbersLoaded(PairwiseNumbersLoadedEvent event);
}
