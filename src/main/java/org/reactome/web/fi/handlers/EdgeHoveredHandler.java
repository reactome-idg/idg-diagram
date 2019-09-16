package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.EdgeHoveredEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface EdgeHoveredHandler extends EventHandler{
	void onEdgeHovered(EdgeHoveredEvent event);
}
