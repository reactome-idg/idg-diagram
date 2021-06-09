package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.UpdateIDGFlagFDRHandler;

import com.google.gwt.event.shared.GwtEvent;

public class UpdateIDGFlagFDREvent extends GwtEvent<UpdateIDGFlagFDRHandler>{
	public static Type<UpdateIDGFlagFDRHandler> TYPE = new Type<>();
	
	private Double fdr;
	
	public UpdateIDGFlagFDREvent(Double fdr) {
		this.fdr = fdr;
	}
	
	public Double getFdr() {
		return fdr;
	}

	public void setFdr(Double fdr) {
		this.fdr = fdr;
	}

	@Override
	public Type<UpdateIDGFlagFDRHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UpdateIDGFlagFDRHandler handler) {
		handler.onUpdateIDGFlagFDR(this);
	}
	
	@Override
	public String toString() {
		return "Updating idg flagging FDR to: " + this.fdr;
	}
}
