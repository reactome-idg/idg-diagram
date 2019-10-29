package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.FireGraphObjectSelectedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class FireGraphObjectSelectedEvent extends GwtEvent<FireGraphObjectSelectedHandler> {
    public static Type<FireGraphObjectSelectedHandler> TYPE = new Type<>();

	private String reactomeId;

	public FireGraphObjectSelectedEvent(String reactomeId) {
		this.reactomeId = reactomeId;
	}
	
	@Override
	public Type<FireGraphObjectSelectedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FireGraphObjectSelectedHandler handler) {
		handler.onFireGraphObjectSelected(this);
	}
	
	public String getReactomeId() {
		return reactomeId;
	}

	@Override
	public String toString() {
		return "Fire graph object selected event with reactomeId: " + getReactomeId();
	}
	
}
