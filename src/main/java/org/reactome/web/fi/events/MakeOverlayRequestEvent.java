package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.OverlayProperties;
import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;
import org.reactome.web.fi.model.OverlayDataType;
import org.reactome.web.fi.model.OverlayEntityType;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class MakeOverlayRequestEvent extends GwtEvent<MakeOverlayRequestHandler>{
    public static Type<MakeOverlayRequestHandler> TYPE = new Type<>();

    private OverlayProperties properties;
    
    public MakeOverlayRequestEvent(OverlayProperties properties) {
    	this.properties = properties;
    }
     
	@Override
	public Type<MakeOverlayRequestHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MakeOverlayRequestHandler handler) {
		handler.onMakeOverlayRequest(this);
	}
	
	public OverlayProperties getOverlayProperties() {
		return this.properties;
	}
}
