package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;
import org.reactome.web.fi.model.OverlayType;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class MakeOverlayRequestEvent extends GwtEvent<MakeOverlayRequestHandler>{
    public static Type<MakeOverlayRequestHandler> TYPE = new Type<>();

    private OverlayType type;
    
    public MakeOverlayRequestEvent(OverlayType type) {
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

	public OverlayType getType() {
		return this.type;
	}
	
}
