package org.reactome.web.fi.data.loader;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class TCRDInfoLoader{

	public interface Handler{
		void onExpressionTypesLoaded(List<String> info);
		void onTCRDInfoError(Throwable exception);
	}
	
	private static final String BASE_URL = "/tcrdws/";
	private static Request request;
	
	public static void loadExpressionTypes(Handler handler) {
		if(request != null && request.isPending())
			request.cancel();
		
		String url = BASE_URL + "expressionTypes";
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setHeader("Accept", "application/json");
		try {
			request = requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if(response.getStatusCode() == Response.SC_OK)
						GWT.log(response.getText());
						//handler.onExpressionTypesLoaded(getList(response.getText(), handler));
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					handler.onTCRDInfoError(new Exception(exception.getMessage()));
				}
			});
		} catch(RequestException ex) {
			handler.onTCRDInfoError(ex);
		}
	}
	
	private List<String> getList(String text, Handler handler) {
		// TODO Auto-generated method stub
		return null;
	}
}
