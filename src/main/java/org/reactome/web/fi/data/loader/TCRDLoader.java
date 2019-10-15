package org.reactome.web.fi.data.loader;

import com.google.gwt.http.client.*;


public class TCRDLoader implements RequestCallback{

	public interface Handler{
		void onTargetLevelLoaded(String json);
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
	
	public void loadTargetLevels(String ids) {
		String url = BASE_URL + "uniprots";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setRequestData(ids);
		requestBuilder.setCallback(this);
		try {
			this.request = requestBuilder.sendRequest(ids, this);
		} catch(RequestException e) {
			this.handler.onTargetLevelLoadedError(e);
		}
	}
	
	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
		case Response.SC_OK:
			this.handler.onTargetLevelLoaded(response.getText());
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
