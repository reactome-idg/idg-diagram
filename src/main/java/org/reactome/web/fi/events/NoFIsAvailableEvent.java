package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.NoFIsAvailableHandler;

import com.google.gwt.event.shared.GwtEvent;

public class NoFIsAvailableEvent extends GwtEvent<NoFIsAvailableHandler>{
	public static Type<NoFIsAvailableHandler> TYPE = new Type<>();
	
	public NoFIsAvailableEvent() {
		
	}
	
	@Override
	public Type<NoFIsAvailableHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(NoFIsAvailableHandler handler) {
		handler.onNoFIsAvailable(this);
	}
}
