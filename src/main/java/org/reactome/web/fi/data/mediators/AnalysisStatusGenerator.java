package org.reactome.web.fi.data.mediators;

import org.reactome.web.fi.model.DataOverlay;

import com.google.gwt.event.shared.EventBus;

public class AnalysisStatusGenerator {

	private EventBus eventBus;
	private DataOverlay dataOverlay;
	
	public AnalysisStatusGenerator(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void makeAnalysis(DataOverlay dataOverlay) {
		this.dataOverlay = dataOverlay;
	}
}
