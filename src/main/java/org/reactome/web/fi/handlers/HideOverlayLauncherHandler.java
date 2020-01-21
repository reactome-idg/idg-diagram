package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.HideOverlayLauncherEvent;

import com.google.gwt.event.shared.EventHandler;

public interface HideOverlayLauncherHandler extends EventHandler{
	void onHideOverlayLauncher(HideOverlayLauncherEvent event);
}
