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
    private String expressionPostdata;
    private OverlayProperties properties;

    
    
    public MakeOverlayRequestEvent(OverlayDataType dataType, String expressionPostData, OverlayProperties properties) {
    	this.dataType = dataType;
    	this.expressionPostdata = expressionPostData;
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

	public String getExpressionPostdata() {
		return this.expressionPostdata;
	}
	
	public OverlayProperties getOverlayProperties() {
		return this.properties;
	}
}
