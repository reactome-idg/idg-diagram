package org.reactome.web.fi.events;

import java.util.Set;

import org.reactome.web.fi.handlers.TargetLevelDataRequestedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class TargetLevelDataRequestedEvent extends GwtEvent<TargetLevelDataRequestedHandler> {
	public static Type<TargetLevelDataRequestedHandler> TYPE = new Type<>();
	
	private Set<String> ids;
	
	public TargetLevelDataRequestedEvent(Set<String> set) {
		this.ids = set;
	}
	
	@Override
	public Type<TargetLevelDataRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(TargetLevelDataRequestedHandler handler) {
		handler.onTargetLevelDataRequested(this);
	}
	
	
	public Set<String> getIds() {
		return ids;
	}

	@Override
	public String toString() {
		return "TCRD Target Level data requested for ids: " + getIds();
	}

}
