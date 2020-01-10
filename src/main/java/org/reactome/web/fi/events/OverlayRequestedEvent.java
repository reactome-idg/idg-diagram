package org.reactome.web.fi.events;

import java.util.Set;

import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.handlers.OverlayDataRequestedHandler;
import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayRequestedEvent extends GwtEvent<OverlayDataRequestedHandler> {
	public static Type<OverlayDataRequestedHandler> TYPE = new Type<>();
	
	private DataOverlayProperties dataOverlayProperties;
	private PairwiseOverlayProperties pairwiseOverlayProperties;
	
	
	public OverlayRequestedEvent(DataOverlayProperties properties) {
		this.dataOverlayProperties = properties;
	}
	
	public OverlayRequestedEvent(PairwiseOverlayProperties properties) {
		this.pairwiseOverlayProperties = properties;
	}
	
	@Override
	public Type<OverlayDataRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OverlayDataRequestedHandler handler) {
		handler.onDataOverlayRequested(this);
	}
	
	public DataOverlayProperties getDataOverlayProperties() {
		return this.dataOverlayProperties;
	}

	public PairwiseOverlayProperties getPairwiseOverlayProperties() {
		return this.pairwiseOverlayProperties;
	}
	
	@Override
	public String toString() {
		if(dataOverlayProperties != null)
			return "Data Overlay Requested!";
		else
			return "Pairwise Overlay Requested!";
	}

}
