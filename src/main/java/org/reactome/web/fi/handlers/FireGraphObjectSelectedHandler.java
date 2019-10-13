package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.FireGraphObjectSelectedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface FireGraphObjectSelectedHandler extends EventHandler{
	void onFireGraphObjectSelected(FireGraphObjectSelectedEvent event);
}
