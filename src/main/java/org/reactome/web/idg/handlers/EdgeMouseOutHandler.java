package org.reactome.web.idg.handlers;

import org.reactome.web.idg.events.EdgeMouseOutEvent;

import com.google.gwt.event.shared.EventHandler;

public interface EdgeMouseOutHandler extends EventHandler{
	void onEdgeMouseOut(EdgeMouseOutEvent event);
}
