package org.reactome.web.idg.handlers;

import org.reactome.web.idg.events.EdgeClickedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface EdgeClickedHandler extends EventHandler {
	void onEdgeClicked(EdgeClickedEvent event);
}
