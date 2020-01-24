package org.reactome.web.fi.data.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntitiesFactory;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class PairwiseDataLoader {

	public interface Handler{
		void onPairwiseDataLoaded(Map<String, List<PairwiseEntity>> uniprotToPairwiseEntityMap);
		void onPairwiseDataLoadedError(Exception e);
	}
	
	private static final String BASE_URL = "/idgpairwise/";
	
	public PairwiseDataLoader() {/*Nothing Here*/}
	
	public void loadPairwiseData(PairwiseOverlayProperties properties, Handler handler) {
		
		String url = BASE_URL + "pairwise/uniprots";
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(getPostData(properties), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != Response.SC_OK) return;
					PairwiseEntities entities;
					try {
						JSONValue val = JSONParser.parseStrict(response.getText());
						JSONObject obj = new JSONObject();
						obj.put("pairwiseEntities", val.isArray());
						GWT.log(obj.toString());
						entities = PairwiseEntitiesFactory.getPairwiseEntities(PairwiseEntities.class, obj.toString());
						handler.onPairwiseDataLoaded(getEntitiesMap(entities));
					}catch(Exception e) {
						handler.onPairwiseDataLoadedError(e);
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onPairwiseDataLoadedError(new Exception(exception));
				}
			});
		} catch (RequestException e) {
			handler.onPairwiseDataLoadedError(e);
		}
	}
	
	private Map<String, List<PairwiseEntity>> getEntitiesMap(PairwiseEntities entities) {
		Map<String, List<PairwiseEntity>> result = new HashMap<>();
		
		for(PairwiseEntity entity: entities.getPairwiseEntities()) {
			if(result.keySet().contains(entity.getGene()))
				result.get(entity.getGene()).add(entity);
			else {
				List<PairwiseEntity> list = new ArrayList<>();
				list.add(entity);
				result.put(entity.getGene(), list);
			}
		}
		
		return result;
	}
	
	private String getPostData(PairwiseOverlayProperties properties) {		
		String result = "";
		List<String> ids = new ArrayList<>();
		for(PairwiseOverlayObject obj : properties.getPairwiseOverlayObjects()) {
			ids.add(obj.getId());
		}
		result = String.join(",",ids) + 
				 "\n" + properties.getGeneNames();
		
		return result;
	}
}
