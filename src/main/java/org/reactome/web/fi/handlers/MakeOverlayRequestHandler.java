package org.reactome.web.fi.handlers;

import org.reactome.web.fi.events.MakeOverlayRequestEvent;
import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author brunsont
 *
 */
public interface MakeOverlayRequestHandler extends EventHandler {
	void onMakeOverlayRequest(MakeOverlayRequestEvent event);
}
