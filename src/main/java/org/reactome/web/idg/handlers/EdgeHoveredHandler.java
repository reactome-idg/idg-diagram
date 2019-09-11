package org.reactome.web.idg.handlers;

import org.reactome.web.idg.events.EdgeHoveredEvent;

import com.google.gwt.event.shared.EventHandler;

public interface EdgeHoveredHandler extends EventHandler{
	void onEdgeHovered(EdgeHoveredEvent event);
}
