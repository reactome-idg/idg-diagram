package org.reactome.web.fi.events;

import org.reactome.web.fi.data.model.drug.DrugTargetEntity;
import org.reactome.web.fi.handlers.DrugTargetsLoadedHandler;

import java.util.List;
import java.util.Map;

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
	private Map<String, List<DrugTargetEntity>> uniprotToEntityListMap;
	
	public DrugTargetsLoadedEvent(Context context, Map<String, List<DrugTargetEntity>> uniprotToEntityListMap) {
		this.context = context;
		this.uniprotToEntityListMap = uniprotToEntityListMap;
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
	
	public Map<String, List<DrugTargetEntity>> getDrugTaretEntityMap(){
		return this.uniprotToEntityListMap;
	}
	
	@Override
	public String toString() {
		return "Drug targets loaded event fired!";
	}

}
