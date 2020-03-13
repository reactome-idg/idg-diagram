package org.reactome.web.fi.data.loader;

import org.reactome.web.fi.data.mediators.DataOverlayEntityMediator;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * 
 * @author brunsont
 *
 */
public class OverlayLoader implements RequestCallback{

	public interface Handler{
		void onDataOverlayLoaded(DataOverlay dataOverlay);
		void onOverlayLoadedError(Throwable exception);
	}
	
	private final static String TCRD_BASE_URL = "/tcrdws/";
	
	private Handler handler;
	private Request request;
	private DataOverlayProperties dataOverlayProperties;
	
	public OverlayLoader(){
	}
	
	public void cancel() {
		dataOverlayProperties = null;
		if(this.request != null && this.request.isPending())
			this.request.cancel();
	}

	public void load(DataOverlayProperties properties, Handler handler) {
		//make sure uniprots exist before doing anything
		if(properties.getUniprots() == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onOverlayLoadedError(exception);
			return;
		}
		
		this.handler = handler;
		cancel();
		this.dataOverlayProperties = properties;
		String postData = getPostData(dataOverlayProperties);
		String url = TCRD_BASE_URL + "expressions/uniprots";
		makeRequest(postData, url);
	}
	
	private void makeRequest(String postData, String url) {
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setRequestData(postData);
		requestBuilder.setCallback(this);
		try {
			this.request = requestBuilder.send();
		} catch(RequestException e) {
			this.handler.onOverlayLoadedError(e);
		}
	}

	private String getPostData(DataOverlayProperties properties) {
		String result = properties.getUniprots() +
						"\n" + properties.getTissues() +
						"\n" + properties.geteType();
		return result;
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
		case Response.SC_OK:
			DataOverlayEntityMediator mediator = new DataOverlayEntityMediator();
			DataOverlay dataOverlay;
			try {
				JSONValue val = JSONParser.parseStrict(response.getText());
				JSONObject obj = new JSONObject();
				obj.put("valueType", new JSONString("String"));
				obj.put("expressionEntity", val.isArray());
				dataOverlay = mediator.transformData(obj.toString(), dataOverlayProperties);
			}catch(Exception e) {
				this.handler.onOverlayLoadedError(e);
				return;
			}
			this.handler.onDataOverlayLoaded(dataOverlay);
			break;
		default:
			this.handler.onOverlayLoadedError(new Exception(response.getStatusText()));
		}
	}
	
	@Override
	public void onError(Request request, Throwable exception) {
		this.handler.onOverlayLoadedError(exception);
	}

}
