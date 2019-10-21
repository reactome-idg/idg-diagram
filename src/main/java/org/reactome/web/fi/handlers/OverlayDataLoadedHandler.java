package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.OverlayDataLoadedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayDataLoadedHandler extends EventHandler {
	void onTargetLevelDataLoaded(OverlayDataLoadedEvent event);
}
