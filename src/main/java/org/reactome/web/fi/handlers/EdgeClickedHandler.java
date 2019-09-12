package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.EdgeClickedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface EdgeClickedHandler extends EventHandler {
	void onEdgeClicked(EdgeClickedEvent event);
}
