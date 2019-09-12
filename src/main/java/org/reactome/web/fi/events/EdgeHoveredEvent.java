package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.EdgeHoveredHandler;

import com.google.gwt.event.shared.GwtEvent;

public class EdgeHoveredEvent extends GwtEvent<EdgeHoveredHandler>{
    public static Type<EdgeHoveredHandler> TYPE = new Type<>();

    String interactionDirection;
    
    public EdgeHoveredEvent(String interactionDirection) {
    	this.interactionDirection = interactionDirection;
    }
    
	@Override
	public Type<EdgeHoveredHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EdgeHoveredHandler handler) {
		handler.onEdgeHovered(this);
	}
	
	public String getInteractionDirection() {
		return interactionDirection;
	}

	@Override
	public String toString() {
		return "EdgeClickedEvent{" +
                "content=" + getInteractionDirection() + "}";
	}
}
