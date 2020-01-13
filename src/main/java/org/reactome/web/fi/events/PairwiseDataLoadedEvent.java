package org.reactome.web.fi.events;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntities;
import org.reactome.web.fi.handlers.PairwiseDataLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PairwiseDataLoadedEvent extends GwtEvent<PairwiseDataLoadedHandler>{
	public static Type<PairwiseDataLoadedHandler> TYPE = new Type<>();

	private PairwiseEntities entities;
	
	public PairwiseDataLoadedEvent(PairwiseEntities entities) {
		this.entities = entities;
	}
	
	public PairwiseEntities getEntities() {
		return this.entities;
	}
	
	@Override
	public Type<PairwiseDataLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PairwiseDataLoadedHandler handler) {
		handler.onPairwisieDataLoaded(this);
	}

	@Override
	public String toString() {
		return "Pairwise data loaded!";
	}
}
