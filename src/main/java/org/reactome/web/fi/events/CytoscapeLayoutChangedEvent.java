package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.CytoscapeLayoutChangedHandler;
import org.reactome.web.fi.model.FILayoutType;

import com.google.gwt.event.shared.GwtEvent;

public class CytoscapeLayoutChangedEvent extends GwtEvent<CytoscapeLayoutChangedHandler> {
    public static Type<CytoscapeLayoutChangedHandler> TYPE = new Type<>();

    private FILayoutType type;
    
    public CytoscapeLayoutChangedEvent(FILayoutType type) {
    	this.type = type;
    }
    
	@Override
	public Type<CytoscapeLayoutChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CytoscapeLayoutChangedHandler handler) {
		handler.onCytoscapeLayoutChanged(this);
		
	}
	
	public FILayoutType getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return "Cytoscape layout changed to: " + getType();
	}

}
