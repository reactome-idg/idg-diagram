package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.SetFIFlagDataDescsEvent;

import com.google.gwt.event.shared.EventHandler;

public interface SetFIFlagDataDescsHandler extends EventHandler{
	void onSetFIFlagDataDescs(SetFIFlagDataDescsEvent event);
}
