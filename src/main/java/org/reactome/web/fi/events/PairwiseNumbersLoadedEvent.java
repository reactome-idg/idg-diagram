package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.PairwiseNumbersLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseNumbersLoadedEvent extends GwtEvent<PairwiseNumbersLoadedHandler>{
    public static Type<PairwiseNumbersLoadedHandler> TYPE = new Type<>();

	
	@Override
	public Type<PairwiseNumbersLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PairwiseNumbersLoadedHandler handler) {
		handler.onPairwiseNumbersLoaded(this);
	}

	@Override
	public String toString() {
		return "Pairwise relationship numbers loaded";
	}
}
