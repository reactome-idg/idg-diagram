package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.CytoscapeLayoutChangedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class CytoscapeLayoutChangedEvent extends GwtEvent<CytoscapeLayoutChangedHandler> {
    public static Type<CytoscapeLayoutChangedHandler> TYPE = new Type<>();

    private String selection;
    
    public CytoscapeLayoutChangedEvent(String selection) {
    	this.selection = selection;
    }
    
	@Override
	public Type<CytoscapeLayoutChangedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CytoscapeLayoutChangedHandler handler) {
		handler.onCytoscapeLayoutChanged(this);
		
	}
	
	public String getSelection() {
		return this.selection;
	}
	
	@Override
	public String toString() {
		return "Cytoscape layout changed to: " + getSelection();
	}

}
