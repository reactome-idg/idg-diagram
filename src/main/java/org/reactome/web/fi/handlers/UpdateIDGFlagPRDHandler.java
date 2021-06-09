package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.UpdateIDGFlagPRDEvent;

import com.google.gwt.event.shared.EventHandler;

public interface UpdateIDGFlagPRDHandler extends EventHandler{
	void onUpdateIDGFlagPRD(UpdateIDGFlagPRDEvent event);
}
