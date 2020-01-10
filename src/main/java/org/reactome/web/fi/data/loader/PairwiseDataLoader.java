package org.reactome.web.fi.data.loader;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class PairwiseDataLoader implements RequestCallback{

	public interface Handler{
		void onPairwiseDataLoaded();
		void onPairwiseDataLoadedError();
	}

	@Override
	public void onResponseReceived(Request request, Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Request request, Throwable exception) {
		// TODO Auto-generated method stub
		
	}
	
}
