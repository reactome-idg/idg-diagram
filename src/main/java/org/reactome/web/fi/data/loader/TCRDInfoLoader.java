package org.reactome.web.fi.data.loader;

import java.util.List;

import org.reactome.web.fi.data.overlay.model.ExpressionTypeEntities;
import org.reactome.web.fi.data.overlay.model.ExpressionTypeFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class TCRDInfoLoader{

	public interface Handler{
		void onExpressionTypesLoaded(ExpressionTypeEntities entities);
		void onExpressionTypesLoadedError(Throwable exception);
	}
	
	private static final String BASE_URL = "/tcrdws/";
	private static Request request;
	
	public static void loadExpressionTypes(Handler handler) {
		if(request != null && request.isPending())
			request.cancel();
		
		String url = BASE_URL + "expressionTypes";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			request = requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK) {
						JSONValue val = JSONParser.parseStrict(response.getText());
						JSONObject obj = new JSONObject();
						obj.put("expressionTypeEntity", val);
						try {
							ExpressionTypeEntities entities = ExpressionTypeFactory.getExpressionTypeEntities(ExpressionTypeEntities.class, obj.toString());
							handler.onExpressionTypesLoaded(entities);
						} catch (Exception e) {
							handler.onExpressionTypesLoadedError(e);
						}
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onExpressionTypesLoadedError(new Exception(exception.getMessage()));
				}
			});
		} catch(RequestException ex) {
			handler.onExpressionTypesLoadedError(ex);
		}
	}
	
}
