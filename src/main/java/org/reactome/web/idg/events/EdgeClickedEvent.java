package org.reactome.web.idg.events;

import org.reactome.web.idg.handlers.EdgeClickedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class EdgeClickedEvent extends GwtEvent<EdgeClickedHandler> {
    public static Type<EdgeClickedHandler> TYPE = new Type<>();

    private String sourceName;
    private String targetName;
    private String direction;
    private String reactomeSources;
    
	public EdgeClickedEvent(String sourceName, String targetName, String direction, String reactomeSources) {
		this.sourceName = sourceName;
		this.targetName = targetName;
		this.direction = direction;
		this.reactomeSources = reactomeSources;
	}
    
	@Override
	public Type<EdgeClickedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EdgeClickedHandler handler) {
		handler.onEdgeClicked(this);
	}
	
	
	public String getSourceName() {
		return sourceName;
	}

	public String getTargetName() {
		return targetName;
	}

	public String getDirection() {
		return direction;
	}

	public String getReactomeSources() {
		return reactomeSources;
	}

	@Override
	public String toString() {
		return "EdgeClickedEvent{" +
                "content=" + 
				"source: " + getSourceName() +
				"direction: " + getDirection() +
				"target: " + getTargetName() + 
				"Reactome Sources: " + getReactomeSources() +
				"}";
	}

}
