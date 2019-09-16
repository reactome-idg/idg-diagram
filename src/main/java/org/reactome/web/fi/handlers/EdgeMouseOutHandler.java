package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.EdgeMouseOutEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface EdgeMouseOutHandler extends EventHandler{
	void onEdgeMouseOut(EdgeMouseOutEvent event);
}
