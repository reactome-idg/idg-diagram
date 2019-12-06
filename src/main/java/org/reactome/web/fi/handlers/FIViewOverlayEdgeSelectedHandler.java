package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.FIViewOverlayEdgeSelectedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface FIViewOverlayEdgeSelectedHandler extends EventHandler {
	void onFIViewOverlayEdgeSelected(FIViewOverlayEdgeSelectedEvent event);
}
