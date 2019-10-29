package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;
import org.reactome.web.fi.model.OverlayTypes;

import com.google.gwt.event.shared.GwtEvent;

public class MakeOverlayRequestEvent extends GwtEvent<MakeOverlayRequestHandler>{
    public static Type<MakeOverlayRequestHandler> TYPE = new Type<>();

    private OverlayTypes type;
    
    public MakeOverlayRequestEvent(OverlayTypes type) {
    	this.type = type;
    }
    
	@Override
	public Type<MakeOverlayRequestHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MakeOverlayRequestHandler handler) {
		handler.onMakeOverlayRequest(this);
	}

	public OverlayTypes getType() {
		return this.type;
	}
	
}
