package org.reactome.web.fi.data.loader;

import java.util.List;

import org.reactome.web.diagram.client.DiagramFactory;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestException;

public class ReactomeSourcesLoader implements RequestCallback{
	
	public interface Handler{
		void onReactomeSourcesLoaded(String json);
		void onReactomeSourcesLoadedError(Throwable exception);
	}
	
	private final static String BASE_URL = DiagramFactory.SERVER + "/ContentService/data/query/ids";

	Handler handler;
	Request request;
	
	public ReactomeSourcesLoader(Handler handler) {
		this.handler = handler;
	}
	
	public void cancel() {
		if(request!=null && request.isPending())
			request.cancel();
	}
	
	public void load(List<String> sourcesList) {
		cancel();
		
		if(sourcesList == null || sourcesList.size()==0)
			this.handler.onReactomeSourcesLoadedError(new Exception("Cannot query database for 0 sources"));
		
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, BASE_URL);
		requestBuilder.setHeader("Accept", "application/json");
		requestBuilder.setRequestData(getPostData(sourcesList));
		requestBuilder.setCallback(this);
		try	{
			this.request = requestBuilder.send();
		} catch(RequestException e) {
			this.handler.onReactomeSourcesLoadedError(e);
		}
	}
	
	private String getPostData(List<String> sourcesList) {
		StringBuilder result = new StringBuilder();
		sourcesList.stream().forEach(S -> result.append(S).append(","));
		if(result.length()>0) {
			result.delete(result.length()-1, result.length());
			return result.toString();
		}
		return null;
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		switch(response.getStatusCode()) {
		case Response.SC_OK:
			this.handler.onReactomeSourcesLoaded(response.getText());
		default:
			this.handler.onReactomeSourcesLoadedError(new Exception(response.getStatusText()));
		}
	}

	@Override
	public void onError(Request request, Throwable exception) {
		// TODO Auto-generated method stub
		
	}
	
}
