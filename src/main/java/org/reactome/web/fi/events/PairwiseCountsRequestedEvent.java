package org.reactome.web.fi.events;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.handlers.PairwiseCountsRequestedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseCountsRequestedEvent extends GwtEvent<PairwiseCountsRequestedHandler>{
	public static Type<PairwiseCountsRequestedHandler> TYPE = new Type<>();

	private PairwiseOverlayProperties props;
	
	public PairwiseCountsRequestedEvent(PairwiseOverlayProperties props) {
		this.props = props;
	}
	
	@Override
	public Type<PairwiseCountsRequestedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PairwiseCountsRequestedHandler handler) {
		handler.onPairwiseCountsRequested(this);
	}
	
	public PairwiseOverlayProperties getPairwiseOverlayProperties() {
		return this.props;
	}

	@Override
	public String toString() {
		return "Pairwise Counts Requested";
	}

}
