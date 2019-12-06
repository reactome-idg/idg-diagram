package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.handlers.FIViewOverlayEdgeHoveredHandler;

import com.google.gwt.event.shared.GwtEvent;

public class FIViewOverlayEdgeHoveredEvent extends GwtEvent<FIViewOverlayEdgeHoveredHandler>{
    public static Type<FIViewOverlayEdgeHoveredHandler> TYPE = new Type<>();

	private List<Double> expression;
	
	public FIViewOverlayEdgeHoveredEvent(List<Double> expression) {
		this.expression = expression;
	}
    
	@Override
	public Type<FIViewOverlayEdgeHoveredHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FIViewOverlayEdgeHoveredHandler handler) {
		handler.onFIViewOverlayEdgeHovered(this);
	}
	
	public List<Double> getExpression(){
		return this.expression;
	}

	@Override
	public String toString() {
		return "FIView Overlay Edge Hovered Event Fired!";
	}
}
