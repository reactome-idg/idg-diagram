package org.reactome.web.fi.data.loader;

import com.google.gwt.http.client.*;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewLoader implements RequestCallback{
	
	public interface Handler{
		void onFIViewLoaded(String stId, String fIJsonPathway);
		void onFIViewLoadedError(String stId, Throwable exception);
	}
	
	private final static String BASE_URL = "/FIService/network/convertPathwayToFIs/";
	
	private Handler handler;
	private Request request;
	private String stId;
	
	FIViewLoader(Handler handler){
		this.handler = handler;
	}
	
	public void cancel() {
		if(this.request != null && this.request.isPending()) {
			this.stId = null;
			this.request.cancel();
		}
	}
	
	public void load(String stId) {
		
		
		this.stId = stId;
		
		String url = BASE_URL + stId;
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			this.request = requestBuilder.sendRequest(null, this);
		} catch(RequestException e) {
			this.handler.onFIViewLoadedError(this.stId, e);
		}
	}
	

	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
		case Response.SC_OK:
			this.handler.onFIViewLoaded(this.stId, response.getText());
			break;
		default:
			this.handler.onFIViewLoadedError(this.stId, new Exception(response.getStatusText()));
		}
		
	}

	@Override
	public void onError(Request request, Throwable exception) {
		this.handler.onFIViewLoadedError(stId, exception);
	}

}
