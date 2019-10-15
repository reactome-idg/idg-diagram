package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.TargetLevelDataRequestedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class TargetLevelDataRequestedEvent extends GwtEvent<TargetLevelDataRequestedHandler> {
	public static Type<TargetLevelDataRequestedHandler> TYPE = new Type<>();
	
	private String ids;
	
	public TargetLevelDataRequestedEvent(String ids) {
		this.ids = ids;
	}
	
	@Override
	public Type<TargetLevelDataRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TargetLevelDataRequestedHandler handler) {
		handler.onTargetLevelDataRequested(this);
	}
	
	
	public String getIds() {
		return ids;
	}

	@Override
	public String toString() {
		return "TCRD Target Level data requested for ids: " + getIds();
	}

}
