package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.handlers.RequestPairwiseCountsHandler;

import com.google.gwt.event.shared.GwtEvent;

public class RequestPairwiseCountsEvent extends GwtEvent<RequestPairwiseCountsHandler>{
	public static Type<RequestPairwiseCountsHandler> TYPE = new Type<>();

	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	
	public RequestPairwiseCountsEvent(List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}

	@Override
	public Type<RequestPairwiseCountsHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RequestPairwiseCountsHandler handler) {
		handler.onRequestPairwiseCountsHandeler(this);
	}
	
	public List<PairwiseOverlayObject> getPairwiseOverlayObjects(){
		return this.pairwiseOverlayObjects;
	}

	@Override
	public String toString() {
		return "RequestPairwiseCounts event fired!";
	}
}
