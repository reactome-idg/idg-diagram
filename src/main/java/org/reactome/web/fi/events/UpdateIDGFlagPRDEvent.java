package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.UpdateIDGFlagPRDHandler;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateIDGFlagPRDEvent extends GwtEvent<UpdateIDGFlagPRDHandler> {
	public static Type<UpdateIDGFlagPRDHandler> TYPE = new Type<>();
	
	private Double prd;
	
	public UpdateIDGFlagPRDEvent(Double prd) {
		this.prd = prd;
	}
	
	public Double getPrd() {
		return prd;
	}

	public void setPrd(Double prd) {
		this.prd = prd;
	}

	@Override
	public Type<UpdateIDGFlagPRDHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateIDGFlagPRDHandler handler) {
		handler.onUpdateIDGFlagPRD(this);
	}
	
	@Override
	public String toString() {
		return "Updating idg flagging PRD to: " + this.prd;
	}
	
}
