package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.FIViewMessageEvent;

import com.google.gwt.event.shared.EventHandler;

public interface FIViewMessageHandler extends EventHandler {
	void onFIViewMessage(FIViewMessageEvent event);
}
