package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.OverlayDataResetEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface OverlayDataResetHandler extends EventHandler{
	void onOverlayDataReset(OverlayDataResetEvent event);
	
}
