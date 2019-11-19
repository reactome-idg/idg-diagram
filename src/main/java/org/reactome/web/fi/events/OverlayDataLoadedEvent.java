package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDataLoadedEvent extends GwtEvent<OverlayDataLoadedHandler> {
    public static Type<OverlayDataLoadedHandler> TYPE = new Type<>();

    private DataOverlay dataOverlay;
    
    public OverlayDataLoadedEvent(DataOverlay dataOverlay) {
    	this.dataOverlay = dataOverlay;
    }
	
	@Override
	public Type<OverlayDataLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OverlayDataLoadedHandler handler) {
		handler.onOverlayDataLoaded(this);
	}

	public DataOverlay getDataOverlay() {
		return dataOverlay;
	}

	@Override
	public String toString() {
		return "Target Level Data Loaded Event Fired!";
	}
}
