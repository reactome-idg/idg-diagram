package org.reactome.web.fi.events;

import java.util.Collection;

import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
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
    private Collection<PairwiseOverlayObject> pairwiseOverlayObjects;
    
    public MakeOverlayRequestEvent(DataOverlayProperties properties) {
    	this.properties = properties;
    }
    
    public MakeOverlayRequestEvent(Collection<PairwiseOverlayObject> pairwiseOverlayObjects) {
    	this.pairwiseOverlayObjects = pairwiseOverlayObjects;
    }
     
	@Override
	public Type<MakeOverlayRequestHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MakeOverlayRequestHandler handler) {
		handler.onMakeOverlayRequest(this);
	}
	
	public DataOverlayProperties getOverlayProperties() {
		return this.properties;
	}
	
	public Collection<PairwiseOverlayObject> getPairwiseOverlayObjects(){
		return this.pairwiseOverlayObjects;
	}
	
}
