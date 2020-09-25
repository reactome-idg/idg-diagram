package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.PairwiseOverlayButtonClickedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface PairwiseOverlayButtonClickedHandler extends EventHandler{
	void onPairwiseOverlayButtonClicked(PairwiseOverlayButtonClickedEvent event);
}
