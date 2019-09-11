package org.reactome.web.idg.events;

import org.reactome.web.idg.handlers.EdgeMouseOutHandler;

import com.google.gwt.event.shared.GwtEvent;

public class EdgeMouseOutEvent extends GwtEvent<EdgeMouseOutHandler> {
    public static Type<EdgeMouseOutHandler> TYPE = new Type<>();

	@Override
	public Type<EdgeMouseOutHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EdgeMouseOutHandler handler) {
		handler.onEdgeMouseOut(this);
	}
	
    @Override
    public String toString() {
        return "NodeMouseOutEvent fired!";
    }

}
