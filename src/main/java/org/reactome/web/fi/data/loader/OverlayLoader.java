package org.reactome.web.fi.data.loader;

import java.util.ArrayList;
import java.util.List;

import org.reactome.web.fi.data.mediators.DataOverlayEntityMediator;
import org.reactome.web.fi.data.overlay.model.OverlayEntities;
import org.reactome.web.fi.data.overlay.model.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayObject;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;
import org.reactome.web.fi.data.overlay.model.DataOverlayProperties;
import org.reactome.web.fi.model.DataOverlay;
import org.reactome.web.fi.model.OverlayDataType;

import com.google.gwt.core.client.GWT;
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
	private final static String PAIRWISE_BASE_URL = "idgpairwise";
	
	private Handler handler;
	private Request request;
	private DataOverlayProperties dataOverlayProperties;
	private PairwiseOverlayProperties pairwiseOverlayProperties;
	
	public OverlayLoader(Handler handler){
		this.handler = handler;
	}
	
	public void cancel() {
		dataOverlayProperties = null;
		pairwiseOverlayProperties = null;
		if(this.request != null && this.request.isPending())
			this.request.cancel();
	}
	
	public void load(PairwiseOverlayProperties properties) {
		//make sure uniprots exits before doing anything
		if(properties.getUniprots() == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onOverlayLoadedError(exception);
			return;
		}
		cancel();
		this.pairwiseOverlayProperties = properties;
		String postData = getPostData(pairwiseOverlayProperties);
		String url = PAIRWISE_BASE_URL + "/pairwise/uniprots";
		makeRequest(postData, url);
	}

	public void load(DataOverlayProperties properties) {
		//make sure uniprots exist before doing anything
		if(properties.getUniprots() == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onOverlayLoadedError(exception);
			return;
		}
		
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
	
	private String getPostData(PairwiseOverlayProperties pairwiseOverlayProperties) {
		String result = "";
		List<String> ids = new ArrayList<>();
		for(PairwiseOverlayObject obj : pairwiseOverlayProperties.getPairwiseOverlayObjects()) {
			ids.add(obj.getId());
		}
		result = String.join(",",ids) + 
				 "\n" + pairwiseOverlayProperties.getUniprots();
		
		return result;
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
			if(dataOverlayProperties != null)
				dataOverlayReturned(response.getText());
			else if(pairwiseOverlayProperties != null)
				pariwiseOverlayReturned(response.getText());
			break;
		default:
			this.handler.onOverlayLoadedError(new Exception(response.getStatusText()));
		}
	}

	private void pariwiseOverlayReturned(String text) {
		GWT.log(text);
	}

	private void dataOverlayReturned(String responseText) {
		DataOverlayEntityMediator mediator = new DataOverlayEntityMediator();
		DataOverlay dataOverlay;
		try {
			JSONValue val = JSONParser.parseStrict(responseText);
			JSONObject obj = new JSONObject();
			obj.put("valueType", new JSONString("String"));
			obj.put("expressionEntity", val.isArray());
			dataOverlay = mediator.transformData(obj.toString(), dataOverlayProperties);
		}catch(Exception e) {
			this.handler.onOverlayLoadedError(e);
			return;
		}
		this.handler.onDataOverlayLoaded(dataOverlay);
	}
	
	@Override
	public void onError(Request request, Throwable exception) {
		this.handler.onOverlayLoadedError(exception);
	}

}
