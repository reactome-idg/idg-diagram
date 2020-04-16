package org.reactome.web.fi.events;

import org.reactome.web.fi.data.model.drug.Drug;
import org.reactome.web.fi.handlers.DrugTargetsLoadedHandler;

import java.util.Collection;

import org.reactome.web.diagram.data.Context;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class DrugTargetsLoadedEvent extends GwtEvent<DrugTargetsLoadedHandler> {
	public static Type<DrugTargetsLoadedHandler> TYPE = new Type<>();
	
	private Context context;
	private Collection<Drug> drugTargetCollection;
	
	public DrugTargetsLoadedEvent(Context context, Collection<Drug> drugTargetCollection) {
		this.context = context;
		this.drugTargetCollection = drugTargetCollection;
	}
	
	@Override
	public Type<DrugTargetsLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DrugTargetsLoadedHandler handler) {
		handler.onDrugTargetsLoaded(this);
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public Collection<Drug> getDrugTargets(){
		return this.drugTargetCollection;
	}
	
	@Override
	public String toString() {
		return "Drug targets loaded event fired!";
	}

}
