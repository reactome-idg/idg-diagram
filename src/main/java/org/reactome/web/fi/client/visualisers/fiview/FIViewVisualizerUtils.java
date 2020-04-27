package org.reactome.web.fi.client.visualisers.fiview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewVisualizerUtils {

	private EventBus eventBus;
	
	public FIViewVisualizerUtils(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	/**
	 * recieves a set of reactomeSources from an edge hovered or edge clicked event and sorts it.
	 * Sorting preferences returning the reactomeId of the source with the lowest reactomeId and source type of "Reaction."
	 * If no reaction exists in a set of sourcesFlowPanel, the lowest reactomeId with source type of "Complex" will be returned.
	 * If no source type exists on any of the passed in sourcesFlowPanel, it will return the lowest reactomeId present.
	 * @param reactomeSources
	 * @return
	 */
	public String sortGraphObject(JSONValue reactomeSources) {

		List<JSONObject> objList = new ArrayList<>();
		
		JSONArray jsonArray = reactomeSources.isArray();
		
		//parse over jsonArray, convert each source to a FIEntityNode, and adds to a FIEntityNode array list
		if(jsonArray != null) {
			for(int i=0; i<jsonArray.size(); i++) {
				JSONObject obj = jsonArray.get(i).isObject();
				objList.add(obj);
			}
		}
		
		//return dbId of the single source passed in
		if(objList.isEmpty()) {
			JSONObject obj = reactomeSources.isObject();
			return obj.get("reactomeId").isString().stringValue();
		}
			
		//Sorts sourcesList by dbId
		Collections.sort(objList, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				return Long.compare(Long.parseLong(o1.get("reactomeId").isString().stringValue()), Long.parseLong(o2.get("reactomeId").isString().stringValue()));
			}
		});
		
		//Sends first reaction when iterating over array from low to high DbId
		for (JSONObject obj : objList) {
			if (obj.get("sourceType").isString().toString().toUpperCase().contentEquals("REACTION"));
				return obj.get("reactomeId").isString().stringValue();
		}
		
		//If no obj in objList has a sourceType, send first entry, which will have lowest DbId after sorting above.
		return objList.get(0).get("reactomeId").isString().stringValue();
	}
	
}
