package org.reactome.web.fi.events;

import java.util.Set;

import org.reactome.web.fi.data.overlay.model.OverlayProperties;
import org.reactome.web.fi.handlers.OverlayDataRequestedHandler;
import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayDataRequestedEvent extends GwtEvent<OverlayDataRequestedHandler> {
	public static Type<OverlayDataRequestedHandler> TYPE = new Type<>();
	
	private String postData;
	private OverlayDataType type;
	private OverlayProperties properties;
	
	public OverlayDataRequestedEvent(String set, OverlayDataType type, OverlayProperties properties) {
		this.postData = set;
		this.type = type;
		this.properties = properties;
	}
	
	@Override
	public Type<OverlayDataRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OverlayDataRequestedHandler handler) {
		handler.onDataOverlayRequested(this);
	}
	
	
	public String getPostData() {
		return postData;
	}
	
	public OverlayDataType getType() {
		return this.type;
	}
	
	public OverlayProperties getOverlayProperties() {
		return this.properties;
	}

	@Override
	public String toString() {
		return "TCRD Target Level data requested.";
	}

}
