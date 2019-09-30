package org.reactome.web.fi.data.loader;

import org.reactome.web.fi.common.CytoscapeViewFlag;

import com.google.gwt.http.client.*;

/**
 * 
 * @author brunsont
 *
 */
public class FIViewLoader implements RequestCallback{
	
	public interface Handler{
		void onFIViewLoaded(String stId, String dbId, String fIJsonPathway);
		void onFIViewLoadedError(String stId, Throwable exception);
	}
	
	private final static String BASE_URL = "/FIService/network/convertPathwayToFIs/";
	
	private Handler handler;
	private Request request;
	private String stId;
	private String dbId;
	
	FIViewLoader(Handler handler){
		this.handler = handler;
	}
	
	public void cancel() {
		if(this.request != null && this.request.isPending()) {
			this.stId = null;
			this.request.cancel();
		}
	}
	
	public void load(String stId, String dbId) {
		this.stId = stId;
		this.dbId = dbId;
		String url = BASE_URL + dbId;
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
			this.handler.onFIViewLoaded(this.stId, this.dbId, response.getText());
			break;
		default:
			CytoscapeViewFlag.ensureCytoscapeViewFlagFalse();
			this.handler.onFIViewLoadedError(this.stId, new Exception(response.getStatusText()));
		}
		
	}

	@Override
	public void onError(Request request, Throwable exception) {
		this.handler.onFIViewLoadedError(stId, exception);
	}

}
