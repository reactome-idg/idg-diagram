package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.NodeClickedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface NodeClickedHandler extends EventHandler {
	void onNodeClicked(NodeClickedEvent event);
}
