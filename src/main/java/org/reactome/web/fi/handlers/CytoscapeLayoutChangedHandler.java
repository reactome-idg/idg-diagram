package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.CytoscapeLayoutChangedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface CytoscapeLayoutChangedHandler extends EventHandler{
	void onCytoscapeLayoutChanged(CytoscapeLayoutChangedEvent event);
}
