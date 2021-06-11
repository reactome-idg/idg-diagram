package org.reactome.web.fi.data.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reactome.web.diagram.client.DiagramFactory;
import org.reactome.web.fi.data.model.EnrichedPathwaysPostData;
import org.reactome.web.fi.data.model.FlagPEsPostData;
import org.reactome.web.fi.data.model.PathwayEnrichmentResult;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionFactory;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseInfoService {

	public interface DataDescHandler{
		void onDataDescLoaded(PairwiseDescriptionEntities entities);
		void onDataDescLoadedError(Throwable exception);
	}
	
	public interface PEFlagHandler{
		void onPEFlagsLoaded(List<Long> pes, List<String> flagInteractors, List<String> dataDescs);
		void onPEFlagsLoadedError(Throwable exception);
	}
	
	public interface PathwayEnrichmentHandler {
		void onPathwaysToFlag(List<PathwayEnrichmentResult> stIds);
		void onPathwaysToFlagError();
	}
	
	private static final String BASE_URL = DiagramFactory.SERVER + "/idgpairwise/";
	private static Request request;
	
	private static Map<String, String> uniprotToGeneMap;
	private static Map<String, String> geneToUniprotMap;
	
	/**
	 * Loads the types of pairwise overlay loadable
	 * @param handler
	 */
	public static void loadDataDesc(DataDescHandler handler) {
		if(request != null && request.isPending())
			request.cancel();
		
		String url = BASE_URL + "datadesc";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			request = requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK) {
						JSONValue val = JSONParser.parseStrict(response.getText());
						JSONObject obj = new JSONObject();
						obj.put("pairwiseDescriptionEntities", val);
						try {
							PairwiseDescriptionEntities entities = PairwiseDescriptionFactory.getPairwiseDescriptionEntities(PairwiseDescriptionEntities.class, obj.toString());
							handler.onDataDescLoaded(entities);
						} catch(Exception e) {
							handler.onDataDescLoadedError(e);
						}
					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onDataDescLoadedError(new Exception(exception.getMessage()));
				}
			});
		} catch(RequestException ex) {
			handler.onDataDescLoadedError(ex);
		}
	}
	
	public static void loadPEFlags(Long dbId, String term, List<Integer> dataDescKeys, Double prd, PEFlagHandler handler) {
		String url = BASE_URL + "relationships/PEsForTermInteractors";
		
		FlagPEsPostData post = new FlagPEsPostData(term, dbId, dataDescKeys, prd);
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setHeader("content-type", "application/json");
		try {
			request = requestBuilder.sendRequest(post.toJSONString(), new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != Response.SC_OK) {
						handler.onPEFlagsLoadedError(new Exception("Interactor Service Error"));
						return;
					}
					JSONValue value = JSONParser.parseStrict(response.getText());
					JSONObject rtnObj = value.isObject();
					JSONArray peArray = rtnObj.get("peIds").isArray();
					List<Long> pes = new ArrayList<>(); 
					if(peArray != null) {
						for(int i=0; i<peArray.size(); i++) {
							pes.add(new Double(peArray.get(i).isNumber().doubleValue()).longValue());
						}
					}
					Map<String, String> geneToUniprot = getGeneToUniprotMap();
					List<String> termInteractors = new ArrayList<>();
					JSONArray termInteractorsArray = rtnObj.get("interactors").isArray();
						if(termInteractorsArray != null) {
							for(int i=0; i<termInteractorsArray.size(); i++)
								termInteractors.add(geneToUniprot.get(termInteractorsArray.get(i).isString().stringValue()));
						}
					List<String> dataDescs = new ArrayList<>();
					JSONArray dataDescsArray = rtnObj.get("dataDescs").isArray();
					if(dataDescsArray != null) {
						for(int i=0; i<dataDescsArray.size(); i++) {
							dataDescs.add(dataDescsArray.get(i).isString().stringValue());
						}
					}
				
					handler.onPEFlagsLoaded(pes, termInteractors, dataDescs);
					
				}
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onPEFlagsLoadedError(new Exception("Interactor Service Error"));
				}
			});
		} catch(RequestException e) {
			handler.onPEFlagsLoadedError(new Exception("Interactor Service Error"));
		}
	}
	
	public static void findPathwaysToFlag(String term, List<Integer> dataDescKeys, Double prd, PathwayEnrichmentHandler handler) {
		String url = BASE_URL + "relationships/enrichedSecondaryPathwaysForTerm";
		EnrichedPathwaysPostData post = new EnrichedPathwaysPostData(term, dataDescKeys, prd);
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setHeader("content-type", "application/json");
		try {
			requestBuilder.sendRequest(post.toJSON(), new RequestCallback() {

				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() != Response.SC_OK) {
						handler.onPathwaysToFlagError();
						return;
					}
					handler.onPathwaysToFlag(parsePathwayStIds(response.getText()));
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onPathwaysToFlagError();
				}
			});
		} catch(RequestException ex) {
			handler.onPathwaysToFlagError();
		}
	}
	
	private static List<PathwayEnrichmentResult> parsePathwayStIds(String text) {
		List<PathwayEnrichmentResult> rtn = new ArrayList<>();
		
		JSONArray val = JSONParser.parseStrict(text).isArray();
		if(val == null) return new ArrayList<>();
		
		for(int i=0; i<val.size(); i++) {
			JSONObject pathway = val.get(i).isObject();
			rtn.add(new PathwayEnrichmentResult(pathway.get("stId").isString().stringValue(),
					   							pathway.get("name").isString().stringValue(),
					   							pathway.get("fdr").isNumber().doubleValue(),
					   							pathway.get("pVal").isNumber().doubleValue()));
		}
		
		return rtn;
	}
	
	public static void loadUniprotToGeneMap() {
		if(uniprotToGeneMap != null) {
			return; //calling the callback does not return the method.
		}
		
		String url  = BASE_URL + "uniprot2gene";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			request = requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK) {
						createMap(response.getText());

					}
				}
				@Override
				public void onError(Request request, Throwable exception) {
					exception.printStackTrace();
				}
			});
		} catch(RequestException ex) {
			ex.printStackTrace();
		}
	}
	
	public static Map<String, String> getUniprotToGeneMap(){
		return uniprotToGeneMap;
	}
	
	public static Map<String, String> getGeneToUniprotMap(){
		return geneToUniprotMap;
	}
	
	/**
	 * Creates a map from gene name to uniprot and stores in a global variable
	 * @param text
	 */
	private static void createMap(String text) {
		uniprotToGeneMap = new HashMap<>();
		geneToUniprotMap = new HashMap<>();
		String[] lines = text.split("\n");
		for(String line : lines) {
			String[] mapTo = line.split("\t");
			uniprotToGeneMap.put(mapTo[0], mapTo[1]);
			geneToUniprotMap.put(mapTo[1], mapTo[0]);
		}
	}
}
