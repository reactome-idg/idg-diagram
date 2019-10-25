package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.OverlayDataResetHandler;

import com.google.gwt.event.shared.GwtEvent;

public class OverlayDataResetEvent extends GwtEvent<OverlayDataResetHandler>{
	public static Type<OverlayDataResetHandler> TYPE = new Type<>();

	@Override
	public Type<OverlayDataResetHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(OverlayDataResetHandler handler) {
		handler.onOverlayDataReset(this);
	}

}
