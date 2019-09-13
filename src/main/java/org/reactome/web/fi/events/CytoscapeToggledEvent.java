package org.reactome.web.fi.events;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.fi.client.flag.CytoscapeViewFlag;
import org.reactome.web.fi.handlers.CytoscapeToggledHandler;

import com.google.gwt.event.shared.GwtEvent;

public class CytoscapeToggledEvent extends GwtEvent<CytoscapeToggledHandler>{
    public static Type<CytoscapeToggledHandler> TYPE = new Type<>();

    private Context context;
    
    public CytoscapeToggledEvent(Context context) {
    	this.context = context;
    }
    
	@Override
	public Type<CytoscapeToggledHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CytoscapeToggledHandler handler) {
		handler.onCytoscapeToggled(this);
	}

	public Context getContext() {
		return this.context;
	}
	
	@Override
	public String toString() {
		return "Cytoscape toggled to: " + CytoscapeViewFlag.isCytoscapeViewFlag();
	}
	
}
