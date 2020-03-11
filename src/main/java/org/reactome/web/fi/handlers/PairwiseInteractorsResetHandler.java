package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.PairwiseInteractorsResetEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseInteractorsResetHandler extends EventHandler{
	void onPairwiseInteractorsReset(PairwiseInteractorsResetEvent event);
}
