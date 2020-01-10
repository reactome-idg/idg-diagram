package org.reactome.web.fi.data.loader;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import org.reactome.web.fi.data.overlay.model.pairwise.PairwiseOverlayProperties;

public class PairwiseDataLoader implements RequestCallback{

	public interface Handler{
		void onPairwiseDataLoaded();
		void onPairwiseDataLoadedError(Exception exception);
	}
	
	private final static String BASE_URL = "/idgpairwise";
	
	private Handler handler;
	private Request request;
	private PairwiseOverlayProperties properties;

	public PairwiseDataLoader(Handler handler) {
		this.handler = handler;
	}

	public void load(PairwiseOverlayProperties properties) {
		this.properties = properties;
		cancel();
		
		if(properties.getUniprots() == null) {
			Exception exception = new Exception("Cannot request overlay data for 0 ids.");
			this.handler.onPairwiseDataLoadedError(exception);
		}
	}
	
	@Override
	public void onResponseReceived(Request request, Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Request request, Throwable exception) {
		// TODO Auto-generated method stub
		
	}
	
	public void cancel() {
		if(this.request != null && this.request.isPending())
			this.request.cancel();
	}
	
}
