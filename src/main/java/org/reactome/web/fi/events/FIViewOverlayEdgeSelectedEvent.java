package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.handlers.FIViewOverlayEdgeSelectedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class FIViewOverlayEdgeSelectedEvent extends GwtEvent<FIViewOverlayEdgeSelectedHandler>{
    public static Type<FIViewOverlayEdgeSelectedHandler> TYPE = new Type<>();

    private List<Double> expression;
    
    public FIViewOverlayEdgeSelectedEvent(List<Double> expression) {
    	this.expression = expression;
    }
    
	@Override
	public Type<FIViewOverlayEdgeSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FIViewOverlayEdgeSelectedHandler handler) {
		handler.onFIViewOverlayEdgeSelected(this);
	}
	
	public List<Double> getExpression(){
		return this.expression;
	}
	
	@Override 
	public String toString() {
		return "FIView Overlay Edge Selected Event Fired!";
	}

}
