package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.NoFIsAvailableEvent;

import com.google.gwt.event.shared.EventHandler;

public interface NoFIsAvailableHandler extends EventHandler{
	void onNoFIsAvailable(NoFIsAvailableEvent event);
}
