package org.reactome.web.fi.events;

import org.reactome.web.fi.handlers.PairwiseInteractorsResetHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseInteractorsResetEvent extends GwtEvent<PairwiseInteractorsResetHandler>{
	public static Type<PairwiseInteractorsResetHandler> TYPE = new Type<>();

	
	@Override
	public Type<PairwiseInteractorsResetHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PairwiseInteractorsResetHandler handler) {
		handler.onPairwiseInteractorsReset(this);
	}
	
	@Override
	public String toString() {
		return "Pairwise interactors reset event fired";
	}

}
