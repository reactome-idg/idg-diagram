package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.handlers.SetFIFlagDataDescsHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class SetFIFlagDataDescsEvent extends GwtEvent<SetFIFlagDataDescsHandler>{
	public static Type<SetFIFlagDataDescsHandler> TYPE = new Type<>();
	
	private List<String> dataDescs;
	private boolean containsEncapsulatedPathways;
	
	public SetFIFlagDataDescsEvent(List<String> dataDescs, boolean containsEncapsulatedPathways) {
		this.dataDescs = dataDescs;
		this.containsEncapsulatedPathways = containsEncapsulatedPathways;
	}
	
	@Override
	public Type<SetFIFlagDataDescsHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(SetFIFlagDataDescsHandler handler) {
		handler.onSetFIFlagDataDescs(this);
	}
	
	public List<String> getDataDescs() {
		return this.dataDescs;
	}
	
	public boolean containsEncapsulatedPathways() {
		return this.containsEncapsulatedPathways;
	}
	
	@Override
	public String toString() {
		return "Set flag data descriptions to: " + dataDescs.toString();
	}
}
