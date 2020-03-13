package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.HideOverlayLauncherHandler;

import com.google.gwt.event.shared.GwtEvent;

public class HideOverlayLauncherEvent extends GwtEvent<HideOverlayLauncherHandler>{
    public static Type<HideOverlayLauncherHandler> TYPE = new Type<>();

	
	@Override
	public Type<HideOverlayLauncherHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideOverlayLauncherHandler handler) {
		handler.onHideOverlayLauncher(this);
	}

}
