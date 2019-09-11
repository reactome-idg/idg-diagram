package org.reactome.web.idg.handlers;

import org.reactome.web.idg.events.NodeClickedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface NodeClickedHandler extends EventHandler {
	void onNodeClicked(NodeClickedEvent event);
}
