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

    private OverlayDataType dataType;
    private OverlayEntityType entityType;
    private OverlayProperties properties;

    
    
    public MakeOverlayRequestEvent(OverlayDataType dataType, OverlayProperties properties) {
    	this.dataType = dataType;
    	this.properties = properties;
    }
    
    public MakeOverlayRequestEvent(OverlayEntityType entityType) {
    	this.entityType = entityType;
    }
    
	@Override
	public Type<MakeOverlayRequestHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MakeOverlayRequestHandler handler) {
		handler.onMakeOverlayRequest(this);
	}

	public OverlayDataType getDataType() {
		return this.dataType;
	}
	
	public OverlayEntityType getEntityType() {
		return this.entityType;
	}
	
	public OverlayProperties getOverlayProperties() {
		return this.properties;
	}
}
