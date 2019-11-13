package org.reactome.web.fi.data.loader;

import java.util.Set;

import org.reactome.web.fi.data.overlay.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;


public class TCRDLoader implements RequestCallback{

	public interface Handler{
		void onTargetLevelLoaded(OverlayEntities entities);
		void onTargetLevelLoadedError(Throwable exception);
	}
	
	private final static String BASE_URL = "/tcrdws/";
	
	private Handler handler;
	private Request request;
	private OverlayDataType type;
	
	public TCRDLoader(Handler handler){
		this.handler = handler;
	}
	
	public void cancel() {
		if(this.request != null && this.request.isPending()) {
			this.request.cancel();
		}
	}
	
	public void load(String postData, OverlayDataType type) {
		this.type = type;
		cancel();
				
		if(postData == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onTargetLevelLoadedError(exception);
		}
		
		String url = BASE_URL + "targetlevel/uniprots";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setRequestData(postData);
		requestBuilder.setCallback(this);
		try {
			this.request = requestBuilder.send();
		} catch(RequestException e) {
			this.handler.onTargetLevelLoadedError(e);
		}
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
		case Response.SC_OK:
			OverlayEntities entities;
			try {
				JSONValue val = JSONParser.parseStrict(response.getText());
				JSONObject obj = new JSONObject();
				obj.put("dataType", new JSONString("Target Development Level"));
				obj.put("valueType", new JSONString("String"));
				JSONArray valArray = val.isArray();
				JSONArray outputArray = new JSONArray();
				for(int i=0; i<valArray.size(); i++) {
					JSONObject innerObj = valArray.get(i).isObject();
					JSONObject arrayObj = new JSONObject();
					arrayObj.put("identifier", innerObj.get("uniprot"));
					arrayObj.put("geneName", innerObj.get("sym"));
					arrayObj.put("value", innerObj.get("targetDevLevel"));
					outputArray.set(outputArray.size(), arrayObj);
				}
				obj.put("entities", outputArray);
				entities = OverlayEntityDataFactory.getTargetLevelEntity(OverlayEntities.class, obj.toString());
			}catch(Exception e) {
				this.handler.onTargetLevelLoadedError(e);
				return;
			}
			this.handler.onTargetLevelLoaded(entities);
			break;
		default:
			this.handler.onTargetLevelLoadedError(new Exception(response.getStatusText()));
		}
	}

	@Override
	public void onError(Request request, Throwable exception) {
		this.handler.onTargetLevelLoadedError(exception);
	}

}
