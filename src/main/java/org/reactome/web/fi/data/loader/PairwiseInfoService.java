package org.reactome.web.fi.data.loader;

import java.util.HashMap;
import java.util.Map;

import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionEntities;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseDescriptionFactory;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

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
	
	private static final String BASE_URL = "/idgpairwise/";
	private static Request request;
	
	public static Map<String, String> geneToUniprotMap;
	
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
	
	public static void loadUniprotToGeneMap(AsyncCallback<Map<String, String>> callback) {
		if(geneToUniprotMap != null) {
			callback.onSuccess(geneToUniprotMap);
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
						callback.onSuccess(geneToUniprotMap);
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
	
	/**
	 * Creates a map from gene name to uniprot and stores in a global variable
	 * @param text
	 */
	private static void createMap(String text) {
		geneToUniprotMap = new HashMap<>();
		String[] lines = text.split("\n");
		for(String line : lines) {
			String[] mapTo = line.split("\t");
			geneToUniprotMap.put(mapTo[0], mapTo[1]);
		}
	}
}
