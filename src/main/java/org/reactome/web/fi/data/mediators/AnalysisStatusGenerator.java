package org.reactome.web.fi.data.mediators;

import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.diagram.data.AnalysisStatus;
import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.event.shared.EventBus;

public class AnalysisStatusGenerator {

	private EventBus eventBus;
	private DataOverlay dataOverlay;
	private final String TOKEN = "aksdlfajlefjfons";
	
	public AnalysisStatusGenerator(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void makeAnalysis(DataOverlay dataOverlay) {
		this.dataOverlay = dataOverlay;
		
		//may require a custom result filter instead of an empty constructor
		AnalysisStatus analysisStatus = new AnalysisStatus(eventBus, TOKEN, new ResultFilter());
		//TODO: make expression summary
		//TODO: make Analysis summary
	}
}
