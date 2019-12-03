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
	private String returnValueType;
	
	public OverlayDataRequestedEvent(String set, OverlayDataType type, String returnValueType) {
		this.postData = set;
		this.type = type;
		this.returnValueType = returnValueType;
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
	
	public String getReturnValueType() {
		return this.returnValueType;
	}

	@Override
	public String toString() {
		return "TCRD Target Level data requested.";
	}

}
