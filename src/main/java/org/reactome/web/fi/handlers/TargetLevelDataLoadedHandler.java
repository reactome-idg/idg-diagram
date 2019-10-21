package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.TargetLevelDataLoadedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface TargetLevelDataLoadedHandler extends EventHandler {
	void onTargetLevelDataLoaded(TargetLevelDataLoadedEvent event);
}
