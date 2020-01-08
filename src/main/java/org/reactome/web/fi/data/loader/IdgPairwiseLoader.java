package org.reactome.web.fi.data.loader;

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

/**
 * 
 * @author brunsont
 *
 */
public class IdgPairwiseLoader {

	public interface dataDescHandler{
		void onDataDescLoaded(PairwiseDescriptionEntities entities);
		void onDataDescLoadedError(Throwable exception);
	}
	
	private static final String BASE_URL = "/idgpairwise/";
	private static Request request;
	
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
							PairwiseDescriptionEntities entities = PairwiseDescriptionFactory.getExpressionTypeEntities(PairwiseDescriptionEntities.class, obj.toString());
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
	
}
