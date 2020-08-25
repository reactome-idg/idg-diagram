package org.reactome.web.fi.data.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactome.web.fi.data.model.FlagPEsPostData;
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

	public interface dataDescHandler{
		void onDataDescLoaded(PairwiseDescriptionEntities entities);
		void onDataDescLoadedError(Throwable exception);
	}
	
	public interface peFlagHandler{
		void onPEFlagsLoaded(List<Long> pes);
		void onPEFlagsLoadedError(Throwable exception);
	}
	
	private static final String BASE_URL = "/idgpairwise/";
	private static Request request;
	
	private static Map<String, String> uniprotToGeneMap;
	private static Map<String, String> geneToUniprotMap;
	
	/**
	 * Loads the types of pairwise overlay loadable
	 * @param handler
	 */
	public static void loadDataDesc(dataDescHandler handler) {
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
	
	public static void loadPEFlags(Long dbId, String term, List<String> dataDescs, peFlagHandler handler) {
		String url = BASE_URL;
		if(!getGeneToUniprotMap().containsValue(term)) //if yes, then load for uniprot
			url += "relationships/PEsForGeneInteractors";
		else url += "relationships/PEsForUniprotInteractors"; //else load for gene
		
		FlagPEsPostData post = new FlagPEsPostData(term, dbId, dataDescs);
		
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
					JSONArray array = value.isArray();
					List<Long> pes = new ArrayList<>(); 
					if(array != null) {
						for(int i=0; i<array.size(); i++) {
							pes.add(new Double(array.get(i).isNumber().doubleValue()).longValue());
						}
					}
					handler.onPEFlagsLoaded(pes);
					
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
