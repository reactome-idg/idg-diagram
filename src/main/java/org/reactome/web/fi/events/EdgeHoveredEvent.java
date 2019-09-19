package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.EdgeHoveredHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class EdgeHoveredEvent extends GwtEvent<EdgeHoveredHandler>{
    public static Type<EdgeHoveredHandler> TYPE = new Type<>();

    private String interactionDirection;
    private	String reactomeSources;
    
    public EdgeHoveredEvent(String interactionDirection, String reactomeSources) {
    	this.interactionDirection = interactionDirection;
    	this.reactomeSources = reactomeSources;
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
	
	public String getReactomeSources() {
		return reactomeSources;
	}

	@Override
	public String toString() {
		return "EdgeClickedEvent{" +
                "content=" + getInteractionDirection() + "}";
	}
}
