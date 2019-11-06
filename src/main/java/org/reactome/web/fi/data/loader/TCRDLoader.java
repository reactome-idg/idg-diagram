package org.reactome.web.fi.data.loader;

import java.util.Set;

import org.reactome.web.fi.data.overlay.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.OverlayEntities;
import org.reactome.web.fi.model.OverlayType;

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
	
	public TCRDLoader(Handler handler){
		this.handler = handler;
	}
	
	public void cancel() {
		if(this.request != null && this.request.isPending()) {
			this.request.cancel();
		}
	}
	
	public void load(Set<String> ids, OverlayType type) {
		cancel();
				
		if(ids == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onTargetLevelLoadedError(exception);
		}
		
		String url = BASE_URL + "targetlevel/uniprots";
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
