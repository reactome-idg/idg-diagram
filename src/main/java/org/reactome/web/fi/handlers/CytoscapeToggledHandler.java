package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.CytoscapeToggledEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface CytoscapeToggledHandler extends EventHandler{
	void onCytoscapeToggled(CytoscapeToggledEvent event);
}
