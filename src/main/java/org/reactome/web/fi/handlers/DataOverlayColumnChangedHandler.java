package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.DataOverlayColumnChangedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface DataOverlayColumnChangedHandler extends EventHandler{
	void onDataOverlayColumnChanged(DataOverlayColumnChangedEvent event);
}
