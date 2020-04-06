package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.DrugTargetsRequestedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetsRequestedEvent extends GwtEvent<DrugTargetsRequestedHandler>{
    public static Type<DrugTargetsRequestedHandler> TYPE = new Type<>();

    private String uniprots;
    
    public DrugTargetsRequestedEvent(String uniprots) {
    	this.uniprots = uniprots;
    }
    
	@Override
	public Type<DrugTargetsRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DrugTargetsRequestedHandler handler) {
		handler.onDrugTargetsRequested(this);
	}

	public String getUniprots() {
		return this.uniprots;
	}
	
	@Override
	public String toString() {
		return "Drug Targets Requested Event";
	}
	
}
