package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.FIViewOverlayEdgeHoveredEvent;

import com.google.gwt.event.shared.EventHandler;

public interface FIViewOverlayEdgeHoveredHandler extends EventHandler{
	void onFIViewOverlayEdgeHovered(FIViewOverlayEdgeHoveredEvent event);
}
