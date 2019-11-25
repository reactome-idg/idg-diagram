package org.reactome.web.fi.data.loader;


import org.reactome.web.fi.data.mediators.DataOverlayEntityMediator;
import org.reactome.web.fi.data.overlay.model.OverlayEntities;
import org.reactome.web.fi.data.overlay.model.OverlayEntityDataFactory;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;


public class TCRDLoader implements RequestCallback{

	public interface Handler{
		void onTargetLevelLoaded(DataOverlay dataOverlay);
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
		if(this.request != null && this.request.isPending())
			this.request.cancel();
	}
	
	public void load(String postData, OverlayDataType type) {
		this.type = type;
		cancel();
						
		if(postData == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onTargetLevelLoadedError(exception);
		}
		
		String url = BASE_URL + "expressions/uniprots";

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
			DataOverlayEntityMediator mediator = new DataOverlayEntityMediator();
			DataOverlay dataOverlay;
			try {
				GWT.log(response.getText());
				JSONValue val = JSONParser.parseStrict(response.getText());
				JSONObject obj = new JSONObject();
				obj.put("dataType", new JSONString(type.getName()));
				obj.put("valueType", new JSONString("String"));
				obj.put(getEntityType(), val.isArray());
				dataOverlay = mediator.transformData(obj.toString());
			}catch(Exception e) {
				this.handler.onTargetLevelLoadedError(e);
				return;
			}
			this.handler.onTargetLevelLoaded(dataOverlay);
			break;
		default:
			this.handler.onTargetLevelLoadedError(new Exception(response.getStatusText()));
		}
	}
	
	private String getEntityType() {
		if(type == OverlayDataType.TARGET_DEVELOPMENT_LEVEL)
			return "targetLevelEntity";
		else if(type == OverlayDataType.TISSUE_EXPRESSION) {
			return "expressionEntity";
		}
		return "overlayEntity";
	}
	
	@Override
	public void onError(Request request, Throwable exception) {
		this.handler.onTargetLevelLoadedError(exception);
	}

}
