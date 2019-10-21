package org.reactome.web.fi.data.loader;

import java.util.Set;

import org.reactome.web.fi.data.overlay.OverlayEntityDataFactory;
import org.reactome.web.fi.data.overlay.RawOverlayEntities;

import com.google.gwt.http.client.*;


public class TCRDLoader implements RequestCallback{

	public interface Handler{
		void onTargetLevelLoaded(RawOverlayEntities entities);
		void onTargetLevelLoadedError(Throwable exception);
	}
	
	private final static String BASE_URL = "/targetlevel/";
	
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
	
	public void load(Set<String> ids) {
		cancel();
		
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
				entities = OverlayEntityDataFactory.getTargetLevelEntity(RawOverlayEntities.class, response.getText());
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
