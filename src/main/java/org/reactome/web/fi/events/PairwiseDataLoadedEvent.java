package org.reactome.web.fi.events;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.handlers.PairwiseDataLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

public class PairwiseDataLoadedEvent extends GwtEvent<PairwiseDataLoadedHandler>{
	public static Type<PairwiseDataLoadedHandler> TYPE = new Type<>();

	private List<PairwiseEntity> entities;
	private List<PairwiseOverlayObject> pairwiseOverlayObjects;
	
	public PairwiseDataLoadedEvent(List<PairwiseEntity> entities, List<PairwiseOverlayObject> pairwiseOverlayObjects) {
		this.entities = entities;
		this.pairwiseOverlayObjects = pairwiseOverlayObjects;
	}
	
	public List<PairwiseEntity> getEntities() {
		return this.entities;
	}
	
	public List<PairwiseOverlayObject> getPairwiseOverlayObjects(){
		return this.pairwiseOverlayObjects;
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
