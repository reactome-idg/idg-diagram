package org.reactome.web.fi.events;

import java.util.Map;

import org.reactome.web.diagram.data.Context;
import org.reactome.web.fi.handlers.PairwiseNumbersLoadedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author brunsont
 *
 */
public class PairwiseNumbersLoadedEvent extends GwtEvent<PairwiseNumbersLoadedHandler>{
    public static Type<PairwiseNumbersLoadedHandler> TYPE = new Type<>();

    Context context;
    Map<String, Integer> geneToTotalMap;
    
	public PairwiseNumbersLoadedEvent(Context context, Map<String, Integer> geneToTotalMap) {
		this.context = context;
		this.geneToTotalMap = geneToTotalMap;
	}

	@Override
	public Type<PairwiseNumbersLoadedHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PairwiseNumbersLoadedHandler handler) {
		handler.onPairwiseNumbersLoaded(this);
	}

	public Context getContext() {
		return context;
	}

	public Map<String, Integer> getGeneToTotalMap() {
		return geneToTotalMap;
	}

	@Override
	public String toString() {
		return "Pairwise relationship numbers loaded";
	}
}
