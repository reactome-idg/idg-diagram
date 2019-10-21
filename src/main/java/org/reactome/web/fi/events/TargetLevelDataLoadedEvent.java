package org.reactome.web.fi.events;

import org.reactome.web.fi.data.tcrd.tagetlevel.RawTargetLevelEntities;
import org.reactome.web.fi.handlers.TargetLevelDataLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class TargetLevelDataLoadedEvent extends GwtEvent<TargetLevelDataLoadedHandler> {
    public static Type<TargetLevelDataLoadedHandler> TYPE = new Type<>();

    private RawTargetLevelEntities entities;
    
    public TargetLevelDataLoadedEvent(RawTargetLevelEntities entities) {
    	this.entities = entities;
    }
	
	@Override
	public Type<TargetLevelDataLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TargetLevelDataLoadedHandler handler) {
		handler.onTargetLevelDataLoaded(this);
	}
	
	public RawTargetLevelEntities getEntities() {
		return entities;
	}

	@Override
	public String toString() {
		return "Target Level Data Loaded Event Fired!";
	}
}
