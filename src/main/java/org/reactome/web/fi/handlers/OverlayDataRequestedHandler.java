package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.OverlayDataRequestedEvent;

import com.google.gwt.event.shared.EventHandler;


public interface OverlayDataRequestedHandler extends EventHandler {
	void onTargetLevelDataRequested(OverlayDataRequestedEvent event);
}
