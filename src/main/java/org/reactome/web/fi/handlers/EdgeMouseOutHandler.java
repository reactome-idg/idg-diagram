package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.EdgeMouseOutEvent;

import com.google.gwt.event.shared.EventHandler;

public interface EdgeMouseOutHandler extends EventHandler{
	void onEdgeMouseOut(EdgeMouseOutEvent event);
}
