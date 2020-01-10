package org.reactome.web.fi.events;

import java.util.Set;

import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
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
	
	private DataOverlayProperties properties;
	
	public OverlayDataRequestedEvent(DataOverlayProperties properties) {
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
	
	public DataOverlayProperties getOverlayProperties() {
		return this.properties;
	}

	@Override
	public String toString() {
		return "TCRD Target Level data requested.";
	}

}
