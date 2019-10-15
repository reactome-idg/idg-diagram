package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.TargetLevelDataRequestedEvent;

import com.google.gwt.event.shared.EventHandler;


public interface TargetLevelDataRequestedHandler extends EventHandler {
	void onTargetLevelDataRequested(TargetLevelDataRequestedEvent event);
}
