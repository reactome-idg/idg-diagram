package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.FIViewMessageHandler;

import com.google.gwt.event.shared.GwtEvent;

public class FIViewMessageEvent extends GwtEvent<FIViewMessageHandler>{
	public static Type<FIViewMessageHandler> TYPE = new Type<>();

	private boolean showMessage;
	
	public FIViewMessageEvent(boolean showMessage) {
		this.showMessage = showMessage;
	}
	
	public boolean getShowMessage() {
		return showMessage;
	}
	
	@Override
	public Type<FIViewMessageHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FIViewMessageHandler handler) {
		handler.onFIViewMessage(this);
	}

	@Override
	public String toString() {
		return "event fired!";
	}
}
