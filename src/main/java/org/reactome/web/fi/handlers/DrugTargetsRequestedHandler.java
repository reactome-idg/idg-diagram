package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.DrugTargetsRequestedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface DrugTargetsRequestedHandler extends EventHandler{
	void onDrugTargetsRequested(DrugTargetsRequestedEvent event);
}
