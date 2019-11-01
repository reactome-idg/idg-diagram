package org.reactome.web.fi.events;

import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.handlers.OverlayDataLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDataLoadedEvent extends GwtEvent<OverlayDataLoadedHandler> {
    public static Type<OverlayDataLoadedHandler> TYPE = new Type<>();

    private OverlayEntities entities;
    
    public OverlayDataLoadedEvent(OverlayEntities entities) {
    	this.entities = entities;
    }
	
	@Override
	public Type<OverlayDataLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OverlayDataLoadedHandler handler) {
		handler.onOverlayDataLoaded(this);
	}
	
	public OverlayEntities getEntities() {
		return entities;
	}

	@Override
	public String toString() {
		return "Target Level Data Loaded Event Fired!";
	}
}
