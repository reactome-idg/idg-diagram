package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.DrugTargetsLoadedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface DrugTargetsLoadedHandler extends EventHandler{
	void onDrugTargetsLoaded(DrugTargetsLoadedEvent event);
}
