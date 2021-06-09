package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.UpdateIDGFlagFDREvent;

import com.google.gwt.event.shared.EventHandler;

public interface UpdateIDGFlagFDRHandler extends EventHandler{
	void onUpdateIDGFlagFDR(UpdateIDGFlagFDREvent event);
}
