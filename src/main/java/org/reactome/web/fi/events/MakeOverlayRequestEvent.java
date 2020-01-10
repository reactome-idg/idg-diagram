package org.reactome.web.fi.events;


import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.handlers.MakeOverlayRequestHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class MakeOverlayRequestEvent extends GwtEvent<MakeOverlayRequestHandler>{
    public static Type<MakeOverlayRequestHandler> TYPE = new Type<>();

    private DataOverlayProperties properties;
    private PairwiseOverlayProperties pairwiseOverlayProperties;
    
    public MakeOverlayRequestEvent(DataOverlayProperties properties) {
    	this.properties = properties;
    }
    
    public MakeOverlayRequestEvent(PairwiseOverlayProperties pairwiseOverlayProperties) {
    	this.pairwiseOverlayProperties = pairwiseOverlayProperties;
    }
     
	@Override
	public Type<MakeOverlayRequestHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MakeOverlayRequestHandler handler) {
		handler.onMakeOverlayRequest(this);
	}
	
	public DataOverlayProperties getDataOverlayProperties() {
		return this.properties;
	}
	
	public PairwiseOverlayProperties getPairwiseOverlayProperties(){
		return this.pairwiseOverlayProperties;
	}
	
}
