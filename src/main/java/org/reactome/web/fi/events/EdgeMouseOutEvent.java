package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.EdgeMouseOutHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
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
