package org.reactome.web.fi.data.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.diagram.data.interactors.raw.RawInteractorEntity;
import org.reactome.web.diagram.data.interactors.raw.RawInteractors;
import org.reactome.web.fi.data.model.interactors.RawInteractorEntityImpl;
import org.reactome.web.fi.data.model.interactors.RawInteractorsImpl;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntitiesFactory;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntitiesFactory;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseNumberEntity;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.tools.overlay.pairwise.PairwiseTableEntity;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class PairwiseDataLoader {
	
	public abstract interface Handler{
		default void onPairwiseDataLoaded(List<PairwiseTableEntity> tableEntities) {};
		default void onPairwiseNumbersLoaded(RawInteractors rawInteractors, PairwiseNumberEntities entities) {};
		void onPairwiseLoaderError(Throwable exception);
	}
	
	private static final String BASE_URL = "/idgpairwise/";
	
	private Map<String, String> uniprotToGeneMap;
	
	public PairwiseDataLoader() {
		this.uniprotToGeneMap = PairwiseInfoService.getUniprotToGeneMap();
	}
	
	/**
	 * Calls idg-pairwise server to get pairwise relationship data. When onlyNumbers is false, returns full data.
	 * When onlyNumbers is true, it returns the number of interactions without the actual interactor data.
	 * @param properties
	 * @param onlyNumbers
	 * @param callback
	 */
	public void loadPairwiseData(PairwiseOverlayProperties properties, boolean onlyNumbers, Handler handler) {
		
		String url = BASE_URL + "pairwise/uniprots/" + String.valueOf(onlyNumbers);
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			requestBuilder.sendRequest(getPostData(properties), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != Response.SC_OK) return;
					try {
						if(onlyNumbers) {
							JSONValue val = JSONParser.parseStrict(response.getText());
							JSONObject obj = new JSONObject();
							obj.put("pairwiseNumberEntities", val.isArray());
							PairwiseNumberEntities numberEntities = PairwiseNumberEntitiesFactory.getPairwiseNumberEntities(PairwiseNumberEntities.class, obj.toString());
							handler.onPairwiseNumbersLoaded(processPairwiseNumbers(properties, numberEntities), numberEntities);
						}else {
							JSONValue val = JSONParser.parseStrict(response.getText());
							JSONObject obj = new JSONObject();
							obj.put("pairwiseEntities", val.isArray());
							PairwiseEntities entities = PairwiseEntitiesFactory.getPairwiseEntities(PairwiseEntities.class, obj.toString());
							handler.onPairwiseDataLoaded(getEntitiesMap(entities));
						}
					}catch(Exception e) {
						handler.onPairwiseLoaderError(e);
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onPairwiseLoaderError(new Exception(exception));
				}
			});
		} catch (RequestException e) {
			handler.onPairwiseLoaderError(e);
		}
	}
	
	/**
	 * Converts pairwise number entities into rawInteractors and caches PairwiseNumberEntities in PairwisePopupFactory
	 * @param numberEntities
	 */
	private RawInteractors processPairwiseNumbers(PairwiseOverlayProperties properties, PairwiseNumberEntities numberEntities) {
		RawInteractorsImpl result = null;
		
		List<RawInteractorEntity> entityList = new ArrayList<>();
		
		for(PairwiseNumberEntity entity : numberEntities.getPairwiseNumberEntities()) {
			entityList.add(new RawInteractorEntityImpl(entity.getGene(), entity.getPosNum()+entity.getNegNum(), new ArrayList<>()));
		}
		
		List<String> resources = new ArrayList<>();
		for(PairwiseOverlayObject id : properties.getPairwiseOverlayObjects()) {
			resources.add(id.getId());
		}
			
		
		result = new RawInteractorsImpl(String.join(",", resources), entityList);
		
		return result;
	}

	private List<PairwiseTableEntity> getEntitiesMap(PairwiseEntities entities) {
		
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
