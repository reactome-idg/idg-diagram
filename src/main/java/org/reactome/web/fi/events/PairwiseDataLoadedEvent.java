package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.handlers.PairwiseDataLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PairwiseDataLoadedEvent extends GwtEvent<PairwiseDataLoadedHandler>{
	public static Type<PairwiseDataLoadedHandler> TYPE = new Type<>();

	private List<PairwiseEntity> entities;
	
	public PairwiseDataLoadedEvent(List<PairwiseEntity> entities) {
		this.entities = entities;
	}
	
	public List<PairwiseEntity> getEntities() {
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
