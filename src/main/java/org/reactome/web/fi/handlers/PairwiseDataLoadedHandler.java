package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.PairwiseDataLoadedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface PairwiseDataLoadedHandler extends EventHandler{
	void onPairwisieDataLoaded(PairwiseDataLoadedEvent event);
}
