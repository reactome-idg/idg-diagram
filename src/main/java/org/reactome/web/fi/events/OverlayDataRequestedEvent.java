package org.reactome.web.fi.events;

import java.util.Set;

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
	
	public OverlayDataRequestedEvent(String set, OverlayDataType type) {
		this.postData = set;
		this.type = type;
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

	@Override
	public String toString() {
		return "TCRD Target Level data requested.";
	}

}
