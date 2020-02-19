package org.reactome.web.fi.data.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.fi.data.model.interactors.RawInteractorEntityImpl;
import org.reactome.web.fi.data.model.interactors.RawInteractorsImpl;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntitiesFactory;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class PairwiseDataLoader {
	
	private static final String BASE_URL = "/idgpairwise/";
	
	private Map<String, String> uniprotToGeneMap;
	
	public PairwiseDataLoader() {
		this.uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
	}
	
	public void loadPairwiseData(PairwiseOverlayProperties properties, AsyncCallback<List<PairwiseTableEntity>> callback) {
		
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
						entities = PairwiseEntitiesFactory.getPairwiseEntities(PairwiseEntities.class, obj.toString());
						callback.onSuccess(getEntitiesMap(entities));
					}catch(Exception e) {
						callback.onFailure(e);
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					callback.onFailure(new Exception(exception));
				}
			});
		} catch (RequestException e) {
			callback.onFailure(e);
		}
	}
	
	public void loadDiagramPairwiseNumbers(PairwiseOverlayProperties properties, AsyncCallback<RawInteractors> callback) {
		
		String url = BASE_URL + ""; //TODO: add route here when ready
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
//		try {
//			requestBuilder.sendRequest(getPostData(properties), new RequestCallback() {
//				@Override
//				public void onResponseReceived(Request request, Response response) {
//					// TODO: convert response to raw interactors for counts
//				}
//				@Override
//				public void onError(Request request, Throwable exception) {
//					callback.onFailure(exception);
//				}
//			});
			callback.onSuccess(makeRawInteractors(properties));
//		} catch(RequestException e) {
//			callback.onFailure(e);
//		}
	}
	
	private RawInteractorsImpl makeRawInteractors(PairwiseOverlayProperties properties) {
		RawInteractorsImpl result = null;
		
		List<RawInteractorEntity> entityList = new ArrayList<>();
		
		int counter = 10000;
		for(String source: new HashSet<String>(Arrays.asList(properties.getGeneNames().split(",")))) {
			entityList.add(new RawInteractorEntityImpl(source, counter, new ArrayList<>()));
			counter++;
		}
		
		result = new RawInteractorsImpl("Test Pairwise Counts Resource.", entityList);
		
		return result;
	}

	private List<PairwiseTableEntity> getEntitiesMap(PairwiseEntities entities) {
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
		
		List<PairwiseTableEntity> tableEntities = new ArrayList<>();
		for(PairwiseEntity entity : entities.getPairwiseEntities()) {
			if(entity.getNegGenes() != null && entity.getNegGenes().size() > 0) {
				for(String uniprot : entity.getNegGenes()) {
					tableEntities.add(new PairwiseTableEntity(entity.getGene(), uniprotToGeneMap.get(entity.getGene()),
															  uniprot, uniprotToGeneMap.get(uniprot),
															  entity.getDataDesc().getId(), "negative", null));
				}
			}
			if(entity.getPosGenes() != null && entity.getPosGenes().size() > 0) {
				for(String uniprot : entity.getPosGenes()){
					tableEntities.add(new PairwiseTableEntity(entity.getGene(), uniprotToGeneMap.get(entity.getGene()),
							  								  uniprot, uniprotToGeneMap.get(uniprot),
							  								  entity.getDataDesc().getId(), "positive", null));
				}
			}
		}
		
		Collections.sort(tableEntities, new Comparator<PairwiseTableEntity>() {
			@Override
			public int compare(PairwiseTableEntity o1, PairwiseTableEntity o2) {
				if(o1.getInteractorName() == null || o2.getInteractorName() == null) return 0;
				return o1.getInteractorName().compareTo(o2.getInteractorName());
			}
		});
		
		return tableEntities;
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
