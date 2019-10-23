package org.reactome.web.fi.data.loader;

import java.util.List;
import java.util.Set;

import org.reactome.web.diagram.data.interactors.common.OverlayResource;
import org.reactome.web.fi.data.overlay.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.OverlayType;
import org.reactome.web.fi.data.overlay.OverlayType.OverlayTypes;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;
import org.reactome.web.fi.data.overlay.RawOverlayEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;


public class TCRDLoader implements RequestCallback{

	public interface Handler{
		void onTargetLevelLoaded(RawOverlayEntities entities);
		void onTargetLevelLoadedError(Throwable exception);
	}
	
	private final static String BASE_URL = "/targetlevel/";
	
	private Handler handler;
	private Request request;
	private OverlayType resource;
	
	public TCRDLoader(Handler handler){
		this.handler = handler;
	}
	
	public void cancel() {
		if(this.request != null && this.request.isPending()) {
			this.request.cancel();
		}
	}
	
	public void load(Set<String> ids, OverlayType resource) {
		cancel();
		
		this.resource = resource;
		
		if(ids == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onTargetLevelLoadedError(exception);
		}
		
		String url = BASE_URL + "uniprots";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setRequestData(getPostData(ids));
		requestBuilder.setCallback(this);
		try {
			this.request = requestBuilder.send();
		} catch(RequestException e) {
			this.handler.onTargetLevelLoadedError(e);
		}
	}
	
	/**
	 * iterates over a set of uniprot identifiers and adds them to a string delineated by ','.
	 * @param ids
	 * @return
	 */
	private String getPostData(Set<String> ids) {
		StringBuilder post = new StringBuilder();
		ids.stream().forEach(S -> post.append(S).append(","));
		if(post.length()>0) {
			post.delete(post.length()-1, post.length());
			return post.toString();
		}
		
		return null;
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
		case Response.SC_OK:
			RawOverlayEntities entities;
			try {
				JSONValue val = JSONParser.parseStrict(response.getText());
				JSONObject obj = new JSONObject();
				obj.put("resource", new JSONString(OverlayTypes.PROTEINTARGETLEVEL.toString().toLowerCase()));
				obj.put("entities", val);
				entities = OverlayEntityDataFactory.getTargetLevelEntity(RawOverlayEntities.class, obj.toString());
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
